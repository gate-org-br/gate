let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	width: 100%;
	height: 40px;
	display: grid;
	cursor: pointer;
	min-height: 40px;
	align-items: center;
	background-color: #CCCCCC;
	grid-template-columns: 32px 1fr;
}

:host([status=ONLINE])
{
	color: #006600;
}

:host([status=OFFLINE])
{
	color: #660000;
}

:host(*)::after
{
	content: attr(name);
}

:host(*)::before
{
	font-size: 16px;
	font-family: gate;
	display: flex;
	align-items: center;
	justify-content: center;
}

:host([status=ONLINE])::before
{
	content: '\\2004';
}

:host([status=OFFLINE])::before
{
	content: '\\2008';
}</style>`;

/* global customElements */

customElements.define('g-chat-user', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", () => this.getRootNode()
				.host.dispatchEvent(new CustomEvent('selected',
					{detail: {id: this.id, name: this.name, status: this.status}})));
	}

	set id(id)
	{
		this.setAttribute("id", id);
	}

	get id()
	{
		return this.getAttribute("id");
	}

	set name(name)
	{
		this.setAttribute("name", name);
	}

	get name()
	{
		return this.getAttribute("name");
	}

	set status(status)
	{
		this.setAttribute("status", status);
	}

	get status()
	{
		return this.getAttribute("status");
	}
});
