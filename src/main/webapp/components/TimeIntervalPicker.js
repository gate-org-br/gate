class TimeIntervalPicker extends Picker
{
	constructor(callback)
	{
		super();
		this.main().classList.add("TimeIntervalPicker");

		var selector = new TimeIntervalSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () => callback(selector.toString()) | this.hide());

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.TimeInterval")).forEach(function (input)
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
				new TimeIntervalPicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});