/* global DateFormat */

function DateTimeSelector(element)
{
	element.classList.add("DateTimeSelector");

	var dateSelector = new DateSelector(element.appendChild(document.createElement("div")));
	var timeSelector = new TimeSelector(element.appendChild(document.createElement("div")));

	dateSelector.element().addEventListener("remove", event => event.preventDefault());
	dateSelector.element().addEventListener("select", event => event.detail.calendar.clear());
	dateSelector.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	timeSelector.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		var date = dateSelector.selection();
		if (!date)
			return undefined;

		var time = timeSelector.selection();
		date.setHours(time.h, time.m, 0, 0);
		return date;
	};

	this.element = () => element;

	this.toString = () => DateFormat.DATETIME.format(this.selection());
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateTimeSelector"))
		.forEach(element => new DateTimeSelector(element));
});