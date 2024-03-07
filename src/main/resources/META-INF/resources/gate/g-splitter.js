let template = document.createElement("template");
template.innerHTML = `
	<slot name="L"></slot>
	<div>
		<button id='L' title='Mostrar painel da direta'>
		</button>
		<button id='E' title='Mostrar ambos os paineis'>
		</button>
		<button id='R' title='Mostrar painel da esquerda'>
		</button>
	</div>
	<slot name="R"></slot>
 <style data-element="g-splitter">* {
	box-sizing: border-box
}

:host(*) {
	height: 100%;
	display: flex;
	align-items: stretch;
}

div
{
	display: flex;
	margin-left: 4px;
	margin-right: 4px;
	cursor: col-resize;
	align-items: stretch;
	justify-content: stretch;
	background-color: var(--main4);
}

::slotted(*)
{
	flex-grow: 1;
	flex-basis: 50%;
}

::slotted([hidden])
{
	flex-basis: 0;
	display: none;
}

button {
	margin: 0;
	padding: 0;
	border: none;


}

#L {
	width: 4px;
	cursor: w-resize;
	background-color: var(--main4);
}

#E {
	width: 2px;
	cursor: col-resize;
	background-color: var(--main2);
}

#R {
	width: 4px;
	cursor: e-resize;
	background-color: var(--main4);
}</style>`;
/* global customElements, template */


customElements.define('g-splitter', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		const panel1 = this.firstElementChild;
		const panel2 = this.lastElementChild;

		let currentX;
		let initialFlexBasis;
		let isDragging = false;

		this.shadowRoot.querySelector('div')
			.addEventListener('mousedown', event =>
			{
				event.preventDefault();
				event.stopPropagation();
				isDragging = true;
				currentX = event.clientX;
				initialFlexBasis = panel1.offsetWidth / this.offsetWidth;
			});

		window.addEventListener('mouseup', () => isDragging = false);
		window.addEventListener('mousemove', event =>
		{
			if (isDragging)
			{
				event.preventDefault();
				event.stopPropagation();
				const deltaX = event.clientX - currentX;
				const flexBasis = initialFlexBasis * this.offsetWidth + deltaX;
				panel1.style.flexBasis = `${flexBasis}px`;
				panel2.style.flexBasis = `${this.offsetWidth - flexBasis}px`;

				panel1.removeAttribute("hidden");
				panel2.removeAttribute("hidden");
			}
		});

		this.shadowRoot.getElementById("E").addEventListener("click", () =>
		{
			panel1.style.flexBasis = "50%";
			panel1.removeAttribute("hidden");
			panel2.style.flexBasis = "50%";
			panel2.removeAttribute("hidden", "");
		});

		this.shadowRoot.getElementById("L").addEventListener("click", () =>
		{
			panel2.removeAttribute("hidden");
			panel1.setAttribute("hidden", "");
		});

		this.shadowRoot.getElementById("R").addEventListener("click", () =>
		{
			panel1.removeAttribute("hidden");
			panel2.setAttribute("hidden", "");
		});
	}

	connectedCallback()
	{
		this.firstElementChild.setAttribute("slot", "L");
		this.lastElementChild.setAttribute("slot", "R");
	}
});