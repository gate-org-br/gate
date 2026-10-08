const VALIDATE = /^([A-Za-z_$][A-Za-z0-9_$]*|\[\d+\]|\["[^"]*"\]|\['[^']*'\]|\[`[^`]*`\]|\[[A-Za-z_$][A-Za-z0-9_$]*\])(\.[A-Za-z_$][A-Za-z0-9_$]*|\[\d+\]|\["[^"]*"\]|\['[^']*'\]|\[`[^`]*`\]|\[[A-Za-z_$][A-Za-z0-9_$]*\])*$/;

export default function property(object, name)
{
	if (!VALIDATE.test(name))
		throw new Error(`Invalid property: ${name}`);

	const capture =
		/\.?([A-Za-z_$][A-Za-z0-9_$]*)|\[(\d+)\]|\["([^"]*)"\]|\['([^']*)'\]|\[`([^`]*)`\]|\[([A-Za-z_$][A-Za-z0-9_$]*)\]/g;

	for (let match = capture.exec(name);
		object !== null &&
		object !== undefined
		&& match !== null;
		match = capture.exec(name))
	{
		const [, ident, index, dquoted, squoted, cquoted, bracket] = match;

		if (index !== undefined)
		{
			if (Array.isArray(object))
			{
				object = object[parseInt(index, 10)];
			} else if (object && typeof object === 'object')
			{
				const key = Object.keys(object)[parseInt(index, 10)];
				object = key !== undefined ? object[key] : undefined;
			} else
				object = undefined;

		} else if (ident !== undefined)
			object = object[ident];
		else if (squoted !== undefined)
			object = object[squoted];
		else if (dquoted !== undefined)
			object = object[dquoted];
		else if (cquoted !== undefined)
			object = object[cquoted];
		else if (bracket !== undefined)
			object = object[bracket];
	}

	return object;
}