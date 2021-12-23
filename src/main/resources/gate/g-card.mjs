let template = document.createElement("template");
template.innerHTML = `
	<div id="icon"></div>
	<header id="head"></header>
	<section id="body"></section>
	<footer id="foot"></footer>
	<nav><slot name="links"></slot></nav>
 <style>:host(*) {
	border: none;
	width: 200px;
	display: flex;
	font-size: 16px;
	flex-basis: 200px;
	text-align: center;
	border-radius: 5px;
	visibility: hidden;
	flex-direction: column;
	background-color: white;
	justify-content: space-between;
}

::slotted(a)
{
	flex-grow: 1;
	display: flex;
	font-size: 16px;
	align-items: center;
	justify-content: center;
}

::slotted(a:first-child)
{
	border-radius: 0 0 0 5px
}

::slotted(a:last-child)
{
	border-radius: 0 0 5px 0
}

::slotted(a:hover)
{
	background-color: rgba(256, 256, 256, 0.2);
	color: #660000
}

div {
	padding: 5px;
	display: flex;
	font-size: 250%;
	font-family: gate;
	align-tems: center;
	justify-content: center
}
header {
	padding: 5px;
	font-size: 150%;
	text-align: center
}
section {
	padding: 5px;
	text-align: inherit
}
footer {
	padding: 5px;
	font-size: 80%;
	text-align: center
}
nav {
	height: 40px;
	display: flex;
	align-items: stretch;
	border-radius: 0 0 5px 5px;
	background-image: var(--g-coolbar-background-image)
}
div:empty {
	display: none
}
header:empty {
	display: none
}
section:empty {
	display: none
}
footer:empty {
	display: none
}
nav:empty {
	display: none
}</style>`;

/* global customElements, template */

customElements.define('g-card', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		Array.from(this.children)
			.filter(e => e.tagName === "A" || e.tagName === "button")
			.forEach(e => e.setAttribute("slot", "links"));

		this.style.visibility = "visible";

	}

	set icon(value)
	{
		this.setAttribute("icon", value);
	}

	get icon()
	{
		return this.getAtribute("icon");
	}

	set head(value)
	{
		this.setAttribute("head", value);
	}

	get head()
	{
		return this.getAtribute("head");
	}

	set body(value)
	{
		this.setAttribute("body", value);
	}

	get body()
	{
		return this.getAtribute("body");
	}

	set foot(value)
	{
		this.setAttribute("foot", value);
	}

	get foot()
	{
		return this.getAtribute("foot");
	}

	attributeChangedCallback(name, ignore, value)
	{
		switch (name)
		{
			case "head":
			case "foot":
			case "body":
				this.shadowRoot.getElementById(name).innerText = value;
				break;
			case "icon":
				this.shadowRoot.getElementById(name).innerHTML = "&#X" + value + ";";
				break;

		}
	}

	connectedCallback()
	{
		this.classList.add(".g-card");
	}

	static get observedAttributes()
	{
		return ["head", "body", "foot", "icon"];
	}
});