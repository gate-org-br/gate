let template = document.createElement("template");
template.innerHTML = `
	<dialog><header><label id="title"></label><a id="cancel" href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><main><section><fieldset><label data-size="2">
							Date
							<span id="timestamp"></span></label><label data-size="2">
							Status
							<span id="status"></span></label><label data-size="4">
							Method
							<span id="method"></span></label><label data-size="8">
							URI
							<span id="uri"></span></label></fieldset><table is="g-tree-grid" class="r7"><caption>ERRORS</caption><thead><tr><th>Exception</th><th>Message</th><th>Class</th><th>Method</th><th>File</th><th style="width: 80px">Line</th></tr></thead><tbody></tbody></table></section></main></section></dialog>
<style data-element="g-error-dialog">dialog {
	width: calc(100vw - 32px);
	height: calc(100vh - 32px);
}

th, td { overflow-wrap: anywhere;}</style>`;
/* global customElements, template */

import './g-icon.js';
import './g-tree-grid.js';
import GWindow from './g-window.js';

function text(value)
{
	return value === null || value === undefined ? "" : String(value);
}

function appendCell(row, value, column)
{
	const cell = row.insertCell();
	cell.innerText = text(value);
	if (column)
		cell.dataset.column = column;
	return cell;
}

export default class GErrorDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.hide());
	}

	set detail(detail)
	{
		this.#render(detail || {});
	}

	#render(detail)
	{
		const errors = Array.isArray(detail.errors) ? detail.errors : [];

		this.shadowRoot.getElementById("title").innerText = "Erro";

		this.shadowRoot.getElementById("method").innerText = detail.method;
		this.shadowRoot.getElementById("status").innerText = detail.status;
		this.shadowRoot.getElementById("timestamp").innerText = new Date(detail.timestamp)
			.toLocaleString("pt-BR", {
				day: "2-digit",
				month: "2-digit",
				year: "numeric",
				hour: "2-digit",
				minute: "2-digit",
				second: "2-digit",
				hour12: false,
			});
		this.shadowRoot.getElementById("uri").innerText = `${detail.path}?${detail.queryString}`;

		const oldTable = this.shadowRoot.querySelector("table");
		const table = oldTable.cloneNode(true);
		const body = table.querySelector("tbody");
		body.innerHTML = "";

		errors.forEach((error, index) =>
		{
			const frame = error?.stackTrace?.[0] || {};
			const row = body.insertRow();
			row.dataset.depth = 0;
			row.toggleAttribute("data-expanded", index === 0);
			appendCell(row, error.type || error.simpleType);
			appendCell(row, error.message);
			appendCell(row, frame.className);
			appendCell(row, frame.methodName);
			appendCell(row, frame.fileName);
			appendCell(row, frame.lineNumber > 0 ? frame.lineNumber : "", "line");

			(error.stackTrace || []).forEach(stackFrame =>
			{
				const stackRow = body.insertRow();
				stackRow.dataset.depth = 1;
				appendCell(stackRow, "");
				appendCell(stackRow, stackFrame.text);
				appendCell(stackRow, stackFrame.className);
				appendCell(stackRow, stackFrame.methodName);
				appendCell(stackRow, stackFrame.fileName);
				appendCell(stackRow, stackFrame.lineNumber > 0 ? stackFrame.lineNumber : "", "line");
			});
		});

		oldTable.replaceWith(table);
	}

	static error(detail)
	{
		const dialog = window.top.document.createElement("g-error-dialog");
		dialog.detail = detail;
		dialog.show();
	}
}

customElements.define('g-error-dialog', GErrorDialog);