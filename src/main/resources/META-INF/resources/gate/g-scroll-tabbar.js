let template = document.createElement("template");
template.innerHTML = `
	<div>
	</div>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	color: black;
	border: none;
	height: auto;
	flex-grow: 1;
	display: flex;
	background-color: var(--main3);
	justify-content: flex-start;
}

:host([data-overflowing=left])::before,
:host([data-overflowing=left])::after,
:host([data-overflowing=right])::before,
:host([data-overflowing=right])::after,
:host([data-overflowing=both])::before,
:host([data-overflowing=both])::after
{
	display: flex;
	content: " ";
	color: #999999;
	font-size: 16px;
	flex-basis: 32px;
	font-family: "gate";
	align-items: center;
	justify-content: center;
}

:host([data-overflowing=left])::before,
:host([data-overflowing=both])::before
{
	content: "\\3017";

}

:host([data-overflowing=right])::after,
:host([data-overflowing=both])::after
{
	content: "\\3017";
}

div
{
	gap: 8px;
	width: 100%;
	padding: 8px;
	height: auto;
	border: none;
	flex-grow: 1;
	display:  flex;
	overflow: hidden;
	white-space:  nowrap;
}

a,
button,
.g-command
{
	gap: 4px;
	padding: 6px;
	height: auto;
	display: flex;
	color: inherit;
	flex-shrink: 0;
	flex-basis: 120px;
	border-radius: 5px;
	font-size: 0.75rem;
	white-space: nowrap;
	align-items: center;
	text-decoration: none;
	flex-direction: column;
	background-color: var(--main4);
	justify-content: space-around;
}

:host(.inline) a,
:host(.inline) button,
:host(.inline) .g-command
{
	flex-basis: 160px;
	flex-direction: row;
	justify-content: flex-start;
}

a[aria-selected],
button[aria-selected],
.g-command[aria-selected]
{
	background-color: #E6E6E6;
}

a:hover,
button:hover,
.g-command:hover
{
	background-color:  #FFFACD;
}

a:focus,
button:focus,
.g-command:focus
{
	outline: none
}

*[hidden="true"]
{
	display: none;
}

hr
{
	border: none;
	flex-grow: 100000;
}

i, g-icon {
	order: -1;
	display: flex;
	color: inherit;
	cursor: inherit;
	font-style: normal;
	font-size: 1.25rem;
	font-family: 'gate';
	align-items: center;
	justify-content: center;
}
</style>`;

/* global customElements */

customElements.define("g-scroll-tabbar", class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let div = this.shadowRoot.firstElementChild;
		this.addEventListener("mouseenter", () => div.style.overflowX = "auto");
		this.addEventListener("mouseleave", () => div.style.overflowX = "hidden");
		this.addEventListener("touchstart", () => div.style.overflowX = "auto");
		this.addEventListener("touchend", () => div.style.overflowX = "hidden");
		this.addEventListener("touchmove", e => div.style.overflowX = this.contains(e.target) ? "auto" : "hidden");

		div.addEventListener("scroll", () => this.update());
	}

	connectedCallback()
	{
		let div = this.shadowRoot.querySelector("div");
		Array.from(this.children).forEach(e => div.appendChild(e));
		window.setTimeout(() => this.update(), 0);
		window.addEventListener("resize", () => this.update());
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

			let left = Math.floor(div.firstElementChild.getBoundingClientRect().left);
			let right = Math.floor(div.lastElementChild.getBoundingClientRect().right);

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

		Array.from(div.children).filter(e => e.getAttribute("aria-selected"))
			.forEach(e => e.scrollIntoView({inline: "center", block: "nearest"}));
	}

	disconnectedCallback()
	{
		window.removeEventListener("resize", () => this.update());
	}
});