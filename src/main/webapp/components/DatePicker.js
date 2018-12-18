/* global DateFormat */

function DatePicker(callback)
{
	var picker = new Picker();
	picker.main().classList.add("DatePicker");
	picker.head().appendChild(document.createTextNode("Selecione uma data"));

	var selector = new Calendar(picker.body().appendChild(document.createElement("div"), {max: 1}));

	selector.element().addEventListener("update", () =>
	{
		if (selector.selection().length === 1)
			callback(DateFormat.DATE.format(selector.selection()[0]));
		picker.modal().hide();
	});

	picker.commit().addEventListener("click", () => alert("selecione uma data"));

	picker.modal().show();
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
				new DatePicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});