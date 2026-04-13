import DOM from './dom.js';
import property from './property.js';

const REQUIRED = new Error("__REQUIRED__");
const RESOLVE_REGEX = /(@attr|@ATTR|@prop|@PROP|@input|@INPUT|@value|@VALUE)\(\s*(?:"([^"]*)"|'([^']*)'|`([^`]*)`|([^)"'`?]*))(?:\?\?([^)]*))?\s*\)/g;

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

function coalesce(value, fallback)
{
	return value !== null
	&& value !== undefined
	&& (typeof value !== 'string' || value.trim() !== '')
		? value
		: (fallback ?? "");
}

export default function resolve(trigger, context, action)
{
	let result = action;
	try
	{
		result = result.replace(RESOLVE_REGEX, function (_, method, dq, sq, bq, uq, fb)
		{
			const parameter = decodeURIComponent(dq ?? sq ?? bq ?? uq ?? "");
			const fallback = decodeURIComponent(fb ?? "");
			switch (method)
			{
				case '@value':
					return convert(coalesce(value(trigger, parameter), fallback));
				case '@VALUE':
					return convert(require(value(trigger, parameter)));
				case '@input':
					return convert(coalesce(prompt(parameter), fallback));
				case '@INPUT':
					return convert(require(prompt(parameter)));
				case '@prop':
					return convert(coalesce(property(context, parameter), fallback));
				case '@PROP':
					return convert(require(property(context, parameter)));
				case '@attr':
					return convert(coalesce(trigger.getAttribute(parameter), fallback));
				case '@ATTR':
					return convert(require(trigger.getAttribute(parameter)));
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