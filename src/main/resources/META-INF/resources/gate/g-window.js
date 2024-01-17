let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	display: flex;
	position: fixed;
	align-items: center;
	justify-content: center;
}

main,
dialog {
	max-width: none;
	max-height: none;
	margin: auto;
	padding: 0;
	height: auto;
	border: none;
	display: flex;
	border-radius: 3px;
	flex-direction: column;
	background-color: var(--main3);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

main > header,
dialog > header
{
	gap: 8px;
	padding: 8px;
	display: flex;
	font-size: 16px;
	flex-basis: 40px;
	font-weight: bold;
	align-items: center;
	justify-content: space-between;
	border-bottom: 1px solid var(--main6);
}

main > section,
dialog > section
{
	padding: 8px;
	flex-grow: 1;
	display: flex;
	overflow: auto;
	align-items: flex-start;
	justify-content: stretch;
	-webkit-overflow-scrolling: touch;
}

main > section > fieldset:only-child,
dialog > section > fieldset:only-child {
	border: none;
}

main > header > label,
dialog > header > label
{
	flex-grow: 1;
	display: flex;
	color: inherit;
	font-size: inherit;
	align-items: center;
	justify-content: flex-start;
}

main > header > a,
main > header > button,
dialog > header > a,
dialog > header > button
{
	border:none;
	color: black;
	display: flex;
	cursor: pointer;
	font-size: 16px;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	background-color: transparent;
}

main > header > a > g-icon,
main > header > button > g-icon
dialog > header > a > g-icon,
dialog > header > button > g-icon
{
	line-height: 16px;
}

main > footer,
dialog > footer {
	gap: 4px;
	padding: 8px;
	display: flex;
	flex-basis: 60px;
	align-items: stretch;
	justify-content: stretch;
	flex-direction: row-reverse;
	border-top: 1px solid var(--main6);
}</style>`;

/* global customElements, template */

import GModal from './g-modal.js';
import stylesheets from './stylesheets.js';
export default class GWindow extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		stylesheets('g-table.css', 'input.css', 'fieldset.css')
			.forEach(e => this.shadowRoot.appendChild(e));
	}
};
customElements.define('g-window', GWindow);