/* global fetch */

import NumberFormat from './number-format.js';

const format = new NumberFormat();

function extract(table)
{
	let columns = [];
	let head = Array.from(table.querySelector("thead > tr").children);
	const catColumns = head.filter(col => col.getAttribute("data-chart") === "cat");
	const valColumns = head.filter(col => col.getAttribute("data-chart") === "val");

	if (catColumns.length === 1 && valColumns.length > 0)
	{
		columns.push(...catColumns.map(col => head.indexOf(col)));
		columns.push(...valColumns.map(col => head.indexOf(col)));
	} else
		columns = head.map((col, index) => index);

	let result = [];
	result.push(columns.map(column => head[column].innerText));

	let data = Array.from(table.querySelectorAll("tbody > tr"));
	data.forEach(row => result.push(columns.map(column => row.children[column].innerText)
			.map((value, index) => index ? format.parse(value) : value)));

	return result;
}

export default class Dataset
{

	static reverse(dataset)
	{
		return dataset.length
			? dataset[0].map((e, index) => dataset.map(row => row[index]))
			: dataset;
	}

	static fetch(value)
	{

		return new Promise((resolve, reject) =>
		{
			try
			{
				if (value instanceof HTMLTableElement)
					return resolve(extract(value));
				else if (typeof value === "string")
					if (value.startsWith("#"))
						return resolve(Dataset.fetch(document.querySelector(value)));
					else
						return fetch(value).then(e => e.json()).then(e => resolve(e));
				throw new Error("Invalid dataset source");
			} catch (error)
			{
				reject(error);
			}
		});
	}
}