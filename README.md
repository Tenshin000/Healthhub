# HealthHub
HealthHub is a university project developed for the LSMD (Large Scale and Multi-structured Databases) course. The application is designed to manage healthcare-related data, providing features for doctors and patients, such as profile management, appointment scheduling, and interaction with favorite doctors. The project leverages a multi-database architecture, combining MongoDB and Neo4j to handle structured and semi-structured data efficiently.

## Features

- **User Profile Management**: Users can update their personal information and manage their profiles.
- **Appointments**: Patients can view, schedule, and manage their appointments with doctors.
- **Favorites**: Patients can interact with their favorite doctors, including endorsements and reviews.
- **Analytics**: Doctors can access analytics on visits, earnings, and patient interactions.

---

## Development Environment

This project is intended to be used within **IntelliJ IDEA**.

### Creating Required Containers
You can instantiate the required databases using Docker:

#### MongoDB
```bash
docker run -d -p 27017:27017 --name mongo-cont mongo
```

#### Neo4j
```bash
docker run -p 7474:7474 -p 7687:7687 \
  -v $HOME/neo4j/data:/data \
  -v $HOME/neo4j/import:/import \
  -d --env NEO4J_AUTH=none \
  --name neo4j-cont neo4j
```

### Accessing Database Interfaces
To access the Mongo shell inside the running container:
```bash
docker container exec -it mongo-cont mongosh
```
This command allows you to connect to the MongoDB shell within the container and directly manipulate database data.

Neo4j offers a web interface accessible at [http://localhost:7474](http://localhost:7474).

### Managing Containers
Stop the containers:
```bash
docker stop mongo-cont neo4j-cont
```

Start the containers again:
```bash
docker start mongo-cont neo4j-cont
```

---

## Data Import Tool

The project includes a data import tool located in the `script` directory. This tool is designed to populate the databases with initial data for testing and development purposes.

### Features

- Imports structured data into **MongoDB** for managing user profiles, appointments, and other healthcare-related information.
- Imports graph-based data into **Neo4j** for managing relationships between users, such as endorsements and reviews.
- Supports JSON and CSV file formats for data input.

### Usage

1. Navigate to the `script` directory:
   ```bash
   cd script/data_manager_cli
   ```

2. Run the import script:
   ```bash
   python cli.py [opzioni]
   ```

### Main Options

- `--generate-json <input_file>`  
  Generates one or more JSON files from a structured input file.

- `--import-mongo`  
  Imports the generated JSON files into the MongoDB database.

- `--export-csv`  
  Exports data from MongoDB into CSV files.

- `--transfer-csv`  
  Transfers the exported CSV files to the remote Neo4j server.

- `--import-neo4j`  
  Imports the CSV files into Neo4j, creating nodes and relationships.

- `--drop-mongo`  
  Deletes the MongoDB database (destructive operation).

- `--drop-neo4j`  
  Deletes all data in the Neo4j database (destructive operation).

### Additional Options

- `--mongo-uri <uri>`  
  Specifies the MongoDB connection URI (default: `localhost:27017`).

- `--host <ip>`  
  Specifies the IP address of the remote server (default: `localhost`).

- `--user <username>`  
  Specifies the SSH username for remote server access (default: `paolo`).

### Requirements

- **Python 3.8+**
- Required Python libraries:
    - `pymongo` for MongoDB integration
    - `neo4j` for Neo4j integration
    - `pandas` for handling CSV files

#### Install dependencies using:
```bash
pip install -r requirements.txt
```

## Examples

- Generate JSON files and import into MongoDB:
  ```bash
  python cli.py --generate-json scraped.json
  ```
- Import JSON files into MongoDB:
  ```bash
  python cli.py --import-mongo --mongo-uri "localhost:27018,localhost:27019,localhost:27020"
  ```

- Export from data from MongoDB and generate CSV files:
  ```bash
  python cli.py --export-csv --mongo-uri "localhost:27018,localhost:27019,localhost:27020"
  ```

- Transfer CSV files to the Neo4j server:
  ```bash
  python cli.py --transfer-csv --host 192.168.2.6 --user root
  ```

- Import CSV into Neo4j:
  ```bash
  python cli.py --neo4j-uri 192.168.2.6:7687 --import-neo4j
  ```

- Clean up both databases:
  ```bash
  python cli.py --drop-mongo --drop-neo4j
  ```

## Notes

- Commands can be combined to automate the full pipeline.
- Remote operations require SSH access and appropriate permissions to read/write in the target destination.
- The `--transfer-csv` command may not work in all environments and could require additional configuration or manual file transfer.
- For manual transfer, move the CSV files generated with `--export-csv` from the `csv` subdirectory within the import tool to the `import` volume mount point (e.g., `$HOME/neo4j/import/`).
- Make sure Docker containers for MongoDB and Neo4j are running before executing the CLI commands.
