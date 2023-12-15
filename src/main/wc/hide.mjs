import GBlock from './g-block.js';


window.addEventListener("@hide", function (event)
{
	event.preventDefault();
	if (window.frameElement
		&& window.frameElement.dialog
		&& window.frameElement.dialog.hide)
		window.frameElement.dialog.hide();
	else
		window.close();
});

window.addEventListener("hide", function (event)
{
	let trigger = event.composedPath()[0] || event.target;
	if (trigger.hasAttribute("data-on-hide"))
	{
		GBlock.show("...");
		let type = trigger.getAttribute("data-on-hide");
		let parameter = null;
		let parentesis = type.indexOf("(");
		if (parentesis > 0)
		{
			if (!type.endsWith(")"))
				throw new Error(`${type} is not a valid data-on-hide value`);
			parameter = type.slice(parentesis + 1, -1);
			type = type.slice(0, parentesis);
		}

		switch (type)
		{
			case "submit":
				let form = trigger.closest("form");
				if (parameter)
					form = trigger.getRootNode().getElementById(parameter);
				else if (trigger.hasAttribute("data-form"))
					form = trigger.getRootNode().getElementById(trigger.getAttribute("data-form"));
				form.dispatchEvent(new SubmitEvent('submit', {bubbles: true, cancelable: true, submitter: trigger}));
				break;
			case "click":
				trigger.getRootNode().getElementById(parameter).click();
				break;
			case "reload":
				window.location.reload();
				break;
		}
	}
});