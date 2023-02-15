let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	display: flex;
	align-items: center;
	justify-content: space-between;

	color: var(--g-window-header-color, white);
	border: var(--g-window-header-border, none);
	padding: var(--g-window-header-padding, 4px);
	font-size: var(--g-window-header-font-size, 20px);
	font-weight: var(--g-window-header-font-weight, bold);
	background-color: var(--g-window-header-background-color, #788185);
	background-image: var(--g-window-header-background-image,
		linear-gradient(to bottom, #788185 0%, #ced7dc 50%, #788185 100%));
}

::slotted(a),
::slotted(button)
{
	display: flex;
	font-family: gate;
	align-items: center;
	text-decoration: none;
	justify-content: center;

	color: var(--g-window-header-button-color, white);
	font-size: var(--g-window-header-button-font-size, 16px);
	font-weight: var(--g-window-header-button-font-weight, bold);
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
