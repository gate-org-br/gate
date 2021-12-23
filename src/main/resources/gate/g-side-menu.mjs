/* global customElements */

import GModal from './g-modal.mjs';

customElements.define('g-side-menu', class extends GModal
{
	constructor()
	{
		super();
		this._private = {};
		this._private.menu = document.createElement("div");
		this.addEventListener("click", e => e.stopPropagation() | this.hide());
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-side-menu");
		this.appendChild(this._private.menu);
	}

	showLeft()
	{
		setTimeout(() => this.classList.remove("R"), 100);
		setTimeout(() => this.classList.add("L"), 100);
	}

	showRight()
	{
		setTimeout(() => this.classList.remove("L"), 100);
		setTimeout(() => this.classList.add("R"), 100);
	}

	show(element)
	{
		let center = element.getBoundingClientRect();
		center = center.left + (center.width / 2);
		if (center <= window.innerWidth / 2)
			this.showLeft();
		else
			this.showRight();
	}

	set elements(elements)
	{
		while (this._private.menu.firstChild)
			this._private.menu.removeChild(this._private.menu.firstChild);
		elements.forEach(e => this._private.menu.appendChild(e));
	}
});