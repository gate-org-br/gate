let template = document.createElement("template");
template.innerHTML = `
	<div>
	</div>
 <style>:host(*)
{
	display: flex;
	font-size: 16px;
	min-width: 240px;
	align-items: center;
	align-content: center;
	flex-direction: column;
	justify-content: center;
	border: 2px solid #CCCCCC;
	background-color: #CCCCCC;
}

a {
	color: black;
	display: flex;
	align-items: center;
	text-decoration: none;
	justify-content: center;
}

label {
	display: flex;
	align-items: center;
	justify-content: center;
}

:host(*):hover {
	border-color: var(--hovered);
}

:host(*) > div
{
	flex-grow: 1;
	width:  100%;
	display: flex;
	flex-wrap: wrap;
	align-content: stretch;
	justify-content: space-around;
}

:host(*) > div > label {
	flex-grow: 1;
	flex-shrink: 0;
	flex-basis: 14%;
	font-size: 16px;
	background-color: var(--base);
}

:host(*) > div > a {
	flex-grow: 1;
	flex-shrink: 0;
	font-size: 16px;
	flex-basis: 14%;
	background-color: white;
}

:host(*) > div > a.current {
	font-weight: bold;
	text-decoration: underline
}
:host(*) > div > a.disabled {
	color: #AAAAAA;
}
:host(*) > div > a.selected {
	background-color: var(--acent);
}

:host(*) > div > a:hover {
	background-color: var(--hovered)
}

:host(*) > div > div:last-child {
	display: flex;
	font-size: 16px;
	flex-basis: 100%;
	padding-left: 4px;
	padding-right: 4px;
	font-weight: bolder;
	align-items:  center;
	align-content: space-around;
	justify-content: space-around;
	background-image: linear-gradient(to bottom, var(--base-tinted40) 0%, var(--base-shaded20) 100% );
}

:host(*) > div > div:last-child	> a {
	flex-basis: 48px;
	font-size: inherit;
	font-weight: bolder;
}

:host(*) > div > div:last-child	> label {
	flex-grow: 1;
	font-size: inherit;
	font-weight: bolder;
}</style>`;

/* global customElements */

import DateFormat from './date-format.mjs';

customElements.define('g-calendar', class extends HTMLElement
{
	constructor()
	{
		super();
		var selection = [];
		this._private = {};
		var month = new Date();
		month.setHours(0, 0, 0, 0);
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		var update = () =>
		{
			let body = this.shadowRoot.firstElementChild;
			while (body.firstChild)
				body.removeChild(body.firstChild);

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
			} else
			{
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

			if (this.max && this.max <= selection.length)
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

	set max(max)
	{
		this.setAttribute("max", max);
	}

	get max()
	{
		return Number(this.getAttribute("max"));
	}
});