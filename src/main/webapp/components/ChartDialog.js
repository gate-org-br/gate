function ChartDialog(data, type, title)
{
	var modal = new Modal(true);

	var dialog = modal.element().appendChild(window.top.document.createElement('div'));
	dialog.className = "Dialog";

	var head = dialog.appendChild(document.createElement("div"));
	head.setAttribute("tabindex", "1");

	var caption = head.appendChild(window.top.document.createElement('label'));
	caption.innerHTML = title;
	caption.style['float'] = "left";

	var body = dialog.appendChild(document.createElement("div"));
	body.style.backgroundColor = "white";

	var canvas = body.appendChild(document.createElement("div"));
	canvas.setAttribute("id", 'Chart');
	canvas.setAttribute("tabindex", "1");

	canvas.onmouseenter = function ()
	{
		this.focus();
	};

	head.onmouseenter = function ()
	{
		this.focus();
	};

	var close = head.appendChild(document.createElement('a'));
	close.style['float'] = "right";
	close.dialog = this;
	close.title = "Fechar";
	close.innerHTML = "&#x1011;";
	close.style.marginLeft = '16px';
	close.style.fontFamily = "gate";
	close.onclick = function ()
	{
		modal.hide();
	};

	var chart = new Chart(data, title);

	var rchart = head.appendChild(document.createElement('a'));
	rchart.style['float'] = "right";
	rchart.dialog = this;
	rchart.title = "Rose";
	rchart.innerHTML = "&#x2247;";
	rchart.style.fontFamily = "gate";
	rchart.onclick = function ()
	{
		chart.draw('Chart', 'rchart');
	};

	var dchart = head.appendChild(document.createElement('a'));
	dchart.style['float'] = "right";
	dchart.dialog = this;
	dchart.title = "Donut";
	dchart.innerHTML = "&#x2245;";
	dchart.style.fontFamily = "gate";
	dchart.onclick = function ()
	{
		chart.draw('Chart', 'dchart');
	};

	var pchart = head.appendChild(document.createElement('a'));
	pchart.style['float'] = "right";
	pchart.dialog = this;
	pchart.title = "Pizza";
	pchart.innerHTML = "&#x2031;";
	pchart.style.fontFamily = "gate";
	pchart.onclick = function ()
	{
		chart.draw('Chart', 'pchart');
	};

	var achart = head.appendChild(document.createElement('a'));
	achart.style['float'] = "right";
	achart.dialog = this;
	achart.title = "Area";
	achart.innerHTML = "&#x2244;";
	achart.style.fontFamily = "gate";
	achart.onclick = function ()
	{
		chart.draw('Chart', 'achart');
	};

	var lchart = head.appendChild(document.createElement('a'));
	lchart.style['float'] = "right";
	lchart.dialog = this;
	lchart.title = "Linha";
	lchart.innerHTML = "&#x2032;";
	lchart.style.fontFamily = "gate";
	lchart.onclick = function ()
	{
		chart.draw('Chart', 'lchart');
	};

	var bchart = head.appendChild(document.createElement('a'));
	bchart.style['float'] = "right";
	bchart.dialog = this;
	bchart.title = "Barra";
	bchart.innerHTML = "&#x2246;";
	bchart.style.fontFamily = "gate";
	bchart.onclick = function ()
	{
		chart.draw('Chart', 'bchart');
	};

	var cchart = head.appendChild(document.createElement('a'));
	cchart.style['float'] = "right";
	cchart.dialog = this;
	cchart.title = "Coluna";
	cchart.innerHTML = "&#x2033;";
	cchart.style.fontFamily = "gate";
	cchart.onclick = function ()
	{
		chart.draw('Chart', 'cchart');
	};

	modal.show();

	if (!type)
		type = 'cchart';
	chart.draw('Chart', type);

	modal.onclick = function (e)
	{
		if (e.target === this
			|| e.srcElement === this)
			modal.hide();
	};

	head.focus();
}

ChartDialog.show = function (chart, series, action, title)
{
	if (!series)
		series = new URL(action).get();
	new ChartDialog(JSON.parse(series), chart, title);
};

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('tr.RChart, td.RChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("rchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.DChart, td.DChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("dchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.PChart, td.PChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("pchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.AChart, td.AChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("achart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});


	Array.from(document.querySelectorAll('tr.LChart, td.LChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("lchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.BChart, td.BChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("bchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.CChart, td.CChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("cchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('a[data-chart]')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show(this.getAttribute('data-chart'),
				this.getAttribute('data-series'),
				this.getAttribute('href'),
				this.getAttribute("title"));
			return false;
		};
	});
});

