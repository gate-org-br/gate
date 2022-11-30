import URL from './url.mjs';
import NumberParser from './number-parser.mjs';

export default class Dataset
{
	static fromTable(table, category = 0, min = 1, max, locale)
	{
		const parser = new NumberParser(locale);
		return Array.from(table.querySelectorAll("tr"))
			.filter(e => e.parentNode.tagName === "THEAD" || e.parentNode.tagName === "TBODY")
			.map(row => row.parentNode.tagName === "THEAD"
					? [row.children[category].innerText.trim(), ...Array.from(row.children)
							.filter((e, index) => index >= min && (!max || index <= max))
							.map(e => e.innerText.trim())]
					: [row.children[category].innerText.trim(), ...Array.from(row.children)
							.filter((e, index) => index >= min && (!max || index <= max))
							.map(e => e.getAttribute("data-value") || e.innerText.trim())
							.map(e => parser.parse(e))]);
	}

	static reverse(dataset)
	{
		return dataset.length
			? dataset[0].map((e, index) => dataset.map(row => row[index]))
			: dataset;
	}
}