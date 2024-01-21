import DOM from './dom.js';
import Parser from './parser.js';
import trigger from './trigger.js';
import validate from './validate.js';
import CancelError from './cancel-error.js';
import EventHandler from './event-handler.js';
import GMessageDialog from './g-message-dialog.js';

export default class TriggerEvent extends CustomEvent
{
	#pipeline;
	constructor(name, cause, method, action, form, parameters, pipeline)
	{
		super(name, {bubbles: true, composed: true, cancelable: false,
			detail: {cause, method, action, form, parameters}});
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
			let {cause, method, action, form} = this.detail;
			EventHandler.dispatch(path,
				new TriggerEvent(name,
					cause,
					method,
					result || action,
					form,
					parameters,
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

	static of(cause, method, action, form, target)
	{
		let pipeline = Parser.pipeline(target);
		let {name, parameters} = pipeline[0];
		pipeline = pipeline.slice(1);
		return new TriggerEvent(name, cause, method, action, form, parameters, pipeline);
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


function triggerEvent()
{

	return function ()
	{
		switch (arguments.length)
		{
			case 0:
				return trigger(event.detail.cause, this);
			case 1:
				let element = arguments[0];
				if (typeof element === "string")
					element = DOM.navigate(event.target, element)
						.orElseThrow(`${element} is not a valid selector.`);
				return trigger(event.detail.cause, element);
			case 2:
			case 3:
			case 4:
				return this.dispatchEvent(new TriggerEvent(event.detail.cause,
					arguments[0], arguments[1], arguments[2], arguments[3]
					? DOM.navigate(this, arguments[3])
					.map(e => new FormData(e))
					.orElseThrow(`${element} is not a valid selector.`) : null));
		}
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
	event.target.setAttribute("data-loading", "data-loading");
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
		GMessageDialog.error(event.detail.error);

	for (let element of event.composedPath())
		call(event, element, "data-on:failure");
});

window.addEventListener("trigger-resolve", function (event)
{
	event.target.removeAttribute("data-loading");
	for (let element of event.composedPath())
		call(event, element, "data-on:resolve");
});