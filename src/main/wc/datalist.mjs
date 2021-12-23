import Populator from './populator.mjs';

Array.from(document.querySelectorAll("input[list]")).forEach(function (element)
{
	element.setAttribute("autocomplete", "off");
	element.addEventListener("input", process);
	process.call(element);

	function process()
	{
		var datalist = document.getElementById(this.getAttribute("list"));

		if (typeof this.value === "string"
			&& datalist.hasAttribute("data-options"))
		{
			var len = 3;
			if (datalist.hasAttribute("data-options-len"))
				len = parseInt(datalist.getAttribute("data-options-len"));

			if (this.value.length < len)
			{
				this.filter = null;
				new Populator([]).populate(datalist);
			} else if (!this.filter || !this.value.toLowerCase().includes(this.filter.toLowerCase()))
			{
				if (document.activeElement === this)
				{
					this.blur();
					this.disabled = true;
					new URL(resolve(datalist.getAttribute("data-options"))).get(options =>
					{
						new Populator(JSON.parse(options)).populate(datalist);
						this.disabled = false;
						this.focus();
						this.click();
						this.filter = this.value;
					});
				} else
				{
					this.disabled = true;
					new URL(resolve(datalist.getAttribute("data-options"))).get(options =>
					{
						new Populator(JSON.parse(options)).populate(datalist);
						this.disabled = false;
						this.filter = this.value;
					});
				}
			}
		}


		var hidden = null;
		if (this.nextElementSibling
			&& this.nextElementSibling.tagName.toLowerCase() === "input"
			&& this.nextElementSibling.getAttribute("type")
			&& this.nextElementSibling.getAttribute("type").toLowerCase() === "hidden")
			hidden = this.nextElementSibling;

		if (hidden && hidden.value)
		{
			hidden.value = "";
			hidden.dispatchEvent(new Event('change', {bubbles: true}));
		}

		if (this.value && this.value.length > 0)
		{
			var option = Array.from(datalist.children)
				.find(option => option.innerHTML === this.value
						|| option.innerHTML.toLowerCase() === this.value.toLowerCase());
			if (option)
			{
				this.setCustomValidity("");
				this.value = option.innerHTML;

				if (hidden)
				{
					hidden.value = option.getAttribute("data-value");
					hidden.dispatchEvent(new Event('change', {bubbles: true}));
				}
			} else
				this.setCustomValidity("Entre com um dos valores da lista");
		} else
			this.setCustomValidity("");
	}
});