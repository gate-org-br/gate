let template = document.createElement("template");
template.innerHTML = `
	<section>
		<slot>
		</slot>
	</section>
	<footer>
	</footer>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	gap: 8px;
	width: auto;
	height: auto;
	margin: 12px;
	padding: 20px;
	display: grid;
	cursor: pointer;
	min-width: 200px;
	max-width: 300px;
	border-radius: 5px;
	align-items: stretch;
	grid-template-rows: 1fr 16px;
}

section
{
	flex-grow: 1;
	font-size: 12px;
	text-align: justify;
}

footer
{
	flex-grow: 1;
	display: flex;
	font-size: 8px;
	color: #666666;
	align-items: center;
	justify-content: flex-end;
}

:host([type=LOCAL])
{
	align-self: flex-start;
	background-color: #E6E9EF;

}

:host([type=REMOTE])
{
	align-self: flex-end;
	background-color: #AACCAA;

}</style>`;

/* global customElements */

customElements.define('g-chat-message', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", e => this.getRootNode().host
				.dispatchEvent(new CustomEvent('selected', {detail: this})));
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	get type()
	{
		return this.getAttribute("type");
	}

	set text(text)
	{
		this.innerText = text;
	}

	get text()
	{
		return this.innerText;
	}

	set date(date)
	{
		this.setAttribute("date", date);
	}

	get date()
	{
		return this.getAttribute("date");
	}

	attributeChangedCallback()
	{
		this.shadowRoot.querySelector("footer")
			.innerText = this.date;
	}

	static get observedAttributes()
	{
		return ['date'];
	}
});
