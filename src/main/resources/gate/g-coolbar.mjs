let template = document.createElement("template");
template.innerHTML = `
	<div id='container'>
	</div>
	<a id='more' href='#'>
		&#X3018;
	</a>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	display: flex;
	align-items: center;
}

#container > a,
#container > button,
#container > .g-command
{
	gap: 8px;
	width: auto;
	height: 44px;
	color: black;
	padding: 8px;
	color: black;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 16px;
	min-width: 140px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	background-color: #E8E8E8;
	justify-content: space-between;
}

#container > a:hover,
#container > button:hover,
#container > .g-command:hover
{
	background-color: #D0D0D0;
}

#container > a.primary,
#container > button.primary,
#container > .g-command.primary {
	color: white;
	border: none;
	background-color: #2A6B9A;
}

#container > a.primary:hover,
#container > button.primary:hover,
#container > .g-command.primary:hover {
	background-color: #25608A;
}

#container > a.warning,
#container > button.warning,
#container > .g-command.warning {
	color: black;
	border: none;
	background-color: #ffc107;
}

#container > a.warning:hover,
#container > button.warning:hover,
#container > .g-command.warning:hover {
	background-color: #dfa700;
}

#container > a.danger,
#container > button.danger,
#container > .g-command.danger {
	color: white;
	border: none;
	background-color: #b23d51;
}

#container > a.danger:hover,
#container > button.danger:hover,
#container > .g-command.danger:hover {
	background-color: #C6445A;
}

#container > a.success,
#container > button.success,
#container > .g-command.success {
	color: white;
	border: none;
	background-color: #009E60;
}

#container > a.success:hover,
#container > button.success:hover,
#container > .g-command.success:hover {
	background-color: #008E56;
}

#container > a.tertiary,
#container > button.tertiary,
#container > .g-command.tertiary {
	background-color: #FFFFFF;
	border: 1px solid #CCCCCC;
}

#container > a.tertiary:hover,
#container > button.tertiary:hover,
#container > .g-command.tertiary:hover {
	border: 1px solid black;
}

a.dark,
button.dark,
.g-command.dark {
	color: white;
	border: none;
	background-color: #000000;
}

#container > a.dark:hover,
#container > button.dark:hover,
#container > .g-command.dark:hover {
	background-color: #222222;
}

#container > a.info,
#container > button.info,
#container > .g-command.info {
	color: black;
	border: none;
	background-color: #0dcaf0;
}

#container > a.info:hover,
#container > button.info:hover,
#container > .g-command.info:hover {
	background-color: #0cbadd;
}

#container > a.link,
#container > button.link,
#container > .g-command.link {
	border: none;
	color: #1371fd;
	background-color: #FFFFFF;
}

#container > a.link:hover,
#container > button.link:hover,
#container > .g-command.link:hover {
	color: #025ee7;
}

#container > a[disabled],
#container > a[disabled]:hover,
#container > button[disabled],
#container > button[disabled]:hover,
#container > .g-command[disabled],
#container > .g-command[disabled]:hover {

	color: #AAAAAA;
	cursor: not-allowed;
	filter: opacity(40%);
	background-color: #CCCCCC;
}

#container > a:focus,
#container > button:focus,
#container > .g-command:focus{
	outline: 4px solid var(--hovered);
}

#container > *[hidden="true"]
{
	display: none;
}

#container > progress
{
	margin: 4px;
	flex-grow: 100000;
}

#container > hr
{
	flex-grow: 100000;
	border: none;
}

#more {
	order: 1;
	padding: 0;
	width: 32px;
	flex-grow: 0;
	height: 100%;
	outline: none;
	display: none;
	flex-shrink: 0;
	color: inherit;
	font-size: 20px;
	cursor: pointer;
	font-family: gate;
	margin-right: auto;
	align-items: center;
	text-decoration: none;
	justify-content: center;
}

#container {
	gap: 8px;
	order: 2;
	flex-grow: 1;
	display: flex;
	overflow: hidden;
	white-space: nowrap;
	align-items: stretch;
	flex-direction: row-reverse;
}

:host([reverse]) #more {
	order: 2;
}

:host([reverse]) #container {
	order: 1;
}

:host([reverse]) #container
{
	flex-direction: row;
}

#container i,
#container e,
#container span,
#container g-icon
{
	order: 1;
	speak: none;
	line-height: 1;
	color: inherit;
	cursor: inherit;
	font-style: normal;
	font-weight: normal;
	font-family: 'gate';
	font-variant: normal;
	text-transform: none;
}</style>`;

/* global customElements, template */

import './g-side-menu.mjs';
import Proxy from './proxy.mjs';
import GSelection from './selection.mjs';

customElements.define('g-coolbar', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.innerHTML = template.innerHTML;
		new ResizeObserver(() => this.update()).observe(this);
		this.shadowRoot.getElementById("container")
			.firstChild.addEventListener('slotchange', () => this.update());

		let more = this.shadowRoot.getElementById("more");
		let container = this.shadowRoot.getElementById("container");

		more.addEventListener("click", () =>
		{
			let elements = Array.from(container.children)
				.filter(e => e.tagName !== "HR")
				.filter(e => !e.getAttribute("hidden"))
				.filter(e => e.style.display === "none")
				.map(element => Proxy.create(element));
			elements.forEach(e => e.style.display = "");

			let menu = document.createElement("g-side-menu");
			document.documentElement.appendChild(menu);
			menu.elements = elements;
			menu.show(more);
		});
	}

	connectedCallback()
	{
		let container = this.shadowRoot.getElementById("container");
		Array.from(this.children).forEach(e => container.appendChild(e));
		this.update();
	}

	update()
	{
		let container = this.shadowRoot.getElementById("container");

		let selected = GSelection.getSelectedLink(container.children);
		if (selected)
			selected.setAttribute("aria-selected", "true");

		Array.from(container.children)
			.filter(e => !e.getAttribute("hidden"))
			.forEach(e => e.style.display = "");

		this.shadowRoot.getElementById("more")
			.style.display = this.overflowed ? "flex" : "none";

		for (let e = container.lastElementChild;
			e && this.overflowed; e = e.previousElementSibling)
			if (!e.hasAttribute("aria-selected")
				&& !e.getAttribute("hidden"))
				e.style.display = "none";
	}

	get overflowed()
	{
		let container = this.shadowRoot.getElementById("container");
		return container.scrollWidth > container.clientWidth
			|| container.scrollHeight > container.clientHeight;
	}
});

