const ENTER = 13;
const HOME = 36;
const END = 35;
const UP = 38;
const DOWN = 40;

window.addEventListener("click", event =>
	{
		event = event || window.event;

		if (event.button !== 0)
			return;

		let action = event.target || event.srcElement;

		if (action.onclick || action.closest("a, button, input, select, textarea"))
			return;

		action = action.closest("tr[data-action], td[data-action], li[data-action], div[data-action]");
		if (!action)
			return;

		action.blur();

		switch (action.getAttribute("data-method") ?
			action.getAttribute("data-method").toLowerCase() : "get")
		{
			case "get":

				let link = document.createElement("a");

				if (event.ctrlKey)
					link.setAttribute("target", "_blank");
				else if (action.hasAttribute("data-target"))
					link.setAttribute("target", action.getAttribute("data-target"));

				if (action.hasAttribute("data-action"))
					link.setAttribute("href", action.getAttribute("data-action"));

				if (action.hasAttribute("data-on-hide"))
					link.setAttribute("data-on-hide", action.getAttribute("data-on-hide"));

				if (action.hasAttribute("title"))
					link.setAttribute("title", action.getAttribute("title"));

				if (action.hasAttribute("data-block"))
					link.setAttribute("data-block", action.getAttribute("data-block"));

				if (action.hasAttribute("data-alert"))
					link.setAttribute("data-alert", action.getAttribute("data-alert"));

				if (action.hasAttribute("data-confirm"))
					link.setAttribute("data-confirm", action.getAttribute("data-confirm"));

				if (!event.ctrlKey
					&& action.getAttribute("data-target") === "_dialog"
					&& (action.hasAttribute("data-navigate") || action.parentNode.hasAttribute("data-navigate")))
					link.navigator = Array.from(action.parentNode.children)
						.map(e => e.getAttribute("data-action"))
						.filter(e => e);

				link.addEventListener("show", e => action.dispatchEvent(new CustomEvent('show', {detail: {modal: e.detail.modal}})));
				link.addEventListener("hide", e => action.dispatchEvent(new CustomEvent('hide', {detail: {modal: e.detail.modal}})));

				document.body.appendChild(link);
				link.click();
				document.body.removeChild(link);
				break;
			case "post":

				let button = document.createElement("button");

				if (event.ctrlKey)
					button.setAttribute("target", "_blank");
				else if (action.hasAttribute("data-target"))
					button.setAttribute("target", action.getAttribute("data-target"));

				if (action.hasAttribute("data-action"))
					button.setAttribute("formaction", action.getAttribute("data-action"));

				if (action.hasAttribute("data-on-hide"))
					button.setAttribute("data-on-hide", action.getAttribute("data-on-hide"));

				if (action.hasAttribute("title"))
					button.setAttribute("title", action.getAttribute("title"));

				if (action.hasAttribute("data-block"))
					button.setAttribute("data-block", action.getAttribute("data-block"));

				if (action.hasAttribute("data-alert"))
					button.setAttribute("data-alert", action.getAttribute("data-alert"));

				if (action.hasAttribute("data-confirm"))
					button.setAttribute("data-confirm", action.getAttribute("data-confirm"));

				button.addEventListener("show", e => action.dispatchEvent(new CustomEvent('show', {detail: {modal: e.detail.modal}})));
				button.addEventListener("hide", e => action.dispatchEvent(new CustomEvent('hide', {detail: {modal: e.detail.modal}})));

				var form = action.closest("form");
				form.appendChild(button);
				button.click();
				form.removeChild(button);
				break;
		}

		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	});

window.addEventListener("keydown", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-action]");
	if (!action)
		return;

	switch (event.keyCode)
	{
		case ENTER:
			action.click();
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;

		case HOME:
			var siblings = Array.from(action.parentNode.childNodes)
				.filter(node => node.tagName.toLowerCase() === "tr");
			if (siblings.length !== 0
				&& siblings[0].getAttribute("tabindex"))
				siblings[0].focus();


			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
		case END:
			var siblings = Array.from(action.parentNode.childNodes)
				.filter(node => node.tagName.toLowerCase() === "tr");
			if (siblings.length !== 0
				&& siblings[siblings.length - 1].getAttribute("tabindex"))
				siblings[siblings.length - 1].focus();

			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
		case UP:
			if (action.previousElementSibling &&
				action.previousElementSibling.getAttribute("tabindex"))
				action.previousElementSibling.focus();

			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
		case DOWN:
			if (action.nextElementSibling &&
				action.nextElementSibling.getAttribute("tabindex"))
				action.nextElementSibling.focus();
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
	}
});

window.addEventListener("mouseover", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-action]");
	if (!action)
		return;

	if (!action.hasAttribute("tabindex"))
		action.setAttribute("tabindex", 1000);

	action.focus();
	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});