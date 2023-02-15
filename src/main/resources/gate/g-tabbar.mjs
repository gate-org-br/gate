let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	border: none;
	height: auto;
	flex-grow: 1;
	display: flex;
	overflow: hidden;
	align-items: stretch;
	justify-content: flex-start;

	color: var(--g-tabbar-color, black);
	background-image:  var(--g-tabbar-background-image, none);
	background-color:  var(--g-tabbar-background-color, #CDCAB9);
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	margin: 4px;
	padding: 4px;
	height: auto;
	flex-grow: 1;
	display: flex;
	color: inherit;
	flex-shrink: 1;
	flex-basis: 100%;
	max-width: 160px;
	border-radius: 5px;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	justify-content: space-around;
}

::slotted(a[aria-selected]),
::slotted(button[aria-selected]),
::slotted(.g-command[aria-selected])
{
	--g-tabbar-selected-button-color: var(--b);
	--g-tabbar-selected-button-background-color: var(--main-tinted40);
	--g-tabbar-selected-button-background-image: linear-gradient(to bottom, #e1dfd5 0%, #ebeae3 50%, #e1dfd5 100%);
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	color: var(--g-tabbar-hovered-button-color, black);
	background-color:  var(--g-tabbar-hovered-button-background-color, #e1dfd5);
	background-image:  var(--g-tabbar-hovered-button-background-image, linear-gradient(to bottom, #d7d5c7 0%, #e1dfd5 50%, #d7d5c7 100%));
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: none
}

::slotted(*[hidden="true"])
{
	display: none;
}

::slotted(hr)
{
	border: none;
	flex-grow: 100000;
}</style>`;

/* global customElements, template */

import GOverflow from "./g-overflow.mjs";

customElements.define('g-tabbar', class extends GOverflow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});