/* global customElements */

class Commands extends HTMLElement
{
	constructor()
	{
		super();
	}

	add(command)
	{
		if (!this.parentNode)
			document.body.insertBefore(this, document.body.firstChild);
		return this.appendChild(command);
	}

	static get instance()
	{
		if (window.frameElement
			&& window.frameElement.dialog
			&& window.frameElement.dialog.commands)
			return window.frameElement.dialog.commands;

		if (!Commands._private)
			Commands._private = {};
		if (!Commands._private.instance)
			Commands._private.instance =
				new Commands(),
				document.body.firstChild;

		return Commands._private.instance;
	}

	static add(command)
	{
		return Commands.instance.add(command);
	}
}

customElements.define('g-commands', Commands);

window.addEventListener("load", () =>
	{
		Array.from(Commands.instance.querySelectorAll("a, button"))
			.forEach(e => e.parentNode.removeChild(e));
		Array.from(document.querySelectorAll("a.Command, button.Command"))
			.map(element =>
			{
				var clone = element.cloneNode(true);
				clone.onclick = event =>
				{
					element.click();
					event.preventDefault();
				}
				return clone;
			}).forEach(clone => Commands.add(clone));
	});