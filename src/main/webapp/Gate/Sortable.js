window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("tbody")).forEach(function (element)
	{
		element.sort = function (index, mode)
		{
			var children = Array.from(this.children);
			children.sort(function (e1, e2)
			{
				e1 = e1.children[index];
				var s1 = e1.hasAttribute("data-value") ?
					Number(e1.getAttribute("data-value")) :
					e1.innerHTML.trim();

				e2 = e2.children[index];
				var s2 = e2.hasAttribute("data-value") ?
					Number(e2.getAttribute("data-value")) :
					e2.innerHTML.trim();

				if (mode === "U")
					return s1 > s2 ? 1 : s1 < s2 ? -1 : 0;
				else if (mode === "D")
					return s1 > s2 ? -1 : s1 < s2 ? 1 : 0;
			});
			for (var i = 0; i < children.length; i++)
				this.appendChild(children[i]);
		};
	});

	Array.from(document.querySelectorAll("th[data-sortable] > a")).forEach(function (link)
	{
		link.parentNode.setAttribute("data-sortable", "N");
		link.addEventListener("click", function ()
		{
			switch (this.parentNode.getAttribute("data-sortable"))
			{
				case "N":
					Array.from(this.parentNode.parentNode.children)
						.forEach(e => e.setAttribute("data-sortable", "N"));
					this.parentNode.setAttribute("data-sortable", "U");
					break;
				case "U":
					this.parentNode.setAttribute("data-sortable", "D");
					break;
				case "D":
					this.parentNode.setAttribute("data-sortable", "U");
					break;
			}

			Array.from(this.parentNode.parentNode.parentNode.parentNode.children)
				.filter(e => e.tagName.toUpperCase() === "TBODY")
				.forEach(e => e.sort(Array.prototype.indexOf
						.call(this.parentNode.parentNode.children, this.parentNode),
						this.parentNode.getAttribute("data-sortable")));
		});
	});
});