window.addEventListener("change", event =>
{
	let action = event.target || event.srcElement;

	if ((!action.tagName === "INPUT"
		&& !action.tagName === "SELECT")
		|| (!action.hasAttribute("data-method")
			&& !action.hasAttribute("data-action")
			&& !action.hasAttribute("data-target")))
		return;

	action.blur();

	switch (action.getAttribute("data-method") ? action.getAttribute("data-method").toLowerCase() : "get")
	{
		case "get":

			let link = document.createElement("a");

			if (action.hasAttribute("data-target"))
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

			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			break;
		case "post":
			let button = document.createElement("button");

			if (action.hasAttribute("data-target"))
				button.setAttribute("target", action.getAttribute("data-target"));

			if (action.hasAttribute("data-action"))
				button.setAttribute("formaction", action.getAttribute("data-action"));

			if (action.hasAttribute("data-on-hide"))
				button.setAttribute("data-on-hide", action.getAttribute("data-on-hide"));
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


			var form = action.closest("form");
			form.appendChild(button);
			button.click();
			form.removeChild(button);
			break;
	}
});