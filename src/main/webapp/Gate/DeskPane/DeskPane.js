function DeskPane(deskMenu)
{
	var desktopIcons = Array.from(deskMenu.getElementsByTagName("li"));

	desktopIcons.forEach(function (e)
	{
		new DeskPaneIcon(e);
	});

	function DeskPaneIcon(deskMenuIcon)
	{
		var icons = $(deskMenuIcon).children("ul", "li");

		if (icons.length > 0)
		{
			deskMenuIcon.onclick = function ()
			{
				var reset = deskMenu.appendChild
					(new Reset($(deskMenu).children("li"),
						this.offsetWidth, this.offsetHeight));
				deskMenu.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskMenu.appendChild(icons[i]);
				deskMenu.appendChild(reset);
				return false;
			};
		}

		function Reset(icons, width, height)
		{
			var li = document.createElement("li");
			li.className = "Reset";
			li.style.width = width + "px";
			li.style.height = height + "px";
			li.style.color = "#006600";

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";

			var i = a.appendChild(document.createElement("i"));
			i.innerHTML = "&#X2232";

			li.onclick = function ()
			{
				deskMenu.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskMenu.appendChild(icons[i]);
				return false;
			};
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskPane")).forEach(function (e)
	{
		new DeskPane(e);
	});
});