window.addEventListener('load', function () {

    let timeout;

    let password = document.getElementById('password')
    let strengthBadge = document.getElementById('strength-disp')

    let strongPassword = new RegExp('(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{8,})')
    let mediumPassword = new RegExp('((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{6,}))|((?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=.{8,}))')

    function StrengthChecker(PasswordParameter) {

        if (strongPassword.test(PasswordParameter)) {
            strengthBadge.style.backgroundColor = "#cae000"
            strengthBadge.textContent = 'Strong'
        } else if (mediumPassword.test(PasswordParameter)) {
            strengthBadge.style.backgroundColor = '#ff6c00'
            strengthBadge.textContent = 'Medium'
        } else {
            strengthBadge.style.backgroundColor = 'red'
            strengthBadge.textContent = 'Weak'
        }
    }

    password.addEventListener("input", () => {

        strengthBadge.style.display = 'block'
        clearTimeout(timeout);

        timeout = setTimeout(() => StrengthChecker(password.value), 1000);

        if (password.value.length !== 0) {
            strengthBadge.style.display !== 'block'
        } else {
            strengthBadge.style.display = 'none'
        }
    });
});