import DOM from './dom.js';
import DataURL from './data-url.js';
import resolve from './resolve.js';
import validate from './validate.js';
import EventHandler from './event-handler.js';
import TriggerEvent, { TriggerStartupEvent } from './trigger-event.js';

export default function trigger(cause, element, context, action)
{
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
	action = action || element.getAttribute("href") || element.getAttribute("formaction") || element.getAttribute("data-action") || form?.action || "";
	let target = element.target || element.getAttribute("formtarget") || element.getAttribute("data-target") || form?.target || "_self";

	if (cause.ctrlKey && cause.type === "click")
		target = "_blank";

	if (!target.startsWith("_") && !target.startsWith("@"))
		target = `@frame(${target})`;

	if (!form
		|| element.hasAttribute("formnovalidate")
		|| form.hasAttribute("novalidate")
		|| form.reportValidity())
	{
		action = resolve(element, context || {}, action);
		let event = TriggerEvent.of(cause, method, action, form, target, context);
		element.dispatchEvent(new TriggerStartupEvent(event));
		element.dispatchEvent(event);
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
			if (!validate(element))
				return EventHandler.cancel(event);

			if (element.target.startsWith("@"))
			{
				trigger(event, element);
				return EventHandler.cancel(event);
			}

			return;
		}

		if (element.tagName === "BUTTON")
		{
			if (!validate(element))
				return EventHandler.cancel(event);

			let method = element.getAttribute("formmethod") || "post";
			let target = element.getAttribute("formtarget") || "_self";

			if (target.startsWith("@") || (method !== "get" && method !== "post"))
			{
				trigger(event, element);
				return EventHandler.cancel(event);
			}

			return;
		}

		if ((element.hasAttribute("data-trigger")
			|| element.hasAttribute("data-method")
			|| element.hasAttribute("data-action")
			|| element.hasAttribute("data-target"))
			&& (element.getAttribute("data-trigger") || DEFAULT.get(element.tagName)) === "click")
		{
			if (validate(element))
				trigger(event, element);
			return EventHandler.cancel(event);
		}
	}
});

window.addEventListener("submit", function (event)
{
	let form = event.composedPath()[0] || event.target;
	if (!validate(form))
		return	event.preventDefault();

	let submiter = event.submitter || form;
	let method = submiter.getAttribute("formmethod") || form.method || "get";
	let target = submiter.getAttribute("formtarget") || form.target || "_self";
	if (target.startsWith("@") || (method !== "get" && method !== "post"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		trigger(event, submiter);
	}
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

window.addEventListener("input", function (event)
{
	let element = event.target || event.composedPath()[0];
	if ((element.hasAttribute("data-trigger")
		|| element.hasAttribute("data-method")
		|| element.hasAttribute("data-action")
		|| element.hasAttribute("data-target"))
		&& (element.dataset.trigger || DEFAULT.get(element.tagName)) === "input")
		trigger(event, element);
});

window.addEventListener("mouseover", function (event)
{
	let element = event.target || event.composedPath()[0];
	if (element.hasAttribute("data-trigger")
		|| element.hasAttribute("data-method")
		|| element.hasAttribute("data-action")
		|| element.hasAttribute("data-target"))
	{
		let type = element.dataset.trigger
			|| DEFAULT.get(element.tagName);
		if (type && type.startsWith("hover(") && type.endsWith(")"))
		{
			const timeout = setTimeout(() => trigger(event, element), Number(type.slice(6, -1)) * 1000);
			element.addEventListener("mouseleave", () => clearTimeout(timeout), {once: true});
		} else if (type === "hover")
		{
			const timeout = setTimeout(() => trigger(event, element), 1000);
			element.addEventListener("mouseleave", () => clearTimeout(timeout), {once: true});
		} else if (type === "mouseenter")
			trigger(event, element);
	}
});

window.addEventListener("load", event =>
{
	Array.from(document.querySelectorAll('*'))
		.filter(e => e.hasAttribute("data-trigger")
				|| e.hasAttribute("data-method")
				|| e.hasAttribute("data-action")
				|| e.hasAttribute("data-target"))
		.filter(e => (e.dataset.trigger || DEFAULT.get(e.tagName)) === "load")
		.forEach(e => trigger(event, e, e.dataset.method, e.dataset.action, e.dataset.target));

	DOM.traverse(document, e => e.nodeType === Node.ELEMENT_NODE,
		e => e.dispatchEvent(new CustomEvent("connected", {bubbles: true, composed: true})));
});

window.addEventListener("load", function (event)
{
	let selector = window.location.hash;
	if (selector)
	{
		let element = document.querySelector(selector);
		if (element)
		{
			let target = element.target
				|| element.getAttribute("formtarget")
				|| element.getAttribute("data-target");
			if (target && target.startsWith("@"))
				trigger(event, element);
		}
	}
});

new MutationObserver(mutations => mutations
		.flatMap(e => Array.from(e.addedNodes))
		.forEach(root => DOM.traverse(root, e => e.nodeType === Node.ELEMENT_NODE,
				e => e.dispatchEvent(new CustomEvent("connected", {bubbles: true, composed: true})))))
	.observe(document, {childList: true, subtree: true});

window.addEventListener("connected", function (event)
{
	let element = event.composedPath()[0] || event.target;

	let type = element.getAttribute("data-trigger") || "";

	if (type === "connected")
		trigger(event, element);
	else if (type.match(/^every\([0-9]+\)$/))
		setInterval(() => trigger(event, element), Number(trigger.slice(6, -1)) * 1000);
	else if (type === "drag-and-drop")
	{
		Array.from(element.children).forEach(e =>
		{
			if (e.draggable)
				e.addEventListener("dragstart", dragstart =>
					dragstart.dataTransfer.setData("text/plain",
						e.value
						|| e.getAttribute("value")
						|| e.getAttribute("data-value")
						|| ""));

			e.addEventListener("drop", function (drop)
			{
				drop.stopPropagation();
				let source = drop.dataTransfer.getData("text/plain");
				let target = e.value
					|| e.getAttribute("value")
					|| e.getAttribute("data-value")
					|| element.value
					|| element.getAttribute("value")
					|| element.getAttribute("data-value")
					|| "";
				trigger(drop, element, {source, target});
			});

			e.addEventListener("dragover", dragover => dragover.preventDefault());
		});

		element.addEventListener("dragover", dragover => dragover.preventDefault());
		element.addEventListener("drop", function (drop)
		{
			drop.stopPropagation();
			let source = drop.dataTransfer.getData("text/plain");
			let target = element.value
				|| element.getAttribute("value")
				|| element.getAttribute("data-value")
				|| "";
			trigger(drop, element, {source, target});
		});
	}
});


window.addEventListener("sse", function (event)
{
	DOM.traverse(document, e => e.hasAttribute("data-trigger") && e.getAttribute("data-trigger").match("sse(\([.+]\))?"), element =>
	{
		let action = element.getAttribute("href")
			|| element.getAttribute("formaction")
			|| element.getAttribute("action")
			|| element.getAttribute("data-action")
			|| new DataURL("application/json", JSON.stringify(event.detail)).toString();

		if (element.getAttribute("data-trigger").startsWith("sse("))
		{
			let predicate = element.getAttribute("data-trigger").slice(4, -1);
			if (!new Function("event", `return ${predicate}`).bind(element)()(event.detail))
				return;
		}

		trigger(event, element, event.detail, action);
	});
});