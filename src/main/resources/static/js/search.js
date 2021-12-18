function search() {
    const e = document.getElementById("search-input");
    const search = e.value;

    let key = encodeURIComponent("search");
    let value = encodeURIComponent(search);

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