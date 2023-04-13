let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	gap: 16px;
	width: 100%;
	color: black;
	display: grid;
	background-color: transparent;
	grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
}

a,
button,
.g-command,
g-desk-pane
{
	gap: 10px;
	margin: 0;
	padding: 16px;
	height: 200px;
	color: inherit;
	font-size: 18px;
	position: relative;
	text-align: center;
	border-radius: 3px;
	font-style: normal;
	text-decoration: none;
	border: 1px solid #F0F0F0;

	display: grid;
	align-items: center;
	justify-items: center;
	justify-content: center;
	grid-template-rows: 1fr 1fr;

}

:host(*) > g-desk-pane::after
{
	right: 8px;
	bottom: 4px;
	color: var(--main6);
	font-size: 16px;
	font-family: gate;
	content: '\\3017';
	position: absolute;
}


a:hover,
button:hover,
.g-command:hover,
g-desk-pane:hover
{
	background-color:  #FFFACD;
}

i,
e,
span,
g-icon
{
	order: -1;
	width: 80px;
	height: 80px;
	display: flex;
	font-size: 36px;
	font-family: gate;
	font-style: normal;
	border-radius: 50%;
	align-items: center;
	justify-content: center;
	background-color: #F6F6F6;
}

img
{
	order: -1;
	width: 48px;
	height: 48px;
	margin-right: 8px;
}

:host(*) > g-desk-pane
{
	cursor: pointer;
}

:host(*) > g-desk-pane::part(button)
{
	display: none;
}

:host(.inline)
{
	grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
}


:host(.inline) > *[part]

{
	height: 80px;
	display: flex;
	text-align: left;
	justify-content: flex-start;
}

:host(.inline) i,
:host(.inline) e,
:host(.inline) span,
:host(.inline) g-icon
{
	width: 36px;
	background-color: transparent;
}</style>`;

/* global customElements */

customElements.define('g-desk-pane', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", function (e)
		{
			let target = this.getRootNode().host;
			if (target)
			{
				let backup = target.buttons;
				backup.forEach(e => e.remove());
				this.buttons.forEach(e => target.shadowRoot.appendChild(e));

				let reset = target.shadowRoot.appendChild(document.createElement("a"));
				reset.href = "#";
				reset.innerText = "Retornar";
				reset.style.color = "#660000";
				reset.setAttribute("part", "button");
				reset.appendChild(document.createElement("i")).innerHTML = "&#X2023";

				reset.addEventListener("click", () =>
				{
					reset.remove();
					target.buttons.forEach(e => this.shadowRoot.appendChild(e));
					backup.forEach(e => target.shadowRoot.appendChild(e));
					e.preventDefault();
					e.stopPropagation();
				});

				e.preventDefault();
				e.stopPropagation();
			}

		}, true);
	}

	get buttons()
	{
		return Array.from(this.shadowRoot.querySelectorAll("a, button, g-command, g-desk-pane"));
	}

	connectedCallback()
	{
		Array.from(this.childNodes)
			.forEach(e => this.shadowRoot.appendChild(e));
		if (this.getRootNode().host)
			this.classList = this.getRootNode().host.classList;
		this.buttons.forEach(e => e.setAttribute("part", "button"));
	}
});