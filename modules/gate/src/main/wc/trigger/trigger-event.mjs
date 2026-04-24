import DOM from './dom.js';
import Parser from './parser.js';
import trigger from './trigger.js';
import DataURL from './data-url.js';
import CancelError from './cancel-error.js';
import EventHandler from './event-handler.js';
import GMessageDialog from './g-message-dialog.js';

export default class TriggerEvent extends CustomEvent
{
	#pipeline;
	constructor(name, cause, method, action, form, parameters, context, pipeline, signal)
	{
		super(name, {bubbles: true, composed: true, cancelable: false,
			detail: {cause, method, action, form, parameters, context, signal}});
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
			let {cause, method, action, form, context, signal} = this.detail;
			EventHandler.dispatch(path,
				new TriggerEvent(name,
					cause,
					method,
					result ?? action,
					form,
					parameters,
					context,
					this.#pipeline.slice(1),
					signal));
		} else
			EventHandler.dispatch(path,
				new TriggerSuccessEvent(this),
				new TriggerResolveEvent(this));
	}

	resolve(path)
	{
		EventHandler.dispatch(path, new TriggerResolveEvent(this));
	}

	static of(cause, method, action, form, target, context, signal)
	{
		if (target === "_top"
			|| target === "_self"
			|| target === "_blank"
			|| target === "_parent"
			|| target === "_dialog")
			return new TriggerEvent(target, cause, method, action, form, [], context, [], signal);

		let pipeline = Parser.pipeline(target);
		let {name, parameters} = pipeline[0];
		pipeline = pipeline.slice(1);
		return new TriggerEvent(name, cause, method, action, form, parameters, context, pipeline, signal);
	}

	toLog(label)
	{
		if (!label)
			throw new Error("Attempt to create unamed log");

		const {cause, method, action, form, parameters, context, signal} = this.detail;

		const log = {
			'@': this.type,
			label,
			time: new Date().toISOString(),
			method, action, parameters, context,
			aborted: !!signal?.aborted,
			pipeline: this.#pipeline?.map(p => p.name)
		};

		const el = this.composedPath?.()[0];
		if (el && el.tagName)
		{
			const id = el.id ? `#${el.id}` : '';
			const cls = el.classList?.length ? '.' + [...el.classList].join('.') : '';
			log.element = `${el.tagName}${id}${cls}`;
		}

		return log;
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
	if (!event.defaultPrevented
		&& event.detail.error.name !== "AbortError"
		&& !(event.detail.error instanceof CancelError))
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
