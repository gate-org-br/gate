import DataURL from './data-url.js';
import DOM from './dom.js';
import property from './property.js';
import trigger from './trigger-core.js';

window.addEventListener("sse", function (event)
{
	const REGEX = /^sse(?:\(([A-Za-z_$][A-Za-z0-9_$]*)\))?$/;
	DOM.traverse(document, e => e.hasAttribute &&
		e.hasAttribute("data-trigger")
		&& REGEX.test(e.getAttribute("data-trigger")), element =>
	{
		const name = element.getAttribute("data-trigger");
		if (name.startsWith("sse("))
		{
			const type = name.slice(4, -1);
			if (event.detail.type !== type)
				return;
		}

		const context = event.detail.detail;

		if (!Array.from(element.attributes)
			.filter(e => e.name.startsWith("data-sse:"))
			.every(e =>
			{
				const value = property(context, e.name.substring(9));
				switch (typeof value)
				{
					case "number":
						return Number(e.value) === value;
					case "boolean":
						return (e.value === "true") === value;
					default:
						return e.value === value;
				}
			}))
			return;

		let action = element.getAttribute("href")
			|| element.getAttribute("action")
			|| element.getAttribute("formaction")
			|| element.getAttribute("data-action")
			|| new DataURL("application/json",
				JSON.stringify(context)).toString();

		trigger(event, element, context, action);
	});
});