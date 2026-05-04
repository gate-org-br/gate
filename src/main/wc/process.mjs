import SSE from './sse.js';
import RequestBuilder from './request-builder.js';

function parseEvent(string)
{
	const bytes = Uint8Array.from(atob(string), c => c.charCodeAt(0));
	const event = JSON.parse(new TextDecoder().decode(bytes));

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

function dispatchProgress(id, name, event)
{
	const detail =
		{id, name, todo: event.todo, done: event.done, text: event.text, progress: event.toString()};

	switch (event.status)
	{
		case "CREATED":
		case "PENDING":
			window.top.dispatchEvent(new CustomEvent('ProcessPending', {detail}));
			break;
		case "COMMITED":
			window.top.dispatchEvent(new CustomEvent('ProcessCommited', {detail}));
			break;
		case "CANCELED":
			window.top.dispatchEvent(new CustomEvent('ProcessCanceled', {detail}));
			break;
	}
}

function resolveResult(id, name, event, resolve)
{
	const {contentType = 'text/plain;charset=utf-8', filename, data} = event;
	window.top.dispatchEvent(new CustomEvent('ProccessResult',
		{detail: {id, name, contentType, filename, data}}));

	const headers = new Headers();
	headers.append("Content-Type", contentType);
	if (filename)
		headers.append("Content-Disposition",
			`attachment; filename="${filename}"`);

	resolve(new Response(data, {status: 200, statusText: 'OK', headers}));
}

function resolveRedirect(id, name, event, resolve, reject)
{
	fetch(RequestBuilder.build("get", event.url))
		.then(response =>
		{
			if (!response.ok)
				throw new Error("Unable to fetch data from server");

			const headers = response.headers;
			const contentType = headers.get('Content-Type')
				|| 'application/octet-stream';

			return response.text().then(data =>
			{
				window.top.dispatchEvent(new CustomEvent('ProccessResult',
					{detail: {id, name, contentType, data}}));
				resolve(new Response(data, {status: 200, statusText: 'OK', headers}));
			});
		}).catch(error =>
	{
		window.top.dispatchEvent(new CustomEvent('ProcessError',
			{detail: {id, name, text: error.message}}));
		reject(error.message);
	});
}

function connect(action, options,
                 id, name, resolve, reject)
{
	const source = new SSE(action, options);

	source.addEventListener("Progress", event =>
		dispatchProgress(id, name, parseEvent(event.data)));

	source.addEventListener("Result", event =>
		resolveResult(id, name, parseEvent(event.data), resolve));

	source.addEventListener("Failure", event =>
	{
		const parsed = parseEvent(event.data);
		window.top.dispatchEvent(new CustomEvent('ProcessError',
			{detail: {id, name, text: parsed.text || "Process failed", fatal: true}}));
		reject(parsed);
	});

	source.addEventListener("Redirect", event =>
		resolveRedirect(id, name, parseEvent(event.data), resolve, reject));

	source.addEventListener("Finish", () => resolve(null));
	source.addEventListener("close", () => resolve(null));

	return source;
}

function fallback(action, id, name, resolve, reject)
{
	const source = connect(action,
		{start: false, reconnect: false},
		id,
		name,
		response => (source.close(), resolve(response)),
		error => (source.close(), reject(error)));

	source.addEventListener("error", event =>
	{
		source.close();
		if (event.responseCode === 404)
		{
			window.top.dispatchEvent(new CustomEvent('ProcessError',
				{detail: {id, name, text: "Unable to reconnect to server", fatal: true}}));
			reject("Connection closed");
			return;
		}

		window.top.dispatchEvent(new CustomEvent('ProcessError',
			{detail: {id, name, text: "Reconnecting to server"}}));

		setTimeout(() => fallback(action, id, name, resolve, reject), 1000);
	});

	source.stream();
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

		const source = connect(action, {start: false, reconnect: false, method, payload},
			id, name, resolve, reject);
		let uuid = null;

		source.addEventListener("UUID", event =>
		{
			uuid = atob(event.data)
			console.log(uuid);
		});

		source.addEventListener("error", () =>
		{
			if (!uuid)
			{
				window.top.dispatchEvent(new CustomEvent('ProcessError',
					{detail: {id, name, text: "Connection lost with server", fatal: true}}));
				reject("Connection closed");
				return;
			}

			window.top.dispatchEvent(new CustomEvent('ProcessError',
				{detail: {id, name, text: "Reconnecting to server"}}));
			fallback(`Progress?uuid=${encodeURIComponent(uuid)}`, id, name, resolve, reject);
		});

		source.stream();
	});
}