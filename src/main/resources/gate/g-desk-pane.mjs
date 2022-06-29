/* global customElements */

customElements.define('g-desk-pane', class extends HTMLElement
{
	constructor()
	{
		super();

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
});