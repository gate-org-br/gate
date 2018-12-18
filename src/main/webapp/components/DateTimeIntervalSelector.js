function DateTimeIntervalSelector(element)
{
	element.classList.add("DateTimeIntervalSelector");

	var dateTime1 = new DateTimeSelector(element.appendChild(document.createElement("div")));
	var dateTime2 = new DateTimeSelector(element.appendChild(document.createElement("div")));

	dateTime1.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	dateTime2.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {dateTime1: dateTime1.selection(), dateTime2: dateTime2.selection()};
	};

	this.element = () => element;

	this.toString = () => dateTime1.toString() + " - " + dateTime2.toString();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateTimeIntervalSelector"))
		.forEach(e => new TimeIntervalSelector(e));
});