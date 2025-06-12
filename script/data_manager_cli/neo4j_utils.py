from neo4j import GraphDatabase
from config import get_config

def run_query(query, config):

    driver = GraphDatabase.driver(config["NEO4J_URI"], auth=config["NEO4J_AUTH"])
    with driver.session() as session:
        session.run(query)
    driver.close()

def import_to_neo4j(config):
    queries = [
        "CREATE INDEX user_id_index IF NOT EXISTS FOR (u:User) ON (u.id);",
        "CREATE INDEX doctor_id_index IF NOT EXISTS FOR (d:Doctor) ON (d.id);",
        """
        LOAD CSV WITH HEADERS FROM 'file:///users.csv' AS row
        CREATE (:User {id: row.id, name: row.name});
        """,
        """
        LOAD CSV WITH HEADERS FROM 'file:///doctors.csv' AS row
        CREATE (:Doctor {id: row.id, name: row.name, specializations: split(row.specializations,";")});
        """,
        """
        LOAD CSV WITH HEADERS FROM 'file:///reviews.csv' AS row
        MATCH (u:User {id: row.user_id})
        MATCH (d:Doctor {id: row.doctor_id})
        CREATE (u)-[:REVIEWED]->(d);
        """,
        """
        LOAD CSV WITH HEADERS FROM 'file:///endorses.csv' AS row
        MATCH (u:User {id: row.user_id})
        MATCH (d:Doctor {id: row.doctor_id})
        CREATE (u)-[:ENDORSED]->(d);
        """
    ]
    for q in queries:
        run_query(q, config)
        print("✅ Query eseguita")

def drop_neo4j(config):
    driver = GraphDatabase.driver(config["NEO4J_URI"])
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
              MATCH (n)
              WITH n LIMIT 30000
              DETACH DELETE n
            }}
            """)

