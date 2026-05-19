let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
<style data-element="g-context-menu">* {
	cursor: pointer;
	box-sizing: border-box;
}

:host {
	margin: 0;
	border: none;
	padding: 10px;
	z-index: 1000;
	overflow: auto;
	font-size: 14px;
	max-width: 50vw;
	max-height: 50vh;
	min-width: 220px;
	width: fit-content;
	border-radius: 10px;
	color: var(--text1, #000000);
	background-color: var(--main2, white);
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

:host(:popover-open) {
	display: flex;
	align-items: stretch;
	flex-direction: column;
}

a,
label,
button,
::slotted(a),
::slotted(button),
::slotted(.g-command),
::slotted(g-trigger) {
	gap: 12px;
	border: none;
	display: flex;
	color: inherit;
	cursor: pointer;
	flex-basis: 16px;
	border-radius: 4px;
	font-size: inherit;
	padding: 10px 16px;
	align-items: center;
	white-space: nowrap;
	text-decoration: none;
	transition: all 0.2s ease;
	background-color: transparent;
	justify-content: flex-start;
	transition: background-color 0.2s ease, padding 0.2s ease;
}

a:hover,
label:hover,
button:hover,
::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover),
::slotted(g-trigger:hover) {
	background-color: var(--hovered, #FFFACD);
	padding-left: 20px;
}

a:focus,
label:focus,
button:focus,
::slotted(a:focus),
::slotted(g-trigger:focus),
::slotted(button:focus),
::slotted(.g-command:focus) {
	outline: none;
	box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
}

a[data-icon]::before,
label[data-icon]::before,
button[data-icon]::before,
::slotted(a[data-icon])::before,
::slotted(g-trigger[data-icon])::before,
::slotted(button[data-icon])::before,
::slotted(.g-command[data-icon])::before {
	font-family: gate;
	content: attr(data-icon);
}

label::after,
::slotted(g-trigger)::after {
	color: #888;
	font-size: 12px;
	content: '\\3017';
	font-family: gate;
	margin-left: auto;
}</style>`;
/* global customElements, template */

import anchor from './anchor.js';
import DOM from './dom.js';
import GMessageDialog from './g-message-dialog.js';
import './g-trigger.js';
import './mutation-events.js';
import resolve from './resolve.js';
import ResponseHandler from './response-handler.js';

const POSITIONS = ["northeast", "southeast", "northwest", "southwest", "north", "south", "east", "west"];

const sheet = new CSSStyleSheet();
sheet.replaceSync(`g-context-menu g-icon { order: -1 }`);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

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
	submenu.addEventListener("toggle",
		e => e.newState === "closed" && submenu.remove());
	submenu.actions = actions;
	link.appendChild(submenu);
	submenu.style.visibility = "hidden";
	submenu.showPopover();
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
			submenu.show({x: event.clientX, y: event.clientY});
		});
}

export default class GContextMenu extends HTMLElement
{
	#context;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.addEventListener("click", () => this.hide());
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		const close = () => this.hide();

		this.addEventListener("toggle", e =>
		{
			if (e.newState === "open")
				window.addEventListener("click", close, {once: true});
			else
				window.removeEventListener("click", close);
		});
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

			let link = this.shadowRoot.appendChild(document
				.createElement(typeof action === 'string'
				|| typeof action === 'function' ? "a" : "label"));
			link.style.color = color;
			link.title = title || "";
			link.innerText = text;
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
							.then(actions => typeof actions === "function" ? actions(this.context) : actions);

					fetcher.then(result => createSubmenu(link, event, result))
						.catch(error => GMessageDialog.error(error.message, 1000))
						.finally(() => link.style.cursor = "");
				});
				scheduleClick(link);
			}
		});
	}

	get issubmenu()
	{
		return this.parentNode?.closest("[popover]")
			|| this.parentNode?.assignedSlot?.closest("[popover]");
	}

	show(target)
	{
		if (target instanceof MouseEvent)
		{
			if (this.issubmenu)
				target = target.target;
			else
				target = {
					x: target.clientX,
					y: target.clientY
				};
		}

		this.style.visibility = "hidden";
		this.togglePopover(true);
		anchor(this, target, 0, ...POSITIONS).then(e =>
		{
			this.style.top = `${e.location.y}px`;
			this.style.left = `${e.location.x}px`;
		}).finally(() => this.style.visibility = "visible");
	}

	root()
	{
		return this.getRootNode().host
		instanceof GContextMenu
			? this.getRootNode().host.root()
			: this;
	}

	hide() { this.parentNode && this.togglePopover(false); }

	static show(context, target, ...actions)
	{
		let menu = document.createElement("g-context-menu");
		menu.context = context;
		menu.actions = actions;
		menu.addEventListener("toggle",
			e => e.newState === "closed" && menu.remove());

		let parent = context;
		while (parent && !parent.contains(menu))
		{
			parent.appendChild(menu);
			parent = parent.parentNode;
		}
		menu.show(target);
		return menu;
	}

	connectedCallback()
	{
		this.setAttribute("popover", "manual");
	}
}

customElements.define('g-context-menu', GContextMenu);

window.addEventListener("contextmenu", function (event)
{
	if (event.ctrlKey)
		return;

	const path = event.composedPath();
	const element = path.find(e => e instanceof HTMLElement
		&& (e.hasAttribute("data-context-menu")
			|| e.hasAttribute("data-context-menu:source")
			|| e.hasAttribute("data-context-menu:module")));
	if (element)
	{
		event.preventDefault();
		event.stopPropagation();
		const x = event.clientX;
		const y = event.clientY;

		if (element.hasAttribute("data-context-menu"))
		{
			const selector = element.getAttribute("data-context-menu");
			const contextmenu = DOM.navigate(element, selector)
				.orElseThrow(`${selector} is not a valid selector`);
			contextmenu.show({x, y});
		} else if (element.hasAttribute("data-context-menu:source"))
		{
			fetch(element.getAttribute("data-context-menu:source"))
				.then(response => response.json())
				.then(actions => GContextMenu.show(element, {x, y}, ...actions))
				.catch(error => console.error('Error fetching context menu source:', error));
		} else if (element.hasAttribute("data-context-menu:module"))
		{
			import(element.getAttribute("data-context-menu:module"))
				.then(module => module.default)
				.then(actions => typeof actions === "function" ? actions(element) : actions)
				.then(actions => GContextMenu.show(element, {x, y}, ...actions))
				.catch(error => console.error('Error importing context menu module:', error));
		}
	}
});