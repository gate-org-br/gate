export default class Base64
{
	static #decoder = new TextDecoder('utf-8');
	static #encoder = new TextEncoder('utf-8');

	static decode(string)
	{
		const bytes = Uint8Array.from(atob(string), char => char.charCodeAt(0));
		return this.#decoder.decode(bytes);
	}

	static encode(string)
	{
		const bytes = this.#encoder.encode(string);
		let binary = '';
		bytes.forEach(byte => binary += String.fromCharCode(byte));
		return btoa(binary);
	}
}