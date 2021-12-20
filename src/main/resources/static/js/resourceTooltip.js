// resource stuff
document.getElementById('resource-tooltip').style.display = 'none';
var tooltip = document.getElementById('resource-tooltip');
tooltip.style.display = 'flex';
var tooltip_height = tooltip.clientHeight;
tooltip.style.display = 'none';
tooltip.style.height = '0px';
console.log(tooltip_height);

document.getElementById('resource-more').addEventListener('click', function () {
    document.getElementById('resource-tooltip').style.display = 'flex';
    setTimeout(function () {
        document.getElementById('resource-tooltip').style.height = tooltip_height + 'px';
    }, 1);
});

document.addEventListener('click', function (e) {
    if (e.target.id != 'resource-more' && e.target.id != 'resource-tooltip' && e.target.tagName != 'svg' && e.target.tagName != 'path'){
        document.getElementById('resource-tooltip').style.height = '0px';
        setTimeout(function () {
            document.getElementById('resource-tooltip').style.display = 'none';
            console.log(e.target);
        }, 500);
    }
});