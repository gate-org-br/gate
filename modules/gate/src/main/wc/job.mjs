import EventReader from "./event-reader.js";

export default class Job extends EventTarget
{
	#uuid = null;
	#events = null;

	get uuid() { return this.#uuid; }

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
		return new Job(message.data.uuid, events);
	}
}