let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	gap: 12px;
	height: 100%;
	display: flex;
	overflow: auto;
	position: relative;
	align-items: stretch;
	flex-direction: column;
	justify-content: flex-start;
}</style>`;

/* global customElements, template */

customElements.define('g-table-scroll', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	connectedCallback()
	{
		Array.from(this.querySelectorAll("thead > tr > *")).forEach(e =>
		{
			e.style.top = "0";
			e.style.position = "sticky";
		});

		Array.from(this.querySelectorAll("tfoot > tr > *")).forEach(e =>
		{
			e.style.bottom = "0";
			e.style.position = "sticky";
		});
	}
});