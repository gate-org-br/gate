export default class ObjectFilter
{

	static filter(options, text)
	{
		return Array.isArray(options[0])
			? options.filter((e, i) => i === 0 || contains(e, text))
			: options.filter(e => contains(e, text));
	}
}

function contains(obj, text)
{
	if (!obj)
		return false;
	else if (typeof obj === 'string')
		return obj.toLowerCase().includes(text.toLowerCase());
	else if (typeof obj === 'number')
		return obj.toString().includes(text);
	else if (Array.isArray(obj))
		return obj.some(item => contains(item, text));
	else if (typeof obj === 'object')
		return Object.values(obj).some(value => contains(value, text));
	else
		return false;
}
