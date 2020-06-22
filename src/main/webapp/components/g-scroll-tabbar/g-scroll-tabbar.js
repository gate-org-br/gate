/* global customElements, GOverflow, GSelection */

class GScrollTabBar extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});

		var div = this.shadowRoot.appendChild(document.createElement("div"));
		div.style.width = "100%";
		div.style.height = "auto";
		div.style.border = "none";
		div.style.display = "flex";
		div.style.overflowX = "hidden";
		div.style.whiteSpace = "nowrap";

		this.addEventListener("mouseenter", () => div.style.overflowX = "auto");
		this.addEventListener("mouseleave", () => div.style.overflowX = "hidden");
		this.addEventListener("touchstart", () => div.style.overflowX = "auto");
		this.addEventListener("touchend", () => div.style.overflowX = "hidden");
		this.addEventListener("touchmove", e => div.style.overflowX = this.contains(e.target) ? "auto" : "hidden");
		div.appendChild(document.createElement("slot"));

		div.addEventListener("scroll", () => this.setAttribute("data-overflowing",
				GOverflow.determineOverflow(this, this.shadowRoot.firstElementChild)));

		this._private =
			{
				update: () =>
				{
					this.setAttribute("data-overflowing",
						GOverflow.determineOverflow(this, this.shadowRoot.firstElementChild));
					Array.from(this.children).filter(e => e.getAttribute("aria-selected"))
						.forEach(e => e.scrollIntoView({inline: "center", block: "nearest"}));
				}
			};
	}

	connectedCallback()
	{
		window.setTimeout(this._private.update, 0);
		window.addEventListener("resize", this._private.update);
	}

	disconnectedCallback()
	{
		window.removeEventListener("resize", this._private.update);
	}
}

customElements.define("g-scroll-tabbar", GScrollTabBar);