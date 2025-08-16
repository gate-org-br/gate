import DOM from './dom.js';
import property from './property.js';

const REQUIRED = new Error("__REQUIRED__");
const RESOLVE_REGEX = /(@attr|@ATTR|@prop|@PROP|@input|@INPUT|@value|@VALUE)\((?:(?:\"([^\"]*)\")|(?:'([^']*)')|(?:`([^`]*)`)|([^)]*))\)/g;

function value(trigger, selector)
{
	return DOM.navigate(trigger, selector)
		.orElseThrow(() =>
			new Error(`${selector} is not a valid element selector`))
		.value || "";
}

function require(value)
{
	if (value === null || value === undefined
		|| (typeof value === 'string' && value.trim() === ''))
		throw REQUIRED;
	return value;
}

function convert(value)
{
	return encodeURIComponent(String(value ?? ""));
}

export default function resolve(trigger, context, action)
{
	var result = action;
	try
	{
		result = result.replace(RESOLVE_REGEX, function (_, method, dq, sq, bq, bare)
		{
			const parameter = decodeURIComponent(dq ?? sq ?? bq ?? bare ?? "");
			switch (method)
			{
				case '@value':
					return convert(value(trigger, parameter));
				case '@input':
					return convert(prompt(parameter));
				case '@prop':
					return convert(property(context, parameter));
				case '@attr':
					return convert(context.getAttribute(parameter));
				case '@VALUE':
					return convert(require(value(trigger, parameter)));
				case '@INPUT':
					return convert(require(prompt(parameter)));
				case '@PROP':
					return convert(require(property(context, parameter)));
				case '@ATTR':
					return convert(require(context.getAttribute(parameter)));
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