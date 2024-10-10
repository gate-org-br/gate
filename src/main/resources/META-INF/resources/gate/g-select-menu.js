let template = document.createElement("template");
template.innerHTML = `
	<header>
	</header>
	<main>
		<slot>
		</slot>
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
	border: 1px solid var(--main6);
	background-color: var(--main4);
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
	z-index: 1000;
	font-size: inherit;
	position: absolute;
	flex-direction: column;
	background-color: var(--main4);
	border: 1px solid var(--main6);
}

::slotted(*) {
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

::slotted(*:hover)
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
		this.addEventListener("mouseleave", () => this.opened = false);
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
		Array.from(this.children)
				.map(option => option.querySelector("g-icon, i"))
				.filter(icon => icon)
				.forEach(icon => icon.parentNode.appendChild(icon));
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