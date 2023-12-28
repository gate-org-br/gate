

export default class Optional
{
	#value;

	constructor(value)
	{
		this.#value = value;
	}

	isEmpty()
	{
		return this.#value === undefined
			|| this.#value === null;
	}

	isPresent()
	{
		return this.#value !== undefined
			&& this.#value !== null;
	}

	map(func)
	{
		return this.isPresent() ? Optional.of(func(this.#value)) : EMPTY;
	}

	flatMap(func)
	{
		return this.isPresent() ? func(this.#value) : EMPTY;
	}

	filter(predicate)
	{
		return this.isPresent() && predicate(this.#value) ? this : EMPTY;
	}

	forEach(func)
	{
		if (this.isPresent())
			func(this.#value);
	}

	orElse(value)
	{
		if (this.isPresent())
			return this.#value;
		return typeof value === "function" ? value() : value;
	}

	orElseThrow(message)
	{
		if (this.isEmpty())
		{
			while (typeof message === 'function')
				message = message();

			if (message instanceof Error)
				throw message;

			throw new TypeError(message || "No such element");
		}
		return this.#value;
	}

	or(value)
	{
		if (this.isPresent())
			return this;
		return Optional.of(typeof value === "function" ? value() : value);
	}

	then(resolve, reject)
	{
		if (this.isEmpty())
			return Promise.reject(reject ? reject() : new Error("No such element"));

		return Promise.resolve(resolve ? resolve(this.#value) : this.#value);
	}
	catch (reject)
	{
		return this.then(null, reject);
	}
	finally(action)
	{
		return this.then(action, action);
	}

	static empty()
	{
		return EMPTY;
	}

	static of(value)
	{
		return value !== null && value !== undefined ?
			new Optional(value) : Optional.empty();
	}
}

const EMPTY = new Optional(null);