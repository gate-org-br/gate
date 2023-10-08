export default class Command extends String
{
	constructor(value)
	{
		super(value || "Gate");
	}

	module(value)
	{
		return this.parameter('MODULE', value);
	}

	screen(value)
	{
		return this.parameter('SCREEN', value);
	}

	action(value)
	{
		return this.parameter('ACTION', value);
	}

	parameter(name, value)
	{
		return new Command(`${this}${this.indexOf("?") === -1 ? '?' : '&'}${name}=${value || new URLSearchParams(window.location.search).get(name)}`);
	}
}

export const module = function (value)
{
	return new Command().module(value);
};

export const screen = function (value)
{
	return new Command().module().screen(value);
};

export const action = function (value)
{
	return new Command().module().screen().action(value);
};

export const parameter = function (name, value)
{
	return new Command().module().screen().action().parameter(name, value);
};