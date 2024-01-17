import GDateTimePicker from './g-icon-picker.js';

customElements.define('g-icon-input', class extends HTMLInputElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		let link = this.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		if (this.hasAttribute('tabindex'))
			link.setAttribute("tabindex", this.getAttribute('tabindex'));
		let icon = link.appendChild(document.createElement("g-icon"));

		icon.innerHTML = this.value ? "&#x1001;" : "&#x2009;";
		this.addEventListener("input", () => icon.innerHTML = this.value ? "&#x1001;" : "&#x2009;");
		this.addEventListener("change", () => icon.innerHTML = this.value ? "&#x1001;" : "&#x2009;");

		link.addEventListener("click", event =>
		{
			event.preventDefault();

			if (this.value)
			{
				this.value = '';
				this.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				GDateTimePicker.pick()
					.then(value => this.value = value)
					.then(() => this.dispatchEvent(new Event('change', {bubbles: true})))
					.catch(() => undefined);

			link.focus();
			link.blur();
		});
	}

}, {extends: 'input'});