import EventHandler from './event-handler.js';
import TriggerEvent from './trigger-event.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import fetchEventSource	from './fetch-event-source.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

let sequence = 1;

export default function process(request, id, name)
{
	return new Promise((resolve, reject) =>
	{
		window.top.dispatchEvent(new CustomEvent('ProcessRequest',
			{detail: {id, name}}));

		fetchEventSource(request, {
			onmessage: (event) =>
			{
				event = JSON.parse(String(atob(event.data)));
				event.toString = function ()
				{
					if (this.done && this.done !== -1)
						if (this.todo && this.todo !== -1)
							return this.done + "/" + this.todo;
						else
							return this.done.toString();
					else
						return "...";
				};
				switch (event.event)
				{
					case "Progress":
						switch (event.status)
						{
							case "CREATED":
								window.top.dispatchEvent(new CustomEvent('ProcessPending',
									{detail: {id, name, todo: event.todo, done: event.done, text: event.text, progress: event.toString()}}));
								break;
							case "PENDING":
								window.top.dispatchEvent(new CustomEvent('ProcessPending',
									{detail: {id, name, todo: event.todo, done: event.done, text: event.text, progress: event.toString()}}));
								break;
							case "COMMITED":
								window.top.dispatchEvent(new CustomEvent('ProcessCommited',
									{detail: {id, name, todo: event.todo, done: event.done, text: event.text, progress: event.toString()}}));
								break;
							case "CANCELED":
								window.top.dispatchEvent(new CustomEvent('ProcessCanceled',
									{detail: {id, name, todo: event.todo, done: event.done, text: event.text, progress: event.toString()}}));
								break;
						}
						break;
					case "Result":
						const {contentType = 'text/plain;charset=utf-8', filename, data} = event;
						window.top.dispatchEvent(new CustomEvent('ProccessResult', {id, name, contentType, filename, data}));

						const headers = new Headers();
						headers.append("Content-Type", contentType);
						if (filename)
							headers.append("Content-Disposition",
								`attachment; filename="${filename}"`);

						resolve(new Response(data, {status: 200, statusText: 'OK', headers}));
						break;
				}
			},

			onerror: e => {
				window.top.dispatchEvent(new CustomEvent('ProcessError',
					{detail: {id, name, text: "Conexão perdida com o servidor"}}));
				reject(e.text);
			}
		});
	});
}

window.addEventListener("@progress", function (event)
{
	let cause = event.detail.cause;
	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let target = event.detail.parameters[0] || "@none";

	let processName = trigger.title || "Progresso";
	let processId = trigger.id || "proccess@" + sequence++;

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	process(RequestBuilder.build(event.detail.method,
		event.detail.action,
		event.detail.form), processId, processName)
		.then(ResponseHandler.dataURL)
		.then(action => trigger.dispatchEvent(new TriggerEvent(cause, "get", action, target)))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});

