/* global fetch */

import URL from './url.js';
import NumberFormat from './number-format.js';

export default class Dataset
{
	static fromTable(table, {dir = 'X', cat = 0, min = 1, max, lang })
	{
		const parser = new NumberFormat(lang);

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

	static parse(type, value)
	{
		return new Promise((resolve, reject) =>
		{
			try {
				switch (type)
				{
					case 'table':
						let matcher = value.match(/^(#[a-z]+)(\(([^)]*)\))?$/i);
						if (!matcher)
							throw new Error("Invalid table id");
						let table = document.querySelector(matcher[1]);
						let options = matcher[3] ? JSON.parse(matcher[3]) : {};
						return resolve(Dataset.fromTable(table, options));
						break;
					case 'url':
						fetch(value).then(e => e.json()).then(e => resolve(e));
						break;
					case 'array':
						resolve(JSON.parse(value));
						break;
					default:
						throw new Error("Invalid value type");
				}
			} catch (error)
			{
				reject(error);
			}
		});
	}
}