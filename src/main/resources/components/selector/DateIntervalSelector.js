/* global customElements */

class DateIntervalSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.min = this.appendChild(new DateSelector());
		this._private.max = this.appendChild(new DateSelector());
		this._private.min.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.max.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	set min(date)
	{
		this._private.min.date = date;
	}

	get min()
	{
		return this._private.min.date;
	}

	set max(date)
	{
		this._private.max.date = date;
	}

	get max()
	{
		return this._private.max.date;
	}

	get selection()
	{
		let min = this._private.min.selection;
		let max = this._private.max.selection;
		return min && max ? min + " - " + max : null;
	}
}


customElements.define('g-date-interval-selector', DateIntervalSelector);