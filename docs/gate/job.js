class LineReader
{
	#reader;
	#lines = [];
	#chunk = '';
	#decoder = new TextDecoder();

	constructor(response)
	{
		this.#reader = response.body.getReader();
	}

	async next()
	{
		while (true)
		{
			if (this.#lines.length > 0)
				return this.#lines.shift();

			let result;
			try
			{
				result = await this.#reader.read();
			} catch (err)
			{
				const e = new Error(err.message);
				e.status = 0;
				throw e;
			}

			if (result.done)
			{
				if (this.#chunk.trim().length === 0)
					return null;
				const line = this.#chunk;
				this.#chunk = '';
				return line;
			}

			const data = this.#decoder.decode(result.value, {stream: true});
			const parts = (this.#chunk + data).split(/\r\n|\r|\n/g);
			this.#chunk = parts.pop();
			this.#lines.push(...parts);
		}
	}
}

class EventReader
{
	#lines;
	#decoder = new TextDecoder();

	constructor(response)
	{
		this.#lines = new LineReader(response);
	}

	async next()
	{
		const lines = [];
		while (true)
		{
			const line = await this.#lines.next();
			if (line !== null && line !== '')
			{
				lines.push(line);
				continue;
			}

			if (lines.length === 0)
				return line === null ? null : await this.next();

			const message = {};

			lines.forEach(line =>
			{
				const index = line.indexOf(':');
				if (index === 0)
					return;

				const field = index > 0 ? line.substring(0, index) : line;
				const value = index > 0
					? line.substring(index + (line[index + 1] === ' ' ? 2 : 1))
					: '';

				switch (field)
				{
					case 'id':
						message.id = value;
						break;
					case 'retry':
						message.retry = value;
						break;
					case 'event':
						message.event = value;
						break;
					case 'data':
						message.data = this.#decoder.decode(Uint8Array.from(atob(value), c => c.charCodeAt(0)));
						break;
				}
			});
			return message;
		}
	}
}

export default class Job extends EventTarget
{
	#uuid = null;
	#events = null;

	get uuid()
	{
		return this.#uuid;
	}

	constructor(uuid, events)
	{
		super();
		this.#uuid = uuid;
		this.#events = events;
	}

	async start()
	{
		try
		{
			let message;
			while ((message = await this.#events.next()) !== null)
				if (message?.data)
					this.dispatchEvent(new CustomEvent(message.event || 'message', {detail: message.data}));
		} catch (err)
		{
			err.uuid = this.#uuid;
			throw err;
		}
	}

	static async from(response)
	{
		const events = new EventReader(response);
		const message = await events.next();
		if (!message || message.event !== 'UUID')
			throw new Error("Expected UUID as first message");
		return new Job(message.data, events);
	}
}
