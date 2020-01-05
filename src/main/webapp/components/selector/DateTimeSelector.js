/* global DateFormat, customElements */

class DateTimeSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.date = this.appendChild(new DateSelector());
		this._private.time = this.appendChild(new TimeSelector());
		this._private.date.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.time.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return this._private.date.selection + " " + this._private.time.selection;
	}
}


customElements.define('g-datetime-selector', DateTimeSelector);