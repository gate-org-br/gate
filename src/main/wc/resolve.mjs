import DOM from './dom.js';
const REQUIRED = new Error();
const RESOLVE_REGEX = /(\?|\?\?|!|!!|@|@@|#|##)\{([^}]*)\}/g;
export default function resolve(trigger, string)
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
					case '#':
					case '##':
					{
						let element = document.getElementById(value);
						if (!element)
							throw new Error(`${value} is not a valid element id`);

						if (method.length == 2 && !element.value)
							throw REQUIRED;
						return element.value || "";
					}
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
						let result = prompt(value);
						if (method.length == 2 && !result)
							throw REQUIRED;
						return result ? "true" : "false";
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

	for (let element of event.composedPath())
	{
		if (element.tagName === "A")
		{
			if (element.href.match(RESOLVE_REGEX))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				let action = resolve(element, element.href);
				if (!action)
					return;

				let clone = element.cloneNode(false);
				clone.href = action;
				element.parentNode.appendChild(clone);
				clone.click();
				clone.remove();
			}
			return;
		}

		if (element.tagName === "BUTTON")
		{
			if (!element.form)
				return;

			let action = element.getAttribute("formaction") || element.form.action;

			if (action && action.match(RESOLVE_REGEX))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				action = resolve(element, element.action);
				if (!action)
					return;

				let clone = element.cloneNode(false);
				clone.setAttribute("formaction", element);
				element.parentNode.appendChild(clone);
				clone.click();
				clone.remove();
			}
			return;
		}
	}
});
