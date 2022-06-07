let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host(*)
{
	margin: 0;
	padding: 0;
	color: black;
	width: 280px;
	display: none;
	z-index: 1000;
	cursor: pointer;
	position: fixed;
	font-size: 16px;
	visibility: hidden;
	align-items: stretch;
	flex-direction: column;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	background-color: var(--g-context-menu-background-color);
}

::slotted(:is(a, button))
{
	color: inherit;
	font-size: inherit;
	padding: 8px !important;
	display: flex !important;
	flex-basis: 24px !important;
	align-items: center !important;
	text-decoration: none !important;
	justify-content: space-between !important;
	border: 1px solid var(--g-context-menu-border-color) !important;
	background-color: var(--g-context-menu-background-color) !important;
}

::slotted(:is(a, button):hover)
{
	background-color: var(--g-context-menu-hovered-background-color);
}</style>`;

/* global customElements, template */

export default class GContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this._private = {};
		this.addEventListener("click", () => this.hide());

		this._private.mousedown = event => !this.contains(event.target) && this.hide();

		this._private.contextmenu = event =>
		{
			event = event || window.event;
			let action = event.target || event.srcElement;

			if (this.id)
				action = action.closest("[data-context-menu=" + this.id + "]");
			else if (this.parentNode.contains(action))
				action = this.parentNode;
			else
				action = null;

			if (action)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
				this.show(action, event.target, event.clientX, event.clientY);
			}
		};
	}

	show(context, target, x, y)
	{
		this._private.target = target;
		this._private.context = context;
		this.style.display = "flex";

		if (x + this.clientWidth > window.innerWidth)
			x = x >= this.clientWidth
				? x - this.clientWidth
				: x = window.innerWidth / 2 - this.clientWidth / 2;

		if (y + this.clientHeight > window.innerHeight)
			y = y >= this.clientHeight
				? y - this.clientHeight
				: y = window.innerHeight / 2 - this.clientHeight / 2;


		this.style.top = y + "px";
		this.style.left = x + "px";
		this.style.visibility = "visible";
	}

	hide()
	{
		this._private.target = null;
		this._private.context = null;
		this.style.display = "none";
		this.style.visibility = "hidden";
	}

	connectedCallback()
	{
		this.classList.add('g-context-menu');
		window.addEventListener("mousedown", this._private.mousedown);
		window.addEventListener("contextmenu", this._private.contextmenu);
	}

	disconnectedCallback()
	{
		window.removeEventListener("mousedown", this._private.mousedown);
		window.addEventListener("contextmenu", this._private.contextmenu);
	}

	get context()
	{
		return this._private.context;
	}

	get target()
	{
		return this._private.target;
	}
}

customElements.define('g-context-menu', GContextMenu);