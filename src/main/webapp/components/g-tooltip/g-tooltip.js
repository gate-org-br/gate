/* global customElements, DefinitionList, Table, HTMLElement */

class GTooltip extends HTMLElement
{
	constructor(element, orientation, content)
	{
		super();
		this._private = {};
		this._private.element = element;
		this._orientation = orientation;

		switch (typeof content)
		{
			case "string":
				var label = document.createElement("label");
				label.innerHTML = content;
				this._content = label;
				break;
			case "object":
				if (content instanceof HTMLElement)
					this._content = content;
				else
					this._content = DefinitionList.of(content);
				break;
		}
	}

	connectedCallback()
	{
		this.appendChild(this._content);
		let tooltip = this.getBoundingClientRect();
		let element = this._private.element.getBoundingClientRect();
		element.center = {x: element.left + (element.width / 2), y: element.top + (element.height / 2)};

		switch (this._orientation || "vertical")
		{

			case "vertical":
				if (element.center.y >= (window.innerHeight / 2))
					this.show(element.center.x - tooltip.width / 2, element.top - tooltip.height - 10, "top");
				else
					this.show(element.center.x - tooltip.width / 2, element.bottom + 10, "bottom");
				break;
			case "horizontal":
				if (element.center.x >= (window.innerWidth / 2))
					this.show(element.left - tooltip.width - 10, element.center.y - (tooltip.height / 2), "left");
				else
					this.show(element.x + element.width + 10, element.center.y - tooltip.height / 2, "right");
				break;
		}
	}

	show(x, y, arrow)
	{
		this.style.top = y + "px";
		this.style.left = x + "px";
		this.setAttribute("arrow", arrow);
		this.style.visibility = "visible";
	}

	static show(element, orientation, content)
	{
		if (!this._private)
			this._private = {};

		GTooltip.hide();
		this._private.instance = document.body
			.appendChild(new GTooltip(element, orientation, content));
	}

	static hide()
	{
		if (this._private && this._private.instance)
		{
			this._private.instance.parentNode
				.removeChild(this._private.instance);
			this._private.instance = null;
		}
	}
}

customElements.define('g-tooltip', GTooltip);


window.addEventListener("mouseover", e =>
{
	e = e.target;
	e = e.closest("*[data-tooltip]");
	if (e)
	{
		var object = e.getAttribute("data-tooltip");
		if (/ *[{"[].*[}"\]] */.test(object))
			object = JSON.parse(object);
		GTooltip.show(e, e.getAttribute("data-tooltip-orientation"), object);
	} else
		GTooltip.hide();
});