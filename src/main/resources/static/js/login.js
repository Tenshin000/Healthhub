document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
        const errorDiv = document.getElementById('error-message');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    }, 3000);
});
