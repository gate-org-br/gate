import DOM from './dom.js';
const REQUIRED = new Error();
import EventHandler from './event-handler.js';
const RESOLVE_REGEX = /(\?|@|\$attr|\$ATTR|\$prop|\$PROP|\$user|\$USER|\$elem|\$ELEM)\{([^}]*)\}/g;

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
	var result = decodeURI(string);
	try
	{
		result = result.replace(RESOLVE_REGEX, function (_, method, value)
		{
			value = decodeURIComponent(value);
			switch (method)
			{
				case '@':
				case '$elem':
					return navigate(trigger, value);
				case '?':
				case '$user':
					return prompt(value);
				case '$prop':
					return property(context, value);
				case '$attr':
					return context.getAttribute(value);
				case '$ELEM':
					return require(navigate(trigger, value));
				case '$USER':
					return require(prompt(value));
				case '$PROP':
					return require(property(context, value));
				case '$ATTR':
					return require(context.getAttribute(value));
			}
		});
	} catch (error)
	{
		if (error === REQUIRED)
			return null;
		throw error;
	}
	return encodeURI(result);
}