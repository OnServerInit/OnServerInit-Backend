document.getElementById('report-close-btn').addEventListener('click', function() {closeReport();});

document.getElementById('report-button').addEventListener('click', function(e) {
    var button = e.target;
    var type = button.getAttribute('type');
    openReport(type);
});

var report_outer_body = document.getElementById('report-body-outer');
var report_overlay = document.getElementById('report-overlay');
var buttons = document.getElementById('report-buttons').children;

buttons[0].style.backgroundColor = 'rgb(226, 226, 226)';

buttons[0].addEventListener('click', function(e){
    e.preventDefault();
    backPage();
});

buttons[1].addEventListener('click', function(e){
    e.preventDefault();
    nextPage();
});


var closing = false;

function closeReport(){
    closing = true;
    report_outer_body.style.top = '200%';
    setTimeout(function(){
        report_overlay.style.display = 'none';
        closing = false;
    }, 500);
}

report_overlay.style.display = 'none';
closeReport();

var lastPage = 1;
var page = 1;

function updateSpans(){
    var spans = document.getElementById('steps').children;
    for (var i = 0; i < spans.length; i++) {
        if(i + 1 == page){
            var span_active = spans[i];
            var offsets = span_active.getBoundingClientRect();
            var offset_left = offsets.left;
            document.getElementById('animated-span').style.left = i + 0.6*i + 'rem';
        }
    }

    for(var i = 0; i < page-1; i++){
        var span = spans[i];
        span.style.backgroundColor = '#EF4444';
    }
}

function enableTransitions(){
    var tabs = document.getElementsByClassName('tab');
    for (var i = 0; i < tabs.length; i++) {
        tabs[i].classList.add('tab-transition');
    }
}

function onPage(){
    if(page == 1){
        buttons[0].style.display = 'none';
    }else{
        buttons[0].style.display = 'inline-block';
    }
    var tabs = document.getElementsByClassName('tab');
    if(page + 1 > tabs.length){
        page = tabs.length;
        buttons[1].style.display = 'none';
        buttons[2].style.display = 'inline-block';
    }else{
        buttons[1].style.display = 'inline-block';
        buttons[2].style.display = 'none';
    }

    for (var i = 0; i < tabs.length; i++) {
        tabs[i].style.display = 'none';
        tabs[i].classList.remove('tab-transition');
        tabs[i].style.width = 22 + 'rem';
    }

    var next_page = tabs[page - 1];
    next_page.style.display = 'block';

    if(lastPage < page){
        // moved forward
        
        var current_page = tabs[lastPage - 1];
        current_page.style.display = 'block';

        next_page.style.left = '110%';
        current_page.style.left = '0%';

        enableTransitions();

        setTimeout(function(){
            next_page.style.left = '0%';
            current_page.style.left = '-110%';
        }, 1);
    }else if(lastPage > page){
        // moved backwards
        
        var current_page = tabs[lastPage - 1];
        current_page.style.display = 'block';

        next_page.style.left = '-110%';
        current_page.style.left = '0%';

        enableTransitions();

        setTimeout(function(){
            next_page.style.left = '0%';
            current_page.style.left = '110%';
        }, 1);
    }
    lastPage = page;
    updateSpans();
}

function backPage(){
    page--;
    onPage();
}

function nextPage(){
    page++;
    onPage();
}

setInterval(function() {
    if(report_overlay.style.display == 'block' && closing != true){
        var center_element = document.getElementById('get-center');
        var rect = center_element.getBoundingClientRect();
        var scroll_top = document.documentElement.scrollTop || document.body.pageXOffset;
        scroll_top = (scroll_top == undefined) ? 0 : scroll_top;
        var center_y = (((rect.top + report_outer_body.getBoundingClientRect().height) / 2) + scroll_top);

        var x = window.innerWidth / 2;
        var y = window.innerHeight / 2;
        report_outer_body.style.left = x + 'px';
        report_outer_body.style.top = center_y + 'px';
    }
}, 100);

function openReport(type){
    switch (type) {
        case 'resource':
            var id = window.location.href.split('/')[window.location.href.split('/').length - 1];
            document.getElementById('report-title').innerHTML = 'Report this resource for';

            document.getElementsByClassName('adfkugosdfiygusdlkjfghsldkfjghlskdffjgh')[0].value = 'Resource';

            break;
    
        default:
            break;
    }
    report_overlay.style.display = 'block';
    onPage();
}

var radios = document.getElementsByName('input');
for (var i = 0; i < radios.length; i++) {
    if (radios[i].type !== 'radio') {
        radios.splice(i, 1);
    }
}

for (var i = 0; i < radios.length; i++) {
    radios[i].checked = false;
}