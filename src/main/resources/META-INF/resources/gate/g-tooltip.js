let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
	<div></div>
 <style>* {
	box-sizing: border-box;
}

:host(*) {
	margin: 0;
	padding: 8px;
	display: none;
	z-index: 1000000;
	border-radius: 3px;
	width: min-content;
	height: min-content;
	position: absolute;
	visibility: hidden;
	flex-direction: column;
	background-color: var(--main4);
	border: 1px solid var(--main6);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

div {
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

div[data-arrow='north'] {
	top: 100%;
	left: calc(50% - 10px);
	transform: rotate(180deg);
}

div[data-arrow='south'] {
	top: -20px;
	left: calc(50% - 10px);
}

div[data-arrow='west'] {
	left: 100%;
	top: calc(50% - 10px);
	transform: rotate(90deg);
}

div[data-arrow='east'] {
	left: -20px;
	top: calc(50% - 10px);
	transform: rotate(270deg);
}

div[data-arrow='northeast'] {
	top: 100%;
	left: -20px;
	transform: rotate(225deg);
}

div[data-arrow='northwest'] {
	top: 100%;
	left: 100%;
	transform: rotate(135deg);
}
div[data-arrow='southeast'] {
	top: -20px;
	left: -20px;
	transform: rotate(-45deg);
}

div[data-arrow='southwest'] {
	left: 100%;
	top: -20px;
	transform: rotate(45deg);
}</style>`;

/* global template */

import Formatter from './formatter.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

let instance;
const POSITIONS = ["northeast", "southwest", "northwest", "southeast", "north", "east", "south", "west"];

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
		this.setAttribute("position", "northeast");
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

		this.shadowRoot.querySelector("div")
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

	static show(element, content, position = "north")
	{
		let tooltip = new GTooltip();
		document.body.appendChild(tooltip);
		tooltip.innerHTML = Formatter.JSONtoHTML(content);
		tooltip.show(element, position);
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

	event.target.dispatchEvent(new TriggerStartupEvent(event));

	let path = event.composedPath();
	return fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.response)
		.then(response => response.headers.get('content-type').startsWith("application/json")
				? response.json().then(json => Formatter.JSONtoHTML(json))
				: response.text())
		.then(content => GTooltip.show(event.target, content, position))
		.then(() => event.target.addEventListener("mouseleave", () => GTooltip.hide()))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});

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
					target.addEventListener("mouseleave", () => tooltip.hide(), {once: true});
					tooltip.show(target);
					return;
				}
			}
		}
	}
});