window.addEventListener('load', function () {

    let timeout;

    let password = document.getElementById('password');
    let strengthBadge = document.getElementById('strength-disp');

    let strengthContainer = document.getElementById('strength-container');

    let strongPassword = new RegExp('(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{8,})');
    let mediumPassword = new RegExp('((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9])(?=.{6,}))|((?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=.{8,}))');

    function StrengthChecker(PasswordParameter){
        if(PasswordParameter.length < 8){
            return 'invalid';
        }
        if (strongPassword.test(PasswordParameter)) {
            return 'strong';
        } else if (mediumPassword.test(PasswordParameter)) {
            return 'medium';
        } else {
            return 'weak';
        }
    }

    function setStrength(strength){
        var default_color = strengthContainer.style.backgroundColor;
        var bars = document.getElementById('strength-container').children;
        console.log(bars);
        switch (strength) {
            case 'weak':
                bars[0].style.backgroundColor = '#ff0000';
                bars[1].style.backgroundColor = default_color;
                bars[2].style.backgroundColor = default_color;
                break;
            
            case 'medium':
                bars[0].style.backgroundColor = '#ff9900';
                bars[1].style.backgroundColor = '#ff9900';
                bars[2].style.backgroundColor = default_color;
                break;

            case 'strong':
                bars[0].style.backgroundColor = '#00ff00';
                bars[1].style.backgroundColor = '#00ff00';
                bars[2].style.backgroundColor = '#00ff00';
                break;

            case 'invalid':
                bars[0].style.backgroundColor = default_color;
                bars[1].style.backgroundColor = default_color;
                bars[2].style.backgroundColor = default_color;

            default:
                break;
        }

    }


    password.addEventListener('input', function () {
        // strengthBadge.style.display = (password.value.length === 0) ? 'none': 'block';
        var strength = StrengthChecker(password.value);
        setStrength(strength);
    });
    // password.addEventListener("input", () => {

    //     strengthBadge.style.display = 'block'
    //     clearTimeout(timeout);

    //     timeout = setTimeout(() => StrengthChecker(password.value), 1000);

    //     if (password.value.length !== 0) {
    //         strengthBadge.style.display !== 'block'
    //     } else {
    //         strengthBadge.style.display = 'none'
    //     }
    // });
});