let template = document.createElement("template");
template.innerHTML = `
	<a id='prev' href='#'>&#X2278;</a>
	<div id='main'></div>
	<a id='next' href='#'>&#X2276;</a>
 <style>:host(*) {
	display: flex;
	font-size: 20px;
	overflow: hidden;
	align-items: stretch;
	flex-direction: column;
	align-content: stretch;
	justify-content: center;
	border: 2px solid #CCCCCC;
	background-image: linear-gradient(to bottom, #AAAAAA 0%, white 50%, #AAAAAA 100%);
}

:host(:hover) {
	border-color: var(--hovered);
}

div {
	display: flex;
	align-items: stretch;
	flex-direction: column;
	align-content: stretch;
	justify-content: center;
}

a {
	color: black;
	display: flex;
	align-items: center;
	font-family: "gate";
	text-decoration: none;
	justify-content: center;
}

a:nth-of-type(1) {
	cursor: n-resize
}

a:nth-of-type(2) {
	cursor: s-resize
}

span {
	display: flex;
	color: #666666;
	cursor: pointer;
	align-items: center;
	justify-content: center;
}

span:hover {
	font-weight: bold;
}

label
{
	display: flex;
	cursor: pointer;
	font-weight: bold;
	align-items: center;
	justify-content: center;
	text-decoration: underline;
}</style>`;

/* global customElements, template */

const execute = function (value, func, count)
{
	for (let i = 0; i < count; i++)
		value = func(value);
	return value;
};

customElements.define('g-slider', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("mouseover", () => this.focus());
		this.addEventListener("mouseoust", () => this.blur());

		let prev = this.shadowRoot.getElementById("prev");
		prev.addEventListener("click", () => this.value = this.prev(this._private.value));

		let next = this.shadowRoot.getElementById("next");
		next.addEventListener("click", () => this.value = this.next(this._private.value));

		this.addEventListener((/Firefox/i.test(navigator.userAgent))
			? "DOMMouseScroll" : "mousewheel", function (event)
			{
				event.preventDefault();
				if (event.detail)
					if (event.detail > 0)
						next.click();
					else
						prev.click();
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						next.click();
					else
						prev.click();
			});

		this.addEventListener("keydown", function (event)
		{
			switch (event.keyCode)
			{
				case 38:
					prev.click();
					break;
				case 40:
					next.click();
					break;
			}
			event.preventDefault();
		});

		this._private.render = size =>
		{
			this._private.size = size;

			let body = this.shadowRoot.children[1];
			for (let i = size - 1; i >= 0; i--)
				body.appendChild(document.createElement("span"))
					.onclick = () => this.value = execute(this._private.value, this.prev, i + 1);

			body.appendChild(document.createElement("label"));

			for (let i = 0; i < size; i++)
				body.appendChild(document.createElement("span"))
					.onclick = () => this.value = execute(this._private.value, this.next, i + 1);
		};

//		this._private.update = value =>
//		{
//			this._private.value = value;
//
//			let labels = this.shadowRoot.getElementById("main").children;
//
//			for (let i = 0; i < this.size; i++)
//				labels[i].innerHTML = this.format(execute(value, this.prev, this.size - i));
//
//			labels[this.size].innerHTML = this.format(value);
//
//			for (let i = 1; i <= this.size; i++)
//				labels[this.size + i].innerHTML = this.format(execute(value, this.next, i));
//
//			this.dispatchEvent(new CustomEvent('update', {detail: {slider: this}}));
//		};
	}

	connectedCallback()
	{
		this.classList.add("g-slider");
	}

	get value()
	{
		return this._private.value || 0;
	}

	set value(value)
	{
		this._private.value = value;

		let labels = this.shadowRoot.getElementById("main").children;

		for (let i = 0; i < this.size; i++)
			labels[i].innerHTML = this.format(execute(value, this.prev, this.size - i));

		labels[this.size].innerHTML = this.format(value);

		for (let i = 1; i <= this.size; i++)
			labels[this.size + i].innerHTML = this.format(execute(value, this.next, i));

		this.dispatchEvent(new CustomEvent('update', {detail: {slider: this}}));
	}

	get next()
	{
		return this._private.next || (e => (e + 1) % 10);
	}

	set next(next)
	{
		this._private.next = next;
		this.value = this._private.value;
	}

	get prev()
	{
		return this._private.prev || (e => (e + 9) % 10);
	}

	set prev(prev)
	{
		this._private.prev = prev;
		this.value = this._private.value;
	}

	get size()
	{
		return this._private.size || 5;
	}

	set size(size)
	{
		this._private.size = size;

		let body = this.shadowRoot.children[1];
		for (let i = size - 1; i >= 0; i--)
			body.appendChild(document.createElement("span"))
				.onclick = () => this.value = execute(this._private.value, this.prev, i + 1);

		body.appendChild(document.createElement("label"));

		for (let i = 0; i < size; i++)
			body.appendChild(document.createElement("span"))
				.onclick = () => this.value = execute(this._private.value, this.next, i + 1);
	}

	get format()
	{
		return this._private.format || (e => e);
	}

	set format(format)
	{
		this._private.format = format;
		this.value = this._private.value;
	}

	attributeChangedCallback(name)
	{
		if (name === 'size')
			this.size = Number(this.getAttribute('size'));
		else if (name === 'value')
			this.value = Number(this.getAttribute('value'));
	}

	static get observedAttributes()
	{
		return ["size", 'value'];
	}
});