let template = document.createElement("template");
template.innerHTML = `
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
	background-color: var(--main-tinted50);
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
}

:host(*) > a,
:host(*) > button
{
	padding: 8px;
	display: flex;
	color: inherit;
	flex-basis: 24px;
	font-size: inherit;
	align-items: center;
	text-decoration: none;
	justify-content: flex-start;
	border: 1px solid var(--base);
	background-color: var(--main-tinted50);
}

:host(*) > a:hover,
:host(*) > button:hover
{
	background-color: var(--acent-tinted35);
}

:host(*) > a > i,
:host(*) > button > i
{
	order: -1;
	speak: none;
	line-height: 1;
	margin-right: 8px;
	font-style: normal;
	font-weight: normal;
	font-family: 'gate';
	font-variant: normal;
	text-transform: none;
	-moz-osx-font-smoothing: grayscale;
	-webkit-font-smoothing: antialiased;
}</style>`;

/* global NodeList, customElements, template */

export default class GContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this._private = {};
		this.addEventListener("click", () => this.hide());
		this._private.mousedown = e => !this.contains(e.target) && this.hide();
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
		Array.from(this.children).forEach(e => this.shadowRoot.appendChild(e));
	}

	disconnectedCallback()
	{
		window.removeEventListener("mousedown", this._private.mousedown);
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

window.addEventListener("contextmenu", event =>
	{
		event = event || window.event;
		let action = event.target || event.srcElement;

		action = action.closest("[data-context-menu]");
		if (action)
		{
			document.getElementById(action.getAttribute("data-context-menu"))
				.show(action, event.target, event.clientX, event.clientY);
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	}, true);