document.addEventListener('DOMContentLoaded', function(){

    let endorsementCount = 0;
    const endorsementElement = document.getElementById('endorsementCount');
    const endorsementButton = document.getElementById('endorsementButton');
    let hasEndorsed = false;

    const doctorId = document.getElementById('doctor-info').getAttribute('data-doctor-id');

    function toggleEndorsement() {
        hasEndorsed = !hasEndorsed;
        if (hasEndorsed) {
            endorsementCount++;
            endorsementButton.innerHTML = '<i class="fas fa-thumbs-up"></i> Endorsed';
        } else {
            endorsementCount--;
            endorsementButton.innerHTML = '<i class="far fa-thumbs-up"></i> Endorse';
        }
        endorsementElement.textContent = endorsementCount;
    }
    endorsementButton.addEventListener('click', toggleEndorsement);

    let reviewButton = document.getElementById('reviewButton');


    // REVIEW CODE

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

    fetchReviews(doctorId);  // Recupera le recensioni al caricamento della pagina

// CALENDARIO



});
