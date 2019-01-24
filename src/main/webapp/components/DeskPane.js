function DeskPane(deskPane)
{
	Array.from(deskPane.getElementsByTagName("a"))
		.forEach(element => new DeskPaneIcon(element));

	function DeskPaneIcon(deskPaneIcon)
	{
		var icons = Array.from(deskPaneIcon.parentNode.children)
			.filter(e => e.tagName.toLowerCase() === "ul")
			.flatMap(e => Array.from(e.children));

		if (icons.length > 0)
		{
			deskPaneIcon.addEventListener("click", function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				var children = Array.from(deskPane.children);
				children.forEach(e => deskPane.removeChild(e));
				icons.forEach(e => deskPane.appendChild(e));
				deskPane.appendChild(new Reset(children));
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

				Array.from(deskPane.children).forEach(e => deskPane.removeChild(e));
				icons.forEach(e => deskPane.appendChild(e));
			});
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskPane"))
		.forEach(element => new DeskPane(element));
});

