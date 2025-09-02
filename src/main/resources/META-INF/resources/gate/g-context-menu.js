let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-context-menu">* {
	cursor: pointer;
	box-sizing: border-box;
}

:host
{
	margin: 0;
	padding: 0;
	width: auto;
	color: black;
	display: flex;
	z-index: 1000;
	overflow: auto;
	font-size: 14px;
	position: fixed;
	max-width: 50vw;
	max-height: 50vh;
	min-width: 220px;
	border-radius: 8px;
	align-items: stretch;
	flex-direction: column;
	background-color: #FFFFFF;
	box-shadow: 3px 10px 15px rgba(0, 0, 0, 0.1);
}

a, button
{
	gap: 12px;
	border: none;
	display: flex;
	color: inherit;
	flex-basis: 16px;
	border-radius: 4px;
	font-size: inherit;
	padding: 10px 16px;
	align-items: center;
	white-space: nowrap;
	text-decoration: none;
	background-color: transparent;
	justify-content: space-between;
	transition: background-color 0.2s ease, padding 0.2s ease;
}

a:hover,
button:hover
{
	background-color: var(--hovered, #FFFACD);
	padding-left: 20px;
}

a:focus,
button:focus
{
	outline: none;
	box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
}

label {
	flex-grow: 1;
	font-weight: 600;
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
	color: #888;
	font-size: 12px;
	content: '\\2207';
	font-family: gate;
	margin-left: 8px;
}

a, button {
	transition: all 0.2s ease;
}

:host {
	padding: 10px;
	border-radius: 10px;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}</style>`;
/* global customElements, template */

import './mutation-events.js';
import anchor from './anchor.js';
import resolve from './resolve.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

const POSITIONS = ["northeast", "southeast", "northwest", "southwest"];

function scheduleClick(link)
{
	link.addEventListener('mouseenter', ({clientX, clientY}) =>
	{
		const clickTimer = setTimeout(() =>
			link.dispatchEvent(new MouseEvent('click', {clientX, clientY})), 400);
		link.addEventListener('mouseleave', () => clearTimeout(clickTimer), {once: true});
	});
}

function createSubmenu(link, event, actions)
{
	const submenu = document.createElement('g-context-menu');
	submenu.actions = actions;
	submenu.style.visibility = "hidden";
	link.appendChild(submenu);
	anchor(submenu, link, 0, ...POSITIONS)
		.then(({location}) =>
		{
			submenu.style.top = `${location.y}px`;
			submenu.style.left = `${location.x}px`;
			link.addEventListener('mouseleave', () => submenu.hide(), {once: true});
			submenu.style.visibility = "";
		})
		.catch(() =>
		{
			document.querySelectorAll("g-context-menu")
				.forEach(menu => menu.hide());
			submenu.show(event.clientX, event.clientY);
		});
}

export default class GContextMenu extends HTMLElement
{
	#context;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.addEventListener("mouseleave", () => this.hide());
		this.shadowRoot.appendChild(template.content.cloneNode(true));
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
		Array.from(this.shadowRoot.querySelectorAll("a")).forEach(e => e.remove());
		actions.forEach(({icon, text, color, method, action, target, title, visible}) =>
		{
			if (visible && !visible(this.context))
				return;

			let link = this.shadowRoot.appendChild(document.createElement("a"));
			link.style.color = color;
			link.title = title || "";
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
				link.addEventListener('click',
					event => createSubmenu(link, event, action));
				scheduleClick(link);
			} else if (typeof action === "object")
			{
				link.setAttribute("submenu", "");
				link.addEventListener('click', event =>
				{
					link.style.cursor = "wait";
					const fetcher = action.type === "source"
						? fetch(action.value).then(ResponseHandler.json)
						: import(resolve(link, this.context, action.value))
						.then(module => module.default)
						.then(create => create(this.context));

					fetcher.then(result => createSubmenu(link, event, result))
						.catch(error => GMessageDialog.error(error.message, 1000))
						.finally(() => link.style.cursor = "");
				});
				scheduleClick(link);
		}
		});
	}

	show(x, y)
	{
		const parent =
			this.context ?
			this.context.parentNode :
			document.body;
		parent.appendChild(this);

		const target = {getBoundingClientRect: () => ({x, y,
					left: x,
					top: y,
					right: x,
					bottom: y,
					width: 0,
					height: 0})};

		this.style.visibility = "hidden";
		anchor(this, target, 0, ...POSITIONS).then(e =>
		{
			this.classList.remove("fallback");
			this.style.top = `${e.location.y}px`;
			this.style.left = `${e.location.x}px`;
		}).catch(error => this.classList.add("fallback"))
			.finally(() => this.style.visibility = "visible");
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

window.addEventListener("click",
	() => document.querySelectorAll("g-context-menu").forEach(menu => menu.hide()));

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
					.then(actions => GContextMenu.show(element, event.clientX, event.clientY, ...actions))
					.catch(error => console.error('Error fetching context menu source:', error));
				event.preventDefault();
				return;
			} else if (element.hasAttribute("data-contextmenu:module"))
			{
				import(element.getAttribute("data-contextmenu:module"))
					.then(module => module.default)
					.then(create => create(element))
					.then(actions => GContextMenu.show(element, event.clientX, event.clientY, ...actions))
					.catch(error => console.error('Error importing context menu module:', error));
				event.preventDefault();
				return;
			}
		}
	}
});