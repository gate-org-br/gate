function TimePicker(callback)
{
	var picker = new Picker();
	picker.main().classList.add("TimePicker");

	var selector = new TimeSelector(picker.body().appendChild(document.createElement("div")));
	selector.element().addEventListener("update", () => picker.head().innerHTML = selector.toString());

	picker.head().appendChild(document.createTextNode(selector.toString()));

	picker.commit().addEventListener("click", () => callback(selector.toString()) | picker.modal().hide());

	picker.modal().addEventListener("show", () => picker.commit().focus());

	picker.modal().show();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Time")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new TimePicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});