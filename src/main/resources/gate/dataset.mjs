export default class Dataset
{
	static fromTable(table)
	{
		let data = Array.from(table.querySelectorAll(":scope > thead > tr, :scope > tbody > tr"));
		data = data.map(e => Array.from(e.children).map(c => c.getAttribute("data-value") || c.innerHTML.trim()));
		for (let i = 1; i < data.length; i++)
			for (let j = 1; j < data[i].length; j++)
				data[i][j] = Number(data[i][j].replace(/\./g, "").replace(/\,/g, "."));
		return data;
	}
}