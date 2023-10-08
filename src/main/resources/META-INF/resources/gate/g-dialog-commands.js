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
	height: 24px;
	color: white;
	outline: none;
	display: flex;
	overflow: hidden;
	border-radius: 5px;
	font-size: inherit;
	align-items: center;
	justify-content: flex-start;
	flex-direction: row-reverse;
}

:host(:empty) {
	display: none
}

::slotted(a),
::slotted(button)
{
	padding: 0;
	width: 24px;
	font-size: 0;
	height: 24px;
	display: flex;
	color: inherit;
	cursor: pointer;
	align-items: center;
	justify-content: center;
}

::slotted(a:hover),
::slotted(button:hover) {
	color: var(--hovered);
}

::slotted(a[hidden="true"]),
::slotted(button[hidden="true"])
{
	display: none;
}

#container {
	flex-grow: 1;
	display: flex;
	overflow: hidden;
	white-space: nowrap;
	flex-direction: row-reverse;
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

/* global customElements */

import GDialog from './g-dialog.js';
import GSelection from './selection.js';

customElements.define('g-dialog-commands', class extends HTMLElement
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
		GDialog.commands = this;
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