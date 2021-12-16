function checkChildren(check) {
    const children = check.parentElement.nextSibling.nextSibling.childNodes[0].parentElement.children;
    for(let i = 0; i < children.length; i++){
        const child = children[i];
        child.children[0].checked = check.checked;
    }
}