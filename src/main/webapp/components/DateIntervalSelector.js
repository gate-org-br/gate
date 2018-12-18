/* global DateFormat */

function DateIntervalSelector(element)
{
	element.classList.add("DateIntervalSelector");

	var date1 = new DateSelector(element.appendChild(document.createElement("div")));
	var date2 = new DateSelector(element.appendChild(document.createElement("div")));

	date1.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	date2.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.element = () => element;

	this.selection = () =>
	{
		return {date1: date1.selection(), date2: date2.selection()};
	};

	this.toString = () => DateFormat.DATE.format(date1.selection()) + " - "
			+ DateFormat.DATE.format(date2.selection());
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateIntervalSelector"))
		.forEach(element => new DateIntervalSelector(element));
});