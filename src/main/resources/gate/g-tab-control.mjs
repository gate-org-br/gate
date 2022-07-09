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
 <style>:host(*)
{
	display: grid;
	margin-top: 16px;
	border-radius: 8px 8px 0 0;
	grid-template-rows: auto 1fr;
	background-color: var(--g-tab-control-background-color);
	border: 4px solid var(--g-tab-control-background-color);
}

:host(:first-child)
{
	margin-top: 0
}

header
{
	display: flex;
	align-items: center;
	justify-content: flex-start;
	flex-wrap: wrap
}

::slotted(div)
{
	display: none;
	padding : 10px;
	overflow: hidden;
	background-color: var(--g-tab-control-tab-background-color);
	background-image: var(--g-tab-control-tab-background-image);
}

:host([type='dummy']) ::slotted(div)
{
	display: block;
}

::slotted(:is(a, button))
{
	width: 50%;
	border: none;
	padding: 5px;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	flex-basis: 50%;
	align-items: center;
	text-decoration: none;
	border-radius: 8px 8px 0 0;
	justify-content: space-between;
	color: var(--g-tab-control-button-color);
	background-color: var(--g-tab-control-button-background-color);
	background-image: var(--g-tab-control-button-background-image);
}

::slotted(:is(a, button):hover)
{
	color: var(--g-tab-control-hovered-button-color);
}

::slotted(:is(a, button):focus)
{
	border: none;
	outline: none;
}

::slotted(:is(a, button)[data-selected=true])
{
	font-weight: bold;
	background-color: var(--g-tab-control-tab-background-color);
	background-image: var(--g-tab-control-tab-background-image);
}

@media only screen and (min-width: 768px)
{
	::slotted(a),
	::slotted(button)
	{
		width: 25%;
		flex-basis: 25%;
	}

	:host([size='9']) ::slotted(:is(a, button)),
	:host([size='10']) ::slotted(:is(a, button)),
	:host([size='20']) ::slotted(:is(a, button)),
	:host([size='30']) ::slotted(:is(a, button)),
	:host([size='40']) ::slotted(:is(a, button)),
	:host([size='50']) ::slotted(:is(a, button)),
	:host([size='60']) ::slotted(:is(a, button)),
	:host([size='70']) ::slotted(:is(a, button)),
	:host([size='80']) ::slotted(:is(a, button)),
	:host([size='90']) ::slotted(:is(a, button)),
	:host([size='100']) ::slotted(:is(a, button))
	{
		width: 20%;
		flex-basis: 20%;
	}
}

@media only screen and (min-width: 1200px)
{
	::slotted(a),
	::slotted(button)
	{
		width: 12.5%;
		flex-basis: 12.5%;
	}

	:host([size='9']) ::slotted(:is(a, button)),
	:host([size='10']) ::slotted(:is(a, button)),
	:host([size='20']) ::slotted(:is(a, button)),
	:host([size='30']) ::slotted(:is(a, button)),
	:host([size='40']) ::slotted(:is(a, button)),
	:host([size='50']) ::slotted(:is(a, button)),
	:host([size='60']) ::slotted(:is(a, button)),
	:host([size='70']) ::slotted(:is(a, button)),
	:host([size='80']) ::slotted(:is(a, button)),
	:host([size='90']) ::slotted(:is(a, button)),
	:host([size='100']) ::slotted(:is(a, button))
	{
		width: 10%;
		flex-basis: 10%;
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
					link.nextElementSibling.style.display = "block";
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