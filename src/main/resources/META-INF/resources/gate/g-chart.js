let template = document.createElement("template");
template.innerHTML = `
	<canvas>
	</canvas>
 <style data-element="g-chart">* {
	box-sizing: border-box;

}

:host(*)
{
	display: flex;
	overflow: hidden;
	align-items: center;
	justify-content: center;
}</style>`;
/* global customElements */

import Dataset from './dataset.js';
import NumberFormat from './number-format.js';

const format = new NumberFormat();

function options(title)
{
	return {responsive: true,
		maintainAspectRatio: false,
		plugins: {title: {display: true,
				text: title,
				font: {size: 16},
				padding: {top: 10, bottom: 10}
			}
		}
	}
}

function data(value, reversed = false, fill = false)
{
	if (!value)
		return {labels: [], datasets: []};

	if (reversed)
		value = Dataset.reverse(value);

	return  {
		labels: value.slice(1).map(e => e[0]),
		datasets: value[0].slice(1).map((label, i) =>
			({label, fill, data: value.slice(1).map(e => e[i + 1])}))
	};
}


function load()
{
	return import("./chart.js").then(e => {
		e.default.register(e.Title);
		return e.default;
	});
}


customElements.define('g-chart', class extends HTMLElement
{
	#chart;
	#value;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set value(value)
	{
		this.#value = [value[0],
			...value.slice(1)
				.map(row => row.map((col, index) => index
							? format.parse(col)
							: col))];
		this.refresh();
	}

	get value()
	{
		return this.#value;
	}

	#line()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'line',
				data: data(this.value, this.reversed),
				options: {...options(this.title), scales: {y: {beginAtZero: true}}}
			});
		});
	}

	#area()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'line',
				data: data(this.value, this.reversed, true),
				options: {...options(this.title), scales: {y: {beginAtZero: true}}}
			});
		});
	}

	#column()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'bar',
				data: data(this.value, this.reversed),
				options: {...options(this.title), scales: {y: {beginAtZero: true}}}
			});
		});
	}

	#bar()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'bar',
				data: data(this.value, this.reversed),
				options: {...options(this.title), indexAxis: 'y', scales: {x: {beginAtZero: true}}}
			});
		});
	}

	#pie()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'pie',
				data: data(this.value, this.reversed),
				options: {...options(this.title)}
			});
		});
	}

	#doughnut()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'doughnut',
				data: data(this.value, this.reversed),
				options: {...options(this.title)}
			});
		});
	}

	#radar()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'radar',
				data: data(this.value, this.reversed),
				options: {...options(this.title)}
			});
		});
	}

	#polar()
	{
		load().then(Chart =>
		{
			this.#chart = new Chart(this.shadowRoot.querySelector("canvas"), {
				type: 'polarArea',
				data: data(this.value, this.reversed),
				options: {...options(this.title)}
			});
		});
	}

	#clear()
	{
		if (this.#chart)
		{
			this.#chart.destroy();
			this.#chart = null;
		}
	}

	get type()
	{
		return this.getAttribute("type");
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	get reversed()
	{
		return this.hasAttribute("reversed");
	}

	set reversed(reversed)
	{
		if (reversed)
			this.setAttribute("reversed", "");
		else
			this.removeAttribute("reversed");
	}

	refresh()
	{
		this.#clear();
		if (this.value && this.type)
			switch (this.type)
			{
				case "area":
					return this.#area();
				case "pie":
					return this.#pie();
				case "line":
					return this.#line();
				case "column":
					return this.#column();
				case "bar":
					return this.#bar();
				case "doughnut":
					return this.#doughnut();
				case "radar":
					return this.#radar();
				case "polar":
					return this.#polar();
			}

	}

	attributeChangedCallback(name, _, value)
	{
		switch (name)
		{
			case 'value':
				fetch(value)
					.then(e => e.json())
					.then(e => this.value = e);
				break;

			case 'type':
			case 'title':
			case 'reversed':
				this.refresh();
				break;
		}
	}

	static get observedAttributes()
	{
		return ['value', 'type', 'reversed', 'title'];
	}
}
);