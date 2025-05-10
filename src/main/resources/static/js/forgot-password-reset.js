document.addEventListener('DOMContentLoaded', function() {
    const form                   = document.querySelector('form');
    const passwordInput          = document.getElementById('password');
    const confirmPasswordInput   = document.getElementById('confirmPassword');
    const successMessageDiv      = document.getElementById('success-message');
    const errorMessageDiv        = document.getElementById('error-message');

    form.addEventListener('submit', async function(evt) {
        evt.preventDefault();

        // clear any old messages
        [ successMessageDiv, errorMessageDiv ].forEach(div => {
            div.style.display = 'none';
            div.textContent = '';
        });

        const password        = passwordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();

        if (!password || !confirmPassword) {
            showError('Please fill in both password fields.');
            return;
        }
        if (password !== confirmPassword) {
            showError('Passwords do not match.');
            return;
        }

        try {
            const resp = await fetch(form.action, {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body:
                    'password=' + encodeURIComponent(password) +
                    '&confirmPassword=' + encodeURIComponent(confirmPassword)
            });

            if (resp.ok) {
                const wasSuccessful = await resp.json();  // true or false

                if (wasSuccessful) {
                    showSuccess('Password changed successfully. You can now log in.');
                    setTimeout(() => {
                        window.location.href = '/login';
                    }, 1500); // wait 1.5 seconds then redirect
                } else {
                    showError('Failed to reset password. Please try again later.');
                }
            } else {
                showError('Server error. Please try again later.');
            }
        } catch (networkErr) {
            showError('Network error. Please check your connection and try again.');
            console.error('Fetch error:', networkErr);
        }
    });

    function showSuccess(message) {
        successMessageDiv.textContent = message;
        successMessageDiv.style.display = 'block';
    }

    function showError(message) {
        errorMessageDiv.textContent = message;
        errorMessageDiv.style.display = 'block';
        setTimeout(() => {
            errorMessageDiv.style.display = 'none';
        }, 5000);  // hide after 5 seconds
    }
});
