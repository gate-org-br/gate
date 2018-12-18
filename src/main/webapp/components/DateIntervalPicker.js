/* global DateFormat */

function DateIntervalPicker(callback)
{
	var picker = new Picker();
	picker.main().classList.add("DateIntervalPicker");

	var selector = new DateIntervalSelector(picker.body().appendChild(document.createElement("div")));
	selector.element().addEventListener("update", () => picker.head().innerHTML = selector.toString());

	picker.head().appendChild(document.createTextNode(selector.toString()));

	picker.commit().addEventListener("click", () =>
	{
		if (selector.selection())
		{
			callback(selector.toString());
			picker.modal().hide();
		}
	});

	picker.modal().addEventListener("show", () => picker.commit().focus());

	picker.modal().show();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.DateInterval")).forEach(function (input)
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
				new DateIntervalPicker(selection =>
				{
					input.value = selection;
					link.focus();
				});

			link.blur();
		});
	});
});