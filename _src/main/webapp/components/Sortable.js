/* global Colorizer, Objects */

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
				var s1 = e1.innerHTML.trim();
				if (e1.hasAttribute("data-value"))
					s1 = e1.getAttribute("data-value").trim() ?
						Number(e1.getAttribute("data-value")) : null;

				e2 = e2.children[index];
				var s2 = e2.innerHTML.trim();
				if (e2.hasAttribute("data-value"))
					s2 = e2.getAttribute("data-value").trim() ?
						Number(e2.getAttribute("data-value")) : null;

				if (mode === "U")
					return Objects.compare(s1, s2);
				else if (mode === "D")
					return Objects.compare(s2, s1);
			});
			for (var i = 0; i < children.length; i++)
				this.appendChild(children[i]);
		};
	});

	Array.from(document.querySelectorAll("th[data-sortable]")).forEach(function (link)
	{
		link.setAttribute("data-sortable", "N");
		link.addEventListener("click", function ()
		{
			switch (this.getAttribute("data-sortable"))
			{
				case "N":
					Array.from(this.parentNode.children)
						.filter(e => e.hasAttribute("data-sortable"))
						.forEach(e => e.setAttribute("data-sortable", "N"));
					this.setAttribute("data-sortable", "U");
					break;
				case "U":
					this.setAttribute("data-sortable", "D");
					break;
				case "D":
					this.setAttribute("data-sortable", "U");
					break;
			}

			var table = this.closest("TABLE");
			Array.from(table.children)
				.filter(e => e.tagName.toUpperCase() === "TBODY")
				.forEach(e => e.sort(Array.prototype.indexOf
						.call(this.parentNode.children, this),
						this.getAttribute("data-sortable")));
			Colorizer.colorize(table);
		});
	});
});

