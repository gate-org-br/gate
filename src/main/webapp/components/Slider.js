function Slider(element, value, next, prev, format)
{
	element.classList.add("Slider");
	element.setAttribute('tabindex', 0);
	element.addEventListener("mouseover", () => element.focus());
	element.addEventListener("mouseoust", () => element.blur());

	if (!format)
		format = e => e;

	element.addEventListener((/Firefox/i.test(navigator.userAgent))
		? "DOMMouseScroll" : "mousewheel",
		function (event)
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


	element.addEventListener("keydown", function (event)
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

	var goprev = element.appendChild(document.createElement("a"));
	goprev.href = "#";
	goprev.appendChild(document.createElement("i")).innerHTML = "&#X2278;";
	goprev.addEventListener("click", () => update(prev(value)));

	var prev2 = element.appendChild(document.createElement("label"));
	var prev1 = element.appendChild(document.createElement("label"));
	var main = element.appendChild(document.createElement("label"));
	var next1 = element.appendChild(document.createElement("label"));
	var next2 = element.appendChild(document.createElement("label"));

	var gonext = element.appendChild(document.createElement("a"));
	gonext.href = "#";
	gonext.appendChild(document.createElement("i")).innerHTML = "&#X2276;";
	gonext.addEventListener("click", () => update(next(value)));

	this.value = () => value;

	var update = function (val)
	{
		if (val !== null)
		{
			if (!element.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {slider: this, value: val}})))
				return false;

			value = val;
			main.innerHTML = format(value);
			prev1.innerHTML = '-';
			prev2.innerHTML = '-';
			next1.innerHTML = '-';
			next2.innerHTML = '-';

			var data = prev(value);
			if (data !== null)
			{
				prev1.innerHTML = format(data);
				var data = prev(data);
				if (data !== null)
					prev2.innerHTML = format(data);
			}

			var data = next(value);
			if (data !== null)
			{
				next1.innerHTML = format(data);
				var data = next(data);
				if (data !== null)
					next2.innerHTML = format(data);
			}

			element.dispatchEvent(new CustomEvent('update', {detail: {slider: this}}));
		}
	};

	this.element = () => element;
	update(value);
}