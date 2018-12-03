function DateDialog(callback, month)
{
	var dialog = new Modal(this);

	month = month ? month : new Date();
	var monthFormat = new DateFormat("MM/yyyy");
	var dateFormat = new DateFormat("dd/MM/yyyy");

	var table = dialog.appendChild(document.createElement("table"));

	var caption = table.createCaption();
	caption.appendChild(document.createTextNode(monthFormat.format(month)));

	var td = table.appendChild(document.createElement("tfoot"))
		.appendChild(document.createElement("tr"))
		.appendChild(document.createElement("td"));
	td.setAttribute("colspan", 7);

	var prev = td.appendChild(document.createElement("span"));
	prev.style['float'] = "left";
	prev.style.cursor = "pointer";
	prev.onclick = function ()
	{
		dialog.setDate(prev.date);
	};
	prev.onmouseover = function ()
	{
		prev.style.fontWeight = "bold";
	};
	prev.onmouseout = function ()
	{
		prev.style.fontWeight = "normal";
	};

	var next = td.appendChild(document.createElement("span"));
	next.style['float'] = "right";
	next.style.cursor = "pointer";
	next.onclick = function ()
	{
		dialog.setDate(next.date);
	};
	next.onmouseover = function ()
	{
		next.style.fontWeight = "bold";
	};
	next.onmouseout = function ()
	{
		next.style.fontWeight = "normal";
	};

	var week = ["14%", "13%", "13%", "13%", "13%", "13%", "14%"];
	for (var i = 0; i < week.length; i++)
	{
		var col = table.appendChild(document.createElement("col"));
		col.style.width = week[i];
	}

	var thead = table.appendChild(document.createElement("thead"));
	var week = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"];
	for (var i = 0; i < week.length; i++)
	{
		var th = thead.appendChild(document.createElement("th"));
		th.appendChild(document.createTextNode(week[i]));
		th.style.textAlign = "center";
	}

	var tbody = table.appendChild(document.createElement("tbody"));
	dialog.setDate = function (month)
	{
		tbody.innerHTML = "";
		var calendar = new Calendar(month);
		caption.innerHTML = monthFormat.format(month);
		prev.date = new Date(month);
		prev.date.setMonth(prev.date.getMonth() - 1);
		prev.innerHTML = "&nbsp;<&nbsp;" + monthFormat.format(prev.date);
		next.date = new Date(month);
		next.date.setMonth(next.date.getMonth() + 1);
		next.innerHTML = monthFormat.format(next.date) + "&nbsp;>&nbsp;";
		for (var i = 0; i < calendar.length; i += 7)
		{
			var tr = tbody.appendChild(document.createElement("tr"));
			for (var j = 0; j < 7; j++)
			{
				var td = tr.appendChild(document.createElement("td"));
				td.style.cursor = "pointer";
				td.style.textAlign = "center";
				td.value = calendar[i + j];
				td.title = dateFormat.format(calendar[i + j]);
				td.appendChild(document.createTextNode(calendar[i + j].getDate()));
				if (calendar[i + j].getMonth() !== month.getMonth())
					td.style.color = "#CCCCCC";

				var date = new Date();
				if (td.value.getDate() === date.getDate()
					&& td.value.getMonth() === date.getMonth()
					&& td.value.getYear() === date.getYear())
					td.style.backgroundColor = "#F0E68C";

				td.onmouseover = function ()
				{
					this.style.fontWeight = "bold";
					this.prev = this.style.backgroundColor;
					this.style.backgroundColor = "#CCCCCC";
				};
				td.onclick = function ()
				{
					callback(this.value);
					dialog.hide();
				};
				td.onmouseout = function ()
				{
					this.style.fontWeight = "normal";
					this.style.backgroundColor = this.prev;
				};
			}
		}
	};

	dialog.setDate(month);
	dialog.show();

	dialog.onclick = function (e)
	{
		if (e.target === this
			|| e.srcElement === this)
			this.hide();
	};
}


window.addEventListener("load", function ()
{
	search("input.Date").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new DateDialog(function (e)
				{
					input.value = new DateFormat("dd/MM/yyyy").format(e);
				});
			return false;
		};
	});
});