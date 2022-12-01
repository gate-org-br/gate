import URL from './url.mjs';
import NumberParser from './number-parser.mjs';

export default class Dataset
{
	static fromTable(table, direction = 'X', category = 0, min = 1, max, locale)
	{
		const parser = new NumberParser(locale);

		let rows = Array.from(table.querySelectorAll("tr"))
			.filter(e => e.parentNode.tagName === "THEAD"
					|| e.parentNode.tagName === "TBODY");

		switch (direction)
		{
			case 'X':
				min = min < 0 ? rows[0].children.length + min : min;
				max = max < 0 ? rows[0].children.length + max : max;
				category = category < 0 ? rows[0].children.length + category : category;

				return rows.map(row => row.parentNode.tagName === "THEAD"
						? [row.children[category].innerText.trim(), ...Array.from(row.children)
								.filter((e, index) => index >= min && (!max || index <= max))
								.map(e => e.innerText.trim())]
						: [row.children[category].innerText.trim(), ...Array.from(row.children)
								.filter((e, index) => index >= min && (!max || index <= max))
								.map(e => e.getAttribute("data-value") || e.innerText.trim())
								.map(e => parser.parse(e))]);

			case 'Y':
				min = min < 0 ? rows.length + min : min;
				max = max < 0 ? rows.length + max : max;
				category = category < 0 ? rows.length + category : category;

				return Array.from(rows[0].children).map((_, index) => rows
						.filter((_, row) => row === category || (row >= min && (!max || row <= max)))
						.map(row => index === category || row.parentNode.tagName === "THEAD"
								? row.children[index].innerText.trim()
								: parser.parse(row.children[index].getAttribute("data-value")
									|| row.children[index].innerText.trim())));

			default:
				throw new Error("Invalid direction");
	}
	}

	static reverse(dataset)
	{
		return dataset.length
			? dataset[0].map((e, index) => dataset.map(row => row[index]))
			: dataset;
	}
}