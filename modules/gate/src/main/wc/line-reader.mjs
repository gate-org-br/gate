export default class LineReader
{
	#reader;
	#lines = [];
	#chunk = '';
	#decoder = new TextDecoder();

	constructor(response) { this.#reader = response.body.getReader(); }

	async next()
	{
		while (true)
		{
			if (this.#lines.length > 0)
				return this.#lines.shift();

			let result;
			let cancelled = false;
			const timeout = setTimeout(() =>
			{
				cancelled = true;
				this.#reader.cancel().catch(() => {});
			}, 60000);

			try
			{
				result = await this.#reader.read();
			} finally
			{
				clearTimeout(timeout);
			}

			if (cancelled) throw new Error("Idle timeout");

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