/* global DateFormat, customElements */

class DatePicker extends Picker
{
	constructor()
	{
		super();
		this.close;
		this.classList.add("g-date-picker");
		this.caption = "Selecione uma data";
		this._private.date = this.body.appendChild(new DateSelector());
		this._private.date.addEventListener("selected", e => this.dispatchEvent(new CustomEvent('picked', {detail: e.detail})) | this.hide());
		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Date")).forEach(function (input)
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
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new DatePicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	});
});

customElements.define('g-date-picker', DatePicker);