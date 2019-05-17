class Colorizer
{
	static colorize(table)
	{
		var type = "odd";
		Array.from(table.children)
			.filter(e => e.tagName.toUpperCase() === "TBODY")
			.flatMap(e => Array.from(e.children)).forEach(row =>
		{
			row.classList.remove("odd");
			row.classList.remove("even");
			if (row.style.display !== "none")
			{
				row.classList.add(type);
				type = type === "even" ? "odd" : "even";
			}
		});
	}
}