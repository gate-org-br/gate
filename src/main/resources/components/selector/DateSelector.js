/* global DateFormat, customElements */

class DateSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.calendar = this.appendChild(new GCalendar());
		this._private.calendar.max = 1;
		this._private.calendar.addEventListener("remove", event => event.preventDefault());
		this._private.calendar.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected',
				{detail: this.selection})));
	}

	set date(date)
	{
		this._private.calendar.clear();
		this._private.calendar.select(date);
	}

	get date()
	{
		let selection = this._private.calendar.selection();
		return selection.length ? selection[0] : null;
	}

	get selection()
	{
		let date = this.date;
		return date ? DateFormat.DATE.format(date) : null;
	}
}


customElements.define('g-date-selector', DateSelector);