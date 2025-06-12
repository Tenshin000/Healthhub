
JSON_DIR = "./json/"
CSV_DIR = "./csv/"


def get_config(host: str = "localhost", 
               user: str = "paolo", 
               mongo_uri: str = "localhost:27017/", 
               neo4j_uri:str = "localhost:7687/") -> dict:
    return {
        "MONGO_URI": f"mongodb://{mongo_uri}",
        "DB_NAME": "healthhub",
        "NEO4J_URI": f"bolt://{neo4j_uri}",
        "NEO4J_AUTH": ("neo4j", "healthhub"),
        "NEO4J_IMPORT_PATH": f"/var/lib/neo4j/import/",
        "USER": user,
        "HOST": host
    }

