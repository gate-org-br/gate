import Base64 from "./base64.js";
import LineReader from "./line-reader.js";

export default class EventReader
{
	#lines;

	constructor(response) { this.#lines = new LineReader(response); }

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
						message.data = JSON.parse(Base64.decode(value));
						break;
				}
			});
			return message;
		}
	}
}