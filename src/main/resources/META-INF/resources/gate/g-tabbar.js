let template = document.createElement("template");
template.innerHTML = `
	<div id='container'>
		<slot></slot>
	</div>
	<a id='more' href='#'>
		&#X3018;
	</a>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	color: black;
	border: none;
	height: auto;
	flex-grow: 1;
	display: flex;
	font-size: 16px;
	align-items: center;
	justify-content: flex-start;
	background-color: var(--main3);
}

#container
{
	gap: 8px;
	padding: 8px;
	flex-grow: 1;
	display: flex;
	overflow: hidden;
	white-space: nowrap;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	gap: 4px;
	padding: 6px;
	height: auto;
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

:host(.inline) ::slotted(a),
:host(.inline) ::slotted(button),
:host(.inline) ::slotted(.g-command)
{
	flex-basis: 160px;
	flex-direction: row;
	justify-content: flex-start;
}

::slotted(a[aria-selected]),
::slotted(button[aria-selected]),
::slotted(.g-command[aria-selected])
{
	color: var(--base1);
	background-color: var(--main5);
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color:  var(--hovered);
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: none
}

::slotted([hidden="true"])
{
	display: none;
}

::slotted(hr)
{
	border: none;
	flex-grow: 100000;
}

#more {
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
}</style>`;

/* global customElements, template */

import './g-side-menu.js';
import Proxy from './proxy.js';
import GSelection from './selection.js';

customElements.define('g-tabbar', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.innerHTML = template.innerHTML;
		new ResizeObserver(() => this.update()).observe(this);

		this.shadowRoot.getElementById("more").addEventListener("click", () =>
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
			menu.show(this.shadowRoot.getElementById("more"));
		});

		this.addEventListener("trigger-resolve", event =>
			this.selected = event.composedPath()[0] || event.target);
	}

	set selected(element)
	{
		Array.from(this.children).forEach(e => e.removeAttribute("aria-selected"));
		if (element)
			element.setAttribute("aria-selected", "true");
	}

	connectedCallback()
	{
		Array.from(this.children).flatMap(e => Array.from(e.children))
			.filter(e => e.tagName)
			.forEach(e => e.parentNode.insertBefore(e, e.parentNode.firstChild));
		GSelection.getSelectedLink(this.children).ifPresent(e => this.selected = e);
	}

	update()
	{
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