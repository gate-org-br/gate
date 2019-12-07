/* global customElements, DefinitionList, Table */

class Tooltip extends HTMLElement
{
	constructor(element, position, content)
	{
		super();
		this.element = element;
		this.setAttribute("position", position);

		switch (typeof content)
		{
			case "string":
				var label = document.createElement("label");
				label.innerHTML = content;
				this.content = label;
				break;
			case "object":
				if (content instanceof HTMLElement)
					this.content = content;
				else
					this.content = DefinitionList.of(content);
				break;
		}
	}

	connectedCallback()
	{
		this.appendChild(this.content);
		var tooltip = this.getBoundingClientRect();
		var element = this.element.getBoundingClientRect();

		switch (this.getAttribute("position"))
		{
			case "top":
				this.style.top = element.top - tooltip.height - 10 + "px";
				this.style.left = element.x + element.width / 2 - tooltip.width / 2 + "px";
				break;
			case "bottom":
				this.style.top = element.bottom + 10 + "px";
				this.style.left = element.x + element.width / 2 - tooltip.width / 2 + "px";
				break;
			case "left":
				this.style.top = element.y + element.height / 2 - (tooltip.height / 2) + "px";
				this.style.left = element.left - tooltip.width - 10 + "px";
				break;
			case "right":
				this.style.top = element.y + element.height / 2 - tooltip.height / 2 + "px";
				this.style.left = element.x + element.width + 10 + "px";
				break;
		}

	}

	static show(element, position, content)
	{
		if (this.instance)
			Tooltip.hide();
		this.instance = new Tooltip(element, position, content);
		document.body.appendChild(this.instance);
	}

	static hide()
	{
		if (this.instance)
		{
			this.instance.parentNode.removeChild(this.instance);
			this.instance = null;
		}
	}
}

customElements.define('g-tooltip', Tooltip);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("*[data-tooltip]")).forEach(e =>
	{
		e.addEventListener("mouseout", () => Tooltip.hide());
		e.addEventListener("mouseover", () =>
		{
			var object = e.getAttribute("data-tooltip");
			if (/ *[{"[].*[}"\]] */.test(object))
				object = JSON.parse(object);
			Tooltip.show(e, e.getAttribute("data-tooltip-position") || "bottom", object)
		});
	});
});