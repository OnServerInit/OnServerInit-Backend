var account = document.getElementById('alert');
var alerts_container = document.getElementById('alert-dropdown');

alerts_container.style.display = 'none';


var header = document.querySelector('header');
document.getElementById('alert-dropdown').style.top = header.offsetHeight + 'px';
console.log(header.offsetHeight);

account.addEventListener('mouseover', function () {
    alerts_container.style.display = 'block';
});

document.body.addEventListener('mousemove', function (e) {
    var bounds = account.getBoundingClientRect();
    if (e.clientX > bounds.left && e.clientX < bounds.right && e.clientY < (header.clientHeight + 5)) {
        alerts_container.style.display = 'block';
    } else if (e.clientY < header.clientHeight) {
        alerts_container.style.display = 'none';
    }
});

account.addEventListener('mouseout', function () {
    alerts_container.style.display = 'none';
});

alerts_container.addEventListener('mouseover', function () {
    alerts_container.style.display = 'block';
});

alerts_container.addEventListener('mouseout', function () {
    alerts_container.style.display = 'none';
});