import CSV from './csv.js';

let causes = new WeakMap();

//function dispatch(cause, target, trigger)
//{
//	let method = /^([@][a-zA-Z]+)(\(([^)]+)\))?$/g.exec(trigger);
//	let type = method[1];
//	let parameters = method[3] ? CSV.parse(method[3]) : [];
//	let rootCause = causes.get(target) || cause;
//	window.dispatchEvent(new CustomEvent("trigger", {detail: {rootCause, cause, target, type, parameters}}));
//}

function dispatch(event, element, method, action, target)
{
	let cause = causes.get(event.target) || event;

	let trigger = /^([@][a-zA-Z]+)(\(([^)]+)\))?$/g.exec(target);
	target = trigger[1];
	let parameters = trigger[3] ? CSV.parse(trigger[3]) : [];

	if (!window.dispatchEvent(new CustomEvent("trigger", {cancelable: true, detail: {cause, element, method, action, target, parameters}})))
		event.preventDefault();
}

window.addEventListener("click", function (event)
{
	if (!event.controlKey)
	{
		let element = event.composedPath()
			.find(e => e.tagName === "A"
					&& e.target && e.target.startsWith("@"));
		if (element)
			return dispatch(event, element, "get", element.href, element.target);
	}
}, true);

window.addEventListener("submit", function (event)
{
	let element = event.target;
	let method = event.submitter.getAttribute("formmethod") || element.method;
	let target = event.submitter.getAttribute("formtarget") || element.target;
	let action = event.submitter.getAttribute("formaction") || element.action;
	if (target && target.startsWith("@"))
		dispatch(event, element, method, action, target);
}, true);

function get(target, ctrlKey)
{
	let link = document.createElement("a");
	causes.set(link, event);

	if (ctrlKey)
		link.setAttribute("target", "_blank");
	else if (target.hasAttribute("data-target"))
		link.setAttribute("target", target.getAttribute("data-target"));

	if (target.hasAttribute("data-action"))
		link.setAttribute("href", target.getAttribute("data-action"));

	if (target.hasAttribute("data-on-hide"))
		link.setAttribute("data-on-hide", target.getAttribute("data-on-hide"));

	if (target.hasAttribute("title"))
		link.setAttribute("title", target.getAttribute("title"));

	if (target.hasAttribute("data-block"))
		link.setAttribute("data-block", target.getAttribute("data-block"));

	if (target.hasAttribute("data-alert"))
		link.setAttribute("data-alert", target.getAttribute("data-alert"));

	if (target.hasAttribute("data-confirm"))
		link.setAttribute("data-confirm", target.getAttribute("data-confirm"));

	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
}

function post(target, ctrlKey)
{
	let button = document.createElement("button");
	causes.button(button, event);

	if (ctrlKey)
		button.setAttribute("target", "_blank");
	else if (target.hasAttribute("data-target"))
		button.setAttribute("formtarget", target.getAttribute("data-target"));

	if (target.hasAttribute("data-action"))
		button.setAttribute("formaction", target.getAttribute("data-action"));

	if (target.hasAttribute("data-on-hide"))
		button.setAttribute("data-on-hide", target.getAttribute("data-on-hide"));

	if (target.hasAttribute("title"))
		button.setAttribute("title", target.getAttribute("title"));

	if (target.hasAttribute("data-block"))
		button.setAttribute("data-block", target.getAttribute("data-block"));

	if (target.hasAttribute("data-alert"))
		button.setAttribute("data-alert", target.getAttribute("data-alert"));

	if (target.hasAttribute("data-confirm"))
		button.setAttribute("data-confirm", target.getAttribute("data-confirm"));


	let form = target.closest("form");
	form.appendChild(button);
	button.click();
	form.removeChild(button);
}

window.addEventListener("change", function (event)
{
	let target = event.target;

	if (target.dataset.method || target.dataset.action || target.dataset.target)
	{
		target.blur();
		switch ((target.dataset.method || "get").toLowerCase())
		{
			case "get":
				return get(target);
			case "post":
				return post(target);
		}
	}
});

window.addEventListener("click", function (event)
{
	if (!event.button)
	{
		let target = event.target;

		if (!target.onclick && !target.closest("a, button, input, select, textarea"))
		{
			target = target.closest("tr[data-action], td[data-action], li[data-action], div[data-action]");
			if (target)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				target.blur();
				switch ((target.dataset.method || "get").toLowerCase())
				{
					case "get":
						return get(target, event.ctrlKey);
					case "post":
						return post(target, event.ctrlKey);
				}
			}
		}
	}
});