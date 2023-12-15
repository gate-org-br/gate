import CSV from './csv.js';
import resolve from './resolve.js';
import navigate from './navigate.js';

const DEFAULT = new Map()
	.set("DIV", "load")
	.set("SPAN", "load")
	.set("LABEL", "load")
	.set("TR", "click")
	.set("TD", "click")
	.set("LI", "click")
	.set("INPUT", "change")
	.set("SELECT", "change")
	.set("TEXTAREA", "change");

function validate(element)
{
	if (element.hasAttribute("data-disabled"))
		return false;

	if (element.hasAttribute("data-cancel"))
		return alert(element.getAttribute("data-cancel"), 2000) && false;

	if (element.hasAttribute("data-confirm"))
		return confirm(element.getAttribute("data-confirm"));

	if (element.hasAttribute("data-alert"))
		return alert(element.getAttribute("data-alert")) || true;

	return true;
}

function dispatch(cause, element, method, action, target)
{
	if (validate(element))
	{
		method = (method || "get").trim();
		action = resolve((action || "").trim());
		target = (target || "_self").trim();

		let type = target;
		let parameters = [];
		let parentesis = target.indexOf("(");
		if (parentesis > 0)
		{
			if (!target.endsWith(")"))
				throw new Error(`${target} is not a valid target`);
			type = target.substring(0, parentesis);
			parameters = CSV.parse(target.slice(parentesis + 1, -1));
		}

		let form = null;
		if (method === "post"
			|| method === "put"
			|| method === "path")
		{
			form = element.form
				|| element.closest("form");
			if (element.hasAttribute("data-form"))
			{
				form = element.getRootNode()
					.getElementById(element.getAttribute("data-form"));
				if (!form)
					throw new Error(`${element.getAttribute("data-form")} is not a valid id`);
			}
		}

		element.dispatchEvent(new CustomEvent(type,
			{bubbles: true,
				composed: true,
				detail: {cause, method, action, target, parameters, form}}));
	}
}


window.addEventListener("click", function (event)
{
	if (!event.button)
	{
		for (let element of event.composedPath())
		{
			if (element.hasAttribute)
			{
				if (element.tagName === "A")
				{
					if (element.target && element.target.startsWith("@"))
					{
						event.preventDefault();
						dispatch(event, element, "get", element.href, element.target);
					} else if (!validate(element))
						event.preventDefault();
					return;
				} else if ((element.hasAttribute("data-trigger")
					|| element.hasAttribute("data-method")
					|| element.hasAttribute("data-action")
					|| element.hasAttribute("data-target"))
					&& (element.getAttribute("data-trigger") || DEFAULT.get(element.tagName)) === "click")
				{
					event.preventDefault();
					dispatch(event,
						element,
						element.getAttribute("data-method"),
						element.getAttribute("data-action"),
						element.getAttribute("data-target"));
					return;
				}
			}
		}
	}
});

window.addEventListener("submit", function (event)
{
	let submiter = event.submitter;
	let element = event.composedPath()[0] || event.target;
	let method = submiter.getAttribute("formmethod") || element.method;
	let target = submiter.getAttribute("formtarget") || element.target;
	let action = submiter.getAttribute("formaction") || element.action;
	if (target && target.startsWith("@"))
	{
		event.preventDefault();
		dispatch(event, submiter, method, action, target);
	} else if (!validate(submiter) || !validate(element))
		event.preventDefault();
});

window.addEventListener("change", function (event)
{
	let element = event.target || event.composedPath()[0];
	if ((element.hasAttribute("data-trigger")
		|| element.hasAttribute("data-method")
		|| element.hasAttribute("data-action")
		|| element.hasAttribute("data-target"))
		&& (element.getAttribute("data-trigger") || DEFAULT.get(element.tagName)) === "change")
		dispatch(event, element, element.dataset.method, element.dataset.action, element.dataset.target);
});

window.addEventListener("load", event => Array.from(document.querySelectorAll('*'))
		.filter(e => e.hasAttribute("data-trigger")
				|| e.hasAttribute("data-method")
				|| e.hasAttribute("data-action")
				|| e.hasAttribute("data-target"))
		.filter(e => (e.getAttribute("data-trigger") || DEFAULT.get(e.tagName)) === "load")
		.forEach(e => dispatch(event, e, e.dataset.method, e.dataset.action, e.dataset.target)));

window.addEventListener("@trigger", function (event)
{
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let element = navigate(source, path).orElseThrow("Invalid target element");

	let method = element.method || element.getAttribute("formmethod") || element.getAttribute("data-method");
	let action = element.action || element.href || element.getAttribute("formaction") || element.getAttribute("data-action");
	let target = element.target || element.getAttribute("formtarget") || element.getAttribute("data-target");

	dispatch(event, element, method, action, target);
});