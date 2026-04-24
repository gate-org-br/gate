import Base64 from './base64.js';
export default class DataURL
{
	static parse(string)
	{
		const match = string.match(/^data:(?<contentType>[^;,]+)(?<parameters>(?:;[^=]+=[^;,]+)*)(?<base64>;base64)?,(?<data>.*)/);
		if (!match)
			throw new Error("Invalid data URL");

		const contentType = match.groups.contentType;
		const parameters = match.groups.parameters ? match.groups.parameters.split(";")
			.filter(e => e)
			.reduce((acc, string) => {
				const [key, value] = string.split("=");
				acc[key.trim()] = decodeURIComponent(value).trim();
				return acc;
			}, {}) : {};

		const data = match.groups.data;
		const base64 = !!match.groups.base64;

		if (!base64)
			return new DataURL(contentType, decodeURIComponent(data), parameters, base64);

		if (contentType.startsWith("text")
			|| contentType === "application/json")
			return new DataURL(contentType,
				Base64.decode(data), parameters, base64);

		return new DataURL(contentType, atob(data), parameters, base64);

	}

	constructor(contentType, data, parameters, base64 = true)
	{
		this.contentType = contentType;
		this.data = data;
		this.base64 = base64;
		this.parameters = parameters || {};
	}

	toString()
	{
		let parameters = '';
		for (const [name, value] of Object.entries(this.parameters))
			parameters += `;${name}=${value === true ? '' : encodeURIComponent(value)}`;
		if (!this.base64)
			return `data:${this.contentType}${parameters},${encodeURIComponent(this.data)}`;

		if (this.contentType.startsWith("text")
			|| this.contentType === "application/json")
			return `data:${this.contentType}${parameters};base64,${Base64.encode(this.data)}`;

		return `data:${this.contentType}${parameters};base64,${btoa(this.data)}`;
	}

	static ofJSON(value)
	{
		return new DataURL("application/json",
			JSON.stringify(value)).toString();
	}

	static ofHTML(value)
	{
		return new DataURL("text/html", value).toString();
	}

	static ofText(value)
	{
		return new DataURL("text/plain", value).toString();
	}

	static toJSON(value)
	{
		return JSON.parse(DataURL.parse(value).data);
	}

	static toText(value)
	{
		return DataURL.parse(value).data;
	}

	static toHTML(value)
	{
		return DataURL.parse(value).data;
	}
}
