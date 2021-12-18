window.addEventListener('load', function () {
    let password = document.getElementById('password');

    let strengthContainer = document.getElementById('strength-container');

    function scorePassword(pass) {
        var score = 0;
        if (!pass) {
            return score;
        }

        // award every unique letter until 5 repetitions
        var letters = new Object();
        for (var i = 0; i < pass.length; i++) {
            letters[pass[i]] = (letters[pass[i]] || 0) + 1;
            score += 5.0 / letters[pass[i]];
        }

        // bonus points for mixing it up
        var variations = {
            digits: /\d/.test(pass),
            lower: /[a-z]/.test(pass),
            upper: /[A-Z]/.test(pass),
            nonWords: /\W/.test(pass),
        }

        let variationCount = 0;
        for (var check in variations) {
            variationCount += (variations[check] === true) ? 1 : 0;
        }
        score += (variationCount - 1) * 10;

        return parseInt(score);
    }

    function checkPasswordStrength(pass) {
        let score = scorePassword(pass);

        if (pass.length < 8) {
            return 'invalid';
        }

        if (score > 80) {
            return 'strong';
        } else if (score > 60) {
            return 'medium';
        } else {
            return 'weak';
        }
    }

    function setStrength(strength) {
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
        var strength = checkPasswordStrength(password.value);
        setStrength(strength);
    });
});