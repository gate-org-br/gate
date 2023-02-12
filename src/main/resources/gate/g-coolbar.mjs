let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box;
}

:host(*)
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

:host([reverse]) #container
{
	flex-direction: row;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	margin: 2px;
	height: 40px;
	padding: 4px;
	color: black;
	display: flex;
	flex: 0 1 auto;
	font-size: 12px;
	cursor: pointer;
	min-width: 120px;
	font-weight: bold;
	font-style: normal;
	border-radius: 5px;
	align-items: center;
	text-decoration: none;
	justify-content: space-between;


	border: 1px solid var(--g-coolbar-button-border-color);
	background-color: var(--g-coolbar-button-background-color);
	background-image: var(--g-coolbar-button-background-image);
}

::slotted(progress)
{
	margin: 4px;
	flex-grow: 100000;
}

::slotted(hr)
{
	flex-grow: 100000;
	border: none;
}

::slotted(:is(a, button, .g-command):hover)
{
	border: 1px solid var(--g-coolbar-hovered-button-border-color);
	background-color: var(--g-coolbar-hovered-button-background-color);
	background-image: var(--g-coolbar-hovered-button-background-image);
}

::slotted(:is(a, button, .g-command):focus)
{
	outline: none;
	border: 2px solid var(--hovered);
}

::slotted(:is(a, button, .g-command).Delete) {
	color: var(--r);
}

::slotted(:is(a, button, .g-command).Commit) {
	color: var(--g);
}

::slotted(:is(a, button, .g-command).Action) {
	color: var(--b);
}

::slotted(:is(a, button, .g-command).Cancel) {
	color: var(--r);
}

::slotted(:is(a, button, .g-command).Return) {
	color: var(--r);
}

::slotted(*[hidden="true"])
{
	display: none;
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