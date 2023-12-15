export default class Optional
{
	constructor(value)
	{
		this._value = value;
	}

	map(func)
	{
		return this._value ? new Optional(func(this._value)) : new Optional();
	}

	orElse(value)
	{
		return this._value !== undefined ? this._value : value;
	}

	orElseThrow(message)
	{
		if (this._value === undefined || this._value === null)
			throw new TypeError(message || "No such element");
		return this._value;
	}

	then(resolve, reject)
	{
		if (this._value === undefined || this._value === null)
			return reject ? Promise.reject(reject()) : this;

		return resolve ? Promise.resolve(resolve(this._value)) : this;
	}

	catch (reject)
	{
		return this.then(null, reject);
	}
	finally(action)
	{
		return this.then(action, action);
	}
}