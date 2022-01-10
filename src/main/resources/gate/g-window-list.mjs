let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	left: 10px;
	right: 10px;
	bottom: 10px;
	height: auto;
	display: none;
	position: fixed;
	flex-wrap: wrap;
	flex-direction: row;
	align-items: stretch;
	justify-content: center;
}

a {
	width: 0;
	margin: 4px;
	height: 32px;
	padding: 8px;
	display: flex;
	overflow: auto;
	flex: 1 1 400px;
	min-width: 300px;
	max-width: 400px;
	font-weight: bold;
	border-radius: 5px;
	align-items: center;
	white-space: nowrap;
	text-decoration: none;
	justify-content: center;
	border: 0px solid transparent;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);

	color: var(--g-window-list-color);
	background-color: var(--g-window-list-background-color);
	background-image: var(--g-window-list-background-image);
}

a:hover {
	color: var(--g-window-list-hovered-color);
	background-color: var(--g-window-list-hovered-background-color);
	background-image: var(--g-window-list-hovered-background-image);
}
</style>`;

/* global customElements, template */

export default class GWindowList extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	add(dialog)
	{
		this.style.display = "flex";

		let link = this.shadowRoot.appendChild(document.createElement("a"));
		link.href = "#";
		link.innerHTML = dialog.caption || "Janela";

		link.addEventListener("click", () =>
		{
			dialog.maximize();
			this.shadowRoot.removeChild(link);
			this.style.display = this.shadowRoot.firstElementChild ? "flex" : "none";
		});
	}

	static get instance()
	{
		if (!window.top.document.querySelector('g-window-list'))
			window.top.document.documentElement
				.appendChild(document.createElement("g-window-list"));
		return window.top.document.querySelector('g-window-list');
	}
}

customElements.define('g-window-list', GWindowList);