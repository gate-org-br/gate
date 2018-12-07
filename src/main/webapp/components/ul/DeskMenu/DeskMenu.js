function DeskMenu(deskMenu)
{
	var desktopIcons = Array.from(deskMenu.getElementsByTagName("li"));

	desktopIcons.forEach(e => new DeskMenuIcon(e));

	function DeskMenuIcon(deskMenuIcon)
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

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";
			a.setAttribute("data-icon", "\u2232");

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
	Array.from(document.querySelectorAll("ul.DeskMenu"))
		.forEach(element => new DeskMenu(element));
});