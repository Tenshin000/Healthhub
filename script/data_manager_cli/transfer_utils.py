import subprocess
from config import CSV_DIR, get_config

def transfer_files(config):
    files = ["users.csv", "doctors.csv", "reviews.csv", "endorses.csv"]
    for file in files:
        local = f"{CSV_DIR}/{file}"
        remote = f"{config["USER"]}@{config["HOST"]}:{config["NEO4J_IMPORT_PATH"]}"
        subprocess.run(["scp", local, remote], check=True)
        print(f"✅ Trasferito {file}")
