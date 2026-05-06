import ResponseHandler from './response-handler.js';

export const CONNECTING = 0;
export const OPEN = 1;
export const CLOSED = 2;

export default class SSE
{
	#controller = null;
	#listeners = {};
	#lastEventId = '';
	#readyState = CONNECTING;

	get readyState() { return this.#readyState; }

	get lastEventId() { return this.#lastEventId; }

	constructor(url, options = {})
	{
		this.#controller = new AbortController();

		const payload = options.payload ?? '';
		const headers = {...(options.headers || {})};

		if (this.#lastEventId)
			headers['Last-Event-ID'] = this.#lastEventId;

		fetch(url, {
			method: options.method || (payload ? 'POST' : 'GET'),
			headers,
			body: payload || undefined,
			credentials: options.withCredentials ? 'include' : 'same-origin',
			signal: this.#controller.signal
		})
			.then(response => this.#onResponse(response))
			.catch(err => this.#onFailure(err));
	}

	async #onResponse(response)
	{
		if (response.status !== 200)
		{
			this.#readyState = CLOSED;
			this.#dispatchEvent(new CustomEvent('close', {detail: {status: response.status}}));
			return;
		}

		this.#readyState = OPEN;
		this.#dispatchEvent(new CustomEvent('open'));

		ResponseHandler.sse(response, message => this.#dispatchMessage(message))
			.then(() =>
			{
				this.#readyState = CLOSED;
				this.#dispatchEvent(new CustomEvent('close', {detail: {status: response.status}}));
			})
			.catch(err => this.#onFailure(err));
	}

	#onFailure(err)
	{
		if (this.#readyState === CLOSED)
			return;
		this.#readyState = CLOSED;
		this.#dispatchEvent(new CustomEvent('error'));
	}

	close()
	{
		if (this.#readyState === CLOSED)
			return;
		this.#readyState = CLOSED;
		this.#controller.abort();
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

	#dispatchEvent(event)
	{
		if (!event)
			return true;

		event.source = this;

		this['on' + event.type]?.call(this, event);
		if (event.defaultPrevented)
			return false;

		if (this.#listeners[event.type])
			return this.#listeners[event.type].every(callback =>
			{
				callback(event);
				return !event.defaultPrevented;
			});

		return true;
	}

	#dispatchMessage(message)
	{
		if (!message || message.data === null)
			return;

		if (message.id !== null)
			this.#lastEventId = message.id;

		const event = new CustomEvent(message.event || 'message');
		event.id = message.id;
		event.data = message.data || '';
		event.lastEventId = this.#lastEventId;
		this.#dispatchEvent(event);
	}
}