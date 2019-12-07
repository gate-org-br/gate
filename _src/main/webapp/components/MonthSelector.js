function MonthSelector(element)
{
	var date = new Date();
	element.classList.add("MonthSelector");
	var months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];
	var m = new Slider(element.appendChild(document.createElement("div")), date.getMonth(), e => e > 0 ? e - 1 : 11, e => e < 11 ? e + 1 : 0, e => months[e]);
	var y = new Slider(element.appendChild(document.createElement("div")), date.getFullYear(), e => e - 1, e => e + 1, e => "0000".concat(String(e)).slice(-4));

	m.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	y.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {m: m.value() + 1, y: y.value()};
	};

	this.element = () => element;

	this.toString = () => "00".concat(String(m.value() + 1)).slice(-2) + "/" + "0000".concat(String(y.value())).slice(-4);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.MonthSelector"))
		.forEach(e => new MonthSelector(e));
});