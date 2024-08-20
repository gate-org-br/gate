let template = document.createElement("template");
template.innerHTML = `
	<div>
		<slot></slot>
	</div>
 <style data-element="g-tabbar">*
{
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	color: black;
	border: none;
	height: auto;
	flex-grow: 1;
	display: flex;
	position: relative;
	align-items: stretch;
	justify-content: stretch;
	background-color: var(--main3);
}

div
{
	gap: 8px;
	padding: 8px;
	height: auto;
	border: none;
	flex-grow: 1;
	display: flex;
	overflow-x: auto;
	white-space: nowrap;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	gap: 4px;
	padding: 6px;
	height: auto;
	display: flex;
	color: inherit;
	flex-shrink: 0;
	cursor: pointer;
	flex-basis: 120px;
	border-radius: 5px;
	font-size: inherit;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	justify-content: space-around;
	background-color: var(--main4);
}

::slotted(a[aria-selected]),
::slotted(button[aria-selected]),
::slotted(.g-command[aria-selected])
{
	color: var(--base1);
	background-color: var(--main5);
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color: var(--hovered);
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: none
}

::slotted([hidden="true"])
{
	display: none;
}

::slotted(hr)
{
	border: none;
	flex-grow: 100000;
}

::slotted(:is(a, button, .g-command)[data-loading])
{
	position: relative;
}

::slotted(:is(a, button, .g-command)[data-loading])::before
{
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	content: "";
	position: absolute;
	background-size: 50%;
	border-radius: inherit;
	background-color: #F0F0F0;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}

:host(.inline) ::slotted(a),
:host(.inline) ::slotted(button),
:host(.inline) ::slotted(.g-command)
{
	flex-basis: 160px;
	flex-direction: row;
	justify-content: flex-start;
}

:host(.inline) ::slotted(a)::before,
:host(.inline) ::slotted(button)::before,
:host(.inline) ::slotted(.g-command)::before
{
	align-items: center;
	justify-content: flex-start;
}

:host(.inline) ::slotted(a)::after,
:host(.inline) ::slotted(button)::after,
:host(.inline) ::slotted(.g-command)::after
{
	left: 32px;
	max-width: calc(100% - 40px)
}</style>`;
/* global customElements */

import loading from './loading.js';
import TriggerExtractor from './trigger-extractor.js';

customElements.define("g-tabbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		let div = this.shadowRoot.querySelector("div");
		window.addEventListener("trigger-success", event =>
		{
			let target = event.detail.cause.target;
			let trigger = Array.from(this.children)
				.filter(e => e === target ||
						(TriggerExtractor.method(e) === TriggerExtractor.method(target)
							&& TriggerExtractor.action(e) === TriggerExtractor.action(target)
							&& TriggerExtractor.target(e) === TriggerExtractor.target(target)))[0];
			if (trigger)
				this.select(trigger);
		});
	}

	connectedCallback()
	{
		loading(this.parentNode);
		Array.from(this.children)
			.flatMap(e => Array.from(e.childNodes))
			.filter(e => e.nodeType === Node.TEXT_NODE)
			.forEach(e => e.parentNode.appendChild(e));
		let action = window.location.href;
		let origin = window.location.origin;
		let triggers = Array.from(this.children).filter(e => TriggerExtractor.target(e) === "_self");
		let selected = triggers.filter(e => action === new URL(TriggerExtractor.action(e), origin).href)[0]
			|| triggers.filter(e => action.startsWith(TriggerExtractor.action(e)))[0];
		if (selected)
			this.select(selected);
	}

	select(element)
	{
		Array.from(this.children).forEach(e =>
		{
			if (e === element)
				e.setAttribute("aria-selected", "")
			else
				e.removeAttribute("aria-selected");
		});
	}
});