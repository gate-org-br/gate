let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-card">:host(*) {
	height: auto;
	display: flex;
	border-radius: 3px;
	flex-direction: column;
	background-color: var(--main1);
	border: 1px solid var(--main3);
	box-shadow: 1px 1px 2px 0px var(--main6);
}

::slotted(i),
::slotted(e),
::slotted(g-icon)
{
	padding: 8px;
	display: flex;
	align-items: center;
	justify-content: center;
}

::slotted(header),
::slotted(section),
::slotted(footer) {
	border-bottom: 1px solid var(--main5);
}

::slotted(header:last-child),
::slotted(section:last-child),
::slotted(footer:last-child) {
	border-bottom: none;
}

::slotted(img) {
	margin: 10px;
	height: 48px;
	align-self: center;
}

::slotted(header)
{
	padding: 4px;
	font-size: 16px;
	flex-basis: 36px;
	font-weight: bold;
}

::slotted(nav)
{
	gap: 4px;
	color: black;
	display: flex;
	font-size: 12px;
	flex-basis: 36px;
	align-items: center;
	justify-content: flex-end;
}

::slotted(section)
{
	padding: 8px;
	flex-grow: 1;
	overflow: auto;
	font-size: 12px;
	text-align: justify;
}

::slotted(footer) {
	gap: 4px;
	padding: 4px;
	color: black;
	display: flex;
	font-size: 10px;
	flex-basis: 36px;
	font-style: italic;
	align-items: center;
	justify-content: flex-end;
}</style>`;
/* global customElements, template */

customElements.define('g-card', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});