import subprocess
from config import CSV_DIR, get_config

def transfer_files(host,user):
    config = get_config(host,user)
    files = ["users.csv", "doctors.csv", "reviews.csv", "endorses.csv"]
    for file in files:
        local = f"{CSV_DIR}/{file}"
        remote = f"{user}@{host}:{config["NEO4J_IMPORT_PATH"]}"
        subprocess.run(["scp", local, remote], check=True)
        print(f"✅ Trasferito {file}")
