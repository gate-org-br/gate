function DeskPane(deskPane)
{
	Array.from(deskPane.children)
		.filter(li => li.tagName.toLowerCase() === "li")
		.forEach(function (li)
		{
			Array.from(li.children)
				.filter(a => a.tagName.toLowerCase() === "a")
				.forEach(a => new DeskPaneIcon(a));
		});

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
				var reset = deskPane.appendChild
					(new Reset(Array.from(deskPane.children).filter(e => e.tagName.toLowerCase() === "li"),
						this.offsetWidth, this.offsetHeight));
				deskPane.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskPane.appendChild(icons[i]);
				deskPane.appendChild(reset);
			});
		}

		function Reset(icons, width, height)
		{
			var li = document.createElement("li");
			li.className = "Reset";
			li.style.width = width + "px";
			li.style.height = height + "px";
			li.style.color = "@G";

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