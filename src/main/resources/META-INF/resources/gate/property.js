export default function property(obj, propName)
{
	const props = propName.split(/\.|\[(.*?)\]/).filter(Boolean);
	for (let i = 0; obj && i < props.length; i++)
		obj = obj[props[i]];
	return obj;
}