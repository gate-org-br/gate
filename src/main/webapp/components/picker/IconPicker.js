/* global DateFormat, customElements */

class IconPicker extends Picker
{
	constructor()
	{
		super();

		this.classList.add("g-picker");
		this.head.appendChild(document.createTextNode("Selecione um ícone"));
		var selector = this.body.appendChild(document.createElement("g-icon-selector"));
		selector.addEventListener("selected", e => this.dispatchEvent(new CustomEvent('picked', {detail: e.detail.icon})) | this.hide());
		this.show();
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

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new CustomEvent('changed', {detail: {source: this}}));
			} else
				new IconPicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new CustomEvent('changed', {detail: {source: this}}));
				});


			link.focus();
			link.blur();
		});
	});
});