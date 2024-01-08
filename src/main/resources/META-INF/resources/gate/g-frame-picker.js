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
		<section >
			<iframe name='g-frame-picker' scrolling="no">
			</iframe>
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
	padding: 0;
	align-items: stretch;
}

iframe {
	margin: 0;
	width: 100%;
	border: none;
	padding: 0px;
	flex-grow: 1;
	overflow: hidden
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

export default class GFramePicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		let iframe = this.shadowRoot.querySelector("iframe");
		iframe.onmouseenter = () => iframe.focus();
		iframe.addEventListener("load", () => iframe.removeAttribute("name"));
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerText;
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerText = caption;
	}

	get iframe()
	{
		return this.shadowRoot.querySelector("iframe");
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
		let picker = window.top.document.createElement("g-frame-picker");
		picker.show();
		picker.iframe.src = url;
		picker.caption = caption || "Pick one";

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("commit", e => resolve(e.detail));
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
		});
	}
};

customElements.define('g-frame-picker', GFramePicker);

window.addEventListener("@frame-picker", function (event)
{
	let trigger = event.composedPath()[0] || event.target;
	let parameters = event.detail.parameters
		.map(e => e ? document.getElementById(e) : e);

	if (event.detail.cause.type === "change")
	{
		GFramePicker.pick(event.detail.action, trigger.title || "")
			.then(values => Return.update(parameters, values))
			.catch(() => parameters.forEach(e => e.value = ""));
	} else if (parameters.every(e => !e.value))
	{
		trigger.style.pointerEvents = "none";
		GFramePicker.pick(event.detail.action, trigger.title || "")
			.then(values => Return.update(parameters, values))
			.catch(() => undefined)
			.finally(() => trigger.style.pointerEvents = "");
	} else
	{
		event.preventDefault();
		parameters.forEach(e => e.value = "");
	}
});