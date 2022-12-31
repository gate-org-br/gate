let template = document.createElement("template");
template.innerHTML = `
	<header>
	</header>
	<main>
	</main>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	width: auto;
	display: flex;
	font-size: 12px;
	cursor: pointer;
	position: relative;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
	background-color: var(--g-coolbar-background-color);
}

header
{
	gap: 8px;
	padding: 4px;
	height: 32px;
	display: grid;
	user-select: none;
	font-size: inherit;
	align-items: center;
	font-family: inherit;
	border: 1px solid #999999;
	grid-template-columns: 1fr 32px;
}

header::before
{
	content: attr(title);
}

header::after
{
	display: flex;
	content: '\\2276';
	font-family: gate;
	font-size: 0.5em;
	align-items: center;
	justify-content: center;
}

main {
	left: 0;
	flex-grow: 1;
	display: flex;
	display: none;
	width: inherit;
	top: calc(100%);
	flex-basis: 100%;
	font-size: inherit;
	position: absolute;
	flex-direction: column;
	border-top: 1px solid #CCCCCC;
	background-color: var(--g-coolbar-background-color);
}

a {
	gap: 8px;
	padding: 4px;
	height: 32px;
	display: grid;
	flex-basis: 100%;
	font-size: inherit;
	align-items: center;
	text-decoration: none;
	grid-template-columns: 1fr 32px;
}

g-icon, i {
	order: 1;
	font-size: 1.5em;
}

a:hover
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