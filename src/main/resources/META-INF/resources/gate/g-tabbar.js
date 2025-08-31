let template = document.createElement("template");
template.innerHTML = `
	<header>
		<slot></slot>
	</header>
	<a id="show" href='#'>
		<g-icon>&#x2265;</g-icon>
	</a>

	<div id='overlay'>
		<div id='more'>
			<slot name='sidemenu'>

			</slot>
		</div>
	</div>
 <style data-element="g-tabbar">* {
	box-sizing: border-box;
}

:host(*) {
	flex: 1;
	width: 100%;
	color: black;
	display: flex;
	position: relative;
	align-items: stretch;
	justify-content: stretch;
	background-color: var(--main3);
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
	background-color: var(--main4);
}

header ::slotted(a[aria-selected]),
header ::slotted(button[aria-selected]),
header ::slotted(.g-command[aria-selected]) {
	color: var(--base1);
	background-color: var(--main5);
}

header ::slotted(a:hover),
header ::slotted(button:hover),
header ::slotted(.g-command:hover) {
	background-color: var(--hovered);
}

header ::slotted(a:focus),
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
	content: "";
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-size: 50%;
	border-radius: inherit;
	background-color: #F0F0F0;
	background-position: center;
	background-repeat: no-repeat;
	background-image: var(--loading);
}

#show {
	margin: 8px;
	padding: 6px;
	display: flex;
	font-size: 2em;
	color: #000088;
	flex-basis: 60px;
	border-radius: 5px;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	background-color: var(--main4);
}

#show:hover {
	background-color: var(--hovered);
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
}

#overlay {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	opacity: 0;
	z-index: 999;
	position: fixed;
	visibility: hidden;
	pointer-events: none;
	background-color: rgba(0, 0, 0, 0.5);
	transition: opacity 0.3s ease, visibility 0.3s ease;
}

#overlay.active {
	opacity: 1;
	visibility: visible;
	pointer-events: auto;
}

#more  {
	margin: 0;
	padding: 0;
	width: auto;
	color: black;
	display: flex;
	z-index: 1000;
	overflow: auto;
	font-size: 14px;
	max-width: 50vw;
	max-height: 50vh;
	min-width: 220px;
	position: absolute;
	border-radius: 8px;
	align-items: stretch;
	flex-direction: column;
	background-color: #FFFFFF;
	box-shadow: 3px 10px 15px rgba(0, 0, 0, 0.1);
}

#more ::slotted(:not(hr))
{
	gap: 12px;
	display: flex;
	color: inherit;
	flex-basis: 16px;
	border-radius: 4px;
	padding: 10px 16px;
	font-size: inherit;
	align-items: center;
	white-space: nowrap;
	text-decoration: none;
}

#more ::slotted(hr)
{
	border: none;
	flex-basis: 16px;
}

#more ::slotted(:not(hr):hover)
{
	background-color: var(--hovered, #FFFACD);
	padding-left: 20px;
}</style>`;
/* global customElements */

import anchor from './anchor.js';
import loading from './loading.js';
import TriggerExtractor from './trigger-extractor.js';

const sheet = new CSSStyleSheet();
sheet.replaceSync(`g-tabbar g-icon { order: -1 }`);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

const POSITIONS = ["northeast", "southeast", "northwest", "southwest"];

customElements.define("g-tabbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
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


		const show = this.shadowRoot.getElementById("show");
		const more = this.shadowRoot.getElementById("more");
		const overlay = this.shadowRoot.getElementById("overlay");

		show.addEventListener("click", event =>
		{
			const x = event.clientX;
			const y = event.clientY;
			const target = {getBoundingClientRect: () => ({x, y, left: x, top: y, right: x, bottom: y, width: 0, height: 0})};
			anchor(more, target, 0, ...POSITIONS).then(e =>
			{
				overlay.classList.add("active");
				more.style.top = `${e.location.y}px`;
				more.style.left = `${e.location.x}px`;
			});
		});

		overlay.addEventListener("click", () => overlay.classList.remove('active'));
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
			child.setAttribute('slot', 'sidemenu');

		this.shadowRoot.getElementById("show").style.display =
			children.some(e => e.hasAttribute("slot")) ? "flex" : "none";
	}
});