document.addEventListener('DOMContentLoaded', function(){

    async function fetchReviews() {
        try {
            const response = await fetch(`/api/doctor/reviews`, {
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

    function createReviewCard(review, index) {
        // Crea gli elementi necessari per la recensione
        const reviewDiv = document.createElement('div');
        reviewDiv.classList.add('review');

        const reviewHeaderDiv = document.createElement('div');
        reviewHeaderDiv.classList.add('review-header');

        const headerInnerDiv = document.createElement('div');

        const h3 = document.createElement('h3');
        h3.textContent = review.name;

        const pDate = document.createElement('p');
        pDate.textContent = new Date(review.date).toLocaleDateString('en-GB', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        });

        const reviewContentP = document.createElement('p');
        reviewContentP.classList.add('review-content');
        reviewContentP.textContent = review.text;

        const reviewActionsDiv = document.createElement('div');
        reviewActionsDiv.classList.add('review-actions');

        const pinButton = document.createElement('button');
        pinButton.textContent = 'Pin';

        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Delete';
        deleteButton.addEventListener('click', () => deleteReview(index));

        // Assembla gli elementi
        headerInnerDiv.appendChild(h3);
        headerInnerDiv.appendChild(pDate);

        reviewHeaderDiv.appendChild(headerInnerDiv);

        reviewActionsDiv.appendChild(pinButton);
        reviewActionsDiv.appendChild(deleteButton);

        reviewDiv.appendChild(reviewHeaderDiv);
        reviewDiv.appendChild(reviewContentP);
        reviewDiv.appendChild(reviewActionsDiv);

        return reviewDiv;
    }

    async function deleteReview(reviewIndex) {
        try {
            const response = await fetch(`/api/doctor/reviews/${reviewIndex}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                fetchReviews();
            } else {
                console.error('Errore durante l\'eliminazione della recensione');
            }
        } catch (error) {
            console.error('Errore durante l\'eliminazione della recensione:', error);
        }
    }


    function renderReviews(reviews) {
        // Trova l'elemento che conterrà le recensioni
        const reviewsSection = document.getElementById('reviews');

        // Rimuove le recensioni esistenti (se presenti)
        reviewsSection.innerHTML = '';

        // Itera sulle recensioni fetchate e crea le review card
        reviews.forEach((review, index) => {
            const reviewCard = createReviewCard(review, index);
            reviewsSection.appendChild(reviewCard);
        });
    }

    fetchReviews();


});