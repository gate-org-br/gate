export default class Extractor
{
	static label(object)
	{
		if (!object)
			return '';
		if (Array.isArray(object))
			return object[1];
		let keys = Object.keys(object);
		if (keys.length < 2)
			return '';

		if (keys.length === 2
			&& keys.includes("label")
			&& keys.includes("value"))
			return object.label;

		if (keys.length === 3
			&& keys.includes("label")
			&& keys.includes("value")
			&& (keys.includes("properties")))
			return object.label;

		if (keys.length === 3
			&& keys.includes("label")
			&& keys.includes("value")
			&& (keys.includes("children")))
			return object.label;

		if (keys.length === 4
			&& keys.includes("label")
			&& keys.includes("value")
			&& keys.includes("parent")
			&& (keys.includes("children")))
			return object.label;

		return object[keys[1]];
	}

	static value(object)
	{
		if (!object)
			return '';
		if (Array.isArray(object))
			return object[0];
		let keys = Object.keys(object);

		if (keys.length < 2)
			return '';

		if (keys.length === 2
			&& keys.includes("label")
			&& keys.includes("value"))
			return object.value;

		if (keys.length === 3
			&& keys.includes("label") > 0
			&& keys.includes("value") > 0
			&& (keys.includes("properties")))
			return object.value;

		if (keys.length === 3
			&& keys.includes("label")
			&& keys.includes("value")
			&& (keys.includes("children")))
			return object.value;

		if (keys.length === 4
			&& keys.includes("label")
			&& keys.includes("value")
			&& keys.includes("parent")
			&& (keys.includes("children")))
			return object.value;

		return object[keys[0]];
	}

	static value(object, index)
	{
		if (!object)
			return '';
		if (Array.isArray(object))
			return object[index] || "";
		let keys = Object.keys(object);
		return object[keys[index]] || "";
	}

}