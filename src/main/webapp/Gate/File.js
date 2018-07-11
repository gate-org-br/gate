function File(node)
{
    if (!node.text)
    {
        node.style.display = 'none';
        node.text = document.createElement("input");
        node.parentNode.appendChild(node.text);
        $(node).getForm().appendChild(node);
        node.onchange = function ()
        {
            this.text.value = this.value;
        };
        node.text.file = node;
        node.text.value = node.value;
        node.text.style = node.style;
        node.text.style.cursor = "pointer";
        node.text.setAttribute('type', 'text');
        node.text.className = node.className;
        node.text.style.backgroundRepeat = 'no-repeat';
        node.text.setAttribute('readonly', 'readonly');
        node.text.style.backgroundPosition = 'right center';
        node.text.onclick = function ()
        {
            this.file.click();
        };
        node.text.setAttribute('tabindex', node.getAttribute('tabindex'));
        node.text.setAttribute('required', node.getAttribute('required'));
        node.text.style.backgroundImage = 'url(../gate/imge/back/FOLDER.png)';
        node.text.onkeypress = function (e)
        {
            e = e ? e : window.event;
            e.keyCode === ENTER
                    && this.file.click();
            return e.keyCode === 9;
        };
    }
}

window.addEventListener("load", function ()
{
    search("input[type='file']").forEach(function (node)
    {
        new File(node);
    });
});
