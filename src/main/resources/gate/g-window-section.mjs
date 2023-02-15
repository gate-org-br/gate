let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	display: flex;
	align-items: stretch;
	justify-content: center;
	background-image: var(--g-window-section-background-image, none);
	background-color: var(--g-window-section-background-color, #c3c0b0);
}</style>`;

/* global customElements */

customElements.define('g-window-section', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});
