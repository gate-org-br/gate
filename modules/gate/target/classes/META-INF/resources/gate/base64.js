export default class Base64
{
	static	decode(string)
	{
		const bytes = new Uint8Array(atob(string).split('').map(char => char.charCodeAt(0)));
		const decoder = new TextDecoder('utf-8');
		return decoder.decode(bytes);
	}

	static	encode(string)
	{
		const encoder = new TextEncoder('utf-8');
		const bytes = encoder.encode(string);
		let binaryString = '';
		bytes.forEach(byte => binaryString += String.fromCharCode(byte));
		return btoa(binaryString);
	}
}