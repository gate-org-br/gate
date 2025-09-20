let template = document.createElement("template");
template.innerHTML = `
	<a id="show" href='#'>
		<g-icon>&#x3018;</g-icon>
	</a>
	<div>
		<slot></slot>
	</div>
	<g-context-menu>
		<slot name='more'>

		</slot>
	</g-context-menu>
 <style data-element="g-coolbar">*
{
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	height: auto;
	color: var(--text, black);
	border: none;
	display: flex;
	position: relative;
	align-items: stretch;
	justify-content: stretch;
}

div
{
	gap: 8px;
	padding: 8px;
	flex-grow: 1;
	display: flex;
	overflow-x: hidden;
	white-space: nowrap;
	flex-direction: row-reverse;
}

div > ::slotted(:is(a, button, .g-command))
{
	gap: 8px;
	width: 120px;
	height: 44px;
	padding: 8px;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	min-width: fit-content;
	justify-content: space-between;

	color: var(--text, black);
	background-color: var(--main5, #FAFAFA);
}

div > ::slotted(a:focus),
div > ::slotted(button:focus),
div > ::slotted(.g-command:focus)
{
	outline: 4px solid var(--hovered);
}

div > ::slotted(a.primary),
div > ::slotted(button.primary),
div > ::slotted(.g-command.primary)
{
	color: white;
	border: none;
	background-color: #2A6B9A;
}

div > ::slotted(a.primary:hover),
div > ::slotted(button.primary:hover),
div > ::slotted(.g-command.primary:hover)
{
	background-color: #25608A;
}

div > ::slotted(a.alternative),
div > ::slotted(button.alternative),
div > ::slotted(.g-command.alternative)
{
	color: white;
	border: none;
	background-color: #009E60;
}

div > ::slotted(a.alternative:hover),
div > ::slotted(button.alternative:hover),
div > ::slotted(.g-command.alternative:hover)
{
	background-color: #008E56;
}

div > ::slotted(a.tertiary),
div > ::slotted(button.tertiary),
div > ::slotted(.g-command.tertiary)
{
	background-color: var(--main1);
	border: 1px solid var(--main6);
}

div > ::slotted(a.tertiary:hover),
div > ::slotted(button.tertiary:hover),
div > ::slotted(.g-command.tertiary:hover)
{
	border: 1px solid black;
}

div > ::slotted(a.danger),
div > ::slotted(button.danger),
div > ::slotted(.g-command.danger)
{
	color: white;
	border: none;
	background-color: #AA2222;
}

div > ::slotted(a.danger:hover),
div > ::slotted(button.danger:hover),
div > ::slotted(.g-command.danger:hover)
{
	background-color: #882222;
}

div > ::slotted([hidden="true"])
{
	display: none;
}

div > ::slotted(hr)
{
	border: none;
	flex-grow: 100000;
}

:host([reverse]) div
{
	flex-direction: row;
}

:host([disabled])
{
	background-color: var(--main6);
}

:host([disabled]) div,
:host([disabled]) button
{
	display: none;
}

:host([disabled])::before
{
	content: "";
	height: 44px;
	grid-column: 2;
	animation-fill-mode: both;
	background-color: var(--base1);
	animation: progress 2s infinite ease-in-out;
}

@keyframes progress
{
	0%
	{
		width: 0;
	}

	100%
	{
		width: 100%;
	}
}

::slotted(:is(a, button, .g-command)[data-loading])
{
	position: relative;
}

::slotted(:is(a, button, .g-command)[data-loading])::before
{
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	content: "";
	color: inherit;
	position: absolute;
	background-size: 50%;
	border-radius: inherit;
	background-color: inherit;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}

#show {
	padding: 0px;
	display: none;
	margin: 8px 0;
	font-size: 2em;
	color: #0000AA;
	flex-basis: 16px;
	border-radius: 5px;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	background-color: transparent;
}

#show:hover {
	background-color: var(--hovered);
}

g-more-menu > ::slotted(a.primary),
g-more-menu > ::slotted(button.primary),
g-more-menu > ::slotted(.g-command.primary)
{
	color: #000088;
}

g-more-menu > ::slotted(a.alternative),
g-more-menu > ::slotted(button.alternative),
g-more-menu > ::slotted(.g-command.alternative)
{
	color: #008800;
}

g-more-menu > ::slotted(a.danger),
g-more-menu > ::slotted(button.danger),
g-more-menu > ::slotted(.g-command.danger)
{
	color: #880000;
}

g-more-menu > ::slotted(a.tertiary),
g-more-menu > ::slotted(button.tertiary),
g-more-menu > ::slotted(.g-command.tertiary)
{
	color: #444444;
}</style>`;
/* global customElements */

import './g-context-menu.js';
import loading from './loading.js';
import WindowListenerHTMLElement from './window-listener-html-element.js';
const POSITIONS = ["southwest", "southeast", "northwest", "northeast"];

const sheet = new CSSStyleSheet();
sheet.replaceSync(`g-coolbar *[slot='more'] g-icon { order: -1 }`);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

export default class GCoolbar extends WindowListenerHTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({ mode: 'open' });
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		new ResizeObserver(() => this.#update()).observe(this);


		const show = this.shadowRoot.getElementById("show");
		const more = this.shadowRoot.querySelector("g-context-menu");
		show.addEventListener("click", event => more.show({ x: event.clientX, y: event.clientY }));
	}

	get disabled()
	{
		return this.hasAttribute("disabled");
	}

	set disabled(value)
	{
		if (value)
			this.setAttribute("disabled", "");
		else
			this.removeAttribute("disabled");
	}

	connectedCallback()
	{
		super.connectedCallback();
		loading(this.parentNode);
		this.setAttribute("size", this.children.length);
	}

	#update()
	{
		const children = Array.from(this.children);
		const div = this.shadowRoot.querySelector('div');

		children.forEach(item => item.removeAttribute('slot'));

		for (let child = this.lastElementChild;
			child && div.scrollWidth > div.clientWidth;
			child = child.previousElementSibling)
			child.setAttribute('slot', 'more');

		this.shadowRoot.getElementById("show").style.display =
			children.some(e => e.hasAttribute("slot")) ? "flex" : "none";
	}
}

customElements.define("g-coolbar", GCoolbar);