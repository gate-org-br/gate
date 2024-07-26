import DOM from './dom.js';
import Parser from './parser.js';
import trigger from './trigger.js';
import CancelError from './cancel-error.js';
import EventHandler from './event-handler.js';
import GMessageDialog from './g-message-dialog.js';

export default class TriggerEvent extends CustomEvent
{
	#pipeline;
	constructor(name, cause, method, action, form, parameters, context, pipeline)
	{
		super(name, {bubbles: true, composed: true, cancelable: false,
			detail: {cause, method, action, form, parameters, context}});
		this.#pipeline = pipeline;
	}

	failure(path, error)
	{
		EventHandler.dispatch(path,
			new TriggerFailureEvent(this, error),
			new TriggerResolveEvent(this));
	}

	success(path, result)
	{
		if (this.#pipeline.length)
		{
			let {name, parameters} = this.#pipeline[0];
			let {cause, method, action, form, context} = this.detail;
			EventHandler.dispatch(path,
				new TriggerEvent(name,
					cause,
					method,
					result || action,
					form,
					parameters,
					context,
					this.#pipeline.slice(1)));
		} else
			EventHandler.dispatch(path,
				new TriggerSuccessEvent(this),
				new TriggerResolveEvent(this));
	}

	resolve(path)
	{
		EventHandler.dispatch(path, new TriggerResolveEvent(this));
	}

	static of(cause, method, action, form, target, context)
	{
		if (target == "_top"
			|| target == "_self"
			|| target == "_blank"
			|| target == "_parent"
			|| target == "_dialog")
			return new TriggerEvent(target, cause, method, action, form, [], context, []);

		let pipeline = Parser.pipeline(target);
		let {name, parameters} = pipeline[0];
		pipeline = pipeline.slice(1);
		return new TriggerEvent(name, cause, method, action, form, parameters, context, pipeline);
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

function call(event, element, attribute)
{
	if (element.hasAttribute && element.hasAttribute(attribute))
	{
		let script = element.getAttribute(attribute);
		let func = new Function("event", `return ${script}`).bind(element);
		func()(event);
	}
}

window.addEventListener("trigger-startup", function (event)
{
	event.target.setAttribute("data-loading",
		event.detail.cause.detail.cause.type ||
		"data-loading");
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
	if (!event.defaultPrevented && !(event.detail.error instanceof CancelError))
		GMessageDialog.error(event.detail.error.message);

	for (let element of event.composedPath())
		call(event, element, "data-on:failure");
});

window.addEventListener("trigger-resolve", function (event)
{
	event.target.removeAttribute("data-loading");
	for (let element of event.composedPath())
		call(event, element, "data-on:resolve");
});