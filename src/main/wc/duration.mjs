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
		return this.value - (this.getHours() * 3600) - (this.getMinutes() * 60);
	}

	format(format)
	{
		var e;
		var result = "";
		var regex = /h+|m+|s+|H+|M+|S+|./g;
		while ((e = regex.exec(format)))
		{
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
}