export default class Lexer
{
	#separators;
	#delimiters;
	#token;
	#string;
	#position;

	constructor(string, separators, delimiters)
	{
		this.#string = string;
		this.#separators = separators;
		this.#delimiters = delimiters;
		this.#position = 0;
		this.next();
	}

	isEOF()
	{
		return this.#position >= this.#string.length;
	}

	isSpace()
	{
		return this.#string[this.#position] === ' ';
	}

	isDelimiter()
	{
		return this.#delimiters.includes(this.#string[this.#position]);
	}

	isSeparator()
	{
		return this.#separators.some(e => this.#string.startsWith(e, this.#position));
	}

	* generateTokens()
	{
		while (!this.isEOF())
		{
			while (!this.isEOF() && this.isSpace())
				this.#position++;

			if (this.isSeparator())
			{
				let separator = '';
				while (!this.isEOF()
					&& this.#separators.some(e =>
						e.startsWith(separator + this.#string[this.#position])))
					separator += this.#string[this.#position++];
				yield separator;
			} else if (this.isDelimiter())
			{
				let delimiter = this.#string[this.#position++];
				let value = delimiter;
				while (!this.isEOF() && this.#string[this.#position] !== delimiter)
					value += this.#string[this.#position++];
				if (this.#string[this.#position] !== delimiter)
					throw new Error(`Unterminated string found on ${this.#string}`);
				value += this.#string[this.#position++];
				yield value;
			} else
			{
				let value = '';
				while (!this.isEOF() && !this.isSpace() && !this.isSeparator() && !this.isDelimiter())
					value += this.#string[this.#position++];
				yield value;
			}
		}
	}

	next()
	{
		const result = this.generateTokens().next();
		this.#token = result.value;
	}

	consume()
	{
		const token = this.#token;
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
