
JSON_DIR = "./json/"
CSV_DIR = "./csv/"


def get_config(host: str = "localhost", user: str = "paolo", mongo_uri: str = "localhost:27017/") -> dict:
    return {
        "MONGO_URI": f"mongodb://{mongo_uri}",
        "DB_NAME": "healthhub",
        "NEO4J_URI": f"bolt://{host}:7687",
        "NEO4J_IMPORT_PATH": f"/home/{user}/neo4j/import",
    }

