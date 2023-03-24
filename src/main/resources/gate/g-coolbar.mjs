let template = document.createElement("template");
template.innerHTML = `
	<div id='container'>
		<slot>
		</slot>
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

::slotted(*)
{
	height: 44px;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
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

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color: #D0D0D0;
}

::slotted(a.primary),
::slotted(button.primary),
::slotted(.g-command.primary) {
	color: white;
	border: none;
	background-color: #2A6B9A;
}

::slotted(a.primary:hover),
::slotted(button.primary:hover),
::slotted(.g-command.primary:hover) {
	background-color: #25608A;
}

::slotted(a.warning),
::slotted(button.warning),
::slotted(.g-command.warning) {
	color: black;
	border: none;
	background-color: #ffc107;
}

::slotted(a.warning:hover),
::slotted(button.warning:hover),
::slotted(.g-command.warning:hover) {
	background-color: #dfa700;
}

::slotted(a.danger),
::slotted(button.danger),
::slotted(.g-command.danger) {
	color: white;
	border: none;
	background-color: #AA2222;
}

::slotted(a.danger:hover),
::slotted(button.danger:hover),
::slotted(.g-command.danger:hover) {
	background-color: #882222;
}

::slotted(a.success),
::slotted(button.success),
::slotted(.g-command.success) {
	color: white;
	border: none;
	background-color: #009E60;
}

::slotted(a.success:hover),
::slotted(button.success:hover),
::slotted(.g-command.success:hover) {
	background-color: #008E56;
}

::slotted(a.tertiary),
::slotted(button.tertiary),
::slotted(.g-command.tertiary) {
	background-color: #FFFFFF;
	border: 1px solid #CCCCCC;
}

::slotted(a.tertiary:hover),
::slotted(button.tertiary:hover),
::slotted(.g-command.tertiary:hover) {
	border: 1px solid black;
}

::slotted(a.dark),
::slotted(button.dark),
::slotted(.g-command.dark) {
	color: white;
	border: none;
	background-color: #000000;
}

::slotted(a.dark:hover),
::slotted(button.dark:hover),
::slotted(.g-command.dark:hover) {
	background-color: #222222;
}

::slotted(a.info),
::slotted(button.info),
::slotted(.g-command.info) {
	color: black;
	border: none;
	background-color: #0dcaf0;
}

::slotted(a.info:hover),
::slotted(button.info:hover),
::slotted(.g-command.info:hover) {
	background-color: #0cbadd;
}

::slotted(a.link),
::slotted(button.link),
::slotted(.g-command.link) {
	border: none;
	color: #1371fd;
	background-color: #FFFFFF;
}

::slotted(a.link:hover),
::slotted(button.link:hover),
::slotted(.g-command.link:hover) {
	color: #025ee7;
}

::slotted(a[disabled]),
::slotted(a[disabled]:hover),
::slotted(button[disabled]),
::slotted(button[disabled]:hover),
::slotted(.g-command[disabled]:hover),
::slotted(.g-command[disabled]:hover) {

	color: #AAAAAA;
	cursor: not-allowed;
	filter: opacity(40%);
	background-color: #CCCCCC;
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus){
	outline: 4px solid var(--hovered);
}

::slotted(*[hidden="true"])
{
	display: none;
}

::slotted(progress)
{
	margin: 4px;
	flex-grow: 100000;
}

::slotted(hr)
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

</style>`;

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

		more.addEventListener("click", () =>
		{
			let elements = Array.from(this.children)
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
		setTimeout(() => this.update(), 200);
	}

	update()
	{
		let selected = GSelection.getSelectedLink(this.children);
		if (selected)
			selected.setAttribute("aria-selected", "true");

		Array.from(this.children)
			.filter(e => !e.getAttribute("hidden"))
			.forEach(e => e.style.display = "");

		this.shadowRoot.getElementById("more")
			.style.display = this.overflowed ? "flex" : "none";

		for (let e = this.lastElementChild;
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

