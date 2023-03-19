let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host(*) {
	height: auto;
	display: flex;
	font-size: 16px;
	border-radius: 3px;
	flex-direction: column;
	background-color: #FAFAFA;
	border: 1px solid #F8F8F8;
	box-shadow: 1px 1px 2px 0px #CCCCCC;
}

::slotted(i),
::slotted(e),
::slotted(g-icon),
::slotted(picture)
{
	padding: 8px;
	display: flex;
	align-items: center;
	justify-content: center;
}

::slotted(header)
{
	gap: 8px;
	padding: 8px;
	display: flex;
	font-size: 1.2em;
	flex-basis: 32px;
	font-weight: bold;
	align-items: center;
	justify-content: space-between;
	border-bottom: 1px solid #DDDDDD;
}

::slotted(nav)
{
	flex-grow: 1;
	padding: 0 8px 0 8px;
	justify-content: flex-end;
	border-bottom: 1px solid #DDDDDD;
}

::slotted(section)
{
	padding: 8px;
	flex-grow: 1;
	font-size: 1em;
	overflow: auto;
	text-align: justify;
}

::slotted(footer) {
	gap: 4px;
	padding: 8px;
	color: black;
	display: flex;
	font-size: 1.2em;
	flex-basis: 32px;
	align-items: center;
	justify-content: flex-end;
	border-top: 1px solid #DDDDDD;
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