export default function colorize(rows)
{
	let type = "odd";
	rows.forEach(row =>
	{
		row.classList.remove("odd");
		row.classList.remove("even");
		if (window.getComputedStyle(row).display !== "none")
		{
			row.classList.add(type);
			type = type === "even" ? "odd" : "even";
		}
	});
}