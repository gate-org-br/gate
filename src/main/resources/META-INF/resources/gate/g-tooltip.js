let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
 <style>* {
	box-sizing: border-box;
}

:host(*) {
	margin: 0;
	padding: 8px;
	display: none;
	z-index: 1000000;
	border-radius: 3px;
	width: fit-content;
	height: fit-content;
	position: absolute;
	visibility: hidden;
	flex-direction: column;
	background-color: var(--main4);
	border: 1px solid var(--main6);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

:host(*)::before {
	width: 20px;
	content: "";
	color: black;
	height: 20px;
	display: flex;
	position: absolute;
	align-items: center;
	background-size: 100%;
	justify-content: center;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: url(data:image/svg+xml;base64,PHN2ZwogICB3aWR0aD0iMzIiCiAgIGhlaWdodD0iMzIiCiAgIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIKICAgeG1sbnM6c3ZnPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CiAgPHBhdGgKICAgICBkPSJNIDguOTgxNzI4OCwzMiBIIDIzLjAxODI3MiBjIDAuNzY3NTQ3LDAgMS4zODc5NywtMC42MjA0MjIgMS4zODc5NywtMS4zODc5NyBWIDE3LjAwMjk4MiBoIDUuODIxMTQ3IGMgMC41NjA3NCwwIDEuMDY4NzM3LC0wLjMzNzI3NyAxLjI4MjQ4NCwtMC44NTYzNzggMC4yMTUxMzYsLTAuNTE5MSAwLjA5NTc3LC0xLjExNTkyOCAtMC4zMDExODksLTEuNTEyODg3IEwgMTYuOTgxOTg5LDAuNDA3MDIyNCBjIC0wLjU0MjY5NiwtMC41NDI2OTY0IC0xLjQxOTg5MywtMC41NDI2OTY0IC0xLjk2MjU5LDAgTCAwLjc5MTMxNjQ2LDE0LjYzNTEwNSBjIC0wLjM5Njk1OTUsMC4zOTY5NTkgLTAuNTE2MzI0OSwwLjk5Mzc4NyAtMC4zMDExODk1LDEuNTEyODg3IDAuMjEzNzQ3NCwwLjUxOTEwMSAwLjcyMTc0NDU0LDAuODU2Mzc4IDEuMjgyNDg0NDQsMC44NTYzNzggaCA1LjgyMTE0NzQgdiAxMy42MDkwNDggYyAwLDAuNzY2MTYgMC42MjE4MSwxLjM4NjU4MiAxLjM4Nzk3LDEuMzg2NTgyIHoiCiAgICAgc3R5bGU9InN0cm9rZS13aWR0aDoxLjM4Nzk3IiAvPgo8L3N2Zz4K);
}

:host([position='north']) {
	left: 50%;
	transform: translateY(calc(-100% - 22px)) translateX(-50%);
}

:host([position='north'])::before {
	top: 100%;
	left: calc(50% - 10px);
	transform: rotate(180deg);
}

:host([position='south']) {
	left: 50%;
	top: calc(100% + 22px);
	transform: translateX(-50%);
}

:host([position='south'])::before {
	top: -20px;
	left: calc(50% - 10px);
}

:host([position='west']) {
	left: 0;
	top: 50%;
	transform: translateX(calc(-100% - 22px)) translateY(-50%);
}

:host([position='west'])::before {
	left: 100%;
	top: calc(50% - 10px);
	transform: rotate(90deg);
}

:host([position='east']) {
	top: 50%;
	left: calc(100% + 22px);
	transform:  translateY(-50%);
}

:host([position='east'])::before {
	left: -20px;
	top: calc(50% - 10px);
	transform: rotate(270deg);
}

:host([position='northeast']) {
	top: 0;
	left: calc(100% + 22px);
	transform:  translateY(calc(-100% - 20px));
}

:host([position='northeast'])::before {
	top: 100%;
	left: -20px;
	transform: rotate(225deg);
}

:host([position='northwest']) {
	top: 0;
	left: 0;
	transform:  translateX(calc(-100% - 20px)) translateY(calc(-100% - 20px));
}

:host([position='northwest'])::before {
	top: 100%;
	left: 100%;
	transform: rotate(135deg);
}

:host([position='southeast']) {
	left: 0;
	top: calc(100% + 20px);
	transform:  translateX(calc(-100% - 20px));
}

:host([position='southeast'])::before {
	top: -20px;
	left: 100%;
	transform: rotate(45deg);
}

:host([position='southwest']) {
	top: calc(100% + 20px);
	left: calc(100% + 20px);
}

:host([position='southwest'])::before {
	left: -20px;
	top: -20px;
	transform: rotate(315deg);
}
</style>`;

/* global template */

import JSONtoHTML from './json-to-html.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

let instance;
const POSITIONS = ["northeast", "southwest", "northwest", "southeast", "north", "east", "south", "west"];

function isVisible(rect)
{
	return rect.top >= 0
		&& rect.left >= 0
		&& rect.top + rect.height <= window.innerHeight
		&& rect.left + rect.width <= window.innerWidth;
}


export default class GTooltip extends HTMLElement
{

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.setAttribute("position", "northeast");
	}

	show(position)
	{
		if (instance)
			instance.hide();

		instance = this;

		if (!this.parentNode)
			throw new Error("Attempt to show disconnected tooltip");

		if (position)
			this.position = position;

		this.parentNode.style.position = "relative";

		let i = 0;
		this.style.display = "flex";
		while (!isVisible(this.getBoundingClientRect()) && i++ < 8)
			this.position = POSITIONS[(POSITIONS.indexOf(this.position) + 1) % 8];
		this.style.visibility = "visible";
	}

	get position()
	{
		return this.getAttribute("position");

	}

	set position(value)
	{
		this.setAttribute("position", value);
	}

	hide()
	{
		this.style.visibility = "hidden";
		this.style.display = "";
	}

	static show(element, content, position = "north")
	{
		let tooltip = new GTooltip();
		element.appendChild(tooltip);
		tooltip.innerHTML = JSONtoHTML(content);
		tooltip.show(position);
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


window.addEventListener("@tooltip", function (event)
{
	event.preventDefault();
	event.stopPropagation();

	let position = event.detail.parameters[0];

	let target = event.target;
	if (target.getRootNode() === document)
		for (let current = event.target; current !== document; current = current.parentNode)
			if (current.shadowRoot)
				target = current = current.shadowRoot.host;

	event.target.dispatchEvent(new TriggerStartupEvent(event));

	return fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.response)
		.then(response => response.headers.get('content-type').startsWith("application/json")
				? response.json().then(json => JSONtoHTML(json))
				: response.text())
		.then(content => GTooltip.show(target, content, position))
		.then(() => event.target.addEventListener("mouseleave", () => GTooltip.hide()))
		.then(() => setTimeout(() => event.target.dispatchEvent(new TriggerSuccessEvent(event)), 0))
		.catch(error => setTimeout(event.target.dispatchEvent(new TriggerFailureEvent(event, error)), 0))
		.finally(setTimeout(() => event.target.dispatchEvent(new TriggerResolveEvent(event)), 0));
});

window.addEventListener("mouseover", function (event)
{
	for (let target of [...event.composedPath(), event.target])
	{
		if (target.hasAttribute && target.hasAttribute("data-tooltip"))
		{
			let content = JSON.parse(target.getAttribute("data-tooltip"));
			target.addEventListener("mouseleave", () => GTooltip.hide(), {once: true});
			let position = target.getAttribute("data-tooltip-position");
			GTooltip.show(target, content, position);
			return;
		} else if (target.children)
		{
			for (let tooltip of target.children)
			{
				if (tooltip.tagName === "G-TOOLTIP")
				{
					target.addEventListener("mouseleave", () => tooltip.hide(), {once: true});
					tooltip.show();
					return;
				}
			}
		}
	}
});