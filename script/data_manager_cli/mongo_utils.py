import json
import os
from pymongo import MongoClient
from config import JSON_DIR, get_config

def drop_mongo(host):
    config = get_config(host)
    client = MongoClient(config["MONGO_URI"])
    db = client[config["DB_NAME"]]
    # Ottieni i nomi di tutte le collezioni
    collection_names = db.list_collection_names()

    # Itera su ogni collezione e stampa un documento campione
    for name in collection_names:
        db[name].drop()

def import_data_to_mongo(host):
    config = get_config(host)
    client = MongoClient(config["MONGO_URI"])
    db = client[config["DB_NAME"]]

    # Users
    if "users" not in db.list_collection_names():
        db["users"].drop()
        with open(os.path.join(JSON_DIR, "users.json"), "r", encoding="utf-8") as file:
            data = json.load(file)
        db["users"].insert_many(data if isinstance(data, list) else [data])

        db["users"].update_many(
            {"dob": {"$type": "string"}},  # Filtro per documenti che hanno "dob" come stringa
            [
                {
                    "$set": {
                        "dob": {
                            "$dateFromString": {
                                "dateString": "$dob",  # Campo da convertire
                                "format": "%Y-%m-%d"  # Formato della data
                            }
                        }
                    }
                }
            ]
        )

        print("✅ Users importati")

    # Doctors
    if "doctors" not in db.list_collection_names():
        with open(os.path.join(JSON_DIR, "doctors.json"), "r", encoding="utf-8") as file:
            data = json.load(file)

        users = {u["username"]: u for u in db["users"].find()}

        for doc in data:
            for rev in doc["reviews"]:
                rev["patientId"] = users[rev["patientId"]]["_id"]

        db["doctors"].insert_many(data if isinstance(data, list) else [data])

        db["doctors"].update_many(
            {
                "reviews.date": {"$type": "string"}
            },
            [
                {
                    "$set": {
                        "reviews": {
                            "$map": {
                                "input": "$reviews",
                                "as": "rev",
                                "in": {
                                    "$mergeObjects": [
                                        "$$rev",
                                        {
                                            "date": {
                                                "$dateFromString": {
                                                    "dateString": "$$rev.date"
                                                }
                                            }
                                        }
                                    ]
                                }
                            }
                        }
                    }
                }
            ]
        )

        print("✅ Doctors importati")


    # Appointments
    if "appointments" not in db.list_collection_names():
        with open(os.path.join(JSON_DIR, "appointments.json"), "r", encoding="utf-8") as file:
            data = json.load(file)

        doctors = {d["name"]: d for d in db["doctors"].find()}
        users = {u["username"]: u for u in db["users"].find()}

        for a in data:
            d = a.get("doctor", {}).get("name")
            u = a.get("patient", {}).get("ousername")
            if d in doctors and u in users:
                a["doctor"]["id"] = doctors[d]["_id"]
                a["patient"]["id"] = users[u]["_id"]
                a["patient"].pop("ousername", None)

        db["appointments"].insert_many(data)

        db["appointments"].update_many(
            { "date": { "$type": "string" } },
            [
                {
                    "$set": {
                        "date": { "$dateFromString": { "dateString": "$date" } }
                    }
                }
            ]
        )
        
        print("✅ Appointments importati")

    client.close()
