/* global DateFormat */

class DatePicker extends Picker
{
	constructor(callback)
	{
		super();

		this.main().classList.add("DatePicker");
		this.head().appendChild(document.createTextNode("Selecione uma data"));

		var selector = new Calendar(this.body().appendChild(document.createElement("div"), {max: 1}));

		selector.element().addEventListener("update", () =>
		{
			if (selector.selection().length === 1)
				callback(DateFormat.DATE.format(selector.selection()[0]));
			this.hide();
		});

		this.commit().addEventListener("click", () => alert("selecione uma data"));

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Date")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new DatePicker(value => input.value = value);

			input.dispatchEvent(new CustomEvent('changed', {detail: {source: this}}));
			link.focus();
			link.blur();
		});
	});
});