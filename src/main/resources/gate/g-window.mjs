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

main {
	height: auto;
	display: flex;
	position: fixed;
	border-radius: 3px;
	flex-direction: column;
	background-color: #F8F8F8;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
}

main > header
{
	padding: 8px;
	display: flex;
	font-size: 16px;
	flex-basis: 40px;
	font-weight: bold;
	align-items: center;
	justify-content: space-between;
	border-bottom: 1px solid #CCCCCC;
}

main > section
{
	padding: 8px;
	display: flex;
	align-items: stretch;
	justify-content: stretch;
}

main > footer {
	padding: 8px;
	display: flex;
	flex-basis: 60px;
	align-items: stretch;
	justify-content: stretch;
	border-top: 1px solid #CCCCCC;
}

main > header > a
{
	color: black;
	font-size: 18px;
	text-decoration: none;
}
</style>`;

/* global customElements */

import GModal from './g-modal.mjs';

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