import GBlock from './g-block.js';
import trigger from './trigger.js';
import validate from './validate.js';
import GMessageDialog from './g-message-dialog.js';

export default class TriggerEvent extends CustomEvent
{
	constructor(type, cause, method, action, target, parameters, form)
	{
		super(type, {bubbles: true, composed: true, cancelable: false,
			detail: {cause, method, action, target, parameters, form}});
	}
}

export class TriggerStartupEvent extends CustomEvent
{
	constructor(cause)
	{
		super("trigger-startup", {bubbles: true, composed: true, cancelable: false, detail: {cause}});
	}
}

export class TriggerSuccessEvent extends CustomEvent
{
	constructor(cause)
	{
		super("trigger-success", {bubbles: true, composed: true, cancelable: false, detail: {cause}});
	}
}

export class TriggerFailureEvent extends CustomEvent
{
	constructor(cause, error)
	{
		super("trigger-failure", {bubbles: true, composed: true, cancelable: false, detail: {cause, error}});
	}
}

export class TriggerResolveEvent extends CustomEvent
{
	constructor(cause)
	{
		super("trigger-resolve", {bubbles: true, composed: true, cancelable: false, detail: {cause}});
	}
}


function triggerEvent(event)
{
	return function (element)
	{
		if (typeof element === "string")
			element = this.target.getRootNode().querySelector(element);
		trigger(event.detail.cause, element);
	};
}

function call(event, element, attribute)
{
	if (element.hasAttribute && element.hasAttribute(attribute))
		new Function("event", "trigger", element.getAttribute(attribute))
			.bind(element)(event, triggerEvent(event));
}

window.addEventListener("trigger-startup", function (event)
{
	if (event.detail.cause.detail.cause.type === "click")
		GBlock.show("...");
	for (let element of event.composedPath())
		call(event, element, "data-on:startup");
});

window.addEventListener("trigger-success", function (event)
{
	for (let element of event.composedPath())
		call(event, element, "data-on:success");
});

window.addEventListener("trigger-failure", function (event)
{
	if (!event.defaultPrevented)
		GMessageDialog.error(event.detail.error.message);

	for (let element of event.composedPath())
		call(event, element, "data-on:failure");
});

window.addEventListener("trigger-resolve", function (event)
{
	GBlock.hide();
	for (let element of event.composedPath())
		call(event, element, "data-on:resolve");
});