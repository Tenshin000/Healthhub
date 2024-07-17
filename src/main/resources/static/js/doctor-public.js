document.addEventListener('DOMContentLoaded', function(){

    let endorsementCount = 0;
    const endorsementElement = document.getElementById('endorsementCount');
    const endorsementButton = document.getElementById('endorsementButton');
    let hasEndorsed = false;

    const doctorId = document.getElementById('doctor-info').getAttribute('data-doctor-id');
    const calendar = new DoctorScheduleCalendar('calendar', doctorId);

// Funzione per ottenere lo stato iniziale degli endorsement
    async function fetchInitialEndorsementState() {
        try {
            const response = await fetch(`/api/doctors/${doctorId}/endorsements`);
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            const data = await response.json();
            endorsementCount = data.endorsementCount;
            hasEndorsed = data.hasEndorsed;

            updateUIWithEndorsementState();
        } catch (error) {
            console.error('Error fetching initial endorsement state:', error);
            // Gestisci gli errori in modo appropriato, ad esempio mostrando un messaggio all'utente
        }
    }

// Funzione per aggiornare l'interfaccia utente con lo stato degli endorsement corrente
    function updateUIWithEndorsementState() {
        endorsementElement.textContent = endorsementCount;
        if (hasEndorsed) {
            endorsementButton.innerHTML = '<i class="fas fa-thumbs-up"></i> Endorsed';
        } else {
            endorsementButton.innerHTML = '<i class="far fa-thumbs-up"></i> Endorse';
        }
    }

// Funzione per gestire l'endorsement
    async function toggleEndorsement() {
        hasEndorsed = !hasEndorsed;
        try {
            let url = `/api/doctors/${doctorId}/endorse`;
            if(!hasEndorsed)
                url = `/api/doctors/${doctorId}/unendorse`;
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ endorsed: hasEndorsed })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const data = await response.json();
            endorsementCount = data.endorsementCount;
            hasEndorsed = data.hasEndorsed;

            updateUIWithEndorsementState();
        } catch (error) {
            console.error('Error endorsing doctor:', error);
            // Gestisci gli errori in modo appropriato, ad esempio mostrando un messaggio all'utente
        }
    }
    endorsementButton.addEventListener('click', toggleEndorsement);


    function sendEndorsement() {
        fetch(`/api/doctors/${doctorId}/endorsements`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                endorsed: hasEndorsed
            })
        })
            .then(response => {
                if (response.ok) {
                    console.log('Endorsement sent!');
                }
            })
            .catch(error => {
                console.error('Error sending endorsement:', error);
            });
    }


    // REVIEW CODE
    let reviewButton = document.getElementById('reviewButton');

    async function sendReview() {
        const reviewText = document.getElementById('newReview').value;
        if (reviewText.trim() !== '') {
            const review = {
                text: reviewText
            };

            try {
                const response = await fetch(`/api/doctors/${doctorId}/reviews`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(review)
                });

                if (response.ok) {
                    document.getElementById('newReview').value = '';
                    fetchReviews(doctorId);  // Ricarica le recensioni dopo averne aggiunta una nuova
                } else {
                    console.error('Errore durante l\'invio della recensione');
                }
            } catch (error) {
                console.error('Errore durante l\'invio della recensione:', error);
            }
        }
    }

    async function fetchReviews(doctorId) {
        try {
            const response = await fetch(`/api/doctors/${doctorId}/reviews`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const reviews = await response.json();
                renderReviews(reviews);
            } else {
                console.error('Errore durante il recupero delle recensioni');
            }
        } catch (error) {
            console.error('Errore durante il recupero delle recensioni:', error);
        }
    }

    function createReviewCard(review) {
        const newReview = document.createElement('div');
        newReview.classList.add('review');

        const reviewHeader = document.createElement('div');
        reviewHeader.classList.add('review-header');

        const reviewHeaderInfo = document.createElement('div');
        const reviewNameElement = document.createElement('h3');
        reviewNameElement.textContent = review.name;  // Nome utente passato nell'oggetto review
        const reviewDateElement = document.createElement('p');
        reviewDateElement.textContent = review.date;  // Data passata nell'oggetto review

        reviewHeaderInfo.appendChild(reviewNameElement);
        reviewHeaderInfo.appendChild(reviewDateElement);

        reviewHeader.appendChild(reviewHeaderInfo);

        const reviewContent = document.createElement('p');
        reviewContent.classList.add('review-content');
        reviewContent.textContent = review.text;  // Testo della recensione passato nell'oggetto review

        newReview.appendChild(reviewHeader);
        newReview.appendChild(reviewContent);

        return newReview;
    }

    function renderReviews(reviews) {
        const reviewList = document.getElementById('reviewList');
        reviewList.innerHTML = '';  // Pulisce la lista prima di renderizzare le recensioni

        reviews.forEach(review => {
            const reviewCard = createReviewCard(review);
            reviewList.appendChild(reviewCard);
        });
    }

    reviewButton.addEventListener('click', sendReview);

    // Appuntamento

    function bookAppointment() {

        const selectedSlot = calendar.getSelectedSlot();
        const service = document.getElementById('service').value;
        const patientNotes = document.getElementById('notes').value;

        const appointment = {
            date: selectedSlot.date,
            slot: selectedSlot.slot,
            service: service,
            patientNotes: patientNotes
        };

        console.log(JSON.stringify(appointment));

        fetch(`/api/doctors/${doctorId}/appointments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(appointment)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to book appointment: ' + response.status);
                }
                console.log('Appointment booked!');
                calendar.updateWeekInfo();
            })
            .catch(error => {
                console.error('Error booking appointment:', error);
            });
    }

    document.getElementById('bookButton').addEventListener('click', bookAppointment);


    // Recupera dati iniziali
    fetchReviews(doctorId);  // Recupera le recensioni al caricamento della pagina
    fetchInitialEndorsementState();

});
