export default function colorize(rows)
{
	rows = Array.from(rows);
	if (rows.some(row => window.getComputedStyle(row).display === "none"))
	{
		let type = "odd";
		rows.forEach(row =>
		{
			row.classList.remove("odd");
			row.classList.remove("even");
			row.classList.add(type);
			type = type === "even" ? "odd" : "even";
		});
	} else
		rows.forEach(row =>
		{
			row.classList.remove("odd");
			row.classList.remove("even");
		})
}