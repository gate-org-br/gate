export default class Populator
{
	#options;
	constructor(options)
	{
		this.#options = options;
	}

	populate(element, value = 'value', label = 'label')
	{
		while (element.firstChild)
			element.removeChild(element.firstChild);

		if (element.tagName === "SELECT")
		{
			element.value = undefined;
			element.appendChild(document.createElement("option")).setAttribute("value", "");
		}

		for (var i = 0; i < this.#options.length; i++)
		{
			var option = element.appendChild(document.createElement("option"));
			option.innerHTML = this.#options[i][label];
			option.setAttribute(element.tagName === "SELECT" ? 'value' : "data-value",
				this.#options[i][value]);
		}

		return this;
	}
}