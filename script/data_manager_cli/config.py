
JSON_DIR = "./json/"
CSV_DIR = "./csv/"


def get_config(host: str = "localhost", user: str = "paolo") -> dict:
    return {
        "MONGO_URI": f"mongodb://{host}:27017/",
        "DB_NAME": "healthhub",
        "NEO4J_URI": f"bolt://{host}:7687",
        "NEO4J_IMPORT_PATH": f"/home/{user}/neo4j/import",
    }

