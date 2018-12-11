window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.DateTime")).forEach(function (input)
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
				new DateDialog(function (date)
				{
					new TimeDialog(function (time)
					{
						input.value = new DateFormat("dd/MM/yyyy").format(date) + " " + time;
					});
				});
			return false;
		};
	});
});
