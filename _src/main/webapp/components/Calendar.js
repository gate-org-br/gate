/* global DateFormat */

var calendars = {};
function Calendar(element, init)
{
	var selection = [];

	var max = undefined;
	if (init && init.max)
		max = init.max;

	var month = new Date();
	if (init && init.month)
		month = init.month;
	month.setHours(0, 0, 0, 0);

	element.className = "Calendar";
	this.element = () => element;

	var update = () =>
	{
		while (element.firstChild)
			element.removeChild(element.firstChild);

		var body = element.appendChild(document.createElement("div"));
		["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"]
			.forEach(e => body.appendChild(document.createElement("label"))
					.appendChild(document.createTextNode(e)));

		var dates = [];
		var ini = new Date(month);
		ini.setDate(1);
		while (ini.getDay() !== 0)
			ini.setDate(ini.getDate() - 1);
		var end = new Date(month);
		end.setMonth(end.getMonth() + 1);
		end.setDate(0);
		while (end.getDay() !== 6)
			end.setDate(end.getDate() + 1);
		for (var date = ini; date <= end; date.setDate(date.getDate() + 1))
			dates.push(new Date(date));
		while (dates.length < 42)
		{
			dates.push(new Date(date));
			date.setDate(date.getDate() + 1);
		}

		var now = new Date();
		now.setHours(0, 0, 0, 0);
		dates.forEach(date =>
		{
			var link = body.appendChild(createLink(date.getDate(), () => this.action(date)));
			if (date.getTime() === now.getTime())
				link.classList.add("current");
			if (date.getMonth() !== month.getMonth())
				link.classList.add("disabled");
			if (selection.some(e => e.getTime() === date.getTime()))
				link.classList.add("selected");
		});

		var head = body.appendChild(document.createElement("div"));
		head.appendChild(createLink("<<", prevYear));
		head.appendChild(createLink("<", prevMonth));
		head.appendChild(document.createElement("label")).appendChild(document.createTextNode(DateFormat.MONTH.format(month)));
		head.appendChild(createLink(">", nextMonth));
		head.appendChild(createLink(">>", nextYear));

		function createLink(text, callback)
		{
			var link = document.createElement("a");
			link.setAttribute("href", "#");
			link.appendChild(document.createTextNode(text));
			link.addEventListener("click", callback);
			return link;
		}
	};

	var prevYear = function ()
	{
		month.setFullYear(month.getFullYear() - 1);
		update();
	};

	var prevMonth = function ()
	{
		month.setMonth(month.getMonth() - 1);
		update();
	};

	var nextMonth = function ()
	{
		month.setMonth(month.getMonth() + 1);
		update();
	};


	var nextYear = function ()
	{
		month.setFullYear(month.getFullYear() + 1);
		update();
	};


	element.addEventListener((/Firefox/i.test(navigator.userAgent))
		? "DOMMouseScroll" : "mousewheel",
		function (event)
		{
			event.preventDefault();

			if (event.ctrlKey)
			{
				if (event.detail)
					if (event.detail > 0)
						nextYear();
					else
						prevYear();
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						nextYear();
					else
						prevYear();
			} else {
				if (event.detail)
					if (event.detail > 0)
						nextMonth();
					else
						prevMonth();
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						nextMonth();
					else
						prevMonth();
			}
		});


	this.clear = () =>
	{
		if (!element.dispatchEvent(new CustomEvent('clear', {cancelable: true, detail: {calendar: this}})))
			return false;

		selection = [];

		update();

		element.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
		return true;
	};


	this.select = (date) =>
	{
		date = new Date(date);
		date.setHours(0, 0, 0, 0);

		if (selection.some(e => e.getTime() === date.getTime()))
			return false;

		if (!element.dispatchEvent(new CustomEvent('select', {cancelable: true, detail: {calendar: this, date: date}})))
			return false;

		if (max !== undefined
			&& max <= selection.length)
			selection.shift();

		selection.push(date);

		update();

		element.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
		return true;
	};

	this.remove = (date) =>
	{
		date = new Date(date);
		date.setHours(0, 0, 0, 0);

		if (!selection.some(e => e.getTime() === date.getTime()))
			return false;

		if (!element.dispatchEvent(new CustomEvent('remove', {cancelable: true, detail: {calendar: this, date: date}})))
			return false;

		selection = selection.filter(e => e.getTime() !== date.getTime());
		update();

		element.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
		return true;
	};

	this.action = (date) =>
	{
		date = new Date(date);
		date.setHours(0, 0, 0, 0);

		if (!element.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {calendar: this, date: date}})))
			return false;

		return selection.some(e => e.getTime() === date.getTime()) ?
			this.remove(date) : this.select(date);
	};

	this.length = () => selection.length;
	this.selection = () => Array.from(selection);
	this.element = () => element;
	update();
}

Object.defineProperty(Calendar, "of", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: (id, init) => calendars[id] = new Calendar(document.getElementById(id), init)
});
Object.defineProperty(Calendar, "get", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: id => calendars[id]
});
window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.Calendar"))
		.forEach(element => Calendar.of(element.id, new Date()));
});