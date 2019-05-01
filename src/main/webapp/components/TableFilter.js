/* global Colorizer */

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input[data-filter]")).forEach(element =>
	{
		var table = element.getAttribute("data-filter") ?
			document.getElementById(element.getAttribute("data-filter"))
			: element.closest("TABLE");

		element.addEventListener("input", function ()
		{
			Array.from(table.children)
				.filter(e => e.tagName.toUpperCase() === "TBODY")
				.flatMap(e => Array.from(e.children)).forEach(row =>
				row.style.display = !this.value || row.innerHTML.toUpperCase()
					.indexOf(this.value.toUpperCase()) !== -1 ? "" : "none");
			Colorizer.colorize(table);
		});
	});
});