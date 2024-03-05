import DOM from './dom.js';
const REQUIRED = new Error();
import EventHandler from './event-handler.js';
const RESOLVE_REGEX = /(\?|\?\?|!|!!|@|@@)\{([^}]*)\}/g;
export default function resolve(trigger, context, string)
{
	var result = decodeURI(string);
	try
	{
		result = result.replace(RESOLVE_REGEX,
			function (_, method, value)
			{
				value = decodeURIComponent(value);
				switch (method[0])
				{
					case '@':
					case '@@':
					{
						let element = DOM.navigate(trigger, value)
							.orElseThrow(() =>
								new Error(`${value} is not a valid element selector`));

						if (method.length == 2 && !element.value)
							throw REQUIRED;
						return element.value || "";
					}
					case '?':
					case '??':
					{
						let result = prompt(value);
						if (method.length == 2 && !result)
							throw REQUIRED;
						return result || "";
					}
					case '!':
					case '!!':
					{
						let result = context[value];
						if (method.length == 2 && !result)
							throw REQUIRED;
						return result;
					}
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

window.addEventListener("click", function (event)
{
	if (event.button)
		return;

	let element = event.target.closest("A, BUTTON");
	if (!element)
		return;

	let current = element.href
		|| element.getAttribute("formaction")
		|| (element.form || {}).action;

	if (!current || !current.match(RESOLVE_REGEX))
		return;

	EventHandler.cancel(event);

	let resolved = resolve(element, {}, current);
	if (!resolved)
		return;

	if (element.tagName === "A")
	{
		element.href = resolved;
		element.click();
		element.href = current;
	} else if (element.hasAttribute("formaction"))
	{
		element.setAttribute("formaction", resolved);
		element.click();
		element.setAttribute("formaction", current);
	} else
	{
		element.form.action = resolved;
		element.click();
		element.form.action = current;
	}
});
