function TimeSelector(element)
{
	var date = new Date();
	element.classList.add("TimeSelector");
	var h = new Slider(element.appendChild(document.createElement("div")), date.getHours(), e => e > 0 ? e - 1 : 23, e => e < 23 ? e + 1 : 0, e => "00".concat(String(e)).slice(-2));
	var m = new Slider(element.appendChild(document.createElement("div")), date.getMinutes(), e => e > 0 ? e - 1 : 59, e => e < 59 ? e + 1 : 0, e => "00".concat(String(e)).slice(-2));

	h.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	m.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {h: h.value(), m: m.value()};
	};

	this.element = () => element;

	this.toString = () => "00".concat(String(h.value())).slice(-2) + ":" + "00".concat(String(m.value())).slice(-2);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.TimeSelector"))
		.forEach(e => new TimeSelector(e));
});