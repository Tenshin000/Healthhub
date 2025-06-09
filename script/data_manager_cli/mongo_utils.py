import json
import os
from pymongo import MongoClient
from config import JSON_DIR, get_config

def drop_mongo(host, mongo_uri):
    config = get_config(host, mongo_uri=mongo_uri)
    client = MongoClient(config["MONGO_URI"])
    db = client[config["DB_NAME"]]
    # Ottieni i nomi di tutte le collezioni
    collection_names = db.list_collection_names()

    # Itera su ogni collezione e stampa un documento campione
    for name in collection_names:
        db[name].drop()

def import_data_to_mongo(host, mongo_uri):
    config = get_config(host, mongo_uri=mongo_uri)
    client = MongoClient(config["MONGO_URI"])
    db = client[config["DB_NAME"]]

    if "templates" not in db.list_collection_names():
        db["templates"].drop()
        with open(os.path.join(JSON_DIR, "templates.json"), "r", encoding="utf-8") as file:
            data = json.load(file)
        db["templates"].insert_many(data if isinstance(data, list) else [data])

        print("✅ Templates importati")

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
        templates = [tem["_id"] for tem in db["templates"].find()]

        for t, doc in enumerate(data):
            doc["calendarTemplates"] = [templates[t]]
            for rev in doc["reviews"]:
                rev["patientId"] = users[rev["patientId"]]["_id"]


        db["doctors"].insert_many(data if isinstance(data, list) else [data])

        db["doctors"].update_many(
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
            d = a.get("doctor", {}).get("_id")
            u = a.get("patient", {}).get("_id")
            if d in doctors and u in users:
                a["doctor"]["_id"] = doctors[d]["_id"]
                a["patient"]["_id"] = users[u]["_id"]
                a["doctor"].pop("id",None)
                a["patient"].pop("id",None)

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
