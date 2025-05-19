document.addEventListener('DOMContentLoaded', function(){

    let endorsementCount = 0;
    const endorsementElement = document.getElementById('endorsementCount');
    const endorsementButton = document.getElementById('endorsementButton');
    let hasEndorsed = false;

    const doctorId = document.getElementById('doctor-info').getAttribute('data-doctor-id');
    const calendar = new DoctorScheduleCalendar('calendar', doctorId);

    // Function to get the initial state of endorsements
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
            // Handle errors appropriately, e.g., by showing a message to the user
        }
    }

    // Function to update the UI with the current endorsement state
    function updateUIWithEndorsementState() {
        endorsementElement.textContent = endorsementCount;
        if (hasEndorsed) {
            endorsementButton.innerHTML = '<i class="fas fa-thumbs-up"></i> Endorsed';
        } else {
            endorsementButton.innerHTML = '<i class="far fa-thumbs-up"></i> Endorse';
        }
    }

    // Function to handle endorsement
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

            if(response.status === 401){
                alert("Login to endorse this doctor!");
            }
            else if (response.status === 403){
                alert("A doctor cannot endorse.");
            }
            else if(!response.ok){
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const data = await response.json();
            endorsementCount = data.endorsementCount;
            hasEndorsed = data.hasEndorsed;

            updateUIWithEndorsementState();
        } catch (error) {
            console.error('Error endorsing doctor:', error);
            // Handle errors appropriately, e.g., by showing a message to the user
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


    // Reviews
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
                    // savedReview should contain { name, text, date, … }
                    const savedReview = await response.json();

                    document.getElementById('newReview').value = '';

                    // We create the card and insert it at the top
                    const reviewList = document.getElementById('reviewList');
                    const newCard = createReviewCard(savedReview);
                    reviewList.prepend(newCard);
                    // fetchReviews(doctorId);  // Reload the reviews after adding a new one
                }
                else if (response.status === 401)
                    alert('You cannot leave a review because you have never been visited by this doctor.');
                else if (response.status === 403)
                    alert('You cannot leave a review because you are a doctor.');
                else
                    console.error('Error sending review');
            } catch (error) {
                console.error('Error sending review:', error);
            }
        }
    }

    async function fetchReviews(doctorId) {
        try {
            const response = await fetch(`/api/doctors/${doctorId}/reviews`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });

            if (response.ok) {
                let reviews = await response.json();

                // SORT by date DESC (b newer than a)
                reviews.sort((a, b) => new Date(b.date) - new Date(a.date));

                renderReviews(reviews);
            } else {
                console.error('Error retrieving reviews');
            }
        } catch (error) {
            console.error('Error retrieving reviews:', error);
        }
    }

    function createReviewCard(review) {
        const newReview = document.createElement('div');
        newReview.classList.add('review');

        const reviewHeader = document.createElement('div');
        reviewHeader.classList.add('review-header');

        const reviewHeaderInfo = document.createElement('div');
        const reviewNameElement = document.createElement('h3');
        reviewNameElement.textContent = review.name;  // Username passed in the review object
        const reviewDateElement = document.createElement('p');
        reviewDateElement.textContent = review.date;  // Date passed in the review object

        reviewHeaderInfo.appendChild(reviewNameElement);
        reviewHeaderInfo.appendChild(reviewDateElement);

        reviewHeader.appendChild(reviewHeaderInfo);

        const reviewContent = document.createElement('p');
        reviewContent.classList.add('review-content');
        reviewContent.textContent = review.text;  // Review text passed in the review object

        newReview.appendChild(reviewHeader);
        newReview.appendChild(reviewContent);

        return newReview;
    }

    function renderReviews(reviews) {
        const reviewList = document.getElementById('reviewList');
        reviewList.innerHTML = '';  // Clear the list before rendering reviews

        reviews.forEach(review => {
            const reviewCard = createReviewCard(review);
            reviewList.appendChild(reviewCard);
        });
    }

    reviewButton.addEventListener('click', sendReview);

    // Appointment
    function bookAppointment() {
        const selectedSlot = calendar.getSelectedSlot();
        const now = new Date();

        const slotDate = new Date(selectedSlot.date);
        const slotTimeParts = selectedSlot.slot.split(':');
        slotDate.setHours(parseInt(slotTimeParts[0]), parseInt(slotTimeParts[1]), 0, 0); // Set the time on the slot

        if (slotDate < now) {
            alert("You cannot book an appointment in the past.");
            return;
        }

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
                if (response.status === 401) {
                    alert("You must be logged in to book an appointment.");
                    // throw to skip the next `.then()` and go to `.catch()`, if needed
                    throw new Error('Unauthorized');
                }
                else if (response.status === 403) {
                    alert("You must be a patient to book an appointment.");
                    // throw to skip the next `.then()` and go to `.catch()`, if needed
                    throw new Error('Unauthorized');
                }
                if (!response.ok) {
                    throw new Error('Failed to book appointment: ' + response.status);
                }
                return response.json(); // assuming you want to process the response
            })
            .then(data => {
                console.log('Appointment booked!', data);
                calendar.updateWeekInfo();
            })
            .catch(error => {
                console.error('Error booking appointment:', error);
            });
    }

    document.getElementById('bookButton').addEventListener('click', bookAppointment);

    // Fetch initial data
    fetchReviews(doctorId);  // Fetch reviews on page load
    fetchInitialEndorsementState();
});
