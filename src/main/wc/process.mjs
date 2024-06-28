import fetchEventSource	from './fetch-event-source.js';

export default function process(id, name, method, action, body)
{
	return new Promise((resolve, reject) =>
	{
		window.top.dispatchEvent(new CustomEvent('ProcessRequest',
			{detail: {id, name}}));

		if (body instanceof HTMLFormElement)
			body = new FormData(body);

		fetchEventSource(action,
			{
				method,
				body,
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