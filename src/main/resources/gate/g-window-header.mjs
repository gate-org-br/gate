let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	padding: 4px;
	display: flex;
	font-size: 20px;
	font-weight: bold;
	align-items: center;
	justify-content: space-between;
	color: var(--g-window-header-color, white);
	background-color: var(--g-window-header-background-color, #788185);
	background-image: var(--g-window-header-background-image, linear-gradient(to bottom, #788185 0%, #828b90 50%, #788185 100%));
}


::slotted(a), ::slotted(button) {
	color: white;
	display: flex;
	font-size: 16px;
	font-family: gate;
	font-weight: bold;
	align-items: center;
	text-decoration: none;
	justify-content: center;
}</style>`;

/* global customElements */

customElements.define('g-window-header', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});
