/* global customElements */

class Commands extends HTMLElement
{
	constructor()
	{
		super();
	}

	add(icon, name, action)
	{
		if (!this.children.length)
			this.appendChild(document.createElement("a")).onclick = () =>
			{
				this.appendChild(new CommandDialog(Array.from(this.getElementsByTagName("g-command"))));
			};

		return this.appendChild(window.top.document.createElement("g-command"))
			.icon(icon)
			.name(name)
			.action(action);
	}

	clear()
	{
		Array.from(this.getElementsByTagName("g-command"))
			.forEach(e => this.removeChild(e));
	}

	static get instance()
	{
		if (window.frameElement
			&& window.frameElement.dialog
			&& window.frameElement.dialog.customCommands)
			return window.frameElement.dialog.customCommands;

		if (!Commands._instance)
			Commands._instance =
				document.body.insertBefore(new Commands(),
					document.body.firstChild);

		return Commands._instance;
	}

	static clear()
	{
		Commands.instance.clear();
	}

	static add(icon, name, action)
	{
		return Commands.instance.add(icon, name, action);
	}
}

customElements.define('g-commands', Commands);