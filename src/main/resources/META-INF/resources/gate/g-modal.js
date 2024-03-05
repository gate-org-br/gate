/* global customElements */

import Scroll from './scroll.js';

export default class GModal extends HTMLElement
{
	constructor()
	{
		super();
		this.addEventListener("keydown", event => event.stopPropagation());
		this.addEventListener("keypress", event => event.stopPropagation());
	}

	show()
	{
		return new Promise(resolve =>
		{
			this.addEventListener("hide", () => resolve(), {once: true});

			if (!this.parentNode)
				window.top.document.body.appendChild(this);
			this.style.display = "flex";
			this.shadowRoot.querySelector("dialog").showModal();
		});
	}

	hide()
	{
		if (this.parentNode)
		{
			this.dispatchEvent(new CustomEvent('hide', {bubbles: true, detail: {modal: this}}));

			this.shadowRoot.querySelector("dialog").close();
			this.style.display = "";
			if (this.parentNode === window.top.document.body)
				this.remove();
		}

		return this;
	}
}

customElements.define('g-modal', GModal);