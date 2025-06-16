from pymongo import MongoClient
from bson import ObjectId
from datetime import datetime
import time

# Connessione a MongoDB
client = MongoClient("mongodb://10.1.1.55:27020,10.1.1.56:27020,10.1.1.57:27020/?replicaSet=lsmdb")
db = client["healthhub"]
collection = db["appointments"]

# Parametri
doctor_id = ObjectId("684adad437804916ca65ef04")  # Sostituisci con un vero ObjectId
year = 2025

# Pipeline di aggregazione
pipeline = [
    {
        "$match": {
            "doctor._id": doctor_id,
            "date": {
                "$gte": datetime(year, 1, 1),
                "$lt": datetime(year + 1, 1, 1)
            }
        }
    },
    {
        "$project": {
            "month": { "$month": "$date" },
            "price": 1
        }
    },
    {
        "$group": {
            "_id": "$month",
            "total": { "$sum": "$price" }
        }
    },
    {
        "$project": {
            "month": "$_id",
            "total": 1,
            "_id": 0
        }
    }
]

def run_aggregation(label):
    return db.command("explain", {
        "aggregate": "appointments",
        "pipeline": pipeline,
        "cursor": {}
    })

# Verifica indice
print("Indici esistenti:")
for idx in collection.list_indexes():
    print(idx)

# Esecuzione con indice attivo
result = run_aggregation("Con indice")

stats = result['stages'][0]['$cursor']['executionStats']
stage = stats['executionStages']
input_stage = stage.get('inputStage', {})

print("=== EXPLAIN ANALYSIS ===")
print(f"Execution time       : {stats['executionTimeMillis']} ms")
print(f"Documents examined   : {stats['totalDocsExamined']}")
print(f"Index keys examined  : {stats['totalKeysExamined']}")
print(f"Documents returned   : {stats['nReturned']}")
print(f"Execution stage      : {stage['stage']}")
if 'docsExamined' in input_stage:
    print(f"Docs examined (input): {input_stage['docsExamined']}")

# Rimozione temporanea indice
print("Rimuovo indice su doctor._id...")
collection.drop_index("idx_doctor_id")

# Esecuzione senza indice
result = run_aggregation("Senza indice")

stats = result['stages'][0]['$cursor']['executionStats']
stage = stats['executionStages']
input_stage = stage.get('inputStage', {})

print("=== EXPLAIN ANALYSIS ===")
print(f"Execution time       : {stats['executionTimeMillis']} ms")
print(f"Documents examined   : {stats['totalDocsExamined']}")
print(f"Index keys examined  : {stats['totalKeysExamined']}")
print(f"Documents returned   : {stats['nReturned']}")
print(f"Execution stage      : {stage['stage']}")
if 'docsExamined' in input_stage:
    print(f"Docs examined (input): {input_stage['docsExamined']}")

# Ricreazione indice per non lasciar il DB in uno stato incoerente
