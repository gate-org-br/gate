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
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

div {
	height: auto;
	overflow: auto;
	width: max-content;
	max-width: calc(50vw - 16px);
	max-height: calc(50vh - 16px);
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
	border-top: 12px solid var(--main4);
	border-left: 6px solid transparent;
	border-right: 6px solid transparent;
}

span[data-arrow='south'] {
	top: -12px;
	left: calc(50% - 6px);
	border-bottom: 12px solid var(--main4);
	border-left: 6px solid transparent;
	border-right: 6px solid transparent;
}

span[data-arrow='west'] {
	left: 100%;
	top: calc(50% - 6px);
	border-left: 12px solid var(--main4);
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}

span[data-arrow='east']
{
	left: -12px;
	top: calc(50% - 6px);
	border-right: 12px solid var(--main4);
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}

span[data-arrow='northwest'] {
	top: 0;
	left: 100%;
	border-left: 12px solid var(--main4);
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}

span[data-arrow='northeast']
{
	top: 0;
	left: -12px;
	border-right: 12px solid var(--main4);
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}

span[data-arrow='southwest'] {
	bottom: 0;
	left: 100%;
	border-left: 12px solid var(--main4);
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}

span[data-arrow='southeast']
{
	bottom: 0;
	left: -12px;
	border-right: 12px solid var(--main4);
	border-top: 6px solid transparent;
	border-bottom: 6px solid transparent;
}</style>`;
/* global template */

import './mutation-events.js';
import anchor from './anchor.js';
import DataURL from './data-url.js';
import Formatter from './formatter.js';
import ResponseHandler from './response-handler.js';

let instance;
const GAP = 12;
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

		if (instance)
			instance.hide();
		instance = this;

		const controller = new AbortController();
		target.addEventListener("mouseleave", () => this.hide() || controller.abort(), {signal: controller.signal});
		target.addEventListener("focusout", () => this.hide() || controller.abort(), {signal: controller.signal});
		target.addEventListener("click", () => this.hide() || controller.abort(), {signal: controller.signal});

		this.style.display = "block";
		this.style.visibility = "hidden";
		const arrow = this.shadowRoot.querySelector("span");
		anchor(this, target, GAP, position, ...POSITIONS)
			.then(e => arrow.dataset.arrow = e)
			.finally(() => this.style.visibility = "visible");
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
		if (this.parentNode === document.body)
			return this.remove();

		this.style.visibility = "hidden";
		this.style.display = "";
	}

	static show(element, content, position)
	{
		let tooltip = new GTooltip();
		document.body.appendChild(tooltip);
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

	connectedCallback()
	{
		if (this.parentNode !== document.body)
		{
			this.#parent = this.parentNode;
			this.#parent.addEventListener("mouseenter", this.#trigger);
		}
	}

	disconnectedCallback()
	{
		if (this.#parent)
		{
			this.#parent.removeEventListener("mouseenter", this.#trigger);
			this.#parent = null;
		}
	}
}

customElements.define('g-tooltip', GTooltip);

function trigger()
{
	let timeout = setTimeout(() =>
	{
		if (this.hasAttribute("data-tooltip"))
			return GTooltip.show(this, this.getAttribute("data-tooltip"));
		if (this.hasAttribute("data-tooltip:source"))
			return fetch(this.getAttribute("data-tooltip:source"))
				.then(ResponseHandler.dataURL)
				.then(response => DataURL.parse(response))
				.then(dataURL => dataURL.contentType === "application/json" ? Formatter.JSONtoHTML(JSON.parse(dataURL.data)) : dataURL.data)
				.then(content => GTooltip.show(this, content))
				.catch(error => console.error('Error trying to fetch tooltip data:', error));
	}, 500);
	this.addEventListener("mouseleave", () => clearTimeout(timeout), {once: true});
}

window.addEventListener("connected", event => {
	let target = event.composedPath()[0] || event.target;
	if (target.hasAttribute("data-tooltip")
		|| target.hasAttribute("data-tooltip:source")
		|| Array.from(target.children).some(e => e.tagName === "G-TOOLTIP"))
		target.addEventListener("mouseenter", trigger);
});

window.addEventListener("disconnected", event => {
	let target = event.composedPath()[0] || event.target;
	target.removeEventListener("mouseenter", trigger);
});

window.addEventListener("attribute-created", event => {
	let attribute = event.attribute;
	let target = event.composedPath()[0] || event.target;
	if (attribute === "data-tooltip"
		|| attribute === "data-tooltip:source")
		target.addEventListener("mouseenter", trigger);
});

window.addEventListener("attribute-removed", event => {
	let attribute = event.attribute;
	let target = event.composedPath()[0] || event.target;
	if (attribute === "data-tooltip"
		|| attribute === "data-tooltip:source")
		if (!target.hasAttribute("data-tooltip")
			&& !target.hasAttribute("data-tooltip:source"))
			target.removeEventListener("mouseenter", trigger);
});