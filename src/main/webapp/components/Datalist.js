window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input[list]")).forEach(function (element)
	{
		element.addEventListener("input", function ()
		{
			var datalist = document.getElementById(this.getAttribute("list"));


			if (this.value.length > 0)
			{
				var datalist = document.getElementById(this.getAttribute("list"));

				if (datalist.hasAttribute("data-populate-url"))
				{
					var len = 3;
					if (datalist.hasAttribute("data-populate-len"))
						len = parseInt(datalist.getAttribute("data-populate-len"));

					if (this.value.length < len)
						new Populator([]).populate(datalist);
					else
					if (this.value.length === len)
					{
						this.blur();
						this.disabled = true;
						new URL(resolve(datalist.getAttribute("data-populate-url")))
							.get(options =>
							{
								new Populator(JSON.parse(options)).populate(datalist);
								this.disabled = false;
								this.focus();
							});
					}
				}

			}
		});
	});

	Array.from(document.querySelectorAll("input[list][data-populate-field]")).forEach(function (element)
	{
		element.addEventListener("change", function ()
		{
			var field = document
				.getElementById(this.getAttribute("data-populate-field"));
			field.value = null;

			var datalist = document.getElementById(this.getAttribute("list"));
			Array.from(datalist.children).filter(option => option.innerHTML === this.value
					|| option.innerHTML.toLowerCase() === this.value.toLowerCase())
				.forEach(option =>
				{
					this.value = option.innerHTML;
					field.value = option.getAttribute("data-value");
				});
		});
	});


	Array.from(document.querySelectorAll("input[list][data-populate-field], input[list][data-require-list]")).forEach(function (element)
	{
		element.addEventListener("input", function ()
		{
			var datalist = document.getElementById(this.getAttribute("list"));
			if (this.value.length > 0)
				if (Array.from(datalist.children).some(e => element.value === e.innerHTML
						|| e.innerHTML.toLowerCase() === element.value.toLowerCase()))
					element.setCustomValidity("");
				else
					element.setCustomValidity("Entre com um dos valores da lista");
		});
	});
});