from pymongo import MongoClient
from bson import ObjectId
from datetime import datetime

# Connessione a MongoDB
client = MongoClient("mongodb://10.1.1.55:27020,10.1.1.56:27020,10.1.1.57:27020/?replicaSet=lsmdb")
db = client["healthhub"]
collection = db["appointments"]

# Parametri
year = 2025

#doctor_id = ObjectId("684adad437804916ca65ef04")  # Sostituisci con un vero ObjectId
# 10 doctor_id da analizzare, sono i primi 10 dottori con più visite totali
doctor_ids = [
  ObjectId('684adad437804916ca668f27'),
  ObjectId('684adad437804916ca65f694'),
  ObjectId('684adad437804916ca65ce02'),
  ObjectId('684adad437804916ca65e751'),
  ObjectId('684adad437804916ca65c08e'),
  ObjectId('684adad437804916ca66281f'),
  ObjectId('684adad437804916ca6603cc'),
  ObjectId('684adad437804916ca660d75'),
  ObjectId('684adad437804916ca66245d'),
  ObjectId('684adad437804916ca65b393')
]


# Costruzione pipeline di aggregazione per un dato doctor_id
def build_pipeline(doctor_id):
    return [
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

# Esegue explain su una pipeline
def run_explain(pipeline):
    return db.command("explain", {
        "aggregate": "appointments",
        "pipeline": pipeline,
        "cursor": {}
    })

# Stampa i risultati dello explain
def print_explain_stats(result, label):
    stats = result['stages'][0]['$cursor']['executionStats']
    stage = stats['executionStages']
    input_stage = stage.get('inputStage', {})

    print(f"--- {label} ---")
    print(f"Execution time       : {stats['executionTimeMillis']} ms")
    print(f"Documents examined   : {stats['totalDocsExamined']}")
    print(f"Index keys examined  : {stats['totalKeysExamined']}")
    print(f"Documents returned   : {stats['nReturned']}")
    print(f"Execution stage      : {stage['stage']}")
    if 'docsExamined' in input_stage:
        print(f"Docs examined (input): {input_stage['docsExamined']}")
    print()

# Loop su ogni dottore
for i, doctor_id in enumerate(doctor_ids, start=1):
    print(f"\n==============================")
    print(f"Analisi per il dottore #{i}: {doctor_id}")
    print("==============================\n")

    pipeline = build_pipeline(doctor_id)

    # Con indice
    result = run_explain(pipeline)
    print_explain_stats(result, "Con indice (idx_doctor_id attivo)")

    # Rimuovi indice
    print("→ Rimuovo indice su 'doctor._id'...\n")
    collection.drop_index("idx_doctor_id")

    # Senza indice
    result = run_explain(pipeline)
    print_explain_stats(result, "Senza indice (idx_doctor_id rimosso)")

    # Ricrea indice
    print("→ Ripristino indice su 'doctor._id'...\n")
    collection.create_index([("doctor._id", 1)], name="idx_doctor_id")
