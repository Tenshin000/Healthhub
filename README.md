# HealthHub
HealthHub is a university project developed for the LSMD (Large Scale and Multi-structured Databases) course. The application is designed to manage healthcare-related data, providing features for doctors and patients, such as profile management, appointment scheduling, and interaction with favorite doctors. The project leverages a multi-database architecture, combining MongoDB and Neo4j to handle structured and semi-structured data efficiently. For more information you can read the documentation (*HealthHubDocumentation.pdf*). 

\[*This is a copy of the original project that is in: "https://github.com/paolpal/healthhub/tree/master". This project had a final score of 26.*

*There are three errors, not in the execution of the code (the application works normally), but in the design of the application.*
1. *In Appointment there is a preview in the patient where we forgot to put the telephone number and therefore every time we go and retrieve it from the Patient;*
2. *We didn't include a paging mechanism (to be honest, it's because we didn't think it would be considered). However, we didn't set a maximum size for reviews. The pin button wasn't defined and could have been defined to limit the number of reviews so that it would automatically delete older ones, but not pinned ones;*
3. *The queries for Neo4j's recommendation system would have benefited from redundancies that we instead put into Mongo DB without ever really using them.*

*However, the result is still satisfactory and these errors can be easily corrected.* \]

## Features

- **User Profile Management**: Users can update their personal information and manage their profiles;
- **Appointments**: Patients can view, schedule, and manage their appointments with doctors;
- **Favorites**: Patients can interact with their favorite doctors, including endorsements and reviews;
- **Analytics**: Doctors can access analytics on visits, earnings, and patient interactions.

---

## Equipment
The tests were conducted on three identical virtual machines (10.1.1.55, 10.1.1.56, 10.1.1.57) provided by the University of Pisa, each configured with the following hardware and software specifications:

- **CPU**: 2 virtual CPUs (vCPUs), mapped to Intel(R) Xeon(R) Silver 4208 CPU @2.10GHz, provided via KVM virtualization
- **RAM**: 6.8 GB of system memory
- **Disk**: 40 GB of allocated virtual storage (ext4 filesystem)
- **Operating System**: Ubuntu 22.04.1 LTS (Jammy Jellyfish), 64-bit

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
