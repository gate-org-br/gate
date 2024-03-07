let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-progress">:host(*)
{
	width: 100%;
	flex-grow: 1;
	display: flex;
	align-items: stretch;
	background-color: var(--main6);
}

:host(*)::before
{
	content: "";
	animation-fill-mode:both;
	background-color: var(--base1);
	animation: progress 2s infinite ease-in-out;
}

@keyframes progress {
	0% {
		flex-basis: 0;
	}
	100% {
		flex-basis: 100%;
	}
}</style>`;
/* global customElements, template */

customElements.define('g-progress', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});