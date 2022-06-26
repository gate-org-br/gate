let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	margin: 0;
	margin-top: 10px;
	margin-bottom: 10px;

	width: 100%;
	height: 48px;
	flex-grow: 1;
	padding: 2px;
	display: flex;
	overflow: hidden;
	border-radius: 5px;
	align-items: stretch;
	flex-direction: row-reverse;
	color: var(--g-coolbar--color);
	background-color: var(--g-coolbar-background-color);
	background-image: var(--g-coolbar-background-image);
}


:host(:first-child) {
	margin-top: 0px;
}

:host(:last-child) {
	margin-bottom: 0px;
}

:host(:only-child)
{
	margin-bottom: 0px;
	margin-bottom: 0px
}

::slotted(:is(a, button, .g-command))
{
	margin: 2px !important;
	color: black !important;
	height: 40px !important;
	padding: 4px !important;
	display: flex !important;
	flex: 0 1 auto !important;
	font-size: 12px !important;
	cursor: pointer !important;
	min-width: 120px !important;
	font-weight: bold !important;
	font-style: normal !important;
	border-radius: 5px !important;
	align-items: center !important;
	text-decoration: none !important;
	justify-content: space-between !important;
	border: 1px solid var(--g-coolbar-button-border-color) !important;
	background-color: var(--g-coolbar-button-background-color) !important;
	background-image: var(--g-coolbar-button-background-image) !important;
}

::slotted(progress)
{
	margin: 4px !important;
	flex-grow: 1 !important;
}

::slotted(hr) 
{
	flex-grow: 1 !important;
	border: none !important;
}


::slotted(:is(a, button, .g-command):hover)
{
	border: 1px solid var(--g-coolbar-hovered-button-border-color) !important;
	background-color: var(--g-coolbar-hovered-button-background-color) !important;
	background-image: var(--g-coolbar-hovered-button-background-image) !important;
}

::slotted(:is(a, button, .g-command):focus)
{
	outline: none !important;
	border: 2px solid var(--hovered) !important;
}

::slotted(:is(a, button, .g-command).Delete) {
	color: var(--r) !important;
}

::slotted(:is(a, button, .g-command).Commit) {
	color: var(--g) !important;
}

::slotted(:is(a, button, .g-command).Action) {
	color: var(--b) !important;
}

::slotted(:is(a, button, .g-command).Cancel) {
	color: var(--r) !important;
}

::slotted(:is(a, button, .g-command).Return) {
	color: var(--r) !important;
}

::slotted(*[hidden="true"])
{
	display: none !important;
}</style>`;

/* global customElements, template */

import GOverflow from "./g-overflow.mjs";

export default class GCoolbar extends GOverflow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
}

customElements.define('g-coolbar', GCoolbar);