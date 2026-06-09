let template = document.createElement("template");
template.innerHTML = `
	<header></header><main><slot></slot></main>
<style data-element="g-select-menu">* {
	box-sizing: border-box;
}

:host(*) {
	padding: 0;
	width: 100%;
	height: 38px;
	display: flex;
	color: inherit;
	font-size: 14px;
	cursor: pointer;
	min-height: 38px;
	position: relative;
	align-items: stretch;
	flex-direction: column;
	border: 1px solid var(--main2, #F0F0F0);
	background-color: var(--main1, #FFFFFF);
}

header {
	gap: 8px;
	margin: 0;
	border: 0;
	height: 100%;
	display: grid;
	padding: 0 4px;
	text-indent: 0;
	line-height: 38px;
	user-select: none;
	font-size: inherit;
	align-items: center;
	font-family: inherit;
	justify-items: stretch;
	grid-template-columns: 1fr 32px;
}

header::before {
	overflow: hidden;
	white-space: nowrap;
	content: attr(title);
	text-overflow: ellipsis;
}

:host(:not([selected])) header::before {
	color: var(--text3, #777777);
}

header::after {
	display: flex;
	font-size: 0.5em;
	content: '\\2276';
	font-family: gate;
	align-items: center;
	justify-content: center;
}

main {
	top: 100%;
	left: -1px;
	right: -1px;
	display: none;
	z-index: 1000;
	color: inherit;
	font-size: inherit;
	position: absolute;
	flex-direction: column;
	background-color: var(--main1, #FFFFFF);
	border: 1px solid var(--main2, #F0F0F0);
	box-shadow: 1px 1px 2px rgba(0, 0, 0, 0.25);
	max-height: min(304px, 50vh);
	overflow-y: auto;
}

::slotted(a),
::slotted(button),
::slotted(.g-command) {
	gap: 8px;
	text-indent: 0;
	margin: 0;
	border: 0;
	padding: 0 4px;
	height: 38px;
	display: grid;
	cursor: pointer;
	color: inherit;
	font-size: inherit;
	min-height: 38px;
	font-family: inherit;
	align-items: center;
	font-style: normal;
	line-height: 38px;
	text-decoration: none;
	background-color: transparent;
	grid-template-columns: 1fr 32px;
	justify-items: stretch;
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
}


:host(:hover) header,
:host(:focus) header,
:host([opened]) header,
::slotted(:hover),
::slotted(:focus) {
	outline: none;
	background-color: var(--hovered, #FFFACD);
}

::slotted([data-active]) {
	color: HighlightText;
	background-color: Highlight;
}

:host([opened]) > main {
	display: flex;
}

:host([opened]) > header::after {
	content: '\\2278';
}</style>`;
/* global customElements */

import './g-icon.js';

const sheet = new CSSStyleSheet();
sheet.replaceSync(`g-select-menu img {order: 1; width: 1.25em; height: 1.25em; margin: 0 auto}
		g-select-menu i, g-select-menu e, g-select-menu g-icon {order: 1; display: flex; font-size: 1.25em; align-items: center; justify-content: center}`);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

customElements.define('g-select-menu', class extends HTMLElement
{
	#observer;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.querySelector("header")
			.addEventListener("click", event =>
			{
				event.stopPropagation();
				this.opened = !this.opened;
			});
		this.addEventListener("keydown", event =>
		{
			if (event.key === " " || event.key === "Enter")
			{
				event.preventDefault();
				this.opened = !this.opened;
			} else if (event.key === "Escape")
				this.opened = false;
		});
		this.addEventListener("focusout", event =>
		{
			if (!event.relatedTarget || !this.contains(event.relatedTarget))
				this.opened = false;
		});
		this.addEventListener("mouseover", event => this.items
			.forEach(e => e.toggleAttribute("data-active", e.contains(event.target))));
		this.addEventListener("focusin", event => this.items
			.forEach(e => e.toggleAttribute("data-active", e.contains(event.target))));
		this.addEventListener("mouseleave", () => this.items
			.forEach(e => e.removeAttribute("data-active")));
		this.addEventListener("click", event =>
		{
			const command = event.target?.closest?.("a, button, .g-command");
			if (command && this.contains(command))
			{
				this.querySelectorAll("[data-selected]")
					.forEach(e => e.removeAttribute("data-selected"));
				command.setAttribute("data-selected", "");
				this.items
					.forEach(e => e.toggleAttribute("data-active", e.contains(command)));
				this.attributeChangedCallback();
				this.opened = false;
			}
		});
	}

	connectedCallback()
	{
		if (!this.hasAttribute("tabindex"))
			this.tabIndex = 0;
		this.#observer = new MutationObserver(() => this.attributeChangedCallback());
		this.#observer.observe(this, {
			childList: true,
			subtree: true,
			attributes: true,
			attributeFilter: ["data-selected"]
		});
		this.attributeChangedCallback();
	}

	disconnectedCallback()
	{
		this.#observer?.disconnect();
		this.#observer = null;
	}

	get items() { return this.querySelectorAll("a, button, .g-command"); }

	get opened() { return this.hasAttribute("opened"); }

	set opened(opened)
	{
		this.toggleAttribute("opened", !!opened);
		this.items.forEach(e => e.toggleAttribute("data-active", opened && e.hasAttribute("data-selected")));
	}

	set placeholder(placeholder)
	{
		if (placeholder)
			this.setAttribute("placeholder", placeholder);
		else
			this.removeAttribute("placeholder");
	}

	get placeholder() { return this.getAttribute("placeholder"); }

	attributeChangedCallback()
	{
		const selected = this.querySelector("[data-selected]");
		this.shadowRoot.querySelector("header")
			.setAttribute("title", (selected ? Array.from(selected.childNodes) : [])
				.filter(e => e.nodeType === Node.TEXT_NODE)
				.map(e => e.textContent)
				.join("")
				.trim() || this.placeholder || "");
		this.toggleAttribute("selected", !!selected);
	}

	static get observedAttributes() { return ["placeholder"]; }
});