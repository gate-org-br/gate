/* global customElements */

import Scroll from './scroll.js';

export default class GModal extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.addEventListener("keydown", event => event.stopPropagation());
		this.addEventListener("keypress", event => event.stopPropagation());
	}

	show()
	{
		return new Promise(resolve =>
		{
			this.addEventListener("hide", () => {
				resolve();
			}, {once: true});
			Scroll.disable(window.top.document.documentElement);
			window.top.document.documentElement.appendChild(this);
			let dialog = this.shadowRoot.querySelector("dialog");
			if (dialog)
				dialog.showModal();
		});
	}

	hide()
	{
		if (this.parentNode)
		{
			this.dispatchEvent(new CustomEvent('hide', {bubbles: true, detail: {modal: this}}));
			let dialog = this.shadowRoot.querySelector("dialog");
			if (dialog)
				dialog.close();
			this.remove();
			Scroll.enable(window.top.document.documentElement);
		}

		return this;
	}
}

customElements.define('g-modal', GModal);