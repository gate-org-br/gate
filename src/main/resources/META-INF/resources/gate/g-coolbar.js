let template = document.createElement("template");
template.innerHTML = `
	<button id="prev"><g-icon>&#X2277;</g-icon></button>
	<div>
		<slot></slot>
	</div>
	<button id="next"><g-icon>&#X2279;</g-icon></button>
 <style data-element="g-coolbar">*
{
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	height: auto;
	color: black;
	border: none;
	height: auto;
	display: grid;
	position: relative;
	align-items: stretch;
	justify-content: stretch;
	grid-template-columns: auto 1fr auto;
}

#next,
#prev
{
	width: 24px;
	border: none;
	color: #999999;
	font-size: 12px;
	position: absolute;
	align-items: center;
	justify-content: center;
}

#prev
{
	left: 0;
}

#next
{
	right: 0;
}

div
{
	gap: 8px;
	display: flex;
	grid-column: 2;
	overflow-x: hidden;
	white-space: nowrap;
	flex-direction: row-reverse;
}

::slotted(:is(a, button, .g-command))
{
	gap: 8px;
	width: 120px;
	height: 44px;
	color: black;
	padding: 8px;
	color: black;
	border: none;
	display: flex;
	cursor: pointer;
	font-size: 12px;
	border-radius: 3px;
	align-items: center;
	text-decoration: none;
	min-width: fit-content;
	background-color: #E8E8E8;
	justify-content: space-between;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color: #D0D0D0;
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: 4px solid var(--hovered);
}

::slotted(a.primary),
::slotted(button.primary),
::slotted(.g-command.primary)
{
	color: white;
	border: none;
	background-color: #2A6B9A;
}

::slotted(a.primary:hover),
::slotted(button.primary:hover),
::slotted(.g-command.primary:hover)
{
	background-color: #25608A;
}

::slotted(a.alternative),
::slotted(button.alternative),
::slotted(.g-command.alternative)
{
	color: white;
	border: none;
	background-color: #009E60;
}

::slotted(a.alternative:hover),
::slotted(button.alternative:hover),
::slotted(.g-command.alternative:hover)
{
	background-color: #008E56;
}

::slotted(a.tertiary),
::slotted(button.tertiary),
::slotted(.g-command.tertiary)
{
	background-color: var(--main1);
	border: 1px solid var(--main6);
}

::slotted(a.tertiary:hover),
::slotted(button.tertiary:hover),
::slotted(.g-command.tertiary:hover)
{
	border: 1px solid black;
}

::slotted(a.danger),
::slotted(button.danger),
::slotted(.g-command.danger)
{
	color: white;
	border: none;
	background-color: #AA2222;
}

::slotted(a.danger:hover),
::slotted(button.danger:hover),
::slotted(.g-command.danger:hover)
{
	background-color: #882222;
}

::slotted([hidden="true"])
{
	display: none;
}

::slotted(hr)
{
	border: none;
	flex-grow: 100000;
}

button
{
	color: black;
	display: none;
	height: 44px;
}

g-icon
{
	color: #000099;
}

button
{
	cursor: pointer;

}

button:hover
{
	background-color: var(--hovered);
}

:host([reverse]) div
{
	flex-direction: row;
}

:host([disabled])
{
	background-color: var(--main6);
}

:host([disabled]) div,
:host([disabled]) button
{
	display: none;
}

:host([disabled])::before
{
	content: "";
	height: 44px;
	grid-column: 2;
	animation-fill-mode: both;
	background-color: var(--base1);
	animation: progress 2s infinite ease-in-out;
}

@keyframes progress
{
	0%
	{
		width: 0;
	}

	100%
	{
		width: 100%;
	}
}

::slotted(:is(a, button, .g-command)[data-loading])
{
	position: relative;
	pointer-events: none;
}

::slotted(:is(a, button, .g-command)[data-loading])::before
{
	top: 0px;
	left: 0px;
	right: 0px;
	bottom: 0px;
	padding: 8px;
	display: flex;
	color: black;
	font-size: 12px;
	content: '\\2017';
	font-family: gate;
	position: absolute;
	align-items: center;
	border-radius: inherit;
	background-color: #F0F0F0;
}

::slotted(:is(a, button, .g-command)[data-loading])::after
{
	left: 32px;
	content: "";
	height: 16px;
	position: absolute;
	animation-fill-mode: both;
	background-color: var(--base1);
	animation: loading 2s infinite ease-in-out;
}</style>`;
/* global customElements */

import loading from './loading.js';

const EPSILON = 0.5;

function visible(element, container)
{
	element = element.getBoundingClientRect();
	container = container.getBoundingClientRect();
	return element.top >= container.top - EPSILON &&
		element.left >= container.left - EPSILON &&
		element.bottom <= container.bottom + EPSILON &&
		element.right <= container.right + EPSILON;
}

function scroll(coolbar, first, next, inline)
{
	let div = coolbar.shadowRoot.querySelector("div");
	for (let element = first; element; element = next(element))
		if (element.tagName === "A"
			|| element.tagName === "BUTTON"
			|| element.classList.contains(".g-command"))
			if (visible(element, div))
				for (element = next(element); element; element = next(element))
					if (!visible(element, div))
						return element.scrollIntoView({inline, behavior: "smooth", block: 'nearest'});
}

customElements.define("g-coolbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let div = this.shadowRoot.querySelector("div");
		let next = this.shadowRoot.querySelector("#next");
		let prev = this.shadowRoot.querySelector("#prev");

		this.shadowRoot.querySelector("#next").addEventListener("click", () =>
		{
			if (this.hasAttribute("reverse"))
				scroll(this, this.firstElementChild, e => e.nextElementSibling, "start");
			else
				scroll(this, this.lastElementChild, e => e.previousElementSibling, "start");
		});

		this.shadowRoot.querySelector("#prev").addEventListener("click", () =>
		{
			if (this.hasAttribute("reverse"))
				scroll(this, this.lastElementChild, e => e.previousElementSibling, "end");
			else
				scroll(this, this.firstElementChild, e => e.nextElementSibling, "end");
		});

		div.addEventListener("scroll", () => this.update());
		new ResizeObserver(() => this.update()).observe(this);
	}

	get disabled()
	{
		return this.hasAttribute("disabled");
	}

	set disabled(value)
	{
		if (value)
			this.setAttribute("disabled", "");
		else
			this.removeAttribute("disabled");
	}

	adoptedCallback()
	{
	}

	connectedCallback()
	{
		this.update();
		this.setAttribute("size", this.children.length);
	}

	update()
	{
		loading(this.parentNode);

		let div = this.shadowRoot.querySelector("div");
		let next = this.shadowRoot.querySelector("#next");
		let prev = this.shadowRoot.querySelector("#prev");

		prev.style.display = "none";
		next.style.display = "none";
		if (this.firstElementChild)
			if (this.hasAttribute("reverse"))
			{
				if (!visible(this.firstElementChild, div))
					if (!visible(this.lastElementChild, div))
						prev.style.display = next.style.display = "flex";
					else
						prev.style.display = "flex";
				else if (!visible(this.lastElementChild, div))
					next.style.display = "flex";
			} else
			{
				if (!visible(this.firstElementChild, div))
					if (!visible(this.lastElementChild, div))
						prev.style.display = next.style.display = "flex";
					else
						next.style.display = "flex";
				else if (!visible(this.lastElementChild, div))
					prev.style.display = "flex";
			}
	}
});