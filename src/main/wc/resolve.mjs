import DOM from './dom.js';

const REQUIRED = new Error();
const RESOLVE_REGEX = /(@attr|@ATTR|@prop|@PROP|@input|@INPUT|@value|@VALUE)\(([^)]*?)\)/g;
const ENCODED_RESOLVE_REGEX = /%40(?:attr|ATTR|prop|PROP|input|INPUT|value|VALUE)%28(?:[^%]|%(?!29))*%29/g;

function convert(value)
{
	return encodeURIComponent(String(value ?? ""));
}

function navigate(trigger, value)
{
	return DOM.navigate(trigger, value)
		.orElseThrow(() =>
			new Error(`${value} is not a valid element selector`))
		.value || "";
}

function property(obj, propName)
{
	const props = propName.split(/\.|\[(.*?)\]/).filter(Boolean);
	for (let i = 0; obj && i < props.length; i++)
		obj = obj[props[i]];
	return obj;
}

function require(value)
{
	if (!value)
		throw REQUIRED;
	return value;
}

export default function resolve(trigger, context, string)
{
	var result = string.replace(ENCODED_RESOLVE_REGEX,
		e => decodeURIComponent(e.replace(/\+/g, "%20")));
	try
	{
		result = result.replace(RESOLVE_REGEX, function (_, method, value)
		{
			value = decodeURIComponent(value);
			switch (method)
			{
				case '@value':
					return convert(navigate(trigger, value));
				case '@input':
					return convert(prompt(value));
				case '@prop':
					return convert(property(context, value));
				case '@attr':
					return convert(context.getAttribute(value));
				case '@VALUE':
					return convert(require(navigate(trigger, value)));
				case '@INPUT':
					return convert(require(prompt(value)));
				case '@PROP':
					return convert(require(property(context, value)));
				case '@ATTR':
					return convert(require(context.getAttribute(value)));
			}
		});
	} catch (error)
	{
		if (error === REQUIRED)
			return null;
		throw error;
	}
	return result;
}
