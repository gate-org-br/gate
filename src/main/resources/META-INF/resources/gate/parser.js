import CSV from './csv.js';

export default class Parser
{
	static path(string)
	{
		let steps = [];

		for (let i = 0; i < string.length; )
		{
			let step = '';
			if (string[i] === '[')
			{
				for (i++; i < string.length && string[i] !== ']'; i++)
				{
					if (string[i] === '"' || string[i] === "'")
					{
						const delimiter = string[i];
						for (i++; i < string.length && string[i] !== delimiter; i++)
							step += string[i];
						if (string[i++] !== delimiter)
							throw new Error(`Unterminated string found on ${string}`);
					} else
						step += string[i];
				}
				if (string[i++] !== ']')
					throw new Error(`Unterminated square brackets found on ${string}`);
			} else if (string[i] === '.')
			{
				for (i++; i < string.length && string[i] !== '.' && string[i] !== '['; i++)
					if (string[i] === '"' || string[i] === "'")
					{
						const delimiter = string[i];
						for (i++; i < string.length && string[i] !== delimiter; i++)
							step += string[i];
						if (string[i++] !== delimiter)
							throw new Error(`Unterminated string found on ${string}`);
					} else
						step += string[i];
			} else
				throw new Error(`${string} is not a valid path`);

			steps.push(step);
		}

		return steps;
	}

	static method(string)
	{
		let parentesis = string.indexOf("(");
		if (parentesis <= 0)
			return ({name: string, parameters: []});

		if (!string.endsWith(")"))
			throw new Error(`${string} is not a valid method`);

		return ({name: string.substring(0, parentesis),
			parameters: CSV.parse(string.slice(parentesis + 1, -1))});
	}

	static queryString(query)
	{
		var qs = {};
		if (query.includes('?'))
			query = query.split('?')[1];
		var vars = query.split("&");
		for (var i = 0; i < vars.length; i++)
		{
			var pair = vars[i].split("=");
			var key = decodeURIComponent(pair[0]);
			var value = decodeURIComponent(pair[1]);
			if (typeof qs[key] === "undefined")
				qs[key] = decodeURIComponent(value);
			else if (typeof qs[key] === "string")
				qs[key] = [qs[key], decodeURIComponent(value)];
			else
				qs[key].push(decodeURIComponent(value));
		}
		return qs;
	}

}
