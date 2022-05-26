export default class Process
{
	constructor(id)
	{
		this._private = {id: id};
		let protocol = location.protocol === "https:" ? "wss://" : "ws://";

		let pathname = location.pathname.replace(/\/Gate$/, "/Progress/");
		let ws = new WebSocket(protocol + location.host + pathname + id);

		ws.onerror = e => window.top.dispatchEvent(new CustomEvent('ProcessError',
				{detail: {process: id, text: e.data | "Conexão perdida com o servidor"}}));

		ws.onclose = e =>
		{
			if (e.code !== 1000)
				window.top.dispatchEvent(new CustomEvent('ProcessError',
					{detail: {process: id, text: e.reason | "Conexão perdida com o servidor"}}));
		};

		ws.onmessage = (event) =>
		{
			event = JSON.parse(event.data);

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
								{detail: {process: id, todo: event.todo, done: event.done, text: event.text, data: event.data, progress: event.toString()}}));
							break;
						case "PENDING":
							window.top.dispatchEvent(new CustomEvent('ProcessPending',
								{detail: {process: id, todo: event.todo, done: event.done, text: event.text, data: event.data, progress: event.toString()}}));
							break;
						case "COMMITED":
							window.top.dispatchEvent(new CustomEvent('ProcessCommited',
								{detail: {process: id, todo: event.todo, done: event.done, text: event.text, data: event.data, progress: event.toString()}}));
							break;
						case "CANCELED":
							window.top.dispatchEvent(new CustomEvent('ProcessCanceled',
								{detail: {process: id, todo: event.todo, done: event.done, text: event.text, data: event.data, progress: event.toString()}}));
							break;
					}
					break;

				case "Redirect":
					window.top.dispatchEvent(new CustomEvent('ProcessRedirect',
						{detail: {process: id, url: event.url}}));
					break;
			}
		};
	}

	get id()
	{
		return this._private.id;
	}
}