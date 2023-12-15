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
		if (!this.parentNode)
			window.top.document.documentElement.appendChild(this);

		if (this.dispatchEvent(new CustomEvent('show', {cancelable: true, bubbles: true, detail: {modal: this}})))
		{
			Scroll.disable(window.top.document.documentElement);
			let dialog = this.shadowRoot.querySelector("dialog");
			if (dialog)
				dialog.showModal();
		}

		return this;
	}

	hide()
	{
		if (this.parentNode && this.dispatchEvent(new CustomEvent('hide', {cancelable: true, bubbles: true, detail: {modal: this}})))
		{
			Scroll.enable(window.top.document.documentElement);
			let dialog = this.shadowRoot.querySelector("dialog");
			if (dialog)
				this.shadowRoot.querySelector("dialog").close();
			this.parentNode.removeChild(this);
		}

		return this;
	}
}

customElements.define('g-modal', GModal);