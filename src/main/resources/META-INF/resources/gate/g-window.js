let template = document.createElement("template");
template.innerHTML = `
 <style>@import './gate/input.css';
@import './gate/table.css';
@import './gate/fieldset.css';

* {
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

main {
	height: auto;
	display: flex;
	position: fixed;
	border-radius: 3px;
	flex-direction: column;
	background-color: var(--main3);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

main > header
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

main > section
{
	padding: 8px;
	flex-grow: 1;
	display: flex;
	overflow: auto;
	align-items: flex-start;
	justify-content: stretch;
	-webkit-overflow-scrolling: touch;
}

main > section > fieldset:only-child {
	border: none;
}

main > header > label
{
	order: 1;
	flex-grow: 1;
	display: flex;
	color: inherit;
	font-size: inherit;
	align-items: center;
	justify-content: flex-start;
}

main > header > a,
main > header > button
{
	order: 2;
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
main > header > button > g-icon {
	line-height: 16px;
}

main > footer {
	gap: 4px;
	padding: 8px;
	display: flex;
	flex-basis: 60px;
	align-items: stretch;
	justify-content: stretch;
	flex-direction: row-reverse;
	border-top: 1px solid var(--main6);
}</style>`;

/* global customElements */

import GModal from './g-modal.js';

export default class GWindow extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
};

customElements.define('g-window', GWindow);