/* global DateFormat, customElements */

class DateSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		var date = new Date();
		this._private.calendar = this.appendChild(new Calendar({month: new Date(), max: 1}));
		this._private.calendar.select(date);
		this._private.calendar.addEventListener("remove", event => event.preventDefault());
		this._private.calendar.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected',
				{detail: this.selection})));
	}

	get selection()
	{
		return DateFormat.DATE.format(this._private.calendar.selection()[0]);
	}
}


customElements.define('g-date-selector', DateSelector);