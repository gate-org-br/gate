let template = document.createElement("template");
template.innerHTML = `
	<header>
	</header>
	<main>
	</main>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	display: flex;
	font-size: 12px;
	cursor: pointer;
	position: relative;
	align-items: stretch;
	flex-direction: column;
	border: 1px solid #CCCCCC;
	background-color: #F0F0F0;
}

header
{
	gap: 8px;
	padding: 8px;
	height: 32px;
	display: grid;
	user-select: none;
	font-size: inherit;
	align-items: center;
	font-family: inherit;
	grid-template-columns: 1fr 24px;
}

header::before
{
	content: attr(title);
}

header::after
{
	display: flex;
	font-size: 0.5em;
	content: '\\2276';
	font-family: gate;
	align-items: center;
	justify-content: center;
}

main {
	left: -1px;
	top: 100%;
	display: none;
	width: inherit;
	right: -1px;
	font-size: inherit;
	position: absolute;
	flex-direction: column;
	background-color: #F0F0F0;
	border: 1px solid #CCCCCC;
}

a, .g-command {
	gap: 8px;
	padding: 8px;
	height: 32px;
	display: grid;
	color: #000066;
	font-size: inherit;
	align-items: center;
	text-decoration: none;
	grid-template-columns: 1fr 24px;
}

g-icon, i {
	order: 1;
	font-size: 1.5em;
}

a:hover,
.g-command:hover
{
	background-color: var(--hovered);
}

:host([opened]) > main
{
	display: block;
}

:host([opened]) > header::after
{
	content: '\\2278';
}</style>`;

/* global customElements */



customElements.define('g-select-menu', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("mouseout", () => this.opened = false);
		this.shadowRoot.addEventListener("click", () => this.opened = !this.opened);
	}

	set commands(commands)
	{
		let main = this.shadowRoot.querySelector("main");
		Array.from(commands).forEach(command =>
		{
			let link = main.appendChild(document.createElement("a"));
			link.href = '#';
			link.innerText = command.text;
			link.appendChild(document.createElement("g-icon")).innerHTML = `&#X${command.icon}`;
			link.addEventListener("click", () => command.action());
		});
	}

	get opened()
	{
		return this.hasAttribute("opened");
	}

	set opened(opened)
	{
		if (opened)
			this.setAttribute("opened", "opened");
		else
			this.removeAttribute("opened");

	}

	set label(label)
	{
		this.shadowRoot.querySelector("header")
			.setAttribute("title", label);
	}

	get label()
	{
		this.shadowRoot.querySelector("header")
			.getAttribute("title");
	}

	connectedCallback()
	{
		let main = this.shadowRoot.querySelector("main");
		Array.from(this.children).forEach(e => main.appendChild(e));
	}

	attributeChangedCallback()
	{
		this.label = this.getAttribute("label");
	}

	static get observedAttributes()
	{
		return ["label"];
	}
});