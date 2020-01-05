/* global customElements */

class DateTimePicker extends Picker
{
	constructor()
	{
		super();
		this.caption = "Selecione uma data e hora";
		var selector = this.body.appendChild(new DateTimeSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent('picked', {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}

customElements.define('g-datetime-picker', DateTimePicker);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.DateTime")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new CustomEvent('changed', {detail: {source: this}}));
			} else
				new DateTimePicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new CustomEvent('changed', {detail: {source: this}}));
				});


			input.dispatchEvent(new CustomEvent('changed', {detail: {source: this}}));
			link.focus();
			link.blur();
		});
	});
});