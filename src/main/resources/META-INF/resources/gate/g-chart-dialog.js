let template = document.createElement("template");
template.innerHTML = `
	<dialog part='dialog'>
		<header part='header' tabindex='1'>
			<label id='caption'>
			</label>
			<g-chart-toolbar chart='chart'>
			</g-chart-toolbar>
			<a id='hide' href='#'>
				<g-icon>
					&#x1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-chart id='chart'>
			</g-chart>
		</section>
	</dialog>
 <style data-element="g-chart-dialog">* {
	box-sizing: border-box;

}

dialog {
	width: 100%;
	height: 100%;
	border-radius: 0;
}

@media only screen and (min-width: 640px)
{
	dialog{
		border-radius: 5px;
		width: calc(100% - 80px);
		height: calc(100% - 80px);
	}
}

section {
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: white;
}

g-chart {
	height: 100%;
	flex-basis: 100%;
	max-width: 100%;
	max-height: 100%;
}</style>`;
/* global customElements, template, fetch */

import  './g-icon.js';
import GWindow from './g-window.js';

export default class GChartDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.innerHTML += template.innerHTML;
		this.shadowRoot.querySelector("#hide").addEventListener("click", () => this.hide());
	}

	set type(type)
	{
		this.shadowRoot.querySelector('g-chart').type = type;
	}

	set caption(caption)
	{
		this.shadowRoot.querySelector('#caption').innerText = caption;
	}

	set value(value)
	{
		this.shadowRoot.querySelector('g-chart').value = value;
	}

	static show(type, title, value)
	{
		let dialog = document.createElement("g-chart-dialog");
		dialog.show();

		dialog.type = type;
		dialog.title = title;
		dialog.value = value;
	}
}

customElements.define('g-chart-dialog', GChartDialog);