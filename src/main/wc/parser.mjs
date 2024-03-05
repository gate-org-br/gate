import Lexer from './lexer.js';

const QUOTES = ['"', '`', "'"];

function trigger(lexer)
{
	let name = lexer.consume();
	if (!name.match(/^@[a-z]+(-[a-z]+)*$/))
		throw new Error(`Invalid trigger name ${name}`);
	return {name, parameters: parameters(lexer)};
}

function parameters(lexer)
{
	let result = [];

	if (lexer.token === "(")
	{

		do
		{
			lexer.next();

			if (lexer.token === ",")
				result.push("");
			else if (lexer.token[0] === "'"
				|| lexer.token[0] === "`"
				|| lexer.token[0] === '"')
				result.push(lexer.consume().slice(1, -1));
			else
			{
				let value = '';
				do
				{
					let string = [];
					while (lexer.token
						&& lexer.token !== "("
						&& lexer.token !== ")"
						&& lexer.token !== ","
						&& lexer.token !== "'"
						&& lexer.token !== "`"
						&& lexer.token !== '"')
						string.push(lexer.consume());
					value += string.join(" ");

					if (lexer.token === "(")
						value += "(" + parameters(lexer).join(" ") + ")";

				} while (lexer.token && lexer.token !== ")" && lexer.token !== ",");
				result.push(value);
			}
		} while (lexer.token === ",");

		if (!lexer.token === ")")
			throw new Error(`Unterminated parameter list found on ${lexer.toString()}`);
		lexer.next();
	}

	return result;
}


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
					if (QUOTES.includes(string[i]))
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
					if (QUOTES.includes(string[i]))
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

	static pipeline(string)
	{
		let result = [];
		let lexer = new Lexer(string, ["(", ")", ">", ",", "=>", ">>"], ["'", '"']);


		result.push(trigger(lexer));
		while (lexer.consume() === ">")
			result.push(trigger(lexer));

		return result;
	}

	static unquote(string)
	{
		string = string.trim();
		if (string.startsWith("'")
			&& string.endsWith("'")
			|| string.startsWith('"')
			&& string.endsWith('"')
			|| string.startsWith('`')
			&& string.endsWith('`'))
			string = string.slice(1, -1);
		return string;
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
