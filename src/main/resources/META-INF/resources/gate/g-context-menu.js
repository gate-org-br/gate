let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-context-menu">* {
	cursor: pointer;
}

:host(*)
{
	margin: 0;
	padding: 0;
	color: black;
	width: auto;
	display: flex;
	z-index: 1000;
	min-width: 200px;
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

import './mutation-events.js';
import resolve from './resolve.js';

function isVisible(element)
{
	const rect = element.getBoundingClientRect();
	return rect.top >= 0 &&
		rect.left >= 0 &&
		rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
		rect.right <= (window.innerWidth || document.documentElement.clientWidth);
}

let instance;

export default class GContextMenu extends HTMLElement
{
	#context;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("mouseleave", () => this.hide());
	}

	set context(context)
	{
		this.#context = context;
	}

	get context()
	{
		return this.#context;
	}

	set actions(actions)
	{
		actions.forEach(({icon, text, color, method, action, target, title, visible}) =>
		{
			if (visible && !visible(this.context))
				return;

			let link = this.shadowRoot.appendChild(document.createElement("a"));
			link.style.color = color;
			link.title = title || null;
			link.appendChild(document.createElement("label")).innerText = text;
			link.setAttribute("data-icon", String.fromCharCode(icon ? parseInt(icon, 16) : 0x1024));
			if (typeof action === 'string')
			{
				link.target = target || "_self";
				link.href = resolve(link, this.context, action);
				if (method)
					link.setAttribute("data-method", method);
				link.addEventListener("click", () => setTimeout(() => this.hide(), 0));
			} else if (typeof action === 'function')
			{
				link.addEventListener("click", event =>
				{
					event.preventDefault();
					event.stopPropagation();
					action(this.#context);
					this.root().hide();
				});
			} else if (Array.isArray(action))
			{
				link.setAttribute("submenu", "");
				link.addEventListener('mouseenter', () =>
				{
					const submenu = document.createElement('g-context-menu');
					link.addEventListener('mouseleave', () => submenu.hide(), {once: true});
					submenu.actions = action;
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
		if (instance)
			instance.hide();
		instance = this;

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

	root()
	{
		return this.getRootNode().host
			instanceof GContextMenu
			? this.getRootNode().host.root()
			: this;
	}
	hide()
	{
		this.remove();
	}

	static show(context, x, y, ...actions)
	{
		let menu = document.createElement("g-context-menu");
		menu.context = context;
		menu.actions = actions;
		menu.show(x, y);
		return menu;
	}
}

customElements.define('g-context-menu', GContextMenu);

window.addEventListener("click", () =>
	{
		if (instance)
		{
			instance.hide();
			instance = null;
		}
	});

window.addEventListener("contextmenu", function (event)
{
	if (event.ctrlKey)
		return;

	for (let element = event.target;
		element;
		element = element.parentNode || element.host)
	{
		if (element.hasAttribute)
		{
			if (element.hasAttribute("data-contextmenu:source"))
			{
				fetch(element.getAttribute("data-contextmenu:source"))
					.then(response => response.json())
					.then(data => GContextMenu.show(element, event.x, event.y, ...data))
					.catch(error => console.error('Error fetching context menu source:', error));
				event.preventDefault();
				return;
			} else if (element.hasAttribute("data-contextmenu:module"))
			{
				import(element.getAttribute("data-contextmenu:module"))
					.then(module => module.default)
					.then(data => GContextMenu.show(element, event.x, event.y, ...data))
					.catch(error => console.error('Error importing context menu module:', error));
				event.preventDefault();
				return;
			}
		}
	}
});