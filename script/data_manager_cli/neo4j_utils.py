from neo4j import GraphDatabase
from config import get_config
from time import sleep

def run_query(query, config, description=""):
    driver = GraphDatabase.driver(config["NEO4J_URI"], auth=config["NEO4J_AUTH"])
    with driver.session() as session:
        result = session.run(query)
        summary = result.consume()

        print(f"✅ {description} completata - {summary.counters}")
    driver.close()

def import_to_neo4j(config):
    # Step 1: Creazione indici e nodi
    node_queries = [
        {
            "description": "Creazione indice User",
            "query": "CREATE INDEX user_id_index IF NOT EXISTS FOR (u:User) ON (u.id);"
        },
        {
            "description": "Creazione indice Doctor",
            "query": "CREATE INDEX doctor_id_index IF NOT EXISTS FOR (d:Doctor) ON (d.id);"
        },
        {
            "description": "Import utenti",
            "query": """
                LOAD CSV WITH HEADERS FROM 'file:///users.csv' AS row
                CALL {
                    WITH row
                    CREATE (:User {id: row.id, name: row.name})
                } IN TRANSACTIONS OF 100 ROWS
                RETURN count(*) AS count;
            """
        },
        {
            "description": "Import dottori",
            "query": """
                LOAD CSV WITH HEADERS FROM 'file:///doctors.csv' AS row
                CALL {
                    WITH row
                    CREATE (:Doctor {id: row.id, name: row.name, specializations: split(row.specializations,";")})
                } IN TRANSACTIONS OF 100 ROWS
                RETURN count(*) AS count;
            """
        }
    ]

    for item in node_queries:
        run_query(item["query"], config, item["description"])
        sleep(1)  # Aggiungi un breve ritardo tra le operazioni per evitare problemi di concorrenza

    # Step 2: Import relazioni con logging
    relationship_queries = [
        {
            "description": "Import relazioni REVIEWED",
            "query": """
                LOAD CSV WITH HEADERS FROM 'file:///reviews.csv' AS row
                CALL {
                    WITH row
                    MATCH (u:User {id: row.user_id})
                    MATCH (d:Doctor {id: row.doctor_id})
                    WITH u, d
                    CREATE (u)-[:REVIEWED]->(d)
                } IN TRANSACTIONS OF 100 ROWS
                RETURN count(*) AS count;
            """
        },
        {
            "description": "Import relazioni ENDORSED",
            "query": """
                LOAD CSV WITH HEADERS FROM 'file:///endorses.csv' AS row
                CALL {
                    WITH row
                    MATCH (u:User {id: row.user_id})
                    MATCH (d:Doctor {id: row.doctor_id})
                    WITH u, d
                    CREATE (u)-[:ENDORSED]->(d)
                } IN TRANSACTIONS OF 100 ROWS
                RETURN count(*) AS count;
            """
        }
    ]

    for item in relationship_queries:
        run_query(item["query"], config, item["description"])

    # Step 3: Stampa riassuntiva
    stats = [
        ("User", "Utenti"),
        ("Doctor", "Dottori"),
        ("[:REVIEWED]", "Relazioni REVIEWED"),
        ("[:ENDORSED]", "Relazioni ENDORSED"),
    ]

    driver = GraphDatabase.driver(config["NEO4J_URI"], auth=config["NEO4J_AUTH"])
    with driver.session() as session:
        for label, label_desc in stats:
            if label.startswith("[:"):
                q = f"MATCH ()-{label}->() RETURN count(*) AS c"
            else:
                q = f"MATCH (n:{label}) RETURN count(n) AS c"
            result = session.run(q)
            print(f"📊 {label_desc}: {result.single()['c']} trovati")
    driver.close()


def drop_neo4j(config):
    driver = GraphDatabase.driver(config["NEO4J_URI"], auth=config["NEO4J_AUTH"])
    with driver.session() as session:
        
        while True:
            # Esegui la query per eliminare i nodi in batch
            # Conta quanti nodi ci sono ancora
            result = session.run("MATCH (n) RETURN count(n) AS count")
            count = result.single()["count"]

            if count == 0:
                break

            # Cancella un batch di nodi
            session.run(f"""
            CALL {{
              WITH *
              MATCH (n)
              WITH n LIMIT 3000
              DETACH DELETE n
            }}
            """)
        print("✅ Database Neo4j eliminato")
        

