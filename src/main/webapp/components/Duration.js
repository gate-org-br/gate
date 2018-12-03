function Duration(value)
{
	this.value = value;

	this.getHours = function ()
	{
		return Math.floor(value / 3600);
	};

	this.getMinutes = function ()
	{
		return Math.floor((value - (this.getHours() * 3600)) / 60);
	};

	this.getSeconds = function ()
	{
		return value - (this.getHours() * 3600) - (this.getMinutes() * 60);
	};

	this.toString = function ()
	{
		return "00".concat(String(this.getHours())).slice(-2)
				+ ':' + "00".concat(String(this.getMinutes())).slice(-2)
				+ ':' + "00".concat(String(this.getSeconds())).slice(-2);
	};
}