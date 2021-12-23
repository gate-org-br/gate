Array.from(document.getElementsByName("select")).forEach(function (element)
{
	element.checkValidity = function ()
	{
		return !this.getAttribute("required") || this.value;
	};
});

Array.from(document.getElementsByName("textarea")).forEach(function (element)
{
	element.checkValidity = function ()
	{
		return (!this.getAttribute("required") || this.innerHTML)
			&& (!this.getAttribute("pattern") || !this.innerHTML
				|| new RegExp(this.getAttribute('pattern'))
				.test(this.innerHTML));
	};
});

Array.from(document.getElementsByName("input")).forEach(function (element)
{
	element.checkValidity = function ()
	{
		return (!this.getAttribute("required") || this.value)
			&& (!this.getAttribute("pattern") || !this.value
				|| new RegExp(this.getAttribute('pattern')).test(this.value));
	};
});

Array.from(document.querySelectorAll("input[type=radio], input[type=checkbox]")).forEach(function (element)
{
	element.checkValidity = function ()
	{
		return !this.getAttribute("required") ||
			Array.from(document.getElementsByName(this.getAttribute('name'))).some(e => e.checked);
	};
});

Array.from(document.getElementsByName("form")).forEach(function (element)
{
	element.checkValidity = function ()
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
	};

	element.addEventListener("submit", function (e)
	{
		if (!this.checkValidity())
			e.preventDefault();
	});
});