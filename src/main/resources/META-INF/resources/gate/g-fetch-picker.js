let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header tabindex='1'>
			<label id='caption'>
			</label>
			<a id='close' href='#'>
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<slot></slot>
		</section>
		<footer>
			<g-coolbar>
				<button id='clear' class='danger'>
					Limpar <g-icon>&#X2018;</g-icon>
				</button>
				<hr>
				<button id='cancel' class='tertiary'>
					Cancelar <g-icon>&#X2027;</g-icon>
				</button>
			</g-coolbar>
		</footer>
	</dialog>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	gap: 8px;
	display: flex;
	align-items: stretch;
	flex-direction: column;
}

dialog {
	width: 100%;
	height: 100%;
	border-radius: 0;
}

@media only screen and (min-width: 640px)
{
	dialog{
		border-radius: 3px;
		width: calc(100% - 80px);
		height: calc(100% - 80px);
	}
}

dialog > section {
	padding: 8px;
	display: flex;
	align-items: stretch;
	flex-direction: column;
}</style>`;

/* global customElements, template, CSV */

import Return from './@return.js';
import GWindow from './g-window.js';

export default class GFetchPicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("clear").addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: []})));
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerText;
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerText = caption;
	}

	show()
	{
		Return.bind(this);
		super.show();
	}

	hide()
	{
		super.hide();
		Return.free(this);
	}

	static pick(url, caption)
	{
		return new Promise((resolve, reject) =>
		{
			let picker = window.top.document.createElement("g-fetch-picker");
			picker.caption = caption || "Pick one";
			picker.show();

			fetch(url)
				.then(e => e.text())
				.then(e => picker.innerHTML = e);

			picker.addEventListener("commit", e => resolve(e.detail));
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
		});
	}
};

customElements.define('g-fetch-picker', GFetchPicker);