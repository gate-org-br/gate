let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header tabindex='1'>
			<label id='caption'>
			</label>
			<a id='cancel' href='#'>
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
		</section>
	</dialog>
 <style>dialog {
	width: 100%;
	height: 100%;
	max-width: none;
	max-height: none;
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
}

iframe[name] {
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}</style>`;

/* global customElements, template, CSV */

import './trigger.js';
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
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerText;
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerText = caption;
	}

	get content()
	{
		return this.shadowRoot.querySelector("section");
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
				.then(e => picker.content.innerHTML = e);

			picker.addEventListener("commit", e => resolve(e.detail));
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
		});
	}
};

customElements.define('g-fetch-picker', GFetchPicker);


function pick(event)
{
	let trigger = event.composedPath()[0] || event.target;
	let parameters = event.detail.parameters
		.map(e => e ? document.getElementById(e) : e);

	if (event.detail.cause.type === "change")
	{
		GFetchPicker.pick(event.detail.action, trigger.title)
			.then(values => Return.update(parameters, values))
			.catch(() => parameters.forEach(e => e.value = ""));
	} else if (parameters.every(e => !e.value))
	{
		trigger.style.pointerEvents = "none";
		GFetchPicker.pick(event.detail.action, trigger.title)
			.then(values => Return.update(parameters, values))
			.catch(() => undefined)
			.finally(() => trigger.style.pointerEvents = "");
	} else
	{
		event.preventDefault();
		parameters.forEach(e => e.value = "");
	}
}

window.addEventListener("@pick", pick);
window.addEventListener("@fetch-picker", pick);