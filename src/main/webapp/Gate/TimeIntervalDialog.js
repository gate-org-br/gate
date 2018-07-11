window.addEventListener("load", function ()
{
	search("input.TimeInterval").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new TimeDialog(function (time1)
				{
					new TimeDialog(function (time2)
					{
						input.value = time1 + " - " + time2;
					});
				});
			return false;
		};
	});
});
