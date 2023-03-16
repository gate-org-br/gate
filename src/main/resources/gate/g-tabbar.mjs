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
	color: black;
	border: none;
	height: auto;
	flex-grow: 1;
	display: flex;
	align-items: center;
	background-color: #F8F8F8;
	justify-content: flex-start;
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

#container a,
#container button,
#container .g-command
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
	font-size: 0.75rem;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	background-color: #F4F4F4;
	justify-content: space-around;
}

:host(.inline) #container a,
:host(.inline) #container button,
:host(.inline) #container .g-command
{
	flex-basis: 160px;
	flex-direction: row;
	justify-content: flex-start;
}

#container a[aria-selected],
#container button[aria-selected],
#container .g-command[aria-selected]
{
	background-color: #E6E6E6;
}

#container a:hover,
#container button:hover,
#container .g-command:hover
{
	background-color:  #FFFACD;
}

#container a:focus,
#container button:focus,
#container .g-command:focus
{
	outline: none
}

#container *[hidden="true"]
{
	display: none;
}

#container hr
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
}

i, span, g-icon {
	order: -1;
	display: flex;
	color: inherit;
	cursor: inherit;
	font-style: normal;
	font-size: 1.25rem;
	font-family: 'gate';
	align-items: center;
	justify-content: center;
}

:host(.inline) i,
:host(.inline) span,
:host(.inline) g-icon
{
	font-size: 1.0rem;
}</style>`;

/* global customElements, template */

import './g-side-menu.mjs';
import Proxy from './proxy.mjs';
import GSelection from './selection.mjs';

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
			let container = this.shadowRoot.getElementById("container");
			let elements = Array.from(container.children)
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