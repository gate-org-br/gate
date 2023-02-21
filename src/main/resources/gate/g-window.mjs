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
	border-bottom: 1px solid #CCCCCC;
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
	border-top: 1px solid #CCCCCC;
}

main > footer > hr {
	flex-grow: 1;
	border: 0px none transparent;
}

main > footer > a,
main > footer > button {
	padding: 8px;
	display: flex;
	cursor: pointer;
	font-size: 16px;
	flex-basis: 140px;
	border-radius: 3px;
	align-items: center;
	background-color: #FFFFFF;
	border: 1px solid #CCCCCC;
	justify-content: space-between;
}

main > footer > a:hover,
main > footer > button:hover {
	border-color: black;
}

main > footer > a.Action,
main > footer > button.Action {
	color: white;
	border: none;
	background-color: #2A6B9A;
}

main > footer > a.Action:hover,
main > footer > button.Action:hover {
	background-color: #25608A;
}

main > footer > a.Commit,
main > footer > button.Commit {
	color: white;
	border: none;
	background-color: #009E60;
}

main > footer > a.Commit:hover,
main > footer > button.Commit:hover {
	background-color: #008E56;
}


main > footer > a.Delete,
main > footer > button.Delete {
	color: white;
	border: none;
	background-color: #DC4C64;
}

main > footer > a.Delete:hover,
main > footer > button.Delete:hover {
	background-color: #C6445A;
}

main > footer > a.Return,
main > footer > button.Return,
main > footer > a.Cancel,
main > footer > button.Cancel {
	color: black;
	border: none;
	background-color: #E8E8E8;
}

main > footer > a.Return:hover,
main > footer > button.Return:hover,
main > footer > a.Cancel:hover,
main > footer > button.Cancel:hover {
	background-color: #D0D0D0;
}

main > footer > a[disabled],
main > footer > button[disabled] {
	filter: grayscale(80%) opacity(40%);
}

main > footer > a:focus,
main > footer > button:focus{
	outline: 4px solid var(--hovered);
}</style>`;

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