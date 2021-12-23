let template = document.createElement("template");
template.innerHTML = `
	<a href='#' data-type='PDF'>PDF<i>&#x2218;</i></a>
	<a href='#' data-type='XLS'>XLS<i>&#x2221;</i></a>
	<a href='#' data-type='DOC'>DOC<i>&#x2220;</i></a>
	<a href='#' data-type='CSV'>CSV<i>&#x2222;</i></a>
 <style>:host(*) {
	width: 100%;
	height: auto;
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	justify-content: flex-start;
	background-image: linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%);
}

a {
	margin: 8px;
	display: flex;
	height: 128px;
	flex-grow: 1;
	cursor: pointer;
	font-size: 24px;
	min-width: 128px;
	border-radius: 5px;
	align-items: center;
	flex-direction: column;
	justify-content: center;
	background-color: white;
	flex-basis: calc(25% - 16px);
}

a:hover {
	background-color: var(--hovered);
}

i
{
	order: -1;
	margin: 8px;
	font-size: 50px;
	border-radius: 5px;
	font-family: "gate";
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