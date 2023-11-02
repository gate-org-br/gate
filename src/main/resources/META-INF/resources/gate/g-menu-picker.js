let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			<label id='caption'>
				Selecione um ítem
			</label>
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<div>

			</div>
		</section>
	</main>
 <style>* {
	box-sizing: border-box;
}

main
{
	padding: 0;
	min-width: 200px;
	max-width: 400px;
	width: calc(100% - 40px);
}

main > section {
	flex-basis: 300px;
}

div {
	flex-grow: 1;
	display: flex;
	overflow: auto;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
}

button {
	color: #333;
	height: 48px;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 16px;
	min-width: 128px;
	align-items: center;
	background: #FFFFFF;
	transition: background-color 0.3s;
	border-bottom: 1px solid var(--main6);

}

button:hover {
	background-color: var(--hovered);
}
</style>`;

/* global customElements, template, fetch */

import './g-icon.js';
import GWindow from './g-window.js';

export default class GMenuPicker extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	set options(options)
	{
		let elements = this.shadowRoot.querySelector("div");
		while (elements.firstChild)
			elements.firstChild.remove();

		options.forEach(option =>
		{
			let element = elements.appendChild(document.createElement("button"));
			element.title = option.title;
			element.innerHTML = option.label;
			element.addEventListener("click", () =>
			{
				if (Array.isArray(option.value))
				{
					this.options = option.value;
				} else if (typeof option.value === 'function')
				{
					this.dispatchEvent(new CustomEvent("select", {detail: option.value(option)}));
					this.hide();
				} else
				{
					this.dispatchEvent(new CustomEvent("select", {detail: option.value}));
					this.hide();
				}
			});
		});
	}

	static pick(options, caption)
	{
		if (typeof options === "string")
			return fetch(options)
				.then(response =>
				{
					return response.ok ?
						response.json()
						: response.text().then(message =>
					{
						throw new Error(message);
					});
				}).then(result => GMenuPicker.pick(result, caption));

		let picker = window.top.document.createElement("g-menu-picker");
		picker.options = options;
		if (caption)
			picker.caption = caption;
		picker.show();

		return new Promise(resolve =>
		{
			picker.addEventListener("cancel", () => resolve());
			picker.addEventListener("select", e => resolve(e.detail));
		});
	}
};

customElements.define('g-menu-picker', GMenuPicker);