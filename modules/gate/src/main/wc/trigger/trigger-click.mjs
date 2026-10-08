import EventHandler from './event-handler.js';
import trigger from './trigger-core.js';
import TriggerExtractor from './trigger-extractor.js';
import resolve from './resolve.js';
import validate from './validate.js';

window.addEventListener("click", function (event)
{
	if (!event.defaultPrevented)
		for (let element of event.composedPath())
		{
			if (!element.hasAttribute)
				continue;
			if (element.tagName === "A")
			{
				if (!validate(element))
					return EventHandler.cancel(event);

				let method = element.getAttribute("data-method") || "get";
				let target = element.getAttribute("target") || "_self";

				if (event.ctrlKey)
					target = "_blank";

				if (target.startsWith("@") || method !== "get")
				{
					trigger(event, element, element);
					return EventHandler.cancel(event);
				}

				let current = element.href;

				let resolved = resolve(element, event, current);
				if (resolved === current)
					return;

				if (!resolved)
					return EventHandler.cancel(event);

				element.href = resolved;
				element.click();
				element.href = current;
				return EventHandler.cancel(event);
			}

			if (element.tagName === "BUTTON")
			{
				if (!validate(element))
					return EventHandler.cancel(event);

				let method = element.getAttribute("formmethod") || "post";
				let target = element.getAttribute("formtarget") || "_self";

				if (event.ctrlKey)
					target = "_blank";

				if (target.startsWith("@") || (method !== "get" && method !== "post"))
				{
					trigger(event, element, element);
					return EventHandler.cancel(event);
				}

				let current = element.getAttribute("formaction") || (element.form || {}).action;
				if (!current)
					return;

				let resolved = resolve(element, event, current);
				if (resolved === current)
					return;

				if (!resolved)
					return EventHandler.cancel(event);

				element.setAttribute("formaction", resolved);
				element.click();
				element.setAttribute("formaction", current);
				return EventHandler.cancel(event);
			}

			if (TriggerExtractor.trigger(element) === "click")
			{
				if (validate(element))
					trigger(event, element, element);
				return event.stopPropagation();
			}
		}
});

window.addEventListener("submit", function (event)
{
	let form = event.composedPath()[0] || event.target;
	if (!validate(form))
		return event.preventDefault();

	let submitter = event.submitter || form;
	let method = submitter.getAttribute("formmethod") || form.method || "get";
	let target = submitter.getAttribute("formtarget") || form.target || "_self";
	if (target.startsWith("@") || (method !== "get" && method !== "post"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		trigger(event, submitter, submitter);
	}
});
