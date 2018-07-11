function Desktop(desktop)
{
	var desktopIcons = Array.from(desktop.getElementsByTagName("li"));

	desktopIcons.forEach(function (e)
	{
		new DesktopIcon(e);
	});

	desktopIcons.forEach(function (e)
	{
		e.style.visibility = "visible";
	});

	function DesktopIcon(desktopIcon)
	{
		var icons = $(desktopIcon).children("ul", "li");

		if (icons.length > 0)
		{
			desktopIcon.onclick = function ()
			{
				var reset = desktop.appendChild
						(new Reset($(desktop).children("li")));
				desktop.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					desktop.appendChild(icons[i]);
				desktop.appendChild(reset);
				return false;
			};
		}

		function Reset(icons)
		{
			var li = document.createElement("li");
			li.style.visibility = "visible";

			var i = li.appendChild(document.createElement("i"));
			i.innerHTML = "&#x2023";
			i.style.color = "#432F21";

			var label = li.appendChild(document.createElement("label"));
			label.innerHTML = "Retornar";
			label.style.color = "#432F21";

			li.onclick = function ()
			{
				desktop.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					desktop.appendChild(icons[i]);
				return false;
			};
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	search("ul.DESKTOP").forEach(function (e)
	{
		new Desktop(e);
	});
});
