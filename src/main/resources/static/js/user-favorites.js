// Populate the "recommended-doctors" section
async function fetchRecommendedDoctors(limit1 = 2, limit2 = 3){
    try{
        // Fetch GET with limit1 and limit2 parameters
        const response = await fetch(`/api/user/recommendations?limit1=${limit1}&limit2=${limit2}`);
        if(!response.ok){
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Parse the JSON response (DoctorDAO array)
        const doctors = await response.json();

        // Select the section and empty it
        const section = document.querySelector('.recommended-doctors');
        section.innerHTML = '';


        // Create the doctor cards container
        const cardsContainer = document.createElement('div');
        cardsContainer.classList.add('doctor-cards');

        if(doctors != null){
            const h2 = document.createElement('h2');
            h2.textContent = "Recommended Doctors";
            section.appendChild(h2);

            // For each doctor, create an <a> with internal structure
            doctors.forEach(doc => {
                const cardLink = document.createElement('a');
                cardLink.href = `/doctors/${doc.id}`;
                cardLink.classList.add('doctor-card');

                const infoDiv = document.createElement('div');

                const nameEl = document.createElement('h3');
                nameEl.textContent = `Dr. ${doc.name}`;
                infoDiv.appendChild(nameEl);

                // Show the first specialization, or all separated by commas
                const specEl = document.createElement('p');
                if(Array.isArray(doc.specializations) && doc.specializations.length > 0){
                    specEl.textContent = doc.specializations.join(', ');
                }
                else{
                    specEl.textContent = 'Specializations not available';
                }
                infoDiv.appendChild(specEl);

                cardLink.appendChild(infoDiv);
                cardsContainer.appendChild(cardLink);
            });
        }
        else{
            const h2 = document.createElement('h2');
            h2.textContent = "No Doctor Recommended";
            section.appendChild(h2);
        }

        // Adds the container to the section
        section.appendChild(cardsContainer);
    }
    catch(error){
        console.error('Error loading recommended doctors:', error);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    fetchRecommendedDoctors(1, 2);
});
