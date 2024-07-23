let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-context-menu">:host(*)
{
	margin: 0;
	padding: 0;
	color: black;
	width: auto;
	min-width: 120px;
	display: flex;
	z-index: 1000;
	cursor: pointer;
	position: fixed;
	font-size: 12px;
	align-items: stretch;
	flex-direction: column;
	background-color: #F0F0F0;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
}

a, button
{
	gap: 8px;
	padding: 8px;
	display: flex;
	color: inherit;
	flex-basis: 16px;
	font-size: inherit;
	align-items: center;
	white-space: nowrap;
	text-decoration: none;
	border: 1px solid #E0E0E0;
	justify-content: space-between;
}

a:hover,
button:hover
{
	background-color: var(--hovered, #FFFACD);
}

label {
	flex-grow: 1;
}

a[data-icon]::before,
button[data-icon]::before
{
	font-family: gate;
	content: attr(data-icon);
}

a[submenu]::after,
button[submenu]::after
{
	font-size: 10px;
	color: #CCCCCC;
	font-family: gate;
	content: '\\2207';

}</style>`;
/* global customElements, template */

function isVisible(element)
{
	const rect = element.getBoundingClientRect();
	return rect.top >= 0 &&
		rect.left >= 0 &&
		rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
		rect.right <= (window.innerWidth || document.documentElement.clientWidth);
}

export default class GContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", () => this.hide());
		this.addEventListener("mouseleave", () => this.hide());
	}

	set actions(actions)
	{
		actions.forEach(action =>
		{
			let link = this.shadowRoot.appendChild(document.createElement("a"));

			link.setAttribute("data-icon", action.icon
				? String.fromCharCode(parseInt(action.icon, 16))
				: String.fromCharCode(0x1024));
			link.appendChild(document.createElement("label")).innerText = action.text;
			if (typeof action.action === 'string')
				link.href = action.action;
			else if (typeof action.action === 'function')
				link.addEventListener("click", event =>
				{
					event.preventDefault();
					event.stopPropagation();
					action.action();
				});
			else if (Array.isArray(action.action))
			{
				link.setAttribute("submenu", "");
				link.addEventListener('mouseenter', () =>
				{
					const submenu = document.createElement('g-context-menu');
					link.addEventListener('mouseleave', () => submenu.hide(), {once: true});
					submenu.actions = action.action;
					link.appendChild(submenu);

					const rect = link.getBoundingClientRect();
					submenu.style.top = `${rect.top}px`;
					submenu.style.left = `${rect.right}px`;
					if (!isVisible(submenu))
					{
						submenu.style.left = `${rect.left - submenu.clientWidth}px`;
						if (!isVisible(submenu))
						{
							submenu.style.top = `${rect.bottom - submenu.clientHeight}px`;
							submenu.style.left = `${rect.right}px`;
							if (!isVisible(submenu))
								submenu.style.left = `${rect.left - submenu.clientWidth}px`;
						}
					}
				});
			}
		});
	}

	show(x, y)
	{
		document.body.appendChild(this);

		this.style.top = `${y - 1}px`;
		this.style.left = `${x - 1}px`;
		if (!isVisible(this))
		{
			this.style.top = `${y + 1 - this.clientHeight}px`;
			this.style.left = `${x - 1}px`;
			if (!isVisible(this))
			{
				this.style.left = `${x + 1 - this.clientWidth}px`;
				this.style.top = `${y - 1}px`;
				if (!isVisible(this))
				{
					this.style.left = `${x + 1 - this.clientWidth}px`;
					this.style.top = `${y + 1 - this.clientHeight}px`;
				}
			}
		}
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
		return menu;
	}
}

customElements.define('g-context-menu', GContextMenu);