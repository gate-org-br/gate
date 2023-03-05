let template = document.createElement("template");
template.innerHTML = `
	<div id='container'>
		<slot>
		</slot>
	</div>
	<a id='more' href='#'>
		&#X3018;
	</a>
 <style>:host(*)
{
	display: grid;
	grid-template-columns: 1fr auto;
}

#container {
	flex-grow: 1;
	display: flex;
	overflow: hidden;
	white-space: nowrap;
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

:host([reverse]) #container
{
	flex-direction: row;
}

:host([reverse])
{
	grid-template-columns: auto 1fr;
}

:host([reverse]) #more
{
	order: -1;
}
</style>`;

/* global customElements, template */

import './g-side-menu.mjs';
import Proxy from './proxy.mjs';
import GSelection from './selection.mjs';

export default class GOverflow extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.innerHTML = template.innerHTML;
		this.more.addEventListener("click", () => this.menu());
		new ResizeObserver(() => this.update()).observe(this);
		this.container.firstChild.addEventListener('slotchange', () => this.update());
	}

	get container()
	{
		return this.shadowRoot.getElementById("container");
	}

	get more()
	{
		return this.shadowRoot.getElementById("more");
	}

	connectedCallback()
	{
		setTimeout(() => this.update(), 200);
	}

	menu()
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
		menu.show(this.more);
	}

	update()
	{
		let selected = GSelection.getSelectedLink(this.children);
		if (selected)
			selected.setAttribute("aria-selected", "true");

		Array.from(this.children)
			.filter(e => !e.getAttribute("hidden"))
			.forEach(e => e.style.display = "");

		this.more.style.display = this.overflowed ? "flex" : "none";

		for (let e = this.lastElementChild;
			e && this.overflowed; e = e.previousElementSibling)
			if (!e.hasAttribute("aria-selected")
				&& !e.getAttribute("hidden"))
				e.style.display = "none";
	}

	get overflowed()
	{
		return GOverflow.isOverflowed(this.container);
	}

	static isOverflowed(element)
	{
		return element.scrollWidth > element.clientWidth
			|| element.scrollHeight > element.clientHeight;
	}
}

customElements.define('g-overflow', GOverflow);