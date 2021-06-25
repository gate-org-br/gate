/* global DateFormat, customElements */

class IconPicker extends Picker
{
	constructor()
	{
		super();
		this.hideButton;
		this.head.appendChild(document.createTextNode("Selecione um ícone"));
		var selector = this.body.appendChild(document.createElement("g-icon-selector"));
		selector.addEventListener("selected", event => this.dispatchEvent(new CustomEvent('picked', {detail: event.detail.icon})) | this.hide());
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-icon-picker");
	}
}

customElements.define('g-icon-picker', IconPicker);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Icon")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2009;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				window.top.document.createElement("g-icon-picker")
					.show().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			link.focus();
			link.blur();
		});
	});
});