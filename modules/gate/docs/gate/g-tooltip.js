let template = document.createElement("template");
template.innerHTML = `
	<div><slot></slot></div><svg id="north" viewBox="0 0 16 16"><polygon points="0,0 16,0 8,16" /></svg><svg id="south" viewBox="0 0 16 16"><polygon points="0,16 16,16 8,0" /></svg><svg id="west" viewBox="0 0 16 16"><polygon points="0,0 16,8 0,16" /></svg><svg id="east" viewBox="0 0 16 16"><polygon points="16,0 0,8 16,16" /></svg><svg id="northwest" viewBox="0 0 16 16"><polygon points="0,0 16,8 0,16" /></svg><svg id="northeast" viewBox="0 0 16 16"><polygon points="16,0 0,8 16,16" /></svg><svg id="southwest" viewBox="0 0 16 16"><polygon points="0,0 16,8 0,16" /></svg><svg id="southeast" viewBox="0 0 16 16"><polygon points="16,0 0,8 16,16" /></svg>
<style data-element="g-tooltip">* {
	box-sizing: border-box;
}

:host(*) {
	margin: 0;
	padding: 10px;
	font-size: 16px;
	max-width: 50vw;
	max-height: 50vh;
	z-index: 1000000;
	border-radius: 6px;
	overflow: visible !important;
	color: var(--main1, #FFFFFF);
	background-color: var(--text1, #000000);
	border: 1px solid rgba(255, 255, 255, 0.08);
	box-shadow: 0 12px 24px rgba(0, 0, 0, 0.28);
}

:host(:popover-open) {
	display: flex;
	align-items: stretch;
	flex-direction: column;
}

div {
	height: auto;
	overflow: auto;
	width: max-content;
}

svg {
	width: 16px;
	height: 16px;
	display: none;
	position: absolute;
	pointer-events: none;
	fill: var(--text1, #000000);
	filter: drop-shadow(0 4px 10px rgba(0, 0, 0, 0.25));
}

#north {
	top: 100%;
	left: calc(50% - 8px);
}

#south {
	top: -16px;
	left: calc(50% - 8px);
}

#west {
	left: 100%;
	top: calc(50% - 8px);
}

#east {
	left: -16px;
	top: calc(50% - 8px);
}

#northwest {
	top: 3px;
	left: 100%;
}

#northeast {
	top: 3px;
	left: -12px;
}

#southwest {
	bottom: 3px;
	left: 100%;
}

#southeast {
	bottom: 3px;
	left: -16px;
}

:host([arrow="north"]) #north {
	display: block;
}

:host([arrow="south"]) #south {
	display: block;
}

:host([arrow="east"]) #east {
	display: block;
}

:host([arrow="west"]) #west {
	display: block;
}

:host([arrow="northeast"]) #northeast {
	display: block;
}

:host([arrow="northwest"]) #northwest {
	display: block;
}

:host([arrow="southeast"]) #southeast {
	display: block;
}

:host([arrow="southwest"]) #southwest {
	display: block;
}</style>`;
/* global template */

import DOM from './dom.js';
import './mutation-events.js';
import anchor from './anchor.js';
import DataURL from './data-url.js';
import ResponseHandler from './response-handler.js';
import GJsonHTMLElement from './g-json-html-element.js';

const GAP = 16;
const DEFAULT_POSITION = "north";
export const POSITIONS = ["north", "south", "east", "west",
	"northeast", "southwest", "northwest", "southeast"];

export default class GTooltip extends HTMLElement
{
	#parent;
	#trigger = () =>
	{
		let timeout = setTimeout(() => this.show(this.parentNode), 500);
		this.parentNode.addEventListener("mouseleave", () => clearTimeout(timeout), {once: true});
	}

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

		const controller = new AbortController();
		target.addEventListener("mouseleave", () => this.hide() || controller.abort(), {signal: controller.signal});
		target.addEventListener("focusout", () => this.hide() || controller.abort(), {signal: controller.signal});
		target.addEventListener("click", () => this.hide() || controller.abort(), {signal: controller.signal});

		this.style.visibility = "hidden";
		this.showPopover();

		anchor(this, target, GAP, position, ...POSITIONS)
			.then(({position, location}) =>
			{
				this.setAttribute("arrow", position);
				this.style.top = `${location.y}px`;
				this.style.left = `${location.x}px`;
			}).catch((error) => console.warn("No valid position found tooltip position found:", error))
			.finally(() => this.style.visibility = "");
	}

	get position()
	{
		return this.getAttribute("position") || DEFAULT_POSITION;
	}

	set position(value)
	{
		if (POSITIONS.includes(value))
			this.setAttribute("position", value);
	}

	hide()
	{
		if (this.getAttribute("auto"))
			this.remove();
		this.style.visibility = "hidden";
	}

	static show(element, content, position)
	{
		let tooltip = new GTooltip();
		tooltip.setAttribute("auto", true);

		element.appendChild(tooltip);
		if (tooltip.parentNode !== element)
			element.parentNode.appendChild(tooltip);

		if (typeof content === "object")
		{
			tooltip.innerHTML = "";
			const element = new GJsonHTMLElement();
			element.value = content;
			tooltip.appendChild(element);
		} else
			tooltip.innerHTML = content;
		tooltip.show(element, position || DEFAULT_POSITION);
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

	connectedCallback()
	{
		this.setAttribute("popover", "auto");
	}
}

customElements.define('g-tooltip', GTooltip);

function trigger()
{
	let timeout = setTimeout(() =>
	{
		if (this.hasAttribute("data-tooltip"))
			DOM.navigate(this, this.getAttribute("data-tooltip"))
				.orElseThrow(`${this.getAttribute("data-tooltip")} is not a valid selector`)
				.show(this);
		else if (this.hasAttribute("data-tooltip:text"))
			GTooltip.show(this, this.getAttribute("data-tooltip:text"));
		else if (this.hasAttribute("data-tooltip:source"))
			fetch(this.getAttribute("data-tooltip:source"))
				.then(ResponseHandler.auto)
				.then(content => GTooltip.show(this, content))
				.catch(error => console.error('Error trying to fetch tooltip data:', error));
	}, 500);
	this.addEventListener("mouseleave", () => clearTimeout(timeout), {once: true});
}

window.addEventListener("connected", event => {
	let target = event.composedPath()[0] || event.target;
	if (target.hasAttribute("data-tooltip")
		|| target.hasAttribute("data-tooltip:source"))
		target.addEventListener("mouseenter", trigger);
});

window.addEventListener("disconnected", event => {
	let target = event.composedPath()[0] || event.target;
	target.removeEventListener("mouseenter", trigger);
});

window.addEventListener("attribute-created", event => {
	let attribute = event.detail.attribute;
	let target = event.composedPath()[0] || event.target;
	if (attribute === "data-tooltip"
		|| attribute === "data-tooltip:source")
		target.addEventListener("mouseenter", trigger);
});

window.addEventListener("attribute-removed", event => {
	let attribute = event.detail.attribute;
	let target = event.composedPath()[0] || event.target;
	if (attribute === "data-tooltip"
		|| attribute === "data-tooltip:source")
		if (!target.hasAttribute("data-tooltip")
			&& !target.hasAttribute("data-tooltip:source"))
			target.removeEventListener("mouseenter", trigger);
});