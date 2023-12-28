import CSV from './csv.js';
import DOM from './dom.js';
import resolve from './resolve.js';
import validate from './validate.js';
import EventHandler from './event-handler.js';
import TriggerEvent from './trigger-event.js';

export default function trigger(cause, element)
{
	if (!validate(element))
		return;

	if (element.tagName === "A" && !(element.target || "_self").startsWith("@"))
		return element.click();
	if (element.tagName === "BUTTON"
		&& element.form
		&& !(element.getAttribute("formtarget") || element.form.target || "_self").startsWith("@"))
		return element.click();

	let form = null;
	if (element.tagName === "FORM")
		form = element;
	else if (element.form)
		form = element.form;
	else if (element.hasAttribute("data-form"))
		form = element.getRootNode().getElementById(element.getAttribute("data-form"));
	else if (["post", "put", "path"].includes((element.getAttribute("formmethod") || element.getAttribute("data-method") || "none").toLowerCase()))
		form = element.closest("form");

	let method = (element.getAttribute("formmethod") || element.getAttribute("data-method") || form?.method || "get").toLowerCase();
	let action = element.href || element.getAttribute("formaction") || element.getAttribute("data-action") || form?.action || "";
	let target = element.target || element.getAttribute("formtarget") || element.getAttribute("data-target") || form?.target || "_self";

	if (!target.startsWith("_") && !target.startsWith("@"))
		target = `@frame(${target})`;

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

	if (!form || element.hasAttribute("formnovalidate") || form.hasAttribute("novalidate") || form.reportValidity())
	{
		action = resolve(action);
		element.dispatchEvent(new TriggerEvent(type, cause, method, action, target, parameters, form));
	}
}

const DEFAULT = new Map()
	.set("DIV", "load")
	.set("SPAN", "load")
	.set("LABEL", "load")
	.set("TR", "click")
	.set("A", "click")
	.set("BUTTON", "click")
	.set("TD", "click")
	.set("LI", "click")
	.set("INPUT", "change")
	.set("SELECT", "change")
	.set("TEXTAREA", "change");

window.addEventListener("click", function (event)
{
	if (event.ctrlKey)
		return;

	for (let element of event.composedPath())
	{
		if (!element.hasAttribute)
			continue;
		if (element.tagName === "A")
		{
			if (element.target.startsWith("@"))
			{
				trigger(event, element);
				return EventHandler.cancel(event);
			}

			if (!validate(element))
				return EventHandler.cancel(event);

			return;
		}

		if (element.tagName === "BUTTON")
		{
			let method = element.getAttribute("formmethod") || element.form?.method || "get";
			let target = element.getAttribute("formtarget") || element.form?.target || "_self";

			if (target.startsWith("@") || (method !== "get" && method !== "post"))
			{
				trigger(event, element);
				return EventHandler.cancel(event);
			}

			if (!validate(element))
				return EventHandler.cancel(event);

			return;
		}

		if ((element.hasAttribute("data-trigger")
			|| element.hasAttribute("data-method")
			|| element.hasAttribute("data-action")
			|| element.hasAttribute("data-target"))
			&& (element.getAttribute("data-trigger") || DEFAULT.get(element.tagName)) === "click")
		{
			trigger(event, element);
			return EventHandler.cancel(event);
		}
	}
}, true);

window.addEventListener("submit", function (event)
{
	let form = event.composedPath()[0] || event.target;

	let submiter = event.submitter;
	let method = submiter.getAttribute("formmethod") || form.method || "get";
	let target = submiter.getAttribute("formtarget") || form.target || "_self";
	if (target.startsWith("@") || (method !== "get" && method !== "post"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		trigger(event, submiter || form);
	} else if (!validate(form))
		event.preventDefault();
});

window.addEventListener("change", function (event)
{
	let element = event.target || event.composedPath()[0];
	if ((element.hasAttribute("data-trigger")
		|| element.hasAttribute("data-method")
		|| element.hasAttribute("data-action")
		|| element.hasAttribute("data-target"))
		&& (element.dataset.trigger || DEFAULT.get(element.tagName)) === "change")
		trigger(event, element);
});

window.addEventListener("mouseover", function (event)
{
	let element = event.target || event.composedPath()[0];
	if ((element.hasAttribute("data-trigger")
		|| element.hasAttribute("data-method")
		|| element.hasAttribute("data-action")
		|| element.hasAttribute("data-target"))
		&& (element.dataset.trigger || DEFAULT.get(element.tagName)) === "mouseover")
		trigger(event, element);
});

window.addEventListener("load", event => Array.from(document.querySelectorAll('*'))
		.filter(e => e.hasAttribute("data-trigger")
				|| e.hasAttribute("data-method")
				|| e.hasAttribute("data-action")
				|| e.hasAttribute("data-target"))
		.filter(e => (e.dataset.trigger || DEFAULT.get(e.tagName)) === "load")
		.forEach(e => trigger(event, e, e.dataset.method, e.dataset.action, e.dataset.target)));

window.addEventListener("@trigger", function (event)
{
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let element = DOM.navigate(source, path).orElseThrow("Invalid target element");
	trigger(event, element);
});