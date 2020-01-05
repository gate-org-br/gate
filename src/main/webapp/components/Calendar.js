/* global DateFormat, customElements */

var calendars = {};
class Calendar extends HTMLElement
{
	constructor(init)
	{
		super();

		var selection = [];

		var max = undefined;
		if (init && init.max)
			max = init.max;

		var month = new Date();
		if (init && init.month)
			month = init.month;
		month.setHours(0, 0, 0, 0);


		var update = () =>
		{
			while (this.firstChild)
				this.removeChild(this.firstChild);

			var body = this.appendChild(document.createElement("div"));
			body.appendChild(document.createElement("label")).innerText = "Dom";
			body.appendChild(document.createElement("label")).innerText = "Seg";
			body.appendChild(document.createElement("label")).innerText = "Ter";
			body.appendChild(document.createElement("label")).innerText = "Qua";
			body.appendChild(document.createElement("label")).innerText = "Qui";
			body.appendChild(document.createElement("label")).innerText = "Sex";
			body.appendChild(document.createElement("label")).innerText = "Sab";

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
				var link = body.appendChild(document.createElement("a"));
				link.setAttribute("href", "#");
				link.innerText = date.getDate();
				link.onclick = () => this.action(date);

				if (date.getTime() === now.getTime())
					link.classList.add("current");
				if (date.getMonth() !== month.getMonth())
					link.classList.add("disabled");
				if (selection.some(e => e.getTime() === date.getTime()))
					link.classList.add("selected");
			});

			var foot = body.appendChild(document.createElement("div"));

			var prevYear = foot.appendChild(document.createElement("a"));
			prevYear.setAttribute("href", "#");
			prevYear.innerText = "<<";
			prevYear.onclick = () => month.setFullYear(month.getFullYear() - 1) | update();

			var prevMonth = foot.appendChild(document.createElement("a"));
			prevMonth.setAttribute("href", "#");
			prevMonth.innerText = "<";
			prevMonth.onclick = () => month.setMonth(month.getMonth() - 1) | update();

			foot.appendChild(document.createElement("label")).innerText = DateFormat.MONTH.format(month);

			var nextMonth = foot.appendChild(document.createElement("a"));
			nextMonth.setAttribute("href", "#");
			nextMonth.innerText = ">";
			nextMonth.onclick = () => month.setMonth(month.getMonth() + 1) | update();

			var nextYear = foot.appendChild(document.createElement("a"));
			nextYear.setAttribute("href", "#");
			nextYear.innerText = ">>";
			nextYear.onclick = () => month.setFullYear(month.getFullYear() + 1) | update();
		};

		this.addEventListener((/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel", event =>
		{
			event.preventDefault();

			if (event.ctrlKey)
			{
				if (event.detail)
					if (event.detail > 0)
						month.setFullYear(month.getFullYear() + 1);
					else
						month.setFullYear(month.getFullYear() - 1);
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						month.setFullYear(month.getFullYear() + 1);
					else
						month.setFullYear(month.getFullYear() - 1);
			} else {
				if (event.detail)
					if (event.detail > 0)
						month.setMonth(month.getMonth() + 1);
					else
						month.setMonth(month.getMonth() - 1);
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						month.setMonth(month.getMonth() + 1);
					else
						month.setMonth(month.getMonth() - 1);
			}

			update();
		});


		this.clear = () =>
		{
			if (!this.dispatchEvent(new CustomEvent('clear', {cancelable: true, detail: {calendar: this}})))
				return false;

			selection = [];

			update();

			this.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
			return true;
		};


		this.select = (date) =>
		{
			date = new Date(date);
			date.setHours(0, 0, 0, 0);

			if (selection.some(e => e.getTime() === date.getTime()))
				return false;

			if (!this.dispatchEvent(new CustomEvent('select', {cancelable: true, detail: {calendar: this, date: date}})))
				return false;

			if (max !== undefined
				&& max <= selection.length)
				selection.shift();

			selection.push(date);

			update();

			this.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
			return true;
		};

		this.remove = (date) =>
		{
			date = new Date(date);
			date.setHours(0, 0, 0, 0);

			if (!selection.some(e => e.getTime() === date.getTime()))
				return false;

			if (!this.dispatchEvent(new CustomEvent('remove', {cancelable: true, detail: {calendar: this, date: date}})))
				return false;

			selection = selection.filter(e => e.getTime() !== date.getTime());
			update();

			this.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
			return true;
		};

		this.action = (date) =>
		{
			date = new Date(date);
			date.setHours(0, 0, 0, 0);

			if (!this.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {calendar: this, date: date}})))
				return false;

			return selection.some(e => e.getTime() === date.getTime()) ?
				this.remove(date) : this.select(date);
		};

		this.length = () => selection.length;
		this.selection = () => Array.from(selection);
		update();
	}
}

customElements.define('g-calendar', Calendar);