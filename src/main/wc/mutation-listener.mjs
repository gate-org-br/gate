export default class MutationListener
{
	#observer;
	#options;
	#roots = new Set();

	constructor(action, options = {
		childList: true, subtree: true,
		attributes: true
	})
	{
		this.#observer = new MutationObserver(action);
		this.#options = {...options};
	}

	has(root)
	{
		return this.#roots.has(root);
	}

	listen(root)
	{
		if (this.#roots.has(root))
			return;

		this.#roots.add(root);
		this.#observer.observe(root, this.#options);
	}

	pause()
	{
		this.#observer.disconnect();
	}

	resume()
	{
		this.#roots.forEach(root => this.#observer.observe(root, this.#options));
	}
}