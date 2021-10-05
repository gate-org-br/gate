/* global Colorizer */

class GDataFilter
{
	static filter(input)
	{
		let table = input.getAttribute("data-filter")
			? document.getElementById(input.getAttribute("data-filter"))
			: input.closest("TABLE");
		Array.from(table.children)
			.filter(e => e.tagName === "TBODY")
			.flatMap(e => Array.from(e.children))
			.forEach(row => row.style.display = !input.value || row.innerHTML.toUpperCase().indexOf(input.value.toUpperCase()) !== -1 ? "" : "none");
		Colorizer.colorize(table);
	}
}

window.addEventListener("input", function (event)
{
	if (event.target.tagName === "INPUT"
		&& event.target.hasAttribute("data-filter"))
		GDataFilter.filter(event.target);
});

window.addEventListener("changed", function (event)
{
	if (event.target.tagName === "INPUT"
		&& event.target.hasAttribute("data-filter"))
		GDataFilter.filter(event.target);
});

window.addEventListener("load", () => Array.from(document.querySelectorAll("input[data-filter]")).forEach(e => setTimeout(() => GDataFilter.filter(e), 0)));