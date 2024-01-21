export default class DataURL
{
	#contentType;
	#data;
	#filename;

	static parse(string)
	{
		const match = string.match(/^data:([a-zA-Z0-9]+\/[a-zA-Z0-9]+)(;base64)?(?:;name=(.*))?,(.*)$/);
		if (!match)
			throw new Error("Invalid Data URL format");

		let contentType = match[1];
		let isBase64 = !!match[2];
		let filename = match[3] || null;
		let data = isBase64 ? atob(match[4]) : decodeURIComponent(match[4]);

		return new DataURL(contentType, data, filename);
	}

	constructor(contentType, data, filename = null)
	{
		this.#contentType = contentType;
		this.#data = data;
		this.#filename = filename;
	}

	get contentType()
	{
		return this.#contentType;
	}

	get data()
	{
		return this.#data;
	}

	get filename()
	{
		return this.#filename;
	}

	toString()
	{
		return `data:${this.#contentType}${this.#filename ? `;name=${this.#filename}` : ''};base64,${btoa(this.#data)}`;
	}
}
