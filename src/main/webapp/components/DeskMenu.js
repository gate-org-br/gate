function DeskMenu(deskMenu)
{
	Array.from(deskMenu.getElementsByTagName("a"))
		.forEach(element => new DeskMenuIcon(element));

	function DeskMenuIcon(deskMenuIcon)
	{
		var icons = Array.from(deskMenuIcon.parentNode.children)
			.filter(e => e.tagName.toLowerCase() === "ul")
			.flatMap(e => Array.from(e.children));

		if (icons.length > 0)
		{
			deskMenuIcon.addEventListener("click", function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				var children = Array.from(deskMenu.children);
				children.forEach(e => deskMenu.removeChild(e));
				icons.forEach(e => deskMenu.appendChild(e));
				deskMenu.appendChild(new Reset(children));
			});
		}

		function Reset(icons)
		{
			var li = document.createElement("li");
			li.className = "Reset";

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";

			a.appendChild(document.createElement("i"))
				.innerHTML = "&#X2232";

			a.addEventListener("click", function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				Array.from(deskMenu.children).forEach(e => deskMenu.removeChild(e));
				icons.forEach(e => deskMenu.appendChild(e));
			});
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskMenu"))
		.forEach(element => new DeskMenu(element));
});