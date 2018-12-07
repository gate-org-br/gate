function DeskPane(deskPane)
{
	var desktopIcons = Array.from(deskPane.getElementsByTagName("li"));

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
				var reset = deskPane.appendChild
					(new Reset($(deskPane).children("li"),
						this.offsetWidth, this.offsetHeight));
				deskPane.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskPane.appendChild(icons[i]);
				deskPane.appendChild(reset);
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
				deskPane.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskPane.appendChild(icons[i]);
				return false;
			};
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskPane"))
		.forEach(element => new DeskPane(element));
});