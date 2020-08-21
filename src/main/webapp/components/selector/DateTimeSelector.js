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

	set date(date)
	{
		this._private.date.date = date;
	}

	get date()
	{
		return this._private.date.date;
	}

	get selection()
	{
		let date = this._private.date.selection;
		let time = this._private.time.selection;
		return date && time ? date + " " + time : null;
	}
}


customElements.define('g-datetime-selector', DateTimeSelector);