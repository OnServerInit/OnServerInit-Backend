function addToHead(urls){
    for(var i = 0; i < urls.length; i++){
        var head = document.head;
        var style = document.createElement('link');
        style.href = urls[i];
        style.rel = "stylesheet";
        head.appendChild(style);
    }
}