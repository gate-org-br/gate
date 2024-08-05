let template = document.createElement("template");
template.innerHTML = `
	<a id='pie' href="#" title='Pizza'>
		&#x2031;
	</a>
	<a id='doughnut' href="#" title='Donut'>
		&#x2245;
	</a>
	<a id='line' href="#" title='Linha'>
		&#x2032;
	</a>
	<a id='column' href="#" title='Coluna'>
		&#x2033;
	</a>
	<a id='bar' href="#" title='Barra'>
		&#x2246;
	</a>
	<a id='area' href="#" title='Área'>
		&#x2244;
	</a>
	<a id='radar' href="#" title='Radar'>
		&#x2160;
	</a>
	<a id='polar' href="#" title='Polar'>
		&#x2247;
	</a>
	<a id='reverse'  href="#" title='Inverter dataset'>
		&#x2025;
	</a>
 <style data-element="g-chart-toolbar">:host(*) {
	display: flex;
	align-items: center;
	justify-content: center;
}

a {
	width: 32px;
	height: 32px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-family: gate;
	font-size: 16px;
	text-decoration: none;
}</style>`;
/* global customElements, template */

customElements.define('g-chart-toolbar', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.shadowRoot.querySelector("#pie").addEventListener("click", () => this.chart.type = "pie");
		this.shadowRoot.querySelector("#doughnut").addEventListener("click", () => this.chart.type = "doughnut");
		this.shadowRoot.querySelector("#line").addEventListener("click", () => this.chart.type = "line");
		this.shadowRoot.querySelector("#column").addEventListener("click", () => this.chart.type = "column");
		this.shadowRoot.querySelector("#bar").addEventListener("click", () => this.chart.type = "bar");
		this.shadowRoot.querySelector("#area").addEventListener("click", () => this.chart.type = "area");
		this.shadowRoot.querySelector("#radar").addEventListener("click", () => this.chart.type = "radar");
		this.shadowRoot.querySelector("#polar").addEventListener("click", () => this.chart.type = "polar");
		this.shadowRoot.querySelector("#reverse").addEventListener("click", () => this.chart.reversed = !this.chart.reversed);
	}

	get chart()
	{
		return this.getRootNode()
			.getElementById(this.getAttribute("chart"));
	}
});