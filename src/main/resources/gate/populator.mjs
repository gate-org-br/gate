export default function Populator(options)
{
	this.populate = function (element)
	{
		while (element.firstChild)
			element.removeChild(element.firstChild);

		switch (element.tagName.toLowerCase())
		{
			case "select":
				element.value = undefined;

				element.appendChild(document.createElement("option"))
					.setAttribute("value", "");

				for (var i = 0; i < options.length; i++)
				{
					var option = element.appendChild(document.createElement("option"));
					option.innerHTML = options[i].label;
					option.setAttribute('value', options[i].value);
				}

				break;

			case "datalist":
				for (var i = 0; i < options.length; i++)
				{
					var option = element.appendChild(document.createElement("option"));
					option.innerHTML = options[i].label;
					option.setAttribute('data-value', options[i].value);
				}

				break;

		}
		return this;
	};
}


