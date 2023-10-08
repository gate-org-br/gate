let template = document.createElement("template");
template.innerHTML = `
	<a href='#' data-type='PDF'><g-icon>&#x2218;</g-icon>PDF</a>
	<a href='#' data-type='XLS'><g-icon>&#x2221;</g-icon>XLS</a>
	<a href='#' data-type='DOC'><g-icon>&#x2220;</g-icon>DOC</a>
	<a href='#' data-type='CSV'><g-icon>&#x2222;</g-icon>CSV</a>
 <style>:host(*) {
	gap: 8px;
	width: 100%;
	height: auto;
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	justify-content: flex-start;
}

a {
	gap: 8px;
	flex-grow: 1;
	display: flex;
	height: 128px;
	cursor: pointer;
	font-size: 24px;
	min-width: 128px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	justify-content: center;
	background-color: white;
}

a:hover {
	background-color: var(--hovered);
}

g-icon
{
	font-size: 50px;
}
</style>`;

/* global customElements, template */

customElements.define('g-report-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		Array.from(this.shadowRoot.querySelectorAll("a"))
			.forEach(e => e.addEventListener("click", () =>
					this.dispatchEvent(new CustomEvent('selected', {cancelable: false, detail: e.getAttribute("data-type")}))));
	}
});