export default function colorize(rows)
{
	rows = Array.from(rows);
	rows.forEach(row =>
	{
		row.classList.remove("odd");
		row.classList.remove("even");
	});

	let visible = rows.filter(e => window.getComputedStyle(e).display === "table-row");
	if (visible.length !== rows.length)
	{
		let type = "odd";
		visible.forEach(row =>
		{
			row.classList.add(type);
			type = type === "even" ? "odd" : "even";
		});
	}
}