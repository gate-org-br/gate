import SSE from './sse.js';

function parseEvent(string)
{
	let event = JSON.parse(String(atob(string)));
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
	return event;
}

export default function process(id, name, method, action, payload)
{
	return new Promise((resolve, reject) =>
	{
		window.top.dispatchEvent(new CustomEvent('ProcessRequest',
			{detail: {id, name}}));

		method = (method || "GET").toUpperCase();

		if (payload instanceof HTMLFormElement)
			payload = new FormData(payload);

		let source = new SSE(action, {start: false, method, payload});

		source.addEventListener("Progress", (event) =>
		{
			event = parseEvent(event.data);

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
		});


		source.addEventListener("Result", (event) =>
		{
			event = parseEvent(event.data);

			const {contentType = 'text/plain;charset=utf-8', filename, data} = event;
			window.top.dispatchEvent(new CustomEvent('ProccessResult', {id, name, contentType, filename, data}));

			const headers = new Headers();
			headers.append("Content-Type", contentType);
			if (filename)
				headers.append("Content-Disposition",
					`attachment; filename="${filename}"`);

			resolve(new Response(data, {status: 200, statusText: 'OK', headers}));
		});

		source.addEventListener("error", e =>
		{
			window.top.dispatchEvent(new CustomEvent('ProcessError',
				{detail: {id, name, text: "Conexão perdida com o servidor"}}));
			reject(e.text);
		});

		source.addEventListener("abort", e =>
		{
			window.top.dispatchEvent(new CustomEvent('ProcessError',
				{detail: {id, name, text: "Conexão perdida com o servidor"}}));
			reject("Connection closed");
		});

		source.addEventListener("readystatechange", e =>
		{
			if (e.readyState === 2)
				resolve(null);
		});

		source.stream();
	});
}