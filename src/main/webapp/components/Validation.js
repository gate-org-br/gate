window.addEventListener("load", function ()
{
    $("select").def("checkValidity", function ()
    {
        return !this.getAttribute("required")
                || this.value;
    });

    $("textarea").def("checkValidity", function ()
    {
        return (!this.getAttribute("required") || this.innerHTML)
                && (!this.getAttribute("pattern") || !this.innerHTML
                        || new RegExp(this.getAttribute('pattern'))
                        .test(this.innerHTML));
    });

    $("input").def("checkValidity", function ()
    {
        return (!this.getAttribute("required") || this.value)
                && (!this.getAttribute("pattern") || !this.value
                        || new RegExp(this.getAttribute('pattern')).test(this.value));
    });

    $("input[type=radio], input[type=checkbox]").def("checkValidity", function ()
    {
        return !this.getAttribute("required") ||
                Array.from(document.getElementsByName(this.getAttribute('name')))
                .some(function (e)
                {
                    e.checked;
                });
    });

    $("form").def("checkValidity", function ()
    {
        for (var i = 0; i < this.elements.length; i++)
            if (this.elements[i].checkValidity
                    && !this.elements[i].checkValidity())
            {
                try
                {
                    this.elements[i].focus();
                } catch (e)
                {
                }
                alert(this.elements[i].getAttribute("title")
                        || "Preencha o formulário corretamente");
                return false;
            }
    });

    $("form").addEventListener("submit", function (e)
    {
        if (!this.checkValidity())
            e.preventDefault();
    });
});