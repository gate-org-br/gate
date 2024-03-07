let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-accordion">:host(*)
{
	display: flex;
	flex-direction: column;
	box-shadow: 1px 1px 2px 0px var(--main6);
}


::slotted(a),
::slotted(button)
{
	padding: 8px;
	display: flex;
	cursor: pointer;
	font-weight: bold;
	position: relative;
	padding-right: 20px;
	align-items: center;
	background-color: var(--main2);
}

::slotted(div)
{
	gap: 12px;
	padding : 8px;
	display: none;
	overflow: hidden;
	align-items: stretch;
	flex-direction: column;
	background-color: white;
}

::slotted(div[data-expanded])
{
	display: flex;
}

::slotted(a)::after,
::slotted(button)::after
{
	display: flex;
	font-size: 0.5em;
	content: '\\2276';
	font-family: gate;
	align-items: center;
	justify-content: center;
	position: absolute;
	right: 8px;
}

::slotted(a[data-expanded])::after,
::slotted(button[data-expanded])::after
{
	content: '\\2278';
}

</style>`;
/* global customElements */

import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

customElements.define('g-accordion', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", event =>
		{
			let target = event.target.closest("a, button");
			if (target)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (!target.hasAttribute("data-expanded"))
					this.expand(target);
				else
					this.colapse(target);
			}
		});
	}

	connectedCallback()
	{
		if (this.multiple)
			Array.from(this.querySelectorAll("a[data-expanded], button[data-expanded]"))
				.forEach(e => this.expand(e));
		else
			this.expand(this.querySelector("a[data-expanded], button[data-expanded]"));

	}

	get multiple()
	{
		return this.hasAttribute("multiple");
	}

	set multiple(value)
	{
		if (value)
			this.setAttribute("multiple", "");
		else
			this.removeAttribute("multiple", "");
	}

	expand(target)
	{
		if (!target)
			return;

		if (!this.multiple)
			Array.from(this.children)
				.forEach(e => e.removeAttribute("data-expanded"));

		let div = target.nextElementSibling;
		if (!div || div.tagName !== "DIV")
		{
			div = div ?
				this.insertBefore(document.createElement("div"), div) :
				this.appendChild(document.createElement("div"));

			let method = target.getAttribute('method') || (target.form || {}).method || "get";
			let action = target.getAttribute('href') || target.getAttribute('formaction') || (target.form || {}).action;

			fetch(RequestBuilder.build(method, action, target.form))
				.then(ResponseHandler.text)
				.then(result => document.createRange().createContextualFragment(result))
				.then(result => div.replaceChildren(...Array.from(result.childNodes)));
		}

		target.setAttribute("data-expanded", "");
		div.setAttribute("data-expanded", "");
	}

	colapse(target)
	{
		if (!target)
			return;

		let div = target.nextElementSibling;
		target.removeAttribute("data-expanded");
		div.removeAttribute("data-expanded");
	}
});