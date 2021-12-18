function getCookie(cookieName) {
    var name = cookieName + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

document.getElementById('yes').href = getCookie('url');

document.getElementById('warning').innerHTML = 'Warning: You are about to leave this site and be redirected to <span style="color: #EF4444; font-weight: bold;">' + getCookie('url') + '</span>. Are you sure you want to do this?';