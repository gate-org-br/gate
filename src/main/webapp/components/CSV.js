var CSV =
	{
		parse: function (text)
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
	};