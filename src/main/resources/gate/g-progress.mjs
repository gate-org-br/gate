let template = document.createElement("template");
template.innerHTML = `
	<span>
	</span>
 <style>:host
{
	width: 100%;
	flex-grow: 1;
	display: flex;
	align-items: stretch;
	background-color: #CCCCCC;
}

span
{
	animation-fill-mode:both;
	background-color: #778899;
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