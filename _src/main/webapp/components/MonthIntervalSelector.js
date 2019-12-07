function MonthIntervalSelector(element)
{
	element.classList.add("MonthIntervalSelector");

	var time1 = new MonthSelector(element.appendChild(document.createElement("div")));
	var time2 = new MonthSelector(element.appendChild(document.createElement("div")));

	time1.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	time2.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {time1: time1.selection(), time2: time2.selection()};
	};

	this.element = () => element;

	this.toString = () => time1.toString() + " - " + time2.toString();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.MonthIntervalSelector"))
		.forEach(e => new MonthIntervalSelector(e));
});