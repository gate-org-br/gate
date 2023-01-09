export default class Duration
{
	constructor(value)
	{
		this.value = value;
	}

	getHours()
	{
		return Math.floor(this.value / 3600);
	}

	getMinutes()
	{
		return Math.floor((this.value - (this.getHours() * 3600)) / 60);
	}

	getSeconds()
	{
		return Math.floor(this.value - (this.getHours() * 3600) - (this.getMinutes() * 60));
	}

	format(format)
	{
		let e;
		let result = "";
		let regex = /h+|m+|s+|H+|M+|S+|./g;
		while ((e = regex.exec(format)))
		{
			if (e[0][0] === '\\')
				continue;
			else if (e.index > 0 && format[e.index - 1] === "\\")
				result += e[0];
			else
				switch (e[0][0])
				{
					case 'h':
					case 'H':
						var value = String(this.getHours());
						var padding = "0".repeat(e[0].length);
						result += (padding + value).slice(-Math.max(padding.length, value.length));
						break;
					case 'm':
					case 'M':
						var value = String(this.getMinutes());
						var padding = "0".repeat(e[0].length);
						result += (padding + value).slice(-Math.max(padding.length, value.length));
						break;
					case 's':
					case 'S':
						var value = String(this.getSeconds());
						var padding = "0".repeat(e[0].length);
						result += (padding + value).slice(-Math.max(padding.length, value.length));
						break;
					default:
						result += e[0];
				}
		}
		return result;
	}

	toString()
	{
		return this.format("hh:mm:ss");
	}

	static parse(string)
	{
		if (/^[0-9]{2,}:[0-9]{2}:[0-9]{2}$/.test(string))
			return new Duration(new Date("1970-01-01T" + string + "Z").getTime() / 1000);
		else if (/^[0-9]{2,}:[0-9]{2}$/.test(string))
			return new Duration(new Date("1970-01-01T" + string + ":00Z").getTime() / 1000);
		else if (/^[0-9]+$/.test(string))
			return new Duration(Number(String * 60));

		const parts = string.split(", ");
		const days = parts.find(part => part.endsWith("d")) || 0;
		const hours = parts.find(part => part.endsWith("h")) || 0;
		const minutes = parts.find(part => part.endsWith("m")) || 0;
		const seconds = parts.find(part => part.endsWith("s")) || 0;

		const totalDays = parseInt(days.substring(0, days.length - 1), 10) || 0;
		const totalHours = parseInt(hours.substring(0, hours.length - 1), 10) || 0;
		const totalMinutes = parseInt(minutes.substring(0, minutes.length - 1), 10) || 0;
		const totalSeconds = parseInt(seconds.substring(0, seconds.length - 1), 10) || 0;

		return new Duration(totalDays * 86400 + totalHours * 3600 + totalMinutes * 60 + totalSeconds);
	}
}