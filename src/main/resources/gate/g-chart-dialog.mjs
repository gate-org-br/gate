let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header tabindex='1'>
			<label id='caption'>
				Chart
			</label>
			<a id='rchart' href='#' title='Rose'>
				&#x2247;
			</a>
			<a id='dchart' href='#' title='Donut'>
				&#x2245;
			</a>
			<a id='pchart' href='#' title='Pizza'>
				&#x2031;
			</a>
			<a id='achart' href='#' title='Área'>
				&#x2244;
			</a>
			<a id='lchart' href='#' title='Linha'>
				&#x2032;
			</a>
			<a id='bchart' href='#' title='Barra'>
				&#x2246;
			</a>
			<a id='cchart' href='#' title='Coluna'>
				&#x2033;
			</a>
			<a id='close' href='#' title='Fechar'>
				&#x1011;
			</a>
		</header>
		<section>
			<g-chart>
			</g-chart>
		</section>
	</main>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	display: flex;
	position: fixed;
	align-items: center;
	justify-content: center;
}

main {
	width: 100%;
	height: 100%;
	display: grid;
	border-radius: 0;
	position: relative;
	grid-template-rows: 40px 1fr;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}

@media only screen and (min-width: 640px)
{
	main{
		border-radius: 5px;
		width: calc(100% - 80px);
		height: calc(100% - 80px);
	}
}

header{
	gap: 4px;
	padding: 4px;
	display: flex;
	font-size: 20px;
	font-weight: bold;
	align-items: center;
	justify-content: space-between;
	color: var(--g-window-header-color);
	background-color: var(--g-window-header-background-color);
	background-image: var(--g-window-header-background-image);
}

#caption {
	order: 1;
	flex-grow: 1;
	display: flex;
	color: inherit;
	font-size: inherit;
	align-items: center;
	justify-content: flex-start;
}

a {
	order: 4;
	color: white;
	padding: 2px;
	display: flex;
	font-size: 16px;
	font-family: gate;
	align-items: center;
	text-decoration: none;
	justify-content: center;
}

#close { margin-left: 8px; }

section {
	flex-grow: 1;
	display: flex;
	overflow: auto;
	align-items: stretch;
	justify-content: center;
	-webkit-overflow-scrolling: touch;
	background-image: var(--g-window-section-background-image);
	background-color: var(--g-window-section-background-color);
}

g-chart {
	flex-grow: 1;
	background-color: white;
}</style>`;

/* global customElements, template, fetch */

import GModal from './g-modal.mjs';
import Dataset from './dataset.mjs';

export default class GChartDialog extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").onclick = () => this.hide();
		this.shadowRoot.getElementById("cchart").onclick = () => this.type = 'cchart';
		this.shadowRoot.getElementById("bchart").onclick = () => this.type = 'bchart';
		this.shadowRoot.getElementById("lchart").onclick = () => this.type = 'lchart';
		this.shadowRoot.getElementById("achart").onclick = () => this.type = 'achart';
		this.shadowRoot.getElementById("pchart").onclick = () => this.type = 'pchart';
		this.shadowRoot.getElementById("dchart").onclick = () => this.type = 'dchart';
		this.shadowRoot.getElementById("rchart").onclick = () => this.type = 'rchart';
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
	}

	set type(type)
	{
		this.shadowRoot.querySelector('g-chart').type = type;
		switch (type)
		{
			case 'cchart':
				this.shadowRoot.getElementById('caption').innerText = 'Coluna';
				break;
			case 'bchart':
				this.shadowRoot.getElementById('caption').innerText = 'Barra';
				break;
			case 'pchart':
				this.shadowRoot.getElementById('caption').innerText = 'Pizza';
				break;
			case 'dchart':
				this.shadowRoot.getElementById('caption').innerText = 'Donnut';
				break;
			case 'achart':
				this.shadowRoot.getElementById('caption').innerText = 'Área';
				break;
			case 'rchart':
				this.shadowRoot.getElementById('caption').innerText = 'Rose';
				break;
			case 'lchart':
				this.shadowRoot.getElementById('caption').innerText = 'Linha';
				break;
		}
	}

	set title(title)
	{
		this.shadowRoot.querySelector('g-chart').title = title;
	}

	set data(data)
	{
		this.shadowRoot.querySelector('g-chart').data = data;
	}

	static show(chart, series, title)
	{
		let dialog = document.createElement("g-chart-dialog");
		dialog.show();

		dialog.type = chart;
		dialog.title = title;
		dialog.data = series;
	}
}

customElements.define('g-chart-dialog', GChartDialog);

window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;

	action = action.closest("a[data-chart]");
	if (!action)
		return;

	event.preventDefault();
	event.stopPropagation();

	let chart = action.getAttribute("data-chart");
	let title = action.getAttribute("title") || "???";

	if (action.hasAttribute('data-series'))
	{
		let series = action.getAttribute("data-series");

		let matcher = series.match(/^(#[a-z]+)(\(((dir|cat|min|max|loc)=[a-z0-9-]+(, *(dir|cat|min|max|loc)=[a-z0-9-]+)*)\))?$/i);
		if (matcher)
		{
			let table = matcher[1];
			let dir = 'X';
			let cat = 0;
			let min = 1;
			let max = undefined;
			let loc = undefined;
			if (matcher[3])
			{
				let parameters = matcher[3].split(",");
				parameters = parameters.map(e => e.split("="));
				parameters.filter(e => e[0] === 'dir').map(e => e[1]).forEach(e => dir = e);
				parameters.filter(e => e[0] === 'cat').map(e => e[1]).forEach(e => cat = e);
				parameters.filter(e => e[0] === 'min').map(e => e[1]).forEach(e => min = e);
				parameters.filter(e => e[0] === 'max').map(e => e[1]).forEach(e => max = e);
				parameters.filter(e => e[0] === 'loc').map(e => e[1]).forEach(e => loc = e);
			}

			let dataset = Dataset.fromTable(document.querySelector(table), dir, cat, min, max, loc);
			GChartDialog.show(chart, dataset, title);
		} else
			GChartDialog.show(chart, JSON.parse(series), title);
	} else
		fetch(action.href)
			.then(dataset => dataset.json())
			.then(dataset => GChartDialog.show(chart, dataset, title));
});

window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;

	action = action.closest("tfoot > tr > *[data-chart]");
	if (!action)
		return;

	event.preventDefault();
	event.stopPropagation();

	let table = action.closest("table");
	let main = table.querySelector(":scope > tfoot > tr > *[data-chart-category]")
		|| table.querySelector(":scope > tfoot > tr > *:first-child");
	main = Array.from(main.parentNode.children).indexOf(main);
	let self = Array.from(action.parentNode.children).indexOf(action);

	let title1 = table.querySelector(`:scope > thead > tr > *:nth-child(${main + 1})`).innerText;
	let title2 = table.querySelector(`:scope > thead > tr > *:nth-child(${self + 1})`).innerText;

	let labels = Array.from(table.querySelectorAll(`:scope > tbody > tr > *:nth-child(${main + 1})`))
		.map(e => e.getAttribute("data-value") || e.innerText);
	let values = Array.from(table.querySelectorAll(`:scope > tbody > tr > *:nth-child(${self + 1})`))
		.map(e => e.getAttribute("data-value") || e.innerText);

	let series = [];
	for (let i = 0; i < labels.length; i++)
		series.push([labels[i], Number(values[i].replace(/\./g, "").replace(/\,/g, "."))]);

	GChartDialog.show(action.getAttribute('data-chart'),
		JSON.stringify(series),
		action.getAttribute("title") || `${title2} X ${title1}`);
});

