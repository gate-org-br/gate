let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-drawer">* {
	box-sizing: border-box;
}

:host {
	top: 0;
	right: 0;
	margin: 0;
	opacity: 0;
	border: none;
	display: flex;
	height: 100vh;
	z-index: 1000;
	font-size: 14px;
	border-radius: 0;
	pointer-events: none;
	flex-direction: column;
	width: min(320px, 80vw);
	color: var(--text1, #000000);
	transform: translateX(-100%);
	transition: transform 0.3s ease-in-out;
	background-color: var(--main1, #FFFFFF);
	box-shadow: -4px 0 12px rgba(0, 0, 0, 0.15);
}

:host(:popover-open) {
	opacity: 1;
	pointer-events: auto;
	transform: translateX(0);
}

::slotted(a),
::slotted(button),
::slotted(g-trigger),
::slotted(.g-command) {
	gap: 12px;
	border: none;
	display: flex;
	color: inherit;
	cursor: pointer;
	border-radius: 6px;
	font-size: inherit;
	padding: 12px 18px;
	align-items: center;
	white-space: nowrap;
	text-decoration: none;
	transition: background-color 0.2s ease, padding 0.2s ease;
	background-color: transparent;
	justify-content: flex-start;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(g-trigger:hover),
::slotted(.g-command:hover) {
	background-color: var(--hovered, #f5f5f5);
	padding-left: 24px;
}

::slotted(a:focus),
::slotted(label:focus),
::slotted(button:focus),
::slotted(g-trigger:focus),
::slotted(.g-command:focus) {
	outline: none;
	box-shadow: inset 0 0 0 2px #0078d4;
}

::slotted(a[data-icon])::before,
::slotted(button[data-icon])::before,
::slotted(g-trigger[data-icon])::before,
::slotted(.g-command[data-icon])::before {
	font-family: gate;
	content: attr(data-icon);
}

g-trigger {
	flex-grow: 1;
	font-weight: 600;
}

::slotted(g-trigger)::after {
	color: #888;
	font-size: 12px;
	content: '\\3017';
	font-family: gate;
	margin-left: auto;
}</style>`;
import GTrigger from './g-trigger.js';

const sheet = new CSSStyleSheet()
sheet.replaceSync(`g-drawer g-icon { order: -1; font-size: 20px }`)
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet]

export default class GDrawer extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		const click = event =>
		{
			if (!event.composedPath().includes(this))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
			}
			this.hide();
		};

		this.addEventListener("toggle", e =>
		{
			if (e.newState === "open")
				window.addEventListener("click",
					click, {once: true, capture: true});
			else
				window.removeEventListener("click", click, {capture: true});
		});
	}

	show()
	{
		this.showPopover();
	}

	hide()
	{
		this.hidePopover();
	}

	connectedCallback()
	{
		this.setAttribute("popover", "manual");
	}
}

customElements.define('g-drawer', GDrawer);