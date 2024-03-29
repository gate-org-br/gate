let template = document.createElement("template");
template.innerHTML = `
	<header>
		<slot name="head">
		</slot>
	</header>
	<section>
		<slot name="body">
		</slot>
	</section>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	display: grid;
	background-color: var(--main3);
	border: 1px outset var(--main4);
	grid-template-rows: auto 1fr;
}

:host(:first-child)
{
	margin-top: 0
}

:host(:last-child)
{
	margin-bottom: 0
}

header
{
	gap: 1px;
	display: grid;
	grid-template-columns: repeat(2, 1fr);
}

::slotted(div)
{
	gap: 12px;
	display: none;
	padding : 18px;
	overflow: hidden;
	align-items: stretch;
	flex-direction: column;
	background-color: white;
}

:host([type='dummy']) ::slotted(div)
{
	display: flex;
}

::slotted(a),
::slotted(button)
{
	gap: 8px;
	width: 100%;
	height: 32px;
	border: none;
	padding: 5px;
	color: black;
	display: flex;
	cursor: pointer;
	flex-basis: 50%;
	font-size: 0.75rem;
	align-items: center;
	text-decoration: none;
	background-color: #E8E8E8;
	justify-content: flex-start;
}

::slotted(a:hover),
::slotted(button:hover)
{
	background-color:  #FFFACD;
}

::slotted(a:focus),
::slotted(button:focus)
{
	border: none;
	outline: none;
}

::slotted(a[data-selected=true]),
::slotted(button[data-selected=true])
{
	color: black;
	font-weight: bold;
	background-color: white;
}

@media only screen and (min-width: 768px)
{
	header
	{
		grid-template-columns: repeat(4, 1fr);
	}

	:host([size='9']) > header,
	:host([size='10']) > header,
	:host([size='20']) > header,
	:host([size='30']) > header,
	:host([size='40']) > header,
	:host([size='50']) > header,
	:host([size='60']) > header,
	:host([size='70']) > header,
	:host([size='80']) > header,
	:host([size='90']) > header,
	:host([size='100']) > header
	{
		grid-template-columns: repeat(5, 1fr);
	}
}

@media only screen and (min-width: 1200px)
{
	header
	{
		grid-template-columns: repeat(8, 1fr);
	}

	:host([size='9']) > header,
	:host([size='10']) > header,
	:host([size='20']) > header,
	:host([size='30']) > header,
	:host([size='40']) > header,
	:host([size='50']) > header,
	:host([size='60']) > header,
	:host([size='70']) > header,
	:host([size='80']) > header,
	:host([size='90']) > header,
	:host([size='100']) > header
	{
		grid-template-columns: repeat(10, 1fr);
	}
}</style>`;

/* global customElements */

customElements.define('g-tab-control', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get type()
	{
		return this.getAttribute("type") || "frame";
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	connectedCallback()
	{
		if (this.type !== "dummy")
		{
			var links = Array.from(this.children).filter(e => e.tagName === "A"
					|| e.tagName === "BUTTON");

			this.setAttribute("size", links.length);

			links.filter(e => !e.nextElementSibling || e.nextElementSibling.tagName !== "DIV")
				.forEach(e => this.insertBefore(document.createElement("div"), e.nextElementSibling));

			var pages = Array.from(this.children).filter(e => e.tagName === "DIV");
			pages.forEach(e => e.setAttribute("slot", "body"));

			links.forEach(link =>
			{
				links.forEach(e => e.setAttribute("slot", "head"));
				let type = link.getAttribute("data-type") || this.type;
				let reload = link.getAttribute("data-reload") || this.getAttribute("reload");

				link.addEventListener("click", event =>
				{
					pages.forEach(e => e.style.display = "none");
					links.forEach(e => e.setAttribute("data-selected", "false"));
					link.nextElementSibling.style.display = "flex";
					link.setAttribute("data-selected", "true");

					if (reload === "always")
						while (link.nextElementSibling.firstChild)
							link.nextElementSibling.removeChild(link.nextElementSibling.firstChild);

					if (!link.nextElementSibling.childNodes.length)
					{
						switch (type)
						{
							case "fetch":
								new URL(link.getAttribute('href')
									|| link.getAttribute('formaction'))
									.get(text => link.nextElementSibling.innerHTML = text);
								break;
							case "frame":
								let iframe = document.createElement("iframe");
								iframe.style.margin = "0";
								iframe.style.width = "100%";
								iframe.style.border = "none";
								iframe.style.overflow = "hidden";
								iframe.style.backgroundPosition = "center";
								iframe.style.backgroundRepeat = "no-repeat";
								iframe.style.backgroundPositionY = "center";
								iframe.style.backgroundImage = "var(--loading)";
								iframe.scrolling = "no";
								iframe.setAttribute("allowfullscreen", "true");
								let name = Math.random().toString(36).substr(2);
								iframe.setAttribute("id", name);
								iframe.setAttribute("name", name);
								link.nextElementSibling.appendChild(iframe);

								iframe.addEventListener("load", () =>
								{
									var height = iframe.contentDocument.body.scrollHeight + "px";
									if (iframe.style.height !== height)
									{
										iframe.style.height = height;
										if (iframe.contentDocument.body.firstElementChild)
											iframe.contentDocument.body.firstElementChild
												.scrollIntoView(true);
									}
									iframe.style.backgroundImage = "none";
								});

								if (link.tagName === "A")
									link.setAttribute("target", name);
								else
									link.setAttribute("formtarget", name);

								return;
						}
					}

					event.preventDefault();
					event.stopPropagation();
				});

				if (link.getAttribute("data-selected") &&
					link.getAttribute("data-selected").toLowerCase() === "true")
					link.click();
			});



			if (links.length && links.every(e => !e.hasAttribute("data-selected")
					|| e.getAttribute("data-selected").toLowerCase() === "false"))
				links[0].click();
		} else
		{
			var links = Array.from(this.children).filter(e => e.tagName === "A" || e.tagName === "BUTTON");
			this.setAttribute("size", links.length);
			links.forEach(e => e.setAttribute("slot", "head"));
			Array.from(this.children).filter(e => e.tagName === "DIV").forEach(e => e.setAttribute("slot", "body"));
		}
	}
});