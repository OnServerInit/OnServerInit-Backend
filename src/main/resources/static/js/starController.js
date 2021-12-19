var stars = document.getElementById('stars').children;
var stars_count = stars.length;
document.getElementById('review-body').style.display = 'none';

for (var i = 0; i < stars_count; i++) {
    var star = stars[i];
    star.setAttribute('fill', '#838383');
    star.setAttribute('data-star', i);
    star.addEventListener('mouseover', function () {
        if (document.getElementById('review-body').style.display != 'block') {
            document.getElementById('review-body').style.display = 'block';
        }
        var star_index = this.getAttribute('data-star');
        for (var j = 0; j <= stars_count - 1; j++) {
            if (j <= star_index) {
                stars[j].setAttribute('fill', '#ffc107');
            } else {
                stars[j].setAttribute('fill', '#838383');
            }
        }
    });
}


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