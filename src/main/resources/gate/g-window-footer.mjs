let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	padding: 2px;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: var(--g-window-footer-background-color);
	background-image: var(--g-window-footer-background-image);
}

::slotted(g-coolbar)
{
	margin: 4px;
}

::slotted(a), ::slotted(button) {
	height: 100%;
	flex-grow: 1;
	display: flex;
	color: var(--g);
	font-size: 16px;
	flex-basis: 100%;
	font-weight: bold;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	border: 1px solid #CCCCCC;
	background-image:  linear-gradient(to bottom, #E3E0D0 0%, #858279 100%);
}

::slotted(a:first-child) {
	border-radius: 5px 0 0 5px;
}

::slotted(a:last-child) {
	border-radius: 0 5px 5px 0;
}

::slotted(a:hover)
{
	font-weight: bold;
	border-color: var(--hovered);
}</style>`;

/* global customElements */

customElements.define('g-window-footer', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});
