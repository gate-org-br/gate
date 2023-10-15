let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*){
	width: 100%;
	display: flex;
	overflow: auto;
	resize: vertical;
	height: fit-content;
	align-items: center;
	justify-content: center;
	border: 1px solid #CCCCCC;
	background-color: #EFEFEF;
}

:host(:focus)
{
	background-color: var(--hovered);
}

* {
	height: 100%;
}</style>`;

/* global customElements */

customElements.define('g-text-editor-box', class extends HTMLElement
{
	constructor()
	{
		super();
		this.tabindex = 1;
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener('click', event => event.stopPropagation() & this.focus());
		this.addEventListener('keydown', event => event.key === 'Delete' && this.remove());
	}

	connectedCallback()
	{
		if (this.firstElementChild)
		{
			this.firstElementChild.tabIndex = 1;
			this.shadowRoot.appendChild(this.firstElementChild);
		}
	}
});