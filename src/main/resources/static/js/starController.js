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


