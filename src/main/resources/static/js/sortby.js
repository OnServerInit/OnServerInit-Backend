function changeSort() {
    const e = document.getElementById("sortby");
    const sortby = e.value;

    let key = encodeURIComponent("sort");
    let value = encodeURIComponent(sortby);

    const kvp = document.location.search.substr(1).split('&');
    let i = 0;

    for (; i < kvp.length; i++) {
        if (kvp[i].startsWith(key + '=')) {
            let pair = kvp[i].split('=');
            pair[1] = value;
            kvp[i] = pair.join('=');
            break;
        }
    }

    if (i >= kvp.length) {
        kvp[kvp.length] = [key, value].join('=');
    }
    document.location.search = kvp.join('&');
}

function changeSoftware() {
    const e = document.getElementById("software-sort");
    const sortby = e.value;

    let key = encodeURIComponent("software");
    let value = encodeURIComponent(sortby);

    const kvp = document.location.search.substr(1).split('&');
    let i = 0;

    for (; i < kvp.length; i++) {
        if (kvp[i].startsWith(key + '=')) {
            let pair = kvp[i].split('=');
            pair[1] = value;
            kvp[i] = pair.join('=');
            break;
        }
    }

    if (i >= kvp.length) {
        kvp[kvp.length] = [key, value].join('=');
    }
    document.location.search = kvp.join('&');
}

function getQueryVariable(variable) {
    const query = window.location.search.substring(1);
    const vars = query.split("&");
    for (let i = 0; i < vars.length; i++) {
        const pair = vars[i].split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
    }
}

window.addEventListener('load', function () {
    if(document.getElementById("sortby") !== undefined && getQueryVariable("sort") !== undefined) {
        document.getElementById("sortby").value = getQueryVariable("sort").toLowerCase();
    }
    if(document.getElementById("software-sort") !== undefined && getQueryVariable("software") !== undefined) {
        document.getElementById("software-sort").value = getQueryVariable("software").toLowerCase();
    }
});