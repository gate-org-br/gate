let template = document.createElement("template");
template.innerHTML = `
<div></div>
 <style>:host(*) {
	color: white;
	display: flex;
	z-index: 100000;
	position: fixed;
	border-radius: 3px;
	visibility: hidden;
	align-items: stretch;
	justify-content: center;
}

div {
	flex-grow: 1;
	padding: 8px;
	display: flex;
	border-radius: 5px;
	align-items: center;
	justify-content: center;
	background-color: black;
	box-shadow: 4px 4px 4px 0px #666666;
}

div > * {
	width: auto;
	max-width: 60vw;
}

div::after {
	content: " ";
	border-width: 6px;
	position: absolute;
	border-style: solid;
}

:host([arrow="bottom"]) > div::after {
	left: 50%;
	bottom: 100%;
	margin-left: -6px;
	border-color: transparent transparent black transparent;
}

:host([arrow="top"]) > div::after {
	top: 100%;
	left: 50%;
	margin-left: -6px;
	border-color: black transparent transparent transparent;
}

:host([arrow="left"]) > div::after {
	top: 50%;
	left: 100%;
	margin-top: -6px;
	border-color: transparent transparent transparent black;
}

:host([arrow="right"]) > div::after {
	top: 50%;
	right: 100%;
	margin-top: -6px;
	border-color: transparent black transparent transparent;
}

dl {
	margin: 0;
	grid-gap: 8px;
	display: grid;
	grid-template-columns: auto auto;
}

dt {
	margin: 0;
	text-align: right;
	font-weight: bold;
}

dd {
	margin: 0;
	text-align: left;
}

ul
{
	margin: 0;
	padding: 0;
	list-style-type: disc;
	list-style-position: inside;
}

li {
	margin: 0;
	padding: 0;
}</style>`;

/* global customElements, HTMLElement, template */

import './g-properties.mjs';

let instance;

export default class GTooltip extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get element()
	{
		return this._private.element
			|| document.documentElement;
	}

	set element(element)
	{
		this._private.element = element;
	}

	get orientation()
	{
		return this._private.orientation || 'horizontal';
	}

	set orientation(orientation)
	{
		this._private.orientation = orientation;
	}

	set content(content)
	{
		while (this.shadowRoot.firstElementChild.firstChild)
			this.shadowRoot.removeChild(this.shadowRoot.firstElementChild.firstChild);

		switch (typeof content)
		{
			case "string":
				this.shadowRoot.firstElementChild.appendChild(document.createElement("label")).innerHTML = content;
				break;
			case "object":
				if (Array.isArray(content))
				{
					let ul = this.shadowRoot.firstElementChild.appendChild(document.createElement("ul"));
					content.forEach(e => ul.appendChild(document.createElement("li")).innerHTML = e);
				} else if (content instanceof HTMLElement)
				{
					this.shadowRoot.firstElementChild.appendChild(content);
				} else
				{
					this.shadowRoot.firstElementChild.appendChild(document.createElement("g-properties"))
						.value = content;
				}
				break;
		}
	}

	connectedCallback()
	{
		const tooltip = this.getBoundingClientRect();
		const element = this.element.getBoundingClientRect();
		element.center = {x: element.left + (element.width / 2), y: element.top + (element.height / 2)};

		const left = element.center.x - tooltip.width / 2;
		const right = element.center.x + tooltip.width / 2;
		const top = element.center.y - (tooltip.height / 2);
		const bottom = element.center.y + (tooltip.height / 2);

		const vertical = () =>
		{
			if (left > 0 && right < window.innerWidth)
				if (element.center.y >= (window.innerHeight / 2))
					this.show(left, element.top - tooltip.height - 10, "top");
				else
					this.show(left, element.bottom + 10, "bottom");
			else if (element.center.x >= (window.innerWidth / 2))
				this.show(element.left - tooltip.width - 10, top, "left");
			else
				this.show(element.x + element.width + 10, top, "right");
		}

		const horizontal = () =>
		{
			if (top > 0 && bottom < window.innerHeight)
				if (element.center.x >= (window.innerWidth / 2))
					this.show(element.left - tooltip.width - 10, top, "left");
				else
					this.show(element.x + element.width + 10, top, "right");
			else if (element.center.y >= (window.innerHeight / 2))
				this.show(left, element.top - tooltip.height - 10, "top");
			else
				this.show(left, element.bottom + 10, "bottom");
		}

		switch (this.orientation)
		{
			case "vertical":
				if (element.top - tooltip.height - 10 < 0 &&
					element.bottom + tooltip.height + 10 > window.innerHeight)
					return horizontal();
				return vertical();

			case "horizontal":
				if (element.left - tooltip.width - 10 < 0 &&
					element.right + tooltip.width + 10 > window.innerWidth)
					return vertical();
				horizontal();
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

		let tooltip = document.createElement("g-tooltip");
		tooltip.content = content;
		tooltip.element = element;
		tooltip.orientation = orientation;
		instance = document.body.appendChild(tooltip);
	}

	static hide()
	{
		if (instance)
		{
			instance.remove();
			instance = null;
		}
	}
}

customElements.define('g-tooltip', GTooltip);

window.addEventListener("mousemove", event => {

	if (instance)
		return;

	let target = event.composed
		&& event.composedPath
		&& event.composedPath().length
		? event.composedPath()[0]
		: event.target;

	target = target.closest
		? target.closest("*[data-tooltip]")
		: null;

	if (!target)
		return;

	let object = target.getAttribute("data-tooltip");
	if (/ *[{"[].*[}"\]] */.test(object))
		object = JSON.parse(object);

	target.addEventListener("mouseleave", () => GTooltip.hide(), {once: true});
	GTooltip.show(target, target.getAttribute("data-tooltip-orientation"), object);
});