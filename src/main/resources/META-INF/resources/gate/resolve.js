const RESOLVE_REGEX = /[@!?]\{([^}]*)\}/g;
export default function resolve(string)
{
	var result = decodeURI(string);
	result = result.replace(RESOLVE_REGEX,
		function (method, value)
		{
			value = decodeURIComponent(value);
			switch (method[0])
			{
				case '@':
					let element = document.getElementById(value);
					if (element)
						return encodeURIComponent(element.value || "");
					else
						throw new Error(`${value} is not a valid element id`)
				case '!':
					return confirm(value) ? "true" : "false";
				case '?':
					return encodeURIComponent(prompt(value) || "");
			}
		});
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

				let clone = element.cloneNode(false);
				clone.href = resolve(element.href);
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

				let clone = element.cloneNode(false);
				clone.setAttribute("formaction", resolve(action));
				element.parentNode.appendChild(clone);
				clone.click();
				clone.remove();
			}
			return;
		}
	}
});
