export default class CSV
{
	static parse(text)
	{
		var a = [];
		text.replace(/(?!\s*$)\s*(?:'([^'\\]*(?:\\[\S\s][^'\\]*)*)'|"([^"\\]*(?:\\[\S\s][^"\\]*)*)"|([^,;'"\s\\]*(?:\s+[^,;'"\s\\]+)*))\s*(?:[,;]|$)/g,
			function (g0, g1, g2, g3)
			{
				if (g1 !== undefined)
					a.push(g1.replace(/\\'/g, "'"));
				else if (g2 !== undefined)
					a.push(g2.replace(/\\"/g, '"'));
				else if (g3 !== undefined)
					a.push(g3);
				return '';
			});
		if (/,\s*$/.test(text))
			a.push('');
		return a;
	}

	static print(rows, name)
	{
		let url = "data:text/csv;charset=utf-8,"
			+ rows
			.map(r => r.map(c => "\"" + c + "\""))
			.map(r => r.join(",")).join("\n");
		url = encodeURI(url);

		let link = document.createElement("a");
		link.setAttribute("href", url);
		link.setAttribute("download", name || "print.csv");
		document.body.appendChild(link);
		link.click();
		document.body.removeChild(link);
	}
}