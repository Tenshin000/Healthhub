from bson import ObjectId
from pymongo import MongoClient
from datetime import datetime

client = MongoClient("mongodb://10.1.1.55:27020,10.1.1.56:27020,10.1.1.57:27020/?replicaSet=lsmdb")
db = client["healthhub"]
collection = db["appointments"]
patients = db["users"]

# Data limite per le visite da cercare
date = datetime(2025, 6, 16)

# Seleziona 10 pazienti con più visite totali 
patient_ids = [
    ObjectId("684ada4637804916ca65019d"),
    ObjectId("684ada4537804916ca63b9d8"),
    ObjectId("684ada4637804916ca656e11"),
    ObjectId("684ada4637804916ca64f5d8"),
    ObjectId("684ada4537804916ca64c07b"),
    ObjectId("684ada4537804916ca64b480"),
    ObjectId("684ada4537804916ca63fe36"),
    ObjectId("684ada4437804916ca635f72"),
    ObjectId("684ada4537804916ca647dd7"),
    ObjectId("684ada4537804916ca63ab2c")
]


# Funzione per eseguire la query e ottenere il piano di esecuzione
def explain_query(patient_id):
    return collection.find(
        {
            "patient._id": patient_id,
            "date": { "$lt": date }
        }
    ).sort("date", 1).explain()

# Funzione per stampare statistiche dettagliate
def print_results(results, label):
    stats = results['executionStats']
    print(f"--- {label} ---")
    print(f"Execution time       : {stats['executionTimeMillis']} ms")
    print(f"Documents examined   : {stats['totalDocsExamined']}")
    print(f"Index keys examined  : {stats['totalKeysExamined']}")
    print(f"Documents returned   : {stats['nReturned']}")
    print()

# Esegui per ogni paziente la query prima, dopo e dopo aver ripristinato l'indice
for i, patient_id in enumerate(patient_ids, start=1):
    print(f"\n==============================")
    print(f"Analisi per il paziente #{i}: {patient_id}")
    print("==============================\n")

    # Con indice
    results = explain_query(patient_id)
    print_results(results, "Con indice (idx_patient_id attivo)")

    # Rimuove indice
    print("→ Rimuovo indice su 'patient._id'...\n")
    collection.drop_index("idx_patient_id")

    # Senza indice
    results = explain_query(patient_id)
    print_results(results, "Senza indice (idx_patient_id rimosso)")

    # Ricrea indice
    print("→ Ripristino indice su 'patient._id'...\n")
    collection.create_index([("patient._id", 1)], name="idx_patient_id")
