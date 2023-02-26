let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	height: 44px;
	flex-grow: 1;
	display: flex;
	margin-top: 10px;
	overflow: hidden;
	margin-bottom: 10px;
	align-items: stretch;
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

#container {
	gap: 8px;
	flex-grow: 1;
	display: flex;
	align-items: stretch;
	flex-direction: row-reverse;
}

:host([reverse]) #container
{
	flex-direction: row;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	color: black;
	padding: 8px;
	color: black;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 16px;
	flex-basis: 140px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	background-color: #E8E8E8;
	border: 1px solid transparent;
	justify-content: space-between;
}

::slotted(a.Action),
::slotted(button.Action),
::slotted(.g-command.Action) {
	color: white;
	border: none;
	background-color: #2A6B9A;
}

::slotted(a.Commit),
::slotted(button.Commit),
::slotted(.g-command.Commit) {
	color: white;
	border: none;
	background-color: #009E60;
}

::slotted(a.Delete),
::slotted(button.Delete),
::slotted(.g-command.Delete) {
	color: white;
	border: none;
	background-color: #DC4C64;
}

::slotted(a.Return),
::slotted(button.Return),
::slotted(.g-command.Return),
::slotted(a.Cancel),
::slotted(button.Cancel),
::slotted(.g-command.Cancel) {
	background-color: #FFFFFF;
	border: 1px solid #E8E8E8;
}

::slotted(a[disabled]),
::slotted(a[disabled]:hover),
::slotted(button[disabled]),
::slotted(button[disabled]:hover),
::slotted(.g-command[disabled]:hover),
::slotted(.g-command[disabled]:hover) {

	color: #888888;
	cursor: not-allowed;
	filter: opacity(40%);
	background-color: #CCCCCC;
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus){
	outline: 4px solid #FFFACD;
}

::slotted(*[hidden="true"])
{
	display: none;
}

::slotted(progress)
{
	margin: 4px;
	flex-grow: 100000;
}

::slotted(hr)
{
	border: none;
	flex-grow: 100000;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover) {
	color: black;
	border: 1px solid #E8E8E8;
	background-color: #FFFACD;
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