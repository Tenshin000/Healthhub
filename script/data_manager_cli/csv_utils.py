import json
import pandas as pd
import os
from pymongo import MongoClient
from config import JSON_DIR, CSV_DIR, get_config

def export_csv(host):
    config = get_config(host)
    client = MongoClient(config["MONGO_URI"])
    db = client[config["DB_NAME"]]

    users = list(db["users"].find())
    doctors = list(db["doctors"].find())
    with open(os.path.join(JSON_DIR,"user_likes.json"), 'r', encoding='utf-8') as f:
        likes = json.load(f)

    usermap = {u["ousername"]: u for u in users}
    doctormap = {d["name"]: d for d in doctors}

    os.makedirs(CSV_DIR, exist_ok=True)
    
    pd.DataFrame([{"id": str(u["_id"]), "name": u.get("name", "")} for u in users])\
        .to_csv(os.path.join(CSV_DIR, "users.csv"), index=False)
    
    pd.DataFrame([{"id": str(d["_id"]), "name": d.get("name", ""), "specializations": ";".join(d.get("specializations", []))} for d in doctors])\
        .to_csv(os.path.join(CSV_DIR, "doctors.csv"), index=False)
    
    reviews = [{"doctor_id": str(d["_id"]), "user_id": str(usermap[r["ousername"]]["_id"])} 
               for d in doctors for r in d.get("reviews", []) if r["ousername"] in usermap]
    pd.DataFrame(reviews).drop_duplicates()\
        .to_csv(os.path.join(CSV_DIR, "reviews.csv"), index=False)

    endorses = [{"user_id": str(usermap[u]["_id"]), "doctor_id": str(doctormap[d]["_id"])} 
                for u, liked in likes.items() for d in liked if u in usermap and d in doctormap]
    pd.DataFrame(endorses).drop_duplicates()\
        .to_csv(os.path.join(CSV_DIR, "endorses.csv"), index=False)

    print("✅ CSV esportati")
    client.close()
