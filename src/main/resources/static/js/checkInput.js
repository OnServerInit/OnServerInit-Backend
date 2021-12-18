window.addEventListener('load', function () {

    let typingTimerUsername, typingTimerEmail;
    const doneTypingInterval = 2000;
    const username = document.getElementById('username');
    const email = document.getElementById('email');

    username.addEventListener('keyup', function () {
        clearTimeout(typingTimerUsername);
        if (username.value) {
            typingTimerUsername = setTimeout(checkUsername, doneTypingInterval);
        }
    });

    email.addEventListener('keyup', function () {
        clearTimeout(typingTimerEmail);
        if (email.value) {
            typingTimerEmail = setTimeout(checkEmail, doneTypingInterval);
        }
    });
});

function checkEmail() {
    const e = document.getElementById("email");
    let email = e.value;
    if (!email.toString().match("^(.+)@(\\S+)$")) {
        document.getElementById("invalid-email").innerHTML = "Invalid character";
    } else {
        document.getElementById("invalid-email").innerHTML = "";
    }
}

function checkUsername() {
    console.log(2);
    const e = document.getElementById("username");
    let email = e.value;
    if (!email.toString().match("^[a-zA-Z0-9]*$")) {
        console.log("bad");
        document.getElementById("invalid-name").innerHTML = "Invalid character";
    } else {
        console.log("good");
        document.getElementById("invalid-name").innerHTML = "";
    }
}