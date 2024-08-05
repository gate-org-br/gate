let template = document.createElement("template");
template.innerHTML = `
	<div>
		<slot></slot>
	</div>
	<span></span>
 <style data-element="g-tooltip">* {
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
	background-color: var(--main4);
	border: 1px solid var(--main10);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

div {
	overflow: auto;
	width: max-content;
	height: max-content;
	max-width: min(50vw - 16px, 600px);
	max-height: min(50vh - 16px, 600px);
}

span
{
	width: 0;
	height: 0;
	position: absolute;
	display: inline-block;
}

span[data-arrow='north']
{
	top: 100%;
	left: calc(50% - 6px);
	border-top: 12px solid black;
	border-left: 6px solid transparent;
	border-right: 6px solid transparent;
}

span[data-arrow='south'] {
	top: -12px;
	left: calc(50% - 6px);
	border-bottom: 12px solid black;
	border-left: 6px solid transparent;
	border-right: 6px solid transparent;
}

span[data-arrow='west'] {
	left: 100%;
	top: calc(50% - 6px);
	border-left: 12px solid black;
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}

span[data-arrow='east']
{
	left: -12px;
	top: calc(50% - 6px);
	border-right: 12px solid black;
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}

span[data-arrow='northwest'],
span[data-arrow='northeast'],
span[data-arrow='southeast'],
span[data-arrow='southwest'] {
	display: none;
}</style>`;
/* global template */

import Formatter from './formatter.js';
const DEFAULT_POSITION = "north";
export const POSITIONS = ["north", "east", "south", "west", "northeast", "southwest", "northwest", "southeast"];
const GAP = 12;
let instance;

function isVisible(element)
{
	let rect = element.getBoundingClientRect();
	return rect.top >= 0 && rect.left >= 0 && rect.bottom <= window.innerHeight && rect.right <= window.innerWidth;
}

function calc(element, tooltip, position)
{
	element = element.getBoundingClientRect();
	tooltip = tooltip.getBoundingClientRect();

	switch (position)
	{
		case "northeast":
			return {x: element.right, y: element.top - tooltip.height};
		case "southwest":
			return {x: element.left - tooltip.width, y: element.bottom};
		case "northwest":
			return {x: element.left - tooltip.width, y: element.top - tooltip.height};
		case "southeast":
			return {x: element.right, y: element.bottom};
		case "north":
			return {x: element.left + (element.width / 2) - (tooltip.width / 2), y: element.top - tooltip.height - GAP};
		case "east":
			return {x: element.right + GAP, y: element.top + (element.height / 2) - (tooltip.height / 2)};
		case "south":
			return {x: element.left + (element.width / 2) - (tooltip.width / 2), y: element.bottom + GAP};
		case "west":
			return {x: element.left - tooltip.width - GAP, y: element.top + (element.height / 2) - (tooltip.height / 2)};
		default:
			return {x: element.right + GAP, y: element.bottom + GAP}; // Default to southeast if invalid position
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

		if (!target.getBoundingClientRect)
			return;

		if (instance)
			instance.hide();
		instance = this;

		const controller = new AbortController();
		target.addEventListener("mouseleave", () => this.hide() || controller.abort(), {signal: controller.signal});
		target.addEventListener("focusout", () => this.hide() || controller.abort(), {signal: controller.signal});
		target.addEventListener("click", () => this.hide() || controller.abort(), {signal: controller.signal});

		this.style.display = "block";
		this.style.visibility = "hidden";

		setTimeout(() =>
		{
			for (let i = 0; i < POSITIONS.length; i++)
			{
				let point = calc(target, this, position);
				this.style.top = `${point.y}px`;
				this.style.left = `${point.x}px`;

				if (isVisible(this))
					break;
				position = POSITIONS[(POSITIONS.indexOf(position) + 1) % POSITIONS.length];
			}

			this.shadowRoot.querySelector("span").dataset.arrow = position;
			this.style.visibility = "visible";
		}, 0);
	}

	get position()
	{
		return this.getAttribute("position") || "southeast";
	}

	set position(value)
	{
		if (POSITIONS.includes(value))
			this.setAttribute("position", value);
	}

	hide()
	{
		if (this.parentNode === document.body)
			return this.remove();

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

		tooltip.innerHTML = content;
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

	static get observedAttributes()
	{
		return ["position"];
	}

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
			let json = JSON.parse(target.getAttribute("data-tooltip"));
			let content = Formatter.JSONtoHTML(json);
			let position = target.getAttribute("data-tooltip:position");
			return GTooltip.show(target, content, position);
		} else if (target.children)
		{
			for (let tooltip of target.children)
				if (tooltip.tagName === "G-TOOLTIP")
					return tooltip.show(target);
		}
	}
});
