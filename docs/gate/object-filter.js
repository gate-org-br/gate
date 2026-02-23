export default class ObjectFilter
{

	static filter(options, text)
	{
		return options.filter(e => ObjectFilter.contains(e, text));
	}

	static contains(obj, search) {
		if (obj == null)
			return false;

		if (typeof obj === 'string')
			return obj.toLowerCase().includes(search);

		if (typeof obj === 'number')
			return obj.toString().includes(search);

		if (Array.isArray(obj))
			return obj.some(item => ObjectFilter.contains(item, search));

		if (typeof obj === 'object')
			return Object.values(obj).some(value => ObjectFilter.contains(value, search));

		return false;
	}
}


