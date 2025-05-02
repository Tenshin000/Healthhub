from collections import defaultdict
import os
import json
import hashlib
import random
from datetime import datetime, timedelta
from faker import Faker
from tqdm import tqdm
from config import JSON_DIR

fake = Faker('it_IT')
PASSWORD_HASH = hashlib.sha256("password".encode()).hexdigest()

def generate_user(username):
    profile = fake.simple_profile()
    name = profile['name']
    gender = profile['sex']
    dob = profile['birthdate']

    fiscal_code = (
        name.split()[1][:3].upper().ljust(3, 'X') +
        name.split()[0][:3].upper().ljust(3, 'X') +
        dob.strftime("%y%m%d") +
        ('M' if gender == 'M' else 'F') +
        ''.join(random.choices("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", k=4))
    )[:16]

    phone_numbers = [fake.phone_number() for _ in range(2)]
    email = profile['mail']

    return {
        "fiscalCode": fiscal_code,
        "name": name,
        "passwordHash": PASSWORD_HASH,
        "dateOfBirth": dob.strftime('%Y-%m-%d'),
        "gender": gender,
        "phoneNumbers": phone_numbers,
        "email": email,
        "ousername": username
    }

def generate_users(data):
    usernames = {review["name"] for doctor in data if "reviews" in doctor for review in doctor["reviews"] if "name" in review}
    users = [generate_user(username) for username in tqdm(usernames, desc="Generating users")]
    return users

def refactor_doctor(doctor, usermap):
    profile = fake.simple_profile()
    email = profile["mail"]
    _, domain = email.split('@') 

    try:
        first_name, last_name = doctor["name"].split()
        email = f"{first_name.lower()}.{last_name.lower()}@{domain}"
    except Exception:
        pass

    reviews = []
    if "reviews" in doctor:
        for review in doctor["reviews"]:
            review["ousername"] = review["name"]
            review["name"] = usermap[review["name"]]["name"]
            reviews.append(review)

    return {
        "name": doctor["name"],
        "email": email,
        "passwordHash": PASSWORD_HASH,
        "address": doctor["address"],
        "phoneNumbers": doctor["phone_numbers"],
        "specializations": doctor["specializations"],
        "services": doctor["servicies"],
        "endorseCount": 0,
        "reviews": reviews,
        "reviewsCount": len(reviews)
    }

def generate_doctors(data, users):
    usermap = {user["ousername"]: user for user in users}
    doctors = [refactor_doctor(doctor, usermap) for doctor in tqdm(data, desc="Generating doctors")]
    return doctors

def generate_appointment_date(review_date_str):
    review_date = datetime.fromisoformat(review_date_str)
    appointment_date = review_date - timedelta(days=random.randint(1, 10))
    return appointment_date.strftime('%Y-%m-%dT%H:%M:%SZ')

def generate_appointment(doctor, patient, review_date, visit_type, notes):
    return {
        "appointmentDateTime": generate_appointment_date(review_date),
        "doctor": {"name": doctor["name"]},
        "patient": {
            "ousername": patient["ousername"],
            "fullName": patient["name"],
            "fiscalCode": patient["fiscalCode"]
        },
        "visitType": visit_type,
        "patientNotes": notes
    }

def generate_appointments(doctors, users):
    usermap = {user["ousername"]: user for user in users}
    appointments = []
    for doctor in tqdm(doctors, desc="Generating appointments"):
        services = [service["service"] for service in doctor.get("services", [])]
        for review in doctor.get("reviews", []):
            user = usermap.get(review["ousername"])
            if user:
                service = random.choice(services) if services else ""
                appointments.append(generate_appointment(doctor, user, review["time"], service, ""))
    return appointments

def generate_user_likes(doctors, appointments):
    user_review_counts = defaultdict(int)
    doctor_province = {}
    for doc in tqdm(doctors, desc="Processing doctors and reviews"):
        doctor_province[doc["name"]] = doc["address"]["province"]
        for rev in doc["reviews"]:
            user_review_counts[rev["ousername"]] += 1

    user_province = defaultdict(set)
    province_doctor = defaultdict(set)

    for app in tqdm(appointments, desc="Processing appointments"):
        user = app["patient"]["ousername"]
        doctor = app["doctor"]["name"]
        province = doctor_province[doctor]

        user_province[user].add(province)
        province_doctor[province].add(doctor)

    user_province = {k: list(v) for k, v in user_province.items()}
    province_doctor = {k: list(v) for k, v in province_doctor.items()}

    user_possible_likes = {}
    for user, provinces in tqdm(user_province.items(), desc="Possible doctors"):
        possible_doctors = set()
        for province in provinces:
            possible_doctors.update(province_doctor[province])
        user_possible_likes[user] = list(possible_doctors)

    user_likes = {}
    for user, possible_doctors in tqdm(user_possible_likes.items(), desc="Sampling doctors"):
        review_count = user_review_counts.get(user, 0)
        num_likes = min(review_count, len(possible_doctors))

        if num_likes > 0:
            user_likes[user] = random.sample(possible_doctors, num_likes)
        elif num_likes == 0:
            user_likes[user] = []
        else:
            user_likes[user] = random.sample(possible_doctors, random.randint(0, 3))

    return user_likes

def save(data, file):
    os.makedirs(JSON_DIR, exist_ok=True)
    with open(file, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

def generate_all_json(datasource):
    with open(datasource, "r", encoding="utf-8") as f:
        data = json.load(f)
    users = generate_users(data)
    doctors = generate_doctors(data, users)
    appointments = generate_appointments(doctors, users)
    user_likes = generate_user_likes(doctors, appointments)
    
    save(users, os.path.join(JSON_DIR, "users.json"))
    save(doctors, os.path.join(JSON_DIR, "doctors.json"))
    save(appointments, os.path.join(JSON_DIR, "appointments.json"))
    save(user_likes, os.path.join(JSON_DIR, "user_likes.json"))
