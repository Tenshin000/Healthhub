import argparse
from mongo_utils import import_data_to_mongo, drop_mongo
from csv_utils import export_csv
from transfer_utils import transfer_files
from neo4j_utils import import_to_neo4j, drop_neo4j
from json_utils import generate_all_json
from config import get_config


def main():
    parser = argparse.ArgumentParser(description="HealthHub Data CLI")
    parser.add_argument("--generate-json", type=str, metavar="INPUT_JSON", help="Genera i file JSON dal un file specificato")

    parser.add_argument("--host", type=str, nargs="?", const="localhost", default="localhost",
                        help="IP del server remoto (default: localhost)")
    
    parser.add_argument("--neo4j-uri", type=str, default="localhost:7687",
                        help="URI del database Neo4j (default: localhost:7687)")
    
    parser.add_argument("--mongo-uri", type=str, default="localhost:27017/",
                        help="URI del database MongoDB (default: localhost:27017/)")
    
    parser.add_argument("--user", type=str, default="paolo", help="Nome utente remoto (default: 'paolo')")

    parser.add_argument("--import-mongo", action="store_true", help="Importa i dati in MongoDB")

    parser.add_argument("--export-csv", action="store_true", help="Esporta i CSV da MongoDB")

    parser.add_argument("--transfer-csv", type=str, default="/var/lib/neo4j/import/",
                        help="Percorso per trasferire i file CSV (default: /var/lib/neo4j/import/)")
    
    parser.add_argument("--import-neo4j", action="store_true", help="Importa i CSV in Neo4j")

    parser.add_argument("--drop-mongo", action="store_true", help="Elimina il database MongoDB")

    parser.add_argument("--drop-neo4j", action="store_true", help="Elimina il database Neo4j")

    args = parser.parse_args()

    config = get_config(args.host, args.user, args.mongo_uri, args.neo4j_uri)

    flags = [
        args.generate_json,
        args.drop_mongo,
        args.import_mongo,
        args.export_csv,
        args.transfer_csv,
        args.drop_neo4j,
        args.import_neo4j
    ]
    total_steps = sum(1 for f in flags if f)
    step = 1

    if args.generate_json:
        print(f"[{step}/{total_steps}] ➡ Generazione JSON...")
        generate_all_json(args.generate_json)
        print(f"[{step}/{total_steps}] ✓ JSON generati.")
        step += 1

    if args.drop_mongo:
        print(f"[{step}/{total_steps}] ➡ Eliminazione MongoDB...")
        drop_mongo(config)
        print(f"[{step}/{total_steps}] ✓ MongoDB eliminato.")
        step += 1

    if args.import_mongo:
        print(f"[{step}/{total_steps}] ➡ Importazione in MongoDB...")
        import_data_to_mongo(config)
        print(f"[{step}/{total_steps}] ✓ Importazione completata.")
        step += 1

    if args.export_csv:
        print(f"[{step}/{total_steps}] ➡ Esportazione CSV...")
        export_csv(config)
        print(f"[{step}/{total_steps}] ✓ CSV esportati.")
        step += 1

    if args.transfer_csv:
        print(f"[{step}/{total_steps}] ➡ Trasferimento CSV...")
        transfer_files(config)
        print(f"[{step}/{total_steps}] ✓ CSV trasferiti.")
        step += 1

    if args.drop_neo4j:
        print(f"[{step}/{total_steps}] ➡ Eliminazione Neo4j...")
        drop_neo4j(config)
        print(f"[{step}/{total_steps}] ✓ Neo4j eliminato.")
        step += 1

    if args.import_neo4j:
        print(f"[{step}/{total_steps}] ➡ Importazione in Neo4j...")
        import_to_neo4j(config)
        print(f"[{step}/{total_steps}] ✓ Importazione completata.")
        step += 1

if __name__ == "__main__":
    main()
