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
	flex-basis: 32px;
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
}

:host([vertical])
{
	grid-template-rows: 1fr;
	grid-template-columns: auto 1fr;
}

:host([vertical]) > header
{
	gap: 0;
	width: 28px;
	display: flex;
	overflow: hidden;
	flex-direction: column;
	justify-content: flex-start;
}

:host([vertical]) > header:hover,
:host([vertical]) > header[active]
{
	width: fit-content;
}

:host([vertical]) > header::before
{
	padding: 8px;
	font-family: gate;
	content: '\\2265';
	font-size: 0.75rem;
	background-color: var(--main4);
}

:host([vertical]) > header > ::slotted(*)
{
	padding: 8px;
	background-color: var(--main3);
}


@media only screen and (min-width: 1024px)
{
	:host([vertical]) > header
	{
		width: auto;
	}

	:host([vertical]) > header[active]
	{
		width: 28px;
	}
}</style>`;

/* global customElements */

import GBlock from './g-block.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';


function resize(iframe)
{
	let iframeDocument = iframe.contentDocument || iframe.contentWindow.document;
	var contentWidth = iframeDocument.body.scrollWidth;
	var contentHeight = iframeDocument.body.scrollHeight;
	iframe.style.width = contentWidth + 'px';
	iframe.style.height = contentHeight + 'px';
}

customElements.define('g-tab-control', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let header = this.shadowRoot.querySelector("header");

		header.addEventListener("click", event =>
		{
			if (event.target === header)
				if (!header.hasAttribute("active"))
					header.setAttribute("active", "");
				else
					header.removeAttribute("active");
		});
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

					if (link.nextElementSibling.childNodes.length)
					{
						event.preventDefault();
						event.stopPropagation();
					} else if (type === "fetch")
					{
						event.preventDefault();
						event.stopPropagation();
						let method = link.getAttribute('method')
							|| (link.form || {}).method
							|| "get";
						let action = link.getAttribute('href')
							|| link.getAttribute('formaction')
							|| (link.form || {}).action;
						fetch(RequestBuilder.build(method, action, link.form))
							.then(ResponseHandler.text)
							.then(e => link.nextElementSibling.innerHTML = e);
					} else if (type === "frame")
					{
						let iframe = document.createElement("iframe");
						iframe.scrolling = "no";
						iframe.setAttribute("allowfullscreen", "true");
						iframe.style.margin = "0";
						iframe.style.width = "100%";
						iframe.style.border = "none";
						iframe.style.overflow = "hidden";
						iframe.style.height = "400px";
						iframe.addEventListener("load", () =>
						{
							resize(iframe);
							let observer = new MutationObserver(() => resize(iframe));
							observer.observe(iframe.contentDocument || iframe.contentWindow.document,
								{attributes: true, childList: true, subtree: true});
						});
						link.nextElementSibling.appendChild(iframe);
						let name = Math.random().toString(36).substr(2);
						iframe.setAttribute("id", name);
						iframe.setAttribute("name", name);
						if (link.tagName === "A")
							link.setAttribute("target", name);
						else if (link.tagName === "BUTTON")
							link.setAttribute("formtarget", name);
						link.nextElementSibling.appendChild(iframe);
					}
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