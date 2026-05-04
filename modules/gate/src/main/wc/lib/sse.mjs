function parseMessage(chunk)
{
	if (!chunk || chunk.length === 0)
		return null;

	const message = {id: null, retry: null, data: null, event: null};
	chunk.split(/\n|\r\n|\r/).forEach(line =>
	{
		const index = line.indexOf(':');
		if (index === 0)
			return;

		const field = index > 0 ? line.substring(0, index) : line;
		const value = index > 0
			? line.substring(index + (line[index + 1] === ' ' ? 2 : 1))
			: '';

		if (!(field in message))
			return;

		if (field === 'data' && message[field] !== null)
			message['data'] += "\n" + value;
		else
			message[field] = value;
	});

	return message;
}

export default class SSE
{
	CONNECTING = 0;
	OPEN = 1;
	CLOSED = 2;

	#url;
	#headers;
	#payload;
	#method;
	#withCredentials;
	#debug;
	#reconnect;
	#retry = 3000;
	#listeners = {};
	#xhr = null;
	#progress = 0;
	#chunk = '';

	readyState = this.CONNECTING;
	lastEventId = '';

	constructor(url, options = {})
	{
		this.#url = url;
		this.#headers = options.headers || {};
		this.#payload = options.payload !== undefined ? options.payload : '';
		this.#method = options.method || (this.#payload ? 'POST' : 'GET');
		this.#withCredentials = !!options.withCredentials;
		this.#debug = !!options.debug;
		this.#reconnect = options.reconnect !== false;

		if (options.start === undefined || options.start)
			this.stream();
	}

	addEventListener(type, listener)
	{
		this.#listeners[type] = this.#listeners[type] || [];
		if (this.#listeners[type].indexOf(listener) === -1)
			this.#listeners[type].push(listener);
	}

	removeEventListener(type, listener)
	{
		if (this.#listeners[type] === undefined)
			return;

		this.#listeners[type] = this.#listeners[type].filter(e => e !== listener);

		if (this.#listeners[type].length === 0)
			delete this.#listeners[type];
	}

	dispatchEvent(event)
	{
		if (!event)
			return true;

		if (this.#debug)
			console.debug(event);

		event.source = this;

		const onHandler = 'on' + event.type;
		if (this.hasOwnProperty(onHandler))
		{
			this[onHandler].call(this, event);
			if (event.defaultPrevented)
				return false;
		}

		if (this.#listeners[event.type])
			return this.#listeners[event.type].every(callback =>
			{
				callback(event);
				return !event.defaultPrevented;
			});

		return true;
	}

	stream()
	{
		if (this.readyState === this.CLOSED || this.#xhr)
			return;

		this.readyState = this.CONNECTING;
		this.#progress = 0;
		this.#chunk = '';

		this.#xhr = new XMLHttpRequest();
		this.#xhr.addEventListener('progress', e => this.#onStreamProgress(e));
		this.#xhr.addEventListener('load', e => this.#onStreamLoaded(e));
		this.#xhr.addEventListener('readystatechange', () => this.#onReadyStateChange());
		this.#xhr.addEventListener('error', e => this.#onStreamFailure(e));
		this.#xhr.addEventListener('abort', () =>
		{
			this.#xhr = null;
		});
		this.#xhr.open(this.#method, this.#url);

		for (let header in this.#headers)
			this.#xhr.setRequestHeader(header, this.#headers[header]);

		if (this.lastEventId.length > 0)
			this.#xhr.setRequestHeader("Last-Event-ID", this.lastEventId);

		this.#xhr.withCredentials = this.#withCredentials;
		this.#xhr.send(this.#payload);
	}

	close()
	{
		if (this.readyState === this.CLOSED)
			return;

		this.readyState = this.CLOSED;
		this.#xhr?.abort();
		this.#xhr = null;
	}

	#onReadyStateChange()
	{
		if (!this.#xhr || this.#xhr.readyState !== XMLHttpRequest.HEADERS_RECEIVED)
			return;

		if (this.#xhr.status !== 200)
			return;

		const headers = {};
		for (const headerPair of this.#xhr.getAllResponseHeaders().trim().split('\r\n'))
		{
			const [key, ...valueParts] = headerPair.split(':');
			const value = valueParts.join(':').trim();
			headers[key.trim().toLowerCase()] = headers[key.trim().toLowerCase()] || [];
			headers[key.trim().toLowerCase()].push(value);
		}

		this.readyState = this.OPEN;
		const event = new CustomEvent('open');
		event.responseCode = this.#xhr.status;
		event.headers = headers;
		this.dispatchEvent(event);
	}

	#onStreamProgress()
	{
		if (!this.#xhr || this.#xhr.status !== 200)
			return;

		const data = this.#xhr.responseText.substring(this.#progress);
		this.#progress += data.length;

		const parts = (this.#chunk + data).split(/(\r\n\r\n|\r\r|\n\n)/g);
		this.#chunk = parts.pop();
		parts.forEach(part =>
		{
			if (part.trim().length > 0)
				this.#dispatchMessage(part);
		});
	}

	#onStreamLoaded(e)
	{
		this.#onStreamProgress(e);
		this.#dispatchMessage(this.#chunk);
		this.#chunk = '';
		this.#xhr = null;
		this.#dispatchError();
	}

	#onStreamFailure()
	{
		const code = this.#xhr.status;
		this.#xhr = null;
		this.#dispatchError(code);
	}

	#dispatchError(responseCode = 0)
	{
		if (this.readyState === this.CLOSED)
			return;

		if (responseCode === 200)
		{
			this.readyState = this.CLOSED;
			this.dispatchEvent(new CustomEvent('close'));
			return;
		}

		this.readyState = this.#reconnect ? this.CONNECTING : this.CLOSED;
		const event = new CustomEvent('error');
		event.responseCode = responseCode;
		this.dispatchEvent(event);

		if (this.#reconnect)
			setTimeout(() => this.stream(), this.#retry);
	}

	#dispatchMessage(chunk)
	{
		if (this.#debug)
			console.debug(chunk);

		const message = parseMessage(chunk);
		if (!message)
			return;

		if (message.id !== null)
			this.lastEventId = message.id;

		if (message.retry !== null && !isNaN(message.retry))
			this.#retry = parseInt(message.retry);

		if (message.data === null)
			return;

		const event = new CustomEvent(message.event || 'message');
		event.id = message.id;
		event.data = message.data || '';
		event.lastEventId = this.lastEventId;
		this.dispatchEvent(event);
	}
}