document.addEventListener('DOMContentLoaded', function() {
    const form              = document.querySelector('form');
    const emailInput        = document.getElementById('email');
    const successDiv        = document.getElementById('success-message');
    const errorDiv          = document.getElementById('error-message');
    const successSpan       = successDiv.querySelector('span');
    const errorSpan         = errorDiv.querySelector('span');

    form.addEventListener('submit', async function(evt) {
        evt.preventDefault();

        // reset display and text
        [successDiv, errorDiv].forEach(div => {
            div.classList.remove('visible');
            div.querySelector('span').textContent = '';
        });

        const email = emailInput.value.trim();
        if (!email) {
            errorSpan.textContent = 'Please enter a valid email address.';
            errorDiv.classList.add('visible');
            setTimeout(() => errorDiv.classList.remove('visible'), 5000);
            return;
        }

        try {
            const resp = await fetch('/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'email=' + encodeURIComponent(email)
            });

            if (resp.ok) {
                const wasSent = await resp.json(); // true or false

                if (wasSent) {
                    successSpan.textContent = 'A password reset link has been sent to your email address.';
                    successDiv.classList.add('visible');
                    setTimeout(() => successDiv.classList.remove('visible'), 10000);
                } else {
                    errorSpan.textContent = 'No account found for that email address, or sending failed.';
                    errorDiv.classList.add('visible');
                    setTimeout(() => errorDiv.classList.remove('visible'), 5000);
                }
            } else {
                errorSpan.textContent = 'Server error. Please try again later.';
                errorDiv.classList.add('visible');
                setTimeout(() => errorDiv.classList.remove('visible'), 5000);
            }
        } catch (networkErr) {
            errorSpan.textContent = 'Network error. Please check your connection and try again.';
            errorDiv.classList.add('visible');
            console.error('Fetch error:', networkErr);
            setTimeout(() => errorDiv.classList.remove('visible'), 5000);
        }
    });
});
