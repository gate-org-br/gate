/* global customElements */

import GWindow from './g-window.mjs';

export default class GPopup extends GWindow
{
	constructor()
	{
		super();
		this.hideButton;
	}

	set element(element)
	{
		if (this.body.firstChild)
			this.body.removeChild(this.body.firstChild);
		this.body.appendChild(element);
	}

	get element()
	{
		return this.body.firstChild;
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-popup");
	}

	static show(template, caption)
	{
		var popup = window.top.document.createElement("g-popup");
		popup.caption = caption || template.getAttribute("title") || "";
		popup.element = template.firstElementChild;
		popup.addEventListener("hide", () => template.appendChild(popup.element));
		popup.show();
	}
}

customElements.define('g-popup', GPopup);

Array.from(document.querySelectorAll('*[data-popup]')).forEach(element => GPopup.show(element));