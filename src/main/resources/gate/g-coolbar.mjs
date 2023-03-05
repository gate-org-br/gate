let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	flex-grow: 1;
	margin-top: 10px;
	margin-bottom: 10px;
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

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	gap: 8px;
	width: auto;
	height: 44px;
	color: black;
	padding: 8px;
	color: black;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 16px;
	min-width: 140px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	background-color: #E8E8E8;
	justify-content: space-between;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color: #D0D0D0;
}

::slotted(a.primary),
::slotted(button.primary),
::slotted(.g-command.primary) {
	color: white;
	border: none;
	background-color: #2A6B9A;
}

::slotted(a.primary:hover),
::slotted(button.primary:hover),
::slotted(.g-command.primary:hover) {
	background-color: #25608A;
}

::slotted(a.warning),
::slotted(button.warning),
::slotted(.g-command.warning) {
	color: black;
	border: none;
	background-color: #ffc107;
}

::slotted(a.warning:hover),
::slotted(button.warning:hover),
::slotted(.g-command.warning:hover) {
	background-color: #dfa700;
}

::slotted(a.danger),
::slotted(button.danger),
::slotted(.g-command.danger) {
	color: white;
	border: none;
	background-color: #b23d51;
}

::slotted(a.danger:hover),
::slotted(button.danger:hover),
::slotted(.g-command.danger:hover) {
	background-color: #C6445A;
}


::slotted(a.success),
::slotted(button.success),
::slotted(.g-command.success) {
	color: white;
	border: none;
	background-color: #009E60;
}

::slotted(a.success:hover),
::slotted(button.success:hover),
::slotted(.g-command.success:hover) {
	background-color: #008E56;
}

::slotted(a.light),
::slotted(button.light),
::slotted(.g-command.light) {
	background-color: #FFFFFF;
	border: 1px solid #CCCCCC;
}

::slotted(a.light:hover),
::slotted(button.light:hover),
::slotted(.g-command.light:hover) {
	border: 1px solid black;
}

::slotted(a.dark),
::slotted(button.dark),
::slotted(.g-command.dark) {
	color: white;
	border: none;
	background-color: #000000;
}

::slotted(a.dark:hover),
::slotted(button.dark:hover),
::slotted(.g-command.dark:hover) {
	background-color: #222222;
}

::slotted(a.info),
::slotted(button.info),
::slotted(.g-command.info) {
	color: black;
	border: none;
	background-color: #0dcaf0;
}

::slotted(a.info:hover),
::slotted(button.info:hover),
::slotted(.g-command.info:hover) {
	background-color: #0cbadd;
}

::slotted(a.link),
::slotted(button.link),
::slotted(.g-command.link) {
	border: none;
	color: #1371fd;
	background-color: #FFFFFF;
}

::slotted(a.link:hover),
::slotted(button.link:hover),
::slotted(.g-command.link:hover) {
	color: #025ee7;
}

::slotted(a[disabled]),
::slotted(a[disabled]:hover),
::slotted(button[disabled]),
::slotted(button[disabled]:hover),
::slotted(.g-command[disabled]:hover),
::slotted(.g-command[disabled]:hover) {

	color: #AAAAAA;
	cursor: not-allowed;
	filter: opacity(40%);
	background-color: #CCCCCC;
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus){
	outline: 4px solid var(--hovered);
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
	flex-grow: 100000;
	border: none;
}
</style>`;

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