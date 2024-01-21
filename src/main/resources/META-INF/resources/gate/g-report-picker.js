let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			Selecione um tipo de relatório
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<button id='PDF'><g-icon>&#x2218;</g-icon>PDF</button>
			<button id='XLS'><g-icon>&#x2221;</g-icon>XLS</button>
			<button id='DOC'><g-icon>&#x2220;</g-icon>DOC</button>
			<button id='CSV'><g-icon>&#x2222;</g-icon>CSV</button>
		</section>
	</dialog>
 <style>dialog {
	width: 600px;
	height: fit-content;
}

section {
	gap: 8px;
	width: 100%;
	height: auto;
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	justify-content: flex-start;
}

button {
	gap: 8px;
	flex-grow: 1;
	display: flex;
	height: 128px;
	color: #000066;
	cursor: pointer;
	font-size: 24px;
	min-width: 128px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	justify-content: center;
	background-color: white;
}

button:hover {
	background-color: var(--hovered);
}

section g-icon
{
	font-size: 50px;
}
</style>`;

/* global customElements, template */

import './g-icon.js';
import GWindow from './g-window.js';

export default class GReportPicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		Array.from(this.shadowRoot.querySelectorAll("button"))
			.forEach(button => button.addEventListener("click", () =>
					this.dispatchEvent(new CustomEvent("commit", {detail: button.id}))));
	}

	static pick(caption)
	{
		let picker = window.top.document.createElement("g-report-picker");
		picker.caption = caption;
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
}

customElements.define('g-report-picker', GReportPicker);