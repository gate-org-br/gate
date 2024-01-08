/* global fetch */

import './trigger.js';
import DOM from './dom.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

const undostack = new WeakMap();
window.addEventListener("@innerHTML", function (event)
{
	let target = DOM.navigate(event, event.detail.parameters[0])
		.orElseThrow(`${event.detail.parameters[0]} is not a valid target element`);
	let operation = event.detail.parameters[1] || "none";

	event.target.dispatchEvent(new TriggerStartupEvent(event));

	if (operation === "pull"
		&& undostack.has(target)
		&& undostack.get(target).length)
	{
		let fragment = document.createRange()
			.createContextualFragment(undostack.get(target).pop());
		target.replaceChildren(...Array.from(fragment.childNodes));
		event.target.dispatchEvent(new TriggerSuccessEvent(event));
		event.target.dispatchEvent(new TriggerResolveEvent(event));
	} else
	{
		let path = event.composedPath();
		fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
			.then(ResponseHandler.text)
			.then(result =>
			{
				if (operation === "push")
					if (undostack.has(target))
						undostack.get(target).push(target.innerHTML);
					else
						undostack.set(target, [target.innerHTML]);
				else
					undostack.delete(target);

				let fragment = document.createRange()
					.createContextualFragment(result);
				target.replaceChildren(...Array.from(fragment.childNodes));
			})
			.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
			.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
			.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
	}
});