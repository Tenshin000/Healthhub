from pymongo import MongoClient
from pprint import pprint

# Connessione al database
client = MongoClient("mongodb://10.1.1.55:27020,10.1.1.56:27020,10.1.1.57:27020/?replicaSet=lsmdb")
db = client["healthhub"]  # <-- Cambia con il nome corretto
collection = db["doctors"]

# Lista dei termini di ricerca
search_terms = ["ortopedia", "cardiologia milano", "gianmarco torrisi", "torino", "medico di base", "pistoia", "psicologia roma"]

def run_aggregation_custom_regex(term):
    pipeline = [
        {
            "$match": {
                "$or": [
                    {"name": {"$regex": term, "$options": "i"}},
                    {"specializations": {"$regex": term, "$options": "i"}},
                    {"address.city": {"$regex": term, "$options": "i"}},
                    {"address.province": {"$regex": term, "$options": "i"}},
                ]
            }
        },
        {
            "$addFields": {
                "score": {
                    "$min": [
                        {
                            "$cond": [
                                {"$regexMatch": {"input": "$name", "regex": term, "options": "i"}},
                                0,
                                5
                            ]
                        },
                        {
                            "$cond": [
                                {
                                    "$gt": [
                                        {
                                            "$size": {
                                                "$filter": {
                                                    "input": "$specializations",
                                                    "as": "s",
                                                    "cond": {
                                                        "$regexMatch": {
                                                            "input": "$$s",
                                                            "regex": term,
                                                            "options": "i"
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        0
                                    ]
                                },
                                1,
                                5
                            ]
                        },
                        {
                            "$cond": [
                                {"$regexMatch": {"input": "$address.city", "regex": term, "options": "i"}},
                                2,
                                5
                            ]
                        },
                        {
                            "$cond": [
                                {"$regexMatch": {"input": "$address.province", "regex": term, "options": "i"}},
                                3,
                                5
                            ]
                        }
                    ]
                }
            }
        },
        {"$sort": {"score": 1}},
        {"$limit": 250}
    ]
    return db.command("explain", {
        "aggregate": "doctors",
        "pipeline": pipeline,
        "cursor": {}
    })

def run_aggregation_text_search(term):
    pipeline = [
        {"$match": {"$text": {"$search": term}}},
        {"$addFields": {"score": {"$meta": "textScore"}}},
        {"$sort": {"score": -1}},
        {"$limit": 250}
    ]
    return db.command("explain", {
        "aggregate": "doctors",
        "pipeline": pipeline,
        "cursor": {}
    })

# Confronto tra le due aggregation
for term in search_terms:
    print(f"\n=== Termine di ricerca: '{term}' ===\n")

    print(">>> Aggregazione con regex personalizzata:")
    result = run_aggregation_custom_regex(term)
    #pprint(custom_result)
    stats = result['stages'][0]['$cursor']['executionStats']
    plan = result['stages'][0]['$cursor']['queryPlanner']['winningPlan']
    stage = stats['executionStages']
    input_stage = stage.get('inputStage', {})

    print("=== EXPLAIN ANALYSIS ===")
    print(f"Execution time       : {stats['executionTimeMillis']} ms")
    print(f"Documents examined   : {stats['totalDocsExamined']}")
    print(f"Index keys examined  : {stats['totalKeysExamined']}")
    print(f"Documents returned   : {stats['nReturned']}")
    print(f"Winning plan stage   : {plan['stage']}")
    print(f"Execution stage      : {stage['stage']}")
    if 'docsExamined' in input_stage:
        print(f"Docs examined (input): {input_stage['docsExamined']}")

    print("\n>>> Aggregazione con text search:")
    text_result = run_aggregation_text_search(term)
    #pprint(text_result)
    stats = text_result['stages'][0]['$cursor']['executionStats']
    plan = text_result['stages'][0]['$cursor']['queryPlanner']['winningPlan']
    stage = stats['executionStages']
    input_stage = stage.get('inputStage', {})

    print("=== EXPLAIN ANALYSIS ===")
    print(f"Execution time       : {stats['executionTimeMillis']} ms")
    print(f"Documents examined   : {stats['totalDocsExamined']}")
    print(f"Index keys examined  : {stats['totalKeysExamined']}")
    print(f"Documents returned   : {stats['nReturned']}")
    print(f"Winning plan stage   : {plan['stage']}")
    print(f"Execution stage      : {stage['stage']}")
    if 'docsExamined' in input_stage:
        print(f"Docs examined (input): {input_stage['docsExamined']}")

    print("\n" + "="*50)
