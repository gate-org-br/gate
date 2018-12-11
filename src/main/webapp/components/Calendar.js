function Calendar()
{
}

Object.defineProperty(Calendar, "getDatesFromMonth", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: function (month)
	{
		var result = new Array();
		var ini = Calendar.getFirstDayOfWeek(Calendar.getFirstDayOfMonth(month));
		var end = Calendar.getLastDayOfWeek(Calendar.getLastDayOfMonth(month));
		for (var date = ini; date <= end; date.setDate(date.getDate() + 1))
			result.push(new Date(date));
		while (result.length < 42)
		{
			result.push(new Date(date));
			date.setDate(date.getDate() + 1);
		}
		return result;
	}
});

Object.defineProperty(Calendar, "getFirstDayOfMonth", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: function (date)
	{
		date = new Date(date);
		date.setDate(1);
		return date;
	}
});

Object.defineProperty(Calendar, "getLastDayOfMonth", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: function (date)
	{
		date = new Date(date);
		date.setMonth(date.getMonth() + 1);
		date.setDate(0);
		return date;
	}
});

Object.defineProperty(Calendar, "getFirstDayOfWeek", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: function (date)
	{
		date = new Date(date);
		while (date.getDay() !== 0)
			date.setDate(date.getDate() - 1);
		return date;
	}
});

Object.defineProperty(Calendar, "getLastDayOfWeek", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: function (date)
	{
		date = new Date(date);
		while (date.getDay() !== 6)
			date.setDate(date.getDate() + 1);
		return date;
	}
});

Object.defineProperty(Calendar, "isToday", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: function (date)
	{
		let now = new Date();
		return date.getDate() === now.getDate()
			&& date.getMonth() === now.getMonth()
			&& date.getYear() === now.getYear();
	}
});