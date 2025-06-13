// Populate the "recommended-doctors" section
function fetchRecommendedDoctors(limit = 3) {
    fetch(`/api/user/recommendations?limit=${limit}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(doctors => {
            const section = document.querySelector('.recommended-doctors');
            section.innerHTML = '';
            renderRecommendedDoctors(section, doctors);
        })
        .catch(error => {
            console.error('Error loading recommended doctors:', error);
        });
}

function fetchEndorsedDoctors() {
    fetch('/api/user/doctors/endorsed')  // API endpoint URL
        .then(response => response.json())
        .then(data => renderDoctorsList('endorsed-doctors-list',data))
        .catch(error => console.error('Error fetching doctors:', error));
}

function fetchReviewedDoctors(){
    fetch('/api/user/doctors/reviewed')  // API endpoint URL
        .then(response => response.json())
        .then(data => renderDoctorsList('reviewed-doctors-list',data))
        .catch(error => console.error('Error fetching appointments:', error));
}

function renderRecommendedDoctors(section, doctors) {
    const cardsContainer = document.createElement('div');
    cardsContainer.classList.add('doctor-cards');

    const h2 = document.createElement('h2');
    h2.textContent = doctors && doctors.length > 0 ? "Recommended Doctors" : "No Doctor Recommended";
    section.appendChild(h2);

    if (doctors && doctors.length > 0) {
        doctors.forEach(doc => {
            const card = createDoctorCard(doc);
            cardsContainer.appendChild(card);
        });
    }

    section.appendChild(cardsContainer);
}

function renderDoctorsList(ulId, endorsedDoctors) {
    const ul = document.getElementById(ulId);
    if (!ul) return;

    ul.innerHTML = ''; // Pulisce la lista, se necessario

    endorsedDoctors.forEach(doctor => {
        const li = document.createElement('li');
        li.className = 'doctor-item';

        const a = document.createElement('a');
        a.href = `/doctors/${doctor.id}`;

        const div = document.createElement('div');

        const h3 = document.createElement('h3');
        h3.textContent = `Dr. ${doctor.name}`;

        const p = document.createElement('p');
        const hasSpecializations = Array.isArray(doctor.specializations) && doctor.specializations.length > 0;
        p.textContent = hasSpecializations ? doctor.specializations[0] : 'No specializations';

        div.appendChild(h3);
        div.appendChild(p);
        a.appendChild(div);
        li.appendChild(a);
        ul.appendChild(li);
    });
}

function createDoctorCard(doc) {
    const cardLink = document.createElement('a');
    cardLink.href = `/doctors/${doc.id}`;
    cardLink.classList.add('doctor-card');

    const infoDiv = document.createElement('div');

    const nameEl = document.createElement('h3');
    nameEl.textContent = `Dr. ${doc.name}`;
    infoDiv.appendChild(nameEl);

    const specEl = document.createElement('p');
    specEl.textContent = Array.isArray(doc.specializations) && doc.specializations.length > 0
        ? doc.specializations.join(', ')
        : 'Specializations not available';
    infoDiv.appendChild(specEl);

    cardLink.appendChild(infoDiv);
    return cardLink;
}

document.addEventListener('DOMContentLoaded', () => {
    fetchEndorsedDoctors();
    fetchReviewedDoctors();
    fetchRecommendedDoctors(3);
});
