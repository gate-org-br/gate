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
	width: 0;
	padding: 0px;
	display: flex;
	max-width: 80%;
	transition: 0.5s;
	overflow-y: auto;
	align-items: stretch;
	flex-direction: column;
	justify-content: flex-start;
}

:host([position='left']) > main > section {
	width: 280px;
	padding: 8px;
	background-image: linear-gradient(to right, var(--main-shaded20) 0%, var(--main-tinted20) 100%);
}


:host([position='right']) > main > section {
	width: 280px;
	padding: 8px;
	background-image: linear-gradient(to left, var(--main-shaded20) 0%, var(--main-tinted20) 100%);
}

a, button, .g-command
{
	color: black;
	padding: 8px;
	height: 40px;
	display: flex;
	font-size: 16px;
	min-height: 40px;
	overflow: hidden;
	font-weight: bold;
	align-items: center;
	text-decoration: none;
}


a:hover, button:hover, .g-command:hover
{
	background-color: var(--hovered);
}


i {
	order: -1;
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
}

</style>`;

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