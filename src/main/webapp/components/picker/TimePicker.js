/* global customElements */

class TimePicker extends Picker
{
	constructor()
	{
		super();
		this.show();
	}
	
	connectedCallback()
	{
		super.connectedCallback();
		this.close;
		this.classList.add("g-time-picker");
		this.caption = "Selecione uma hora";
		var selector = this.body.appendChild(new TimeSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("picked", {detail: this.commit.innerText})) | this.hide());
	}
}


customElements.define('g-time-picker', TimePicker);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Time")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new TimePicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			link.focus();
			link.blur();
		});
	});
});
