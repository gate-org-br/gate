let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-context-menu">:host(*)
{
	margin: 0;
	padding: 0;
	color: black;
	width: 280px;
	display: flex;
	z-index: 1000;
	cursor: pointer;
	position: fixed;
	font-size: 16px;
	align-items: stretch;
	flex-direction: column;
	background-color: #F0F0F0;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
}

a, button
{
	padding: 8px;
	display: flex;
	color: inherit;
	flex-basis: 24px;
	font-size: inherit;
	align-items: center;
	text-decoration: none;
	border: 1px solid #E0E0E0;
	justify-content: space-between;
}

a:hover,
button:hover
{
	background-color: var(--hovered, #FFFACD);
}</style>`;
/* global customElements, template */

function isVisible(element)
{
	let rect = element.getBoundingClientRect();
	return rect.top >= 0
		&& rect.left >= 0
		&& rect.bottom <= window.innerHeight
		&& rect.right <= window.innerWidth;
}

export default class GContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", () => this.hide());
		this.addEventListener("mouseleave", () => this.hide())
	}

	set actions(actions)
	{
		actions.forEach(action =>
		{
			let link = this.shadowRoot.appendChild(document.createElement("a"));
			link.innerText = action.text;
			link.appendChild(document.createElement("g-icon")).innerHTML = `&#X${action.icon || '1024'};`;
			if (typeof action.action === 'string')
				link.href = action.action;
			else
				link.addEventListener("click", () => action.action());
		});
	}

	show(x, y)
	{
		this.style.top = (y - 1) + "px";
		this.style.left = (x - 1) + "px";
		document.documentElement.appendChild(this);

		if (isVisible(this))
			return;

		this.style.top = (y + 1 - this.clientHeight) + "px";
		this.style.left = (x - 1) + "px";

		if (isVisible(this))
			return;

		this.style.left = (x + 1 - this.clientWidth) + "px";
		this.style.top = y - 1 + "px";

		if (isVisible(this))
			return;

		this.style.left = (x + 1 - this.clientWidth) + "px";
		this.style.top = (y + 1 - this.clientHeight) + "px";
	}

	hide()
	{
		this.remove();
	}

	static show(x, y, ...actions)
	{
		let menu = document.createElement("g-context-menu");
		menu.actions = actions;
		menu.show(x, y);
	}
}

customElements.define('g-context-menu', GContextMenu);