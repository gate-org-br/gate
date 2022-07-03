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
	padding: 12px;
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
	align-self: flex-end;
	background-color: #AACCAA;

}

:host([type=REMOTE])
{
	align-self: flex-start;
	background-color: #FFFFFF;
}

:host([type=LOCAL]) footer::after
{
	margin-left: 4px;
	font-family: gate;
	font-size: inherit;
}

:host([type=LOCAL][status=POSTED]) footer::after
{
	color: #EFFDDE;
	content: '\\2017';
}

:host([type=LOCAL][status=RECEIVED]) footer::after
{
	color: #006600;
	content: '\\1000';
}
</style>`;

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

	set status(status)
	{
		this.setAttribute("status", status);
	}

	get status()
	{
		return this.getAttribute("status");
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
