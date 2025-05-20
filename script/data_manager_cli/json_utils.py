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
PASSWORD = "password"
PASSWORD_HASH = hashlib.sha256(PASSWORD.encode()).hexdigest()

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

    phone_numbers = fake.phone_number()
    email = profile['mail']
    gender = 'male' if gender == 'M' else "female"

    return {
        "fiscalCode": fiscal_code,
        "name": name,
        "password": PASSWORD_HASH,
        "dob": dob.strftime('%Y-%m-%d'),
        "gender": gender,
        "personalNumber": phone_numbers,
        "email": email,
        "username": username
    }

def generate_users(data):
    usernames = {review["name"] for doctor in data if "reviews" in doctor for review in doctor["reviews"] if "name" in review}
    users = [generate_user(username) for username in tqdm(usernames, desc="Generating users")]
    return users

def refactor_doctor(doctor, usermap):
    profile = fake.simple_profile()
    email = profile["mail"]
    dob = profile["birthdate"]
    gender = profile['sex']
    _, domain = email.split('@') 

    orderRegistrationNumber = lambda: f"{doctor["address"]["province"]}-{random.randint(1, 999999):06d}"

    name = doctor["name"]
    fiscal_code = (
        name.split()[1][:3].upper().ljust(3, 'X') +
        name.split()[0][:3].upper().ljust(3, 'X') +
        dob.strftime("%y%m%d") +
        ('M' if gender == 'M' else 'F') +
        ''.join(random.choices("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", k=4))
    )[:16]

    try:
        first_name, last_name = doctor["name"].split()
        email = f"{first_name.lower()}.{last_name.lower()}@{domain}"
    except Exception:
        pass

    reviews = []
    if "reviews" in doctor:
        for review in doctor["reviews"]:
            username = review["name"]
            new_rev = {}
            new_rev["patientId"] = username
            new_rev["name"] = usermap[username]["name"]
            new_rev["text"] = review["review"]
            new_rev["date"] = review["time"]
            reviews.append(new_rev)
        
    if "address" in doctor:
        address = doctor["address"]
        if "cap" in address:
            address["postalCode"] = address["cap"]
            address.pop("cap",None)

    if "servicies" in doctor:
        services = doctor.get("servicies",[])
        for serv in services:
            try:
                serv["price"] = float(serv["price"])
            except (ValueError, TypeError):
                serv["price"] = 0.0

    if not doctor.get("specializations"):
        doctor["specializations"] = ["Medico di Base"]

    return {
        "name": doctor["name"],
        "email": email,
        "username": doctor["name"].lower().replace(" ", "_"),
        "password": PASSWORD_HASH,
        "address": doctor["address"],
        "phoneNumbers": doctor["phone_numbers"],
        "specializations": doctor["specializations"],
        "services": doctor["servicies"],
        "endorsementCount": 0,
        "reviews": reviews,
        "reviewsCount": len(reviews),
        "dob": dob.strftime('%Y-%m-%d'),
        "fiscal_code": fiscal_code,
        "orderRegistrationNumber": orderRegistrationNumber()
    }

def find_most_recent_review_date(doctors):
    latest_date = None
    for doc in tqdm(doctors, desc="Finding most recent review"):
        for rev in doc.get("reviews", []):
            date_str = rev.get("date", None)
            if date_str:
                try:
                    date = datetime.fromisoformat(date_str)
                    if latest_date is None or date > latest_date:
                        latest_date = date
                except ValueError:
                    continue
    return latest_date

def postpone_reviews(doctors, manual_delta: timedelta = timedelta(weeks=0)):
    most_recent_date = find_most_recent_review_date(doctors)
    if not most_recent_date:
        print("Nessuna data valida trovata.")
        return

    now = datetime.now(most_recent_date.tzinfo)  # Fuso orario coerente
    delta = (now - most_recent_date) + manual_delta

    for doc in tqdm(doctors, desc="Postponing reviews"):
        for rev in doc.get("reviews", []):
            date_str = rev.get("date", None)
            if date_str:
                try:
                    original_date = datetime.fromisoformat(date_str)
                    new_date = original_date + delta
                    rev["date"] = new_date.isoformat()
                except ValueError:
                    continue

def generate_doctors(data, users):
    usermap = {user["username"]: user for user in users}
    doctors = [refactor_doctor(doctor, usermap) for doctor in tqdm(data, desc="Generating doctors")]
    postpone_reviews(doctors)
    return doctors

