let template = document.createElement("template");
template.innerHTML = `
	<header><slot></slot></header><g-trigger id="show"><g-icon>&#x2265;</g-icon><g-context-menu><slot name='more'></slot></g-context-menu></g-trigger>
<style data-element="g-tabbar">* {
	box-sizing: border-box;
}

:host(*) {
	flex: 1;
	width: 100%;
	display: flex;
	position: relative;
	align-items: stretch;
	justify-content: stretch;
	color: var(--text1, #000000);
	background-color: var(--main2, #F0F0F0);
}

header {
	flex: 1;
	gap: 8px;
	padding: 8px;
	display: flex;
	overflow-x: hidden;
	white-space: nowrap;
}

header ::slotted(a),
header ::slotted(button),
header ::slotted(g-trigger),
header ::slotted(.g-command) {
	gap: 4px;
	padding: 6px;
	display: flex;
	color: inherit;
	flex-shrink: 0;
	cursor: pointer;
	flex-basis: 120px;
	border-radius: 5px;
	font-size: inherit;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	justify-content: space-around;
	background-color: var(--main3, #DDDDDD);
}

header ::slotted(a[aria-selected]),
header ::slotted(button[aria-selected]),
header ::slotted(.g-command[aria-selected]) {
	color: var(--base1, #2f5674);
	background-color: var(--main3, #DDDDDD);
}

header ::slotted(a:hover),
header ::slotted(g-trigger:hover),
header ::slotted(button:hover),
header ::slotted(.g-command:hover) {
	background-color: var(--hovered, #FFFACD);
}

header ::slotted(a:focus),
header ::slotted(g-trigger:focus),
header ::slotted(button:focus),
header ::slotted(.g-command:focus) {
	outline: none;
}

header ::slotted([hidden="true"]) {
	display: none;
}

header ::slotted(hr) {
	border: none;
	flex-grow: 100000;
}

header ::slotted(:is(a, button, .g-command)[data-loading]) {
	position: relative;
}

header ::slotted(:is(a, button, .g-command)[data-loading])::before {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	content: "";
	position: absolute;
	background-size: 50%;
	border-radius: inherit;
	background-position: center;
	background-repeat: no-repeat;
	background-image: var(--loading);
	background-color: var(--main2, #F0F0F0);
}

#show {
	margin: 8px;
	display: none;
	font-size: 2em;
	cursor: pointer;
	color: #000088;
	flex-basis: 60px;
	border-radius: 5px;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	background-color: var(--main2, #F0F0F0);
}

#show:hover {
	background-color: var(--hovered, #FFFACD);
}

:host(.inline) #show {
	font-size: 1em;
}

:host(.inline) header ::slotted(a),
:host(.inline) header ::slotted(button),
:host(.inline) header ::slotted(.g-command) {
	flex-basis: 160px;
	flex-direction: row;
	justify-content: flex-start;
}

:host(.inline) header ::slotted(a)::before,
:host(.inline) header ::slotted(button)::before,
:host(.inline) header ::slotted(.g-command)::before {
	align-items: center;
	justify-content: flex-start;
}

:host(.inline) header ::slotted(a)::after,
:host(.inline) header ::slotted(button)::after,
:host(.inline) header ::slotted(.g-command)::after {
	left: 32px;
	max-width: calc(100% - 40px);
}</style>`;
/* global customElements */

import './g-context-menu.js';
import loading from './loading.js';
import TriggerExtractor from './trigger-extractor.js';

const sheet = new CSSStyleSheet();
sheet.replaceSync(`g-tabbar g-icon { order: -1 }`);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

const POSITIONS = ["southwest", "southeast", "northwest", "northeast"];

customElements.define("g-tabbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({ mode: 'open' });
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		let div = this.shadowRoot.querySelector("div");
		window.addEventListener("trigger-success", event =>
		{
			const element = event.detail.cause.target;
			const method = TriggerExtractor.method(element);
			const action = TriggerExtractor.action(element);
			const target = TriggerExtractor.target(element);

			const trigger = Array.from(this.children)
				.find(e => e === element ||
					(TriggerExtractor.method(e) === method
						&& TriggerExtractor.action(e) === action
						&& TriggerExtractor.target(e) === target));
			if (trigger)
				this.#select(trigger);
		});


		new ResizeObserver(() => this.#update()).observe(this);
	}

	connectedCallback()
	{
		this.#update();

		loading(this.parentNode);

		let action = window.location.href;
		let origin = window.location.origin;
		let triggers = Array.from(this.children).filter(e => TriggerExtractor.target(e) === "_self");
		let selected = triggers.filter(e => action === new URL(TriggerExtractor.action(e), origin).href)[0]
			|| triggers.filter(e => action.startsWith(TriggerExtractor.action(e)))[0];
		if (selected)
			this.#select(selected);

	}

	#select(element)
	{
		Array.from(this.children).forEach(e => e.removeAttribute("aria-selected"));
		element.setAttribute("aria-selected", "");
	}

	#update()
	{
		const children = Array.from(this.children);
		const header = this.shadowRoot.querySelector('header');

		children.forEach(item => item.removeAttribute('slot'));

		for (let child = this.lastElementChild;
			child && header.scrollWidth > header.clientWidth;
			child = child.previousElementSibling)
			child.setAttribute('slot', 'more');

		this.shadowRoot.getElementById("show").style.display =
			children.some(e => e.hasAttribute("slot")) ? "flex" : "none";
	}
});