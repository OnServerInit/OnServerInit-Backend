var account = document.getElementById('account-username');
var alerts_container = document.getElementById('alert-dropdown');

alerts_container.style.display = 'none';


var header = document.querySelector('header');
document.getElementById('alert-dropdown').style.top = header.offsetHeight + 'px';
console.log(header.offsetHeight);

account.addEventListener('mouseover', function(){
    alerts_container.style.display = 'block';
});

account.addEventListener('mouseout', function(){
    alerts_container.style.display = 'none';
});

alerts_container.addEventListener('mouseover', function(){
    alerts_container.style.display = 'block';
});

alerts_container.addEventListener('mouseout', function(){
    alerts_container.style.display = 'none';
});