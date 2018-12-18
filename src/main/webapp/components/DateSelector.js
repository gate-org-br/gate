/* global DateFormat */

function DateSelector(element)
{
	element.classList.add("DateSelector");

	var date = new Date();
	var calendar = new Calendar(element.appendChild(document.createElement("div")), {month: new Date(), max: 1});
	calendar.select(date);

	calendar.element().addEventListener("remove", event => event.preventDefault());
	calendar.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () => new Date(calendar.selection()[0]);

	this.element = () => element;

	this.toString = () => DateFormat.DATE.format(this.selection());
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateSelector"))
		.forEach(element => new DateSelector(element));
});