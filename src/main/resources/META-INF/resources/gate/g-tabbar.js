let template = document.createElement("template");
template.innerHTML = `
	<button id="prev"><g-icon>&#X2277;</g-icon></button>
	<div>
		<slot></slot>
	</div>
	<button id="next"><g-icon>&#X2279;</g-icon></button>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	width: 100%;
	color: black;
	border: none;
	height: auto;
	flex-grow: 1;
	display: grid;
	align-items: stretch;
	justify-content: stretch;
	grid-template-columns: 24px 1fr 24px;
	background-color: var(--main3);
}

#next, #prev
{
	border: none;
	color: #999999;
	font-size: 16px;
	min-width: 24px;
	flex-basis: 24px;
	align-items: center;
	justify-content: center;
}

div
{
	gap: 8px;
	padding: 8px;
	height: auto;
	border: none;
	flex-grow: 1;
	display: flex;
	grid-column: 2;
	overflow-x: hidden;
	white-space:  nowrap;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	gap: 4px;
	padding: 6px;
	height: auto;
	display: flex;
	color: inherit;
	flex-shrink: 0;
	cursor: pointer;
	flex-basis: 120px;
	border-radius: 5px;
	font-size: inherit;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	justify-content: space-around;
	background-color: var(--main4);
}

:host(.inline) ::slotted(a),
:host(.inline) ::slotted(button),
:host(.inline) ::slotted(.g-command)
{
	flex-basis: 160px;
	flex-direction: row;
	justify-content: flex-start;
}

::slotted(a[aria-selected]),
::slotted(button[aria-selected]),
::slotted(.g-command[aria-selected])
{
	color: var(--base1);
	background-color: var(--main5);
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color:  var(--hovered);
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: none
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
button {
	color: black;
	display: none;
}

g-icon {
	color: #000099;
}

button {
	cursor: pointer;

}

button:hover
{
	background-color:  var(--hovered);
}</style>`;

/* global customElements */

function visible(element, container)
{
	element = element.getBoundingClientRect();
	container = container.getBoundingClientRect();
	return 	element.top >= container.top &&
		element.left >= container.left &&
		element.bottom <= container.bottom &&
		element.right <= container.right;
}

function scroll(tabbar, first, next, inline)
{
	let div = tabbar.shadowRoot.querySelector("div");
	for (let element = first; element; element = next(element))
		if (element.tagName === "A"
			|| element.tagName === "BUTTON"
			|| element.classList.contains(".g-command"))
			if (visible(element, div))
				for (element = next(element); element; element = next(element))
					if (!visible(element, div))
						return element.scrollIntoView({inline, behavior: "smooth"});
}

customElements.define("g-tabbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let div = this.shadowRoot.querySelector("div");
		this.shadowRoot.querySelector("#next").addEventListener("click", () => scroll(this, this.firstElementChild, e => e.nextElementSibling, "start"));
		this.shadowRoot.querySelector("#prev").addEventListener("click", () => scroll(this, this.lastElementChild, e => e.previousElementSibling, "end"));

		div.addEventListener("scroll", () => this.update());
		new ResizeObserver(() => this.update()).observe(this);
	}

	connectedCallback()
	{
		this.update();
	}

	update()
	{
		let div = this.shadowRoot.querySelector("div");
		let next = this.shadowRoot.querySelector("#next");
		let prev = this.shadowRoot.querySelector("#prev");
		prev.style.display = visible(this.firstElementChild, div) ? "none" : "flex";
		next.style.display = visible(this.lastElementChild, div) ? "none" : "flex";
	}
});