/* global customElements */

class DateTimeIntervalSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.min = this.appendChild(new DateTimeSelector());
		this._private.max = this.appendChild(new DateTimeSelector());
		this._private.min.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.max.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return this._private.min.selection + " - " + this._private.max.selection;
	}
}


customElements.define('g-datetime-interval-selector', DateTimeIntervalSelector);