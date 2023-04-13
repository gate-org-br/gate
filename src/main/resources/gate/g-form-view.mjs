let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*) {
	display: grid;
	grid-template-columns: repeat(16, 1fr);
}

label {
	padding: 4px;
	display: flex;
	grid-column: span 16;
	flex-direction: column;
}

span {
	padding: 4px;
	flex-grow: 1;
	display: flex;
	flex-basis: 32px;
	border-radius:5px;
	align-items: center;
	background-color: var(--main1);
}

span.multiple {
	display: block;
	overflow: auto;
	flex-basis: 64px;
}

@media only screen and (min-width: 576px) {
	label[data-size="1"] {
		grid-column: span 8;
	}
}

@media only screen and (min-width: 768px) {
	label[data-size="1"] {
		grid-column: span 4;
	}

	label[data-size="2"] {
		grid-column: span 8;
	}
}

@media only screen and (min-width: 992px) {
	label[data-size="1"] {
		grid-column: span 2;
	}

	label[data-size="2"] {
		grid-column: span 4;
	}

	label[data-size="4"] {
		grid-column: span 8;
	}
}


@media only screen and (min-width: 1200px) {
	label[data-size="1"] {
		grid-column: span 1;
	}

	label[data-size="2"] {
		grid-column: span 2;
	}

	label[data-size="4"] {
		grid-column: span 4;
	}

	label[data-size="8"] {
		grid-column: span 8;
	}
}
</style>`;

/* global customElements */

customElements.define('g-form-view', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
	}

	set value(value)
	{
		Array.from(this.shadowRoot.querySelectorAll("label")).forEach(e => e.remove());

		if (value)
			value.forEach(element => {
				let label = this.shadowRoot.appendChild(document.createElement("label"));

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

	attributeChangedCallback()
	{
		this.value = JSON.parse(this.getAttribute("value"));
	}

	static get observedAttributes()
	{
		return ['value'];
	}
});