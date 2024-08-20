let template = document.createElement("template");
template.innerHTML = `
	<div>
		<slot></slot>
	</div>
 <style data-element="g-coolbar">*
{
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	height: auto;
	color: black;
	border: none;
	flex-grow: 1;
	display: flex;
	position: relative;
	align-items: stretch;
	justify-content: stretch;
}

div
{
	gap: 8px;
	padding: 8px;
	flex-grow: 1;
	display: flex;
	overflow-x: auto;
	white-space: nowrap;
	flex-direction: row-reverse;
}

::slotted(:is(a, button, .g-command))
{
	gap: 8px;
	width: 120px;
	height: 44px;
	color: black;
	padding: 8px;
	color: black;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	min-width: fit-content;
	background-color: #E8E8E8;
	justify-content: space-between;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color: #D0D0D0;
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: 4px solid var(--hovered);
}

::slotted(a.primary),
::slotted(button.primary),
::slotted(.g-command.primary)
{
	color: white;
	border: none;
	background-color: #2A6B9A;
}

::slotted(a.primary:hover),
::slotted(button.primary:hover),
::slotted(.g-command.primary:hover)
{
	background-color: #25608A;
}

::slotted(a.alternative),
::slotted(button.alternative),
::slotted(.g-command.alternative)
{
	color: white;
	border: none;
	background-color: #009E60;
}

::slotted(a.alternative:hover),
::slotted(button.alternative:hover),
::slotted(.g-command.alternative:hover)
{
	background-color: #008E56;
}

::slotted(a.tertiary),
::slotted(button.tertiary),
::slotted(.g-command.tertiary)
{
	background-color: var(--main1);
	border: 1px solid var(--main6);
}

::slotted(a.tertiary:hover),
::slotted(button.tertiary:hover),
::slotted(.g-command.tertiary:hover)
{
	border: 1px solid black;
}

::slotted(a.danger),
::slotted(button.danger),
::slotted(.g-command.danger)
{
	color: white;
	border: none;
	background-color: #AA2222;
}

::slotted(a.danger:hover),
::slotted(button.danger:hover),
::slotted(.g-command.danger:hover)
{
	background-color: #882222;
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

:host([reverse]) div
{
	flex-direction: row;
}

:host([disabled])
{
	background-color: var(--main6);
}

:host([disabled]) div,
:host([disabled]) button
{
	display: none;
}

:host([disabled])::before
{
	content: "";
	height: 44px;
	grid-column: 2;
	animation-fill-mode: both;
	background-color: var(--base1);
	animation: progress 2s infinite ease-in-out;
}

@keyframes progress
{
	0%
	{
		width: 0;
	}

	100%
	{
		width: 100%;
	}
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
	color: inherit;
	position: absolute;
	background-size: 50%;
	border-radius: inherit;
	background-color: inherit;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}</style>`;
/* global customElements */

import loading from './loading.js';

customElements.define("g-coolbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get disabled()
	{
		return this.hasAttribute("disabled");
	}

	set disabled(value)
	{
		if (value)
			this.setAttribute("disabled", "");
		else
			this.removeAttribute("disabled");
	}

	connectedCallback()
	{
		loading(this.parentNode);
		this.setAttribute("size", this.children.length);
	}
});