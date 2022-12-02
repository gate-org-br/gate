import URL from './url.mjs';
import NumberParser from './number-parser.mjs';

export default class Dataset
{
	static fromTable(table, {dir = 'X', cat = 0, min = 1, max,
		lang = document.querySelector("html").getAttribute("lang")
		|| navigator.language})
	{
		const parser = new NumberParser(lang);

		let rows = Array.from(table.querySelectorAll("tr"))
			.filter(e => e.parentNode.tagName === "THEAD"
					|| e.parentNode.tagName === "TBODY");

		let size = rows[0].children.length;
		min = min < 0 ? size + min : min;
		max = max < 0 ? size + max : max;
		cat = cat < 0 ? size + cat : cat;
		let result = rows.map(row => row.parentNode.tagName === "THEAD"
				? [row.children[cat].innerText.trim(), ...Array.from(row.children)
						.filter((_, index) => index >= min && (!max || index <= max))
						.map(e => e.innerText.trim())]
				: [row.children[cat].innerText.trim(), ...Array.from(row.children)
						.filter((_, index) => index >= min && (!max || index <= max))
						.map(e => e.getAttribute("data-value") || e.innerText.trim())
						.map(e => parser.parse(e))]);
		return dir === 'X' ? result : Dataset.reverse(result);
	}

	static reverse(dataset)
	{
		return dataset.length
			? dataset[0].map((e, index) => dataset.map(row => row[index]))
			: dataset;
	}
}