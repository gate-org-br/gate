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
			<iframe name='g-url-picker' scrolling="no">
			</iframe>
		</section>
	</dialog>
 <style>dialog {
	min-width: 100vw;
	min-height: 100vh;
	border-radius: 0;
}

@media only screen and (min-width: 640px)
{
	dialog{
		min-width: unset;
		min-height: unset;
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
import GWindow from './g-window.js';

export default class GURLPicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		let iframe = this.shadowRoot.querySelector("iframe");
		iframe.dialog = this;
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

	static pick(url)
	{
		let picker = window.top.document.createElement("g-url-picker");
		picker.show();

		if (typeof url === 'function')
			url(picker);
		else if (typeof url === 'string')
			picker.iframe.src = url;

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("commit", e => resolve(e.detail));
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
		});
	}
};

customElements.define('g-url-picker', GURLPicker);

window.addEventListener("trigger", function (event)
{
	if (event.detail.target === "@pick")
	{
		let cause = event.detail.cause;
		let element = event.detail.element;
		let parameters = event.detail.parameters
			.map(e => e ? document.getElementById(e) : e);

		if (cause.type === "change")
		{
			GURLPicker.pick(dialog =>
			{
				dialog.iframe.name = element.target;
				dialog.caption = element.title || "";
			})
				.then(values => update(parameters, values))
				.catch(() => parameters.forEach(e => e.value = ""));
		} else if (parameters.every(e => !e.value))
		{
			element.style.pointerEvents = "none";
			GURLPicker.pick(dialog =>
			{
				dialog.iframe.name = element.target;
				dialog.caption = element.title || "";
			})
				.then(values => update(parameters, values))
				.catch(() => undefined)
				.finally(() => element.style.pointerEvents = "");
		} else
		{
			event.preventDefault();
			parameters.forEach(e => e.value = "");
		}
	} else if (event.detail.target === "@return")
	{
		event.preventDefault();
		window.frameElement.dialog.dispatchEvent(new CustomEvent('commit', {detail: event.detail.parameters}));
	}
});

function update(parameters, values)
{
	let size = Math.min(parameters.length, values.length);
	for (var i = 0; i < size; i++)
		if (parameters[i])
			parameters[i].value = values[i] || "";

	for (var i = 0; i < size; i++)
		if (parameters[i])
			parameters[i].dispatchEvent(new CustomEvent('changed', {bubbles: true}));
}