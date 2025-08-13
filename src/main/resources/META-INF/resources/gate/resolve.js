import DOM from './dom.js';
import property from './property.js';
import EventHandler from './event-handler.js';

const REQUIRED = new Error();
const RESOLVE_REGEX = /(@attr|@ATTR|@prop|@PROP|@input|@INPUT|@value|@VALUE)\(([^)]*?)\)/g;

function navigate(trigger, value)
{
	return DOM.navigate(trigger, value)
		.orElseThrow(() =>
			new Error(`${value} is not a valid element selector`))
		.value || "";
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
				case '@value':
					return navigate(trigger, value);
				case '@input':
					return prompt(value);
				case '@prop':
					return property(context, value);
				case '@attr':
					return context.getAttribute(value);
				case '@VALUE':
					return require(navigate(trigger, value));
				case '@INPUT':
					return require(prompt(value));
				case '@PROP':
					return require(property(context, value));
				case '@ATTR':
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