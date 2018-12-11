
/* global DateFormat, Calendar */

function DateDialog(callback, month)
{
	month = month ? month : new Date();

	var dialog = new Modal(this);
	dialog.addEventListener("click", function (event)
	{
		if (event.target === dialog || event.srcElement === dialog)
			dialog.hide();
	});

	var calendar = dialog.appendChild(document.createElement("table"));
	calendar.className = "DateDialog";

	var caption = calendar.createCaption();
	caption.appendChild(document.createTextNode(DateFormat.MONTH.format(month)));

	var columns = calendar.appendChild(document.createElement("colgroup"));
	columns.appendChild(document.createElement("col"));
	columns.appendChild(document.createElement("col"));
	columns.appendChild(document.createElement("col"));
	columns.appendChild(document.createElement("col"));
	columns.appendChild(document.createElement("col"));
	columns.appendChild(document.createElement("col"));
	columns.appendChild(document.createElement("col"));

	var daysOfWeek = calendar.appendChild(document.createElement("thead"))
		.appendChild(document.createElement("tr"));
	daysOfWeek.appendChild(document.createElement("th")).innerHTML = "Dom";
	daysOfWeek.appendChild(document.createElement("th")).innerHTML = "Seg";
	daysOfWeek.appendChild(document.createElement("th")).innerHTML = "Ter";
	daysOfWeek.appendChild(document.createElement("th")).innerHTML = "Qua";
	daysOfWeek.appendChild(document.createElement("th")).innerHTML = "Qui";
	daysOfWeek.appendChild(document.createElement("th")).innerHTML = "Sex";
	daysOfWeek.appendChild(document.createElement("th")).innerHTML = "Sab";

	var footer = calendar.appendChild(document.createElement("tfoot"))
		.appendChild(document.createElement("tr"))
		.appendChild(document.createElement("td"));
	footer.setAttribute("colspan", 7);

	var prev = footer.appendChild(document.createElement("span"));
	prev.addEventListener("click", () => setMonth(prev.date));

	var next = footer.appendChild(document.createElement("span"));
	next.addEventListener("click", () => setMonth(next.date));

	var body = calendar.appendChild(document.createElement("tbody"));

	function setMonth(month)
	{
		body.innerHTML = "";
		var calendar = Calendar.getDatesFromMonth(month);
		caption.innerHTML = DateFormat.MONTH.format(month);

		prev.date = new Date(month);
		prev.date.setMonth(prev.date.getMonth() - 1);
		prev.innerHTML = "&nbsp;<&nbsp;" + DateFormat.MONTH.format(prev.date);

		next.date = new Date(month);
		next.date.setMonth(next.date.getMonth() + 1);
		next.innerHTML = DateFormat.MONTH.format(next.date) + "&nbsp;>&nbsp;";

		for (var i = 0; i < calendar.length; i += 7)
		{
			var week = body.appendChild(document.createElement("tr"));
			for (var j = 0; j < 7; j++)
				week.appendChild(createDay(calendar[i + j]));
		}

		function createDay(value)
		{
			let element = document.createElement("td");
			element.title = DateFormat.DATE.format(value);
			element.appendChild(document.createTextNode(value.getDate()));
			if (value.getMonth() !== month.getMonth())
				element.className = "disabled";

			if (Calendar.isToday(value))
				element.className = "selected";

			element.addEventListener("click", () => callback(value));
			element.addEventListener("click", () => dialog.hide());
			return element;
		}
	}

	setMonth(month);
	dialog.show();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Date")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";
		link.addEventListener("click", function (event)
		{
			if (input.value)
				input.value = '';
			else
				new DateDialog(date => input.value = DateFormat.DATE.format(date));
			event.preventDefault();
		});
	});
});