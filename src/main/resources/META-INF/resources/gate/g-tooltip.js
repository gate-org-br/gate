let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
	<g-icon>&#X3043;</g-icon>
 <style>* {
	box-sizing: border-box;
}

:host(*) {
	margin: 0;
	padding: 8px;
	display: none;
	position: fixed;
	z-index: 1000000;
	visibility: hidden;
	border-radius: 3px;
	width: min-content;
	height: min-content;
	flex-direction: column;
	background-color: var(--main4);
	border: 1px solid var(--main10);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}


g-icon
{
	font-size: 20px;
	position: absolute;
}

g-icon[data-arrow='north'] {
	top: 100%;
	left: calc(50% - 10px);
	transform: rotate(180deg);
}

g-icon[data-arrow='south'] {
	top: -20px;
	left: calc(50% - 10px);
}

g-icon[data-arrow='west'] {
	left: 100%;
	top: calc(50% - 10px);
	transform: rotate(90deg);
}

g-icon[data-arrow='east'] {
	left: -20px;
	top: calc(50% - 10px);
	transform: rotate(270deg);
}

g-icon[data-arrow='northeast'] {
	top: 100%;
	left: -20px;
	transform: rotate(225deg);
}

g-icon[data-arrow='northwest'] {
	top: 100%;
	left: 100%;
	transform: rotate(135deg);
}
g-icon[data-arrow='southeast'] {
	top: -20px;
	left: -20px;
	transform: rotate(-45deg);
}

g-icon[data-arrow='southwest'] {
	left: 100%;
	top: -20px;
	transform: rotate(45deg);
}</style>`;

/* global template */

import Formatter from './formatter.js';
let instance;
const DEFAULT_POSITION = "northeast";
export const POSITIONS = ["northeast", "southwest", "northwest", "southeast", "north", "east", "south", "west"];

const GAP = 20;

function isVisible(element)
{
	let rect = element.getBoundingClientRect();
	return rect.top >= 0
		&& rect.left >= 0
		&& rect.bottom <= window.innerHeight
		&& rect.right <= window.innerWidth;
}


function calc(element, tooltip, position)
{
	element = element.getBoundingClientRect();
	tooltip = tooltip.getBoundingClientRect();

	switch (position)
	{
		case "northeast":
			return {x: element.left + element.width + GAP,
				y: element.top - tooltip.height - GAP};

		case "southwest":
			return {x: element.left - tooltip.width - GAP,
				y: element.bottom + GAP};
		case "northwest":
			return {x: element.left - tooltip.width - GAP,
				y: element.top - tooltip.height - GAP};
		case "southeast":
			return {x: element.right + GAP,
				y: element.bottom + GAP};
		case "north":
			return {x: element.left + (element.width / 2)
					- (tooltip.width / 2),
				y: element.top - tooltip.height - GAP};
		case "east":
			return {x: element.left + element.width + GAP,
				y: element.top + (element.height / 2)
					- (tooltip.height / 2)};
		case "south":
			return {x: element.left + (element.width / 2)
					- (tooltip.width / 2),
				y: element.bottom + GAP};
		case "west":
			return {x: element.left - tooltip.width - GAP,
				y: element.top + (element.height / 2)
					- (tooltip.height / 2)};
	}
}

export default class GTooltip extends HTMLElement
{

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	show(target, position = this.position)
	{
		if (!this.parentNode)
			throw new Error("Attempt to show disconnected tooltip");

		if (instance)
			instance.hide();

		if (!target.getBoundingClientRect)
			return;

		this.style.display = "flex";

		for (let i = 0; i < 8; i++)
		{
			let point = calc(target, this, position);
			this.style.top = point.y + "px";
			this.style.left = point.x + "px";

			if (isVisible(this))
				break;
			else
				position = POSITIONS[(POSITIONS.indexOf(position) + 1) % 8];
		}

		this.shadowRoot.querySelector("g-icon")
			.dataset.arrow = position;
		this.style.visibility = "visible";
		instance = this;
	}

	get position()
	{
		return this.getAttribute("position")
			|| "southeast";

	}

	set position(value)
	{
		if (POSITIONS.includes(value))
			this.setAttribute("position", value);
	}

	hide()
	{
		this.style.visibility = "hidden";
		this.style.display = "";
	}

	static show(element, content, position, size, height)
	{
		let tooltip = new GTooltip();
		document.body.appendChild(tooltip);

		if (size)
			tooltip.style.width = tooltip.style.height = size;
		if (height)
			tooltip.style.height = height;

		tooltip.innerHTML = Formatter.JSONtoHTML(content);
		tooltip.show(element, position || DEFAULT_POSITION);
	}

	static hide()
	{
		if (instance)
		{
			instance.hide();
			instance.remove();
			instance = null;
		}
	}

	static observedAttributes = ["position"];

	attributeChangedCallback(name, oldValue, newValue)
	{
		if (name === "position" && !POSITIONS.includes(newValue))
			this.position = oldValue;
	}
}

customElements.define('g-tooltip', GTooltip);

window.addEventListener("mouseover", function (event)
{
	for (let target of [...event.composedPath(), event.target])
	{
		if (target.hasAttribute && target.hasAttribute("data-tooltip"))
		{
			let content = JSON.parse(target.getAttribute("data-tooltip"));
			target.addEventListener("mouseleave", () => GTooltip.hide(), {once: true});
			let position = target.getAttribute("data-tooltip:position");
			GTooltip.show(target, content, position);
			return;
		} else if (target.children)
		{
			for (let tooltip of target.children)
			{
				if (tooltip.tagName === "G-TOOLTIP")
				{
					target.addEventListener("mouseleave",
						() => tooltip.hide(), {once: true});
					tooltip.show(target);
					return;
				}
			}
		}
	}
});