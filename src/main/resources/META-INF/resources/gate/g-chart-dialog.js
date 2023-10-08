let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header tabindex='1'>
			<label id='caption'>
				Chart
			</label>
			<a id='rchart' href='#' title='Rose'>
				<g-icon>
					&#x2247;
				</g-icon>
			</a>
			<a id='dchart' href='#' title='Donut'>
				<g-icon>
					&#x2245;
				</g-icon>
			</a>
			<a id='pchart' href='#' title='Pizza'>
				<g-icon>
					&#x2031;
				</g-icon>
			</a>
			<a id='achart' href='#' title='Área'>
				<g-icon>
					&#x2244;
				</g-icon>
			</a>
			<a id='lchart' href='#' title='Linha'>
				<g-icon>
					&#x2032;
				</g-icon>
			</a>
			<a id='bchart' href='#' title='Barra'>
				<g-icon>
					&#x2246;
				</g-icon>
			</a>
			<a id='cchart' href='#' title='Coluna'>
				<g-icon>
					&#x2033;
				</g-icon>
			</a>
			<a id='close' href='#' title='Fechar'>
				<g-icon>
					&#x1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-chart>
			</g-chart>
		</section>
	</main>
 <style>main {
	width: 100%;
	height: 100%;
}

@media only screen and (min-width: 640px)
{
	main{
		border-radius: 5px;
		width: calc(100% - 80px);
		height: calc(100% - 80px);
	}
}

main > section {
	align-items: stretch;
}

g-chart {
	flex-grow: 1;
	background-color: white;
}</style>`;

/* global customElements, template, fetch */

import  './g-icon.js';
import Dataset from './dataset.js';
import GWindow from './g-window.js';


export default class GChartDialog extends GWindow
{
	constructor()
	{
		super();
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

	static show(chart, title, data)
	{
		let dialog = document.createElement("g-chart-dialog");
		dialog.show();

		dialog.type = chart;
		dialog.title = title;
		dialog.data = data;
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

	if (action.hasAttribute('data-table'))
		Dataset.parse('table', action.getAttribute("data-table")).then(e => GChartDialog.show(chart, title, e));
	else if (action.hasAttribute('data-array'))
		Dataset.parse('array', action.getAttribute("data-array")).then(e => GChartDialog.show(chart, title, e));
	else
		Dataset.parse('url', action.href).then(e => GChartDialog.show(chart, title, e));
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

	let indx = Array.from(action.parentNode.children).indexOf(action);

	let chart = action.getAttribute('data-chart');

	let dataset = Dataset.fromTable(table, {cat: main, min: indx, max: indx});

	let title1 = table.querySelector(`:scope > thead > tr > *:nth-child(${main + 1})`).innerText;
	let title2 = table.querySelector(`:scope > thead > tr > *:nth-child(${indx + 1})`).innerText;
	let title = action.getAttribute("title") || `${title2} X ${title1}`;

	GChartDialog.show(chart, title, dataset);
});

