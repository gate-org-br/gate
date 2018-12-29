/* global DateFormat */

class DateIntervalPicker extends Picker
{
	constructor(callback)
	{
		super();
		this.main().classList.add("DateIntervalPicker");

		var selector = new DateIntervalSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () =>
		{
			if (selector.selection())
			{
				callback(selector.toString());
				this.hide();
			}
		});

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
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