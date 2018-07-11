window.addEventListener("load", function ()
{
	search("input.DateTimeInterval").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";
		link.onclick = function ()
		{
			if (!input.value)
				input.value = '';
			else
				new DateDialog(function (date1)
				{
					new TimeDialog(function (time1)
					{
						new DateDialog(function (date2)
						{
							new TimeDialog(function (time2)
							{
								var format = new DateFormat("dd/MM/yyyy");
								input.value = format.format(date1) + " " + time1 + " - " + format.format(date2) + " " + time2;
							});
						}, date1);
					});
				});

			return false;
		};
	});
});