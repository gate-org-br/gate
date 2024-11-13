let template = document.createElement("template");
template.innerHTML = `
	<fieldset>
	</fieldset>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	border: 0;
	padding: 0;
	display: flex;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
}

fieldset {
	padding: 0;
	border: none;
}</style>`;

/* global customElements */
import stylesheets from './stylesheets.js';

customElements.define('g-form-view', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
		stylesheets('input.css', 'fieldset.css')
				.forEach(e => this.shadowRoot.appendChild(e));
	}

	set value(value)
	{
		const fieldset = this.shadowRoot.querySelector("fieldset");
		Array.from(fieldset.children).forEach(e => e.remove());

		if (value)
		{
			value.forEach(element => {
				let label = fieldset.appendChild(document.createElement("label"));

				if (element.size)
					switch (Number(element.size))
					{
						case 0:
							label.setAttribute("data-size", 1);
							break;
						case 1:
							label.setAttribute("data-size", 2);
							break;
						case 2:
							label.setAttribute("data-size", 4);
							break;
						case 3:
							label.setAttribute("data-size", 8);
							break;
					}

				label.innerText = element.name + ': ';

				let span = label.appendChild(document.createElement("span"));
				if (element.multiple)
					span.className = "multiple";

				if (element.value)
					span.innerHTML = element.value.join("<br/>");
			});
		}
	}

	attributeChangedCallback()
	{
		this.value = JSON.parse(this.getAttribute("value"));
	}

	static get observedAttributes()
	{
		return ['value'];
	}
});