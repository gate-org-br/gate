let template = document.createElement("template");
template.innerHTML = `
	<div>
		<slot>
		</slot>
	</div>
 <style>:host(*)
{
	display: flex;
	overflow: hidden;
	color: var(--g-tabbar-color);
	background-color:  var(--g-tabbar-background-color);
	background-image:  var(--g-tabbar-background-image);
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
	width: 100%;
	height: auto;
	border: none;
	display:  flex;
	overflow-x : hidden;
	white-space:  nowrap;
}</style>`;

/* global customElements */

const determineOverflow = function (component, container)
{
	container = container || component;

	if (!component.firstElementChild)
		return "none";

	let containerMetrics = container.getBoundingClientRect();
	let containerMetricsRight = Math.floor(containerMetrics.right);
	let containerMetricsLeft = Math.floor(containerMetrics.left);

	let left = Math.floor(component.firstElementChild.getBoundingClientRect().left);
	let right = Math.floor(component.lastElementChild.getBoundingClientRect().right);

	if (containerMetricsLeft > left
		&& containerMetricsRight < right)
		return "both";
	else if (left < containerMetricsLeft)
		return "left";
	else if (right > containerMetricsRight)
		return "right";
	else
		return "none";
};

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
		div.appendChild(document.createElement("slot"));

		div.addEventListener("scroll", () => this.setAttribute("data-overflowing",
				determineOverflow(this, this.shadowRoot.firstElementChild)));
	}

	update()
	{
		this.setAttribute("data-overflowing", determineOverflow(this, this.shadowRoot.firstElementChild));
		Array.from(this.children).filter(e => e.getAttribute("aria-selected"))
			.forEach(e => e.scrollIntoView({inline: "center", block: "nearest"}));
	}

	connectedCallback()
	{
		window.setTimeout(() => this.update(), 0);
		window.addEventListener("resize", () => this.update());
	}

	disconnectedCallback()
	{
		window.removeEventListener("resize", () => this.update());
	}
});