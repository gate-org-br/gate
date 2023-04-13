let template = document.createElement("template");
template.innerHTML = `
	<main>
		<section>
		</section>
	</main>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	position: fixed;
}

main
{
	width: 100%;
	height: 100%;
	display: flex;
	align-items: stretch;

}

:host([position="left"]) > main
{
	justify-content: flex-start;
}
:host([position="right"]) > main
{
	justify-content: flex-end;
}

section
{
	gap: 6px;
	width: 0;
	padding: 0px;
	display: flex;
	max-width: 80%;
	transition: 0.5s;
	overflow-y: auto;
	align-items: stretch;
	flex-direction: column;
	background-color: var(--main2);
	justify-content: flex-start;
}

:host([position='left']) > main > section {
	width: 280px;
	padding: 6px;
	border-right: 1px solid var(--main4);
}

:host([position='right']) > main > section {
	width: 280px;
	padding: 6px;
	border-left: 1px solid var(--main4);
}

a,
button,
.g-command
{
	border: none;
	padding: 8px;
	display: flex;
	color: inherit;
	cursor: pointer;
	flex-basis: 24px;
	font-size: inherit;
	align-items: center;
	text-decoration: none;
	background-color: white;
	justify-content: space-between;
	box-shadow: 1px 1px 2px 0px var(--main6);
}

a.primary,
button.primary,
.g-command.primary
{
	color: #000066;
}

a.tertiary,
button.tertiary,
.g-command.tertiary
{
	box-shadow: none;
	background-color: var(--main4);
}

a.danger,
button.danger,
.g-command.danger
{
	color: #660000;
}


a.warning,
button.warning,
.g-command.warning
{
	color: #666600;
}

a.success,
button.success,
.g-command.success
{
	color: #006600;
}

a.info,
button.info,
.g-command.info
{
	color: #4444FF;
}

a.link,
button.link,
.g-command.link
{
	box-shadow: none;
	background-color: transparent;
}

a.dark,
button.dark,
.g-command.dark
{
	color: white;
	background-color: black;
}

a.dark:hover,
button.dark:hover,
.g-command.dark:hover
{
	color: black;
}

a[disabled],
a[disabled]:hover,
button[disabled],
button[disabled]:hover,
.g-command[disabled],
.g-command[disabled]:hover {

	color: #AAAAAA;
	cursor: not-allowed;
	filter: opacity(40%);
	background-color: var(--main6);
}

a:focus,
button:focus,
.g-command:focus{
	outline: 4px solid var(--hovered);
}

a:hover,
button:hover,
.g-command:hover
{
	background-color: #FFFACD;
}

i {
	order: 1;
	font-family: gate;
	margin-right: 8px;
	font-size: inherit;
	font-style: normal;
	text-decoration: none;
}

hr {
	border: none;
	flex-grow: 1;
}

br {
	height: 16px;
}</style>`;

/* global customElements */

import GModal from './g-modal.mjs';

customElements.define('g-side-menu', class extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", e => e.stopPropagation() | this.hide());
	}

	showLeft()
	{
		setTimeout(() => this.position = "left", 100);
	}

	showRight()
	{
		setTimeout(() => this.position = "right", 100);
	}

	set position(position)
	{
		this.setAttribute("position", position);
	}

	get position()
	{
		return this.getAttribute("position");
	}

	show(element)
	{
		let center = element.getBoundingClientRect();
		center = center.left + (center.width / 2);
		if (center <= window.innerWidth / 2)
			this.showLeft();
		else
			this.showRight();
	}

	set elements(elements)
	{
		let menu = this.shadowRoot.querySelector("section");
		while (menu.firstChild)
			menu.removeChild(menu.firstChild);
		elements.forEach(e => menu.appendChild(e));
	}
});