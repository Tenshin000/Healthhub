let endorsementCount = 0;
const endorsementElement = document.getElementById('endorsementCount');
const endorsementButton = document.getElementById('endorsementButton');
let hasEndorsed = false;

function toggleEndorsement() {
    hasEndorsed = !hasEndorsed;
    if (hasEndorsed) {
        endorsementCount++;
        endorsementButton.innerHTML = '<i class="fas fa-thumbs-up"></i> Endorsed';
    } else {
        endorsementCount--;
        endorsementButton.innerHTML = '<i class="far fa-thumbs-up"></i> Lascia un Endorsement';
    }
    endorsementElement.textContent = endorsementCount;
}

function addReview() {
    const reviewText = document.getElementById('newReview').value;
    if (reviewText.trim() !== '') {
        const reviewList = document.getElementById('reviewList');
        const newReview = document.createElement('div');
        newReview.classList.add('review');

        const reviewHeader = document.createElement('div');
        reviewHeader.classList.add('review-header');

        const reviewImage = document.createElement('img');
        reviewImage.src = 'patient1.jpg';
        reviewImage.alt = 'Patient';

        const reviewHeaderInfo = document.createElement('div');
        const reviewNameElement = document.createElement('h3');
        reviewNameElement.textContent = 'Nome Utente';  // Placeholder, sostituire con il nome utente dalla sessione
        const reviewDateElement = document.createElement('p');
        const currentDate = new Date();
        reviewDateElement.textContent = currentDate.toLocaleDateString('it-IT');

        reviewHeaderInfo.appendChild(reviewNameElement);
        reviewHeaderInfo.appendChild(reviewDateElement);

        reviewHeader.appendChild(reviewImage);
        reviewHeader.appendChild(reviewHeaderInfo);

        const reviewContent = document.createElement('p');
        reviewContent.classList.add('review-content');
        reviewContent.textContent = reviewText;

        newReview.appendChild(reviewHeader);
        newReview.appendChild(reviewContent);

        reviewList.appendChild(newReview);
        document.getElementById('newReview').value = '';
    } else {
        alert('Per favore, inserisci una recensione prima di inviarla.');
    }
}

// CALENDARIO


