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
                console.error('Error retrieving reviews:');
            }
        } catch (error) {
            console.error('Error retrieving reviews:', error);
        }
    }

    function createReviewCard(review, index) {
        // Create the necessary elements for the review
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

        // Assemble the elements
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
                console.error('Error deleting review');
            }
        } catch (error) {
            console.error('Error deleting review:', error);
        }
    }


    function renderReviews(reviews) {
        // Find the element that will contain the reviews
        const reviewsSection = document.getElementById('reviews');

        // Remove existing reviews (if any)
        reviewsSection.innerHTML = '';

        // Iterate over the sorted reviews and create the review cards
        reviews.forEach((review, index) => {
            const reviewCard = createReviewCard(review, index);
            reviewsSection.appendChild(reviewCard);
        });
    }

    fetchReviews();
});