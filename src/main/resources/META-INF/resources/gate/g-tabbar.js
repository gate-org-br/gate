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
	grid-template-columns: 1fr;
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

:host([data-overflowing=left])
{
	grid-template-columns: 24px 1fr;
}

:host([data-overflowing=right])
{
	grid-template-columns: 1fr 24px;
}

:host([data-overflowing=both])
{
	grid-template-columns: 24px 1fr 24px;
}

:host([data-overflowing=left]) #prev,
:host([data-overflowing=both]) #prev
{
	display: flex;
}

:host([data-overflowing=right]) #next,
:host([data-overflowing=both]) #next
{
	display: flex;
}

div
{
	gap: 8px;
	padding: 8px;
	height: auto;
	border: none;
	flex-grow: 1;
	display: flex;
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

customElements.define("g-tabbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let div = this.shadowRoot.querySelector("div");
		this.addEventListener("mouseenter", () => div.style.overflowX = "auto");
		this.addEventListener("mouseleave", () => div.style.overflowX = "hidden");
		this.addEventListener("touchstart", () => div.style.overflowX = "auto");
		this.addEventListener("touchend", () => div.style.overflowX = "hidden");
		this.addEventListener("touchmove", e => div.style.overflowX = this.contains(e.target) ? "auto" : "hidden");

		this.shadowRoot.querySelector("#next").addEventListener("click", () =>
		{
			for (let element = this.firstElementChild; element; element = element.nextElementSibling)
				if (visible(element, div))
					for (element = element.nextElementSibling; element; element = element.nextElementSibling)
						if (!visible(element, div))
							return element.scrollIntoView({inline: "start",
								behavior: "smooth"});
		}
		);

		this.shadowRoot.querySelector("#prev").addEventListener("click", () =>
		{
			for (let element = this.lastElementChild; element; element = element.previousElementSibling)
				if (visible(element, div))
					for (element = element.previousElementSibling; element; element = element.previousElementSibling)
						if (!visible(element, div))
							return element.scrollIntoView({inline: "end",
								behavior: "smooth"});

		});

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
		this.setAttribute("data-overflowing", "none");

		if (div.firstElementChild)
		{
			let containerMetrics = div.getBoundingClientRect();
			let containerMetricsRight = Math.floor(containerMetrics.right);
			let containerMetricsLeft = Math.floor(containerMetrics.left);

			let left = Math.floor(this.firstElementChild.getBoundingClientRect().left);
			let right = Math.floor(this.lastElementChild.getBoundingClientRect().right);

			if (containerMetricsLeft > left
				&& containerMetricsRight < right)
				this.setAttribute("data-overflowing", "both");
			else if (left < containerMetricsLeft)
				this.setAttribute("data-overflowing", "left");
			else if (right > containerMetricsRight)
				this.setAttribute("data-overflowing", "right");
			else
				this.setAttribute("data-overflowing", "none");
		}

		Array.from(this.children).filter(e => e.getAttribute("aria-selected"))
			.forEach(e => e.scrollIntoView({inline: "center", block: "nearest"}));
	}
});