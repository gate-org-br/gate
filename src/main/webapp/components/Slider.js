class Slider extends HTMLElement
{
	constructor(value, next, prev, format, size = 5)
	{
		super();
		this.classList.add("Slider");
		this.addEventListener("mouseover", () => this.focus());
		this.addEventListener("mouseoust", () => this.blur());

		if (!format)
			format = e => e;
		this.addEventListener((/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel", function (event)
		{
			event.preventDefault();
			if (event.detail)
				if (event.detail > 0)
					gonext.click();
				else
					goprev.click();
			else if (event.wheelDelta)
				if (event.wheelDelta > 0)
					gonext.click();
				else
					goprev.click();
		});
		this.addEventListener("keydown", function (event)
		{
			switch (event.keyCode)
			{
				case 38:
					goprev.click();
					break;
				case 40:
					gonext.click();
					break;
			}
			event.preventDefault();
		});

		var goprev = this.appendChild(document.createElement("a"));
		goprev.href = "#";
		goprev.appendChild(document.createElement("i")).innerHTML = "&#X2278;";
		goprev.addEventListener("click", () => update(prev(value)));

		function execute(val, func, count)
		{
			for (let i = 0; i < count; i++)
				val = func(val);
			return val;
		}


		var prevs = [];
		for (let i = size - 1; i >= 0; i--)
		{
			prevs[i] = this.appendChild(document.createElement("label"));
			prevs[i].onclick = () => update(execute(value, prev, i + 1));
		}

		var main = this.appendChild(document.createElement("label"));
		main.style.fontWeight = "bold";

		var nexts = []
		for (let i = 0; i < size; i++)
		{
			nexts[i] = this.appendChild(document.createElement("label"));
			nexts[i].onclick = () => update(execute(value, next, i + 1));
		}

		var gonext = this.appendChild(document.createElement("a"));
		gonext.href = "#";
		gonext.appendChild(document.createElement("i")).innerHTML = "&#X2276;";
		gonext.addEventListener("click", () => update(next(value)));
		this.value = () => value;
		var update = val =>
		{
			if (val !== null)
			{
				if (!this.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {slider: this, value: val}})))
					return false;
				value = val;
				prevs[0].innerHTML = '-';
				prevs[1].innerHTML = '-';
				prevs[2].innerHTML = '-';
				prevs[3].innerHTML = '-';
				prevs[4].innerHTML = '-';
				main.innerHTML = format(value);
				nexts[0].innerHTML = '-';
				nexts[1].innerHTML = '-';
				nexts[2].innerHTML = '-';
				nexts[1].innerHTML = '-';
				nexts[0].innerHTML = '-';


				for (let i = 0; i < prevs.length; i++)
					prevs[i].innerHTML = format(execute(val, prev, i + 1));

				for (let i = 0; i < nexts.length; i++)
					nexts[i].innerHTML = format(execute(val, next, i + 1));
				this.dispatchEvent(new CustomEvent('update', {detail: {slider: this}}));
			}
		};

		update(value);
	}
}



customElements.define('g-slider', Slider);