let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host([empty][value='0'])
{
	flex-grow: 1;
	display: flex;
	padding: 12px;
	display: flex;
	font-size: 16px;
	border: 1px solid;
	text-align: justify;
	align-items: stretch;
	border-left: 6px solid;
	justify-content: center;
	border-color: var(--main6);
	border-radius: 0 3px 3px 0;
	background-color: var(--main1);
}

:host([empty][value='0']) ::slotted(*) {
	display: none !important;
}

:host([empty][value='0'])::before
{
	content: attr(empty);
}</style>`;

/* global customElements */

import DOM from './dom.js';

customElements.define('g-counter', class extends HTMLElement
{
	#observer;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.#observer = new MutationObserver(mutations => this.connectedCallback());
	}

	get target()
	{
		return this.getAttribute("target") || "this.0";
	}

	set target(target)
	{
		return this.setAttribute("target", target);
	}

	get value()
	{
		return this.getAttribute("value");
	}

	set value(value)
	{
		this.setAttribute("value", value);
	}

	connectedCallback()
	{
		const count = DOM.navigate(this, this.target)
			.orElseThrow("Invalid counter target")
			.children.length;
		this.value = count;
		this.querySelectorAll("g-counter-value").forEach(e => e.innerText = count);
	}

	attributeChangedCallback()
	{
		this.#observer.disconnect();
		let target = DOM.navigate(this, this.target)
			.orElseThrow("Invalid counter target");
		this.#observer.observe(target, {childList: true, subtree: true});
	}

	static get observedAttributes()
	{
		return ['target'];
	}
});

customElements.define('g-counter-value', class extends HTMLElement
{
});

