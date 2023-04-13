let template = document.createElement("template");
template.innerHTML = `
	<header>
	</header>
	<section>
		<slot>
		</slot>
	</section>
 <style>:host(*)
{
	display: flex;
	flex-direction: column;
	background-color: var(--main1);
	border: 1px solid var(--main3);
}

header
{
	padding: 8px;
	display: flex;
	cursor: pointer;
	font-weight: bold;
	background-color: var(--main2);
	justify-content: space-between;
}

header:hover
{
	background-color: var(--hovered);
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

:host([expanded]) header
{
	background-color: var(--main4);
}

:host([expanded]) header::after
{
	content: '\\2278';
}

section {
	padding: 8px;
	display: none;
	align-items: stretch;
	flex-direction: column;
}

:host([expanded]) section
{
	display: flex;
}</style>`;

/* global customElements */

customElements.define('g-accordion-section', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.shadowRoot.querySelector("header")
			.addEventListener("click", () =>
			{
				this.expanded = !this.expanded;
				this.dispatchEvent(new CustomEvent(this.expanded ? "expanded" : "collapsed", {bubbles: true}));
			});
	}

	get caption()
	{
		return this.getAttribute("caption");
	}

	set caption(value)
	{
		this.setAttribute("caption", value);
	}

	get expanded()
	{
		return this.hasAttribute("expanded");
	}

	set expanded(value)
	{
		if (value)
			this.setAttribute("expanded", "");
		else
			this.removeAttribute("expanded", "");
	}

	attributeChangedCallback(name, old, val)
	{
		this.shadowRoot.querySelector("header").innerText
			= val;
	}

	static get observedAttributes()
	{
		return ['caption'];
	}
});