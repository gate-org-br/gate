export default class Lexer
{
	#type;
	#lexer;
	#token;
	#string;

	constructor(string, type)
	{
		this.#string = string;
		this.#type = type;
		this.#lexer = this.#createLexer();
		this.next();
	}

	* #createLexer()
	{
		for (let i = 0; i < this.#string.length; i++)
		{
			while (this.#string[i] === ' ')
				i++;
			switch (this.#type(this.#string[i]))
			{
				case "SEPARATOR":
					yield this.#string[i];
					break;
				case "DELIMITER":
				{
					let value = '';
					let delimiter = this.#string[i];
					value += delimiter;
					for (i++; this.#string[i] !== delimiter; i++)
						value += this.#string[i];
					if (this.#string[i] !== delimiter)
						throw new Error(`Unterminated string found on ${this.#string}`);
					value += delimiter;
					yield value;
					break;
				}
				case "CHARACTER":
				{
					let value = '';
					while (i < this.#string.length
						&& this.#string[i] !== ' '
						&& this.#type(this.#string[i]) === "CHARACTER")
						value += this.#string[i++];
					i--;
					yield value;
				}
			}
		}
	}

	next()
	{
		this.#token = this.#lexer.next().value;
	}

	consume()
	{
		let token = this.#token;
		this.next();
		return token;
	}

	get token()
	{
		return this.#token;
	}

	toString()
	{
		return this.#string;
	}
}
