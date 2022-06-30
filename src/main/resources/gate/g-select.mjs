let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
 <style>:host(*)
{
	gap: 4px;
	padding: 8px;
	display: grid;
	overflow: auto;
	overflow-y: auto;
	border-radius: 5px;
	align-items: center;
	justify-items: stretch;
	background-color: white;
	grid-template-columns: 32px 1fr;
}

:host([columns='1'])  {
	grid-template-columns: 32px 1fr
}
:host([columns='2'])  {
	grid-template-columns: 32px 1fr 32px 1fr
}
:host([columns='3'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='4'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='5'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='6'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='7'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='8'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

::slotted(*) {
	cursor: pointer
}
</style>`;

/* global customElements */

customElements.define('g-select', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.shadowRoot.children[0].addEventListener("slotchange", () =>
		{
			Array.from(this.querySelectorAll("label")).forEach(e =>
			{
				e.addEventListener("click", () => e.previousElementSibling && e.previousElementSibling.click());
			});
		});
	}
});