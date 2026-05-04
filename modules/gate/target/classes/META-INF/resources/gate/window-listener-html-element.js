export default class WindowListenerHTMLElement extends HTMLElement
{
	#listeners = [];

	addWindowListener(event, listener, options)
	{
		this.#listeners.push({event, listener, options});
		if (this.isConnected)
			window.addEventListener(event, listener, options);
	}

	connectedCallback()
	{
		for (const {event, listener, options} of this.#listeners)
			window.addEventListener(event, listener, options);
	}

	disconnectedCallback()
	{
		for (const {event, listener, options} of this.#listeners)
		{
			const capture = typeof options === 'boolean'
				? options : options?.capture ?? false;
			window.removeEventListener(event, listener, capture);
		}
	}
}