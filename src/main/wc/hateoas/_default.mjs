import './trigger.js';
import DOM from './dom.js';
import submit from './submit.js';
import GFilePicker from './g-file-picker.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

window.addEventListener("_self", event => proccess(event, window));
window.addEventListener("_top", event => proccess(event, window.top));
window.addEventListener("_parent", event => proccess(event, window.parent));
window.addEventListener("_blank", event => proccess(event, window.open()));
window.addEventListener("@frame", event => proccess(event, DOM.navigate(event,
		`[name='${event.detail.parameters[0]}'], #${event.detail.parameters[0]}`)
		.orElseThrow("Invalid target element")
		.contentWindow));

function proccess(event, target)
{
	event.target.dispatchEvent(new TriggerStartupEvent(event));
	if (event.detail.method === "get")
	{
		target.addEventListener("load", () =>
		{
			event.target.dispatchEvent(new TriggerSuccessEvent(event));
			event.target.dispatchEvent(new TriggerResolveEvent(event));
		}, {once: true});

		target.location = event.detail.action;
	} else if (event.detail.method === "post" && event.detail.form)
	{
		target.addEventListener("load", () =>
		{
			event.target.dispatchEvent(new TriggerSuccessEvent(event));
			event.target.dispatchEvent(new TriggerResolveEvent(event));
		}, {once: true});

		submit(event.detail.form, event.detail.method,
			event.detail.action, event.detail.target);
	} else
	{
		let path = event.composedPath();
		fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
			.then(ResponseHandler.response)
			.then(response =>
			{
				if (response.headers.get('content-type') === "text/html")
					return response.text()
						.then(text => new DOMParser().parseFromString(text, 'text/html').documentElement.childNodes)
						.then(nodes => target.document.documentElement.replaceChildren(...nodes))

				return GFilePicker.fetch(response);
			})
			.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
			.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
			.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
	}
}