def generate_appointment_date(review_date_str):
    review_date = datetime.fromisoformat(review_date_str)
    appointment_date = review_date - timedelta(days=random.randint(1, 10))
    return appointment_date.strftime('%Y-%m-%dT%H:%M:%SZ')

def generate_appointment(doctor, patient, review_date, visit_type, price, notes):
    return {
        "date": generate_appointment_date(review_date),
        "doctor": {
            "_id": doctor["name"],
            "name": doctor["name"],
            "address": doctor["address"],
            "email": doctor["email"]
        },
        "patient": {
            "_id": patient["username"],
            "name": patient["name"],
            "fiscalCode": patient["fiscalCode"],
            "email": patient["email"],
            "gender": patient["gender"]
        },
        "visitType": visit_type,
        "patientNotes": notes,
        "price": price
    }

def generate_appointments(doctors, users):
    usermap = {user["username"]: user for user in users}
    appointments = []
    for doctor in tqdm(doctors, desc="Generating appointments"):
        services = [(service["service"], service["price"]) for service in doctor.get("services", [])]
        for review in doctor.get("reviews", []):
            user = usermap.get(review["patientId"])
            if user:
                service = random.choice(services) if services else ("",0)
                appointments.append(generate_appointment(doctor, user, review["date"], service[0], service[1], ""))
    return appointments


def generate_default_templates(doctors):
    default_template = {
        'name': 'Standard',
        'slots': {
            'monday': [
                { 'start': '08:30', 'end': '09:00' },
                { 'start': '09:00', 'end': '09:30' },
                { 'start': '09:30', 'end': '10:00' },
                { 'start': '10:00', 'end': '10:30' },
                { 'start': '10:30', 'end': '11:00' },
                { 'start': '11:00', 'end': '11:30' },
                { 'start': '11:30', 'end': '12:00' },
                { 'start': '12:00', 'end': '12:30' }
            ],
            'wednesday': [
                { 'start': '14:30', 'end': '15:00' },
                { 'start': '15:00', 'end': '15:30' },
                { 'start': '15:30', 'end': '16:00' },
                { 'start': '16:00', 'end': '16:30' },
                { 'start': '16:30', 'end': '17:00' },
                { 'start': '17:00', 'end': '17:30' },
                { 'start': '17:30', 'end': '18:00' },
                { 'start': '18:00', 'end': '18:30' }
            ],
            'friday': [
                { 'start': '10:00', 'end': '10:30' },
                { 'start': '10:30', 'end': '11:00' },
                { 'start': '11:00', 'end': '11:30' },
                { 'start': '11:30', 'end': '12:00' },
                { 'start': '16:00', 'end': '16:30' },
                { 'start': '16:30', 'end': '17:00' },
                { 'start': '17:00', 'end': '17:30' },
                { 'start': '17:30', 'end': '18:00' }
            ]
        },
        'active': True,
    }

    templates = [default_template for doc in doctors]
    return templates

def generate_user_likes(doctors, appointments):
    user_review_counts = defaultdict(int)
    doctor_province = {}
    for doc in tqdm(doctors, desc="Processing doctors and reviews"):
        doctor_province[doc["name"]] = doc["address"]["province"]
        for rev in doc["reviews"]:
            user_review_counts[rev["patientId"]] += 1

    user_province = defaultdict(set)
    province_doctor = defaultdict(set)

    for app in tqdm(appointments, desc="Processing appointments"):
        user = app["patient"]["_id"]
        doctor = app["doctor"]["_id"]
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

def update_endorseCount(doctors, users_like):
    doctormap = {doctor["name"]: doctor for doctor in doctors}
    for _, endorsedDoctors in tqdm(users_like.items(), desc="Updating endorsementCount"):
        for doc in endorsedDoctors:
            doctormap[doc]["endorsementCount"] += 1

def save(data, file):
    os.makedirs(JSON_DIR, exist_ok=True)
    with open(file, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

def generate_all_json(datasource):
    with open(datasource, "r", encoding="utf-8") as f:
        data = json.load(f)
    users = generate_users(data)
    doctors = generate_doctors(data, users)
    templates = generate_default_templates(doctors)
    appointments = generate_appointments(doctors, users)
    user_likes = generate_user_likes(doctors, appointments)
    update_endorseCount(doctors, user_likes)
    
    save(users, os.path.join(JSON_DIR, "users.json"))
    save(doctors, os.path.join(JSON_DIR, "doctors.json"))
    save(templates, os.path.join(JSON_DIR, "templates.json"))
    save(appointments, os.path.join(JSON_DIR, "appointments.json"))
    save(user_likes, os.path.join(JSON_DIR, "user_likes.json"))
