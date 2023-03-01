let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	gap: 16px;
	margin: 0;
	padding: 0;
	width: 100%;
	color: black;
	display: grid;
	padding: 16px;
	background-color: transparent;
	grid-template-columns: repeat(8, 1fr);
}

::slotted(hr:last-child),
::slotted(hr:first-child),
:host([child]) > ::slotted(a),
:host([child]) > ::slotted(hr),
:host([child]) > ::slotted(button),
:host([child]) > ::slotted(g-desk-pane)
{
	display: none;
}

::slotted(hr)
{
	grid-column: span 8;
	border: solid 4px #BBBBBB;
}

::slotted(a),
::slotted(button),
::slotted(g-desk-pane)
{
	padding: 8px;
	display: flex;
	color: inherit;
	cursor: pointer;
	border-radius: 3px;
	grid-column: span 8;
	align-items: center;
	font-weight: inherit;
	border: 2px solid white;
	background-color: #F8F8F8;
	border: 1px solid #F0F0F0;
	justify-content: flex-start;
	box-shadow: 5px 10px #888888;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(g-desk-pane:hover)
{
	background-color:  #FFFACD;
}

@media only screen and (min-width: 768px) {

	::slotted(a[data-size='1']),
	::slotted(button[data-size='1']),
	::slotted(g-desk-pane[data-size='1'])
	{
		grid-column: span 4;
	}

	::slotted(a[data-size='2']),
	::slotted(button[data-size='2']),
	::slotted(g-desk-pane[data-size='2'])
	{
		grid-column: span 8;
	}

	::slotted(a[data-size='4']),
	::slotted(button[data-size='4']),
	::slotted(g-desk-pane[data-size='4'])
	{
		grid-column: span 8;
	}

	::slotted(a[data-size='8']),
	::slotted(button[data-size='8']),
	::slotted(g-desk-pane[data-size='8'])
	{
		grid-column: span 8;
	}
}

@media only screen and (min-width: 992px) {
	::slotted(a),
	::slotted(button),
	::slotted(g-desk-pane)
	{
		grid-column: span 4;
	}

	::slotted(a[data-size='1']),
	::slotted(button[data-size='1']),
	::slotted(g-desk-pane[data-size='1'])
	{
		grid-column: span 2;
	}

	::slotted(a[data-size='2']),
	::slotted(button[data-size='2']),
	::slotted(g-desk-pane[data-size='2'])
	{
		grid-column: span 4;
	}

	::slotted(a[data-size='4']),
	::slotted(button[data-size='4']),
	::slotted(g-desk-pane[data-size='4'])
	{
		grid-column: span 8;
	}

	::slotted(a[data-size='8']),
	::slotted(button[data-size='8']),
	::slotted(g-desk-pane[data-size='8'])
	{
		grid-column: span 8;
	}
}

@media only screen and (min-width: 1200px) {
	::slotted(a),
	::slotted(button),
	::slotted(g-desk-pane)
	{
		grid-column: span 2;
	}

	::slotted(a[data-size='1']),
	::slotted(button[data-size='1']),
	::slotted(g-desk-pane[data-size='1'])
	{
		grid-column: span 1;
	}

	::slotted(a[data-size='2']),
	::slotted(button[data-size='2']),
	::slotted(g-desk-pane[data-size='2'])
	{
		grid-column: span 2;
	}

	::slotted(a[data-size='4']),
	::slotted(button[data-size='4']),
	::slotted(g-desk-pane[data-size='4'])
	{
		grid-column: span 4;
	}

	::slotted(a[data-size='8']),
	::slotted(button[data-size='8']),
	::slotted(g-desk-pane[data-size='8'])
	{
		grid-column: span 8;
	}
}</style>`;

/* global customElements */

customElements.define('g-desk-pane', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", function (e)
		{
			let target = e.target.closest('g-desk-pane');
			if (target !== this)
			{
				let backup = Array.from(this.children);
				let elements = Array.from(target.children)
					.filter(e => e.tagName !== 'I'
							&& e.tagName !== 'IMG');
				backup.forEach(e => e.parentNode.removeChild(e));
				elements.forEach(e => this.appendChild(e));

				let reset = this.appendChild(document.createElement("a"));
				reset.href = "#";
				reset.style.color = "#660000";
				reset.innerText = "Retornar";
				reset.appendChild(document.createElement("i")).innerHTML = "&#X2023";

				reset.addEventListener("click", () =>
				{
					reset.parentNode.removeChild(reset);
					elements.forEach(e => target.appendChild(e));
					backup.forEach(e => this.appendChild(e));
					e.preventDefault();
					e.stopPropagation();
				});

				e.preventDefault();
				e.stopPropagation();
			}

		}, true);
	}

	connectedCallback()
	{
		Array.from(this.querySelectorAll("hr + hr"))
			.forEach(e => e.remove());
		Array.from(this.querySelectorAll("g-desk-pane"))
			.forEach(e => e.setAttribute("child", true));
	}
});