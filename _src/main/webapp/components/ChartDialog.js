class ChartDialog extends Modal
{
	constructor(options)
	{
		super(options);

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.classList.add("ChartDialog");

		var head = dialog.appendChild(document.createElement("div"));
		head.setAttribute("tabindex", "1");

		var body = dialog.appendChild(document.createElement("div"));
		body.style.backgroundColor = "white";

		var canvas = body.appendChild(document.createElement("div"));
		canvas.setAttribute("id", 'Chart');
		canvas.setAttribute("tabindex", "1");

		canvas.onmouseenter = () => canvas.focus();
		head.onmouseenter = () => head.focus();

		var chart = new Chart(options.data, options.title ? options.title : "");

		var cchart = head.appendChild(document.createElement('a'));
		cchart.dialog = this;
		cchart.title = "Coluna";
		cchart.innerHTML = "&#x2033;";
		cchart.onclick = () => chart.draw('Chart', 'cchart');

		var bchart = head.appendChild(document.createElement('a'));
		bchart.dialog = this;
		bchart.title = "Barra";
		bchart.innerHTML = "&#x2246;";
		bchart.onclick = () => chart.draw('Chart', 'bchart');

		var lchart = head.appendChild(document.createElement('a'));
		lchart.dialog = this;
		lchart.title = "Linha";
		lchart.innerHTML = "&#x2032;";
		lchart.onclick = () => chart.draw('Chart', 'lchart');

		var achart = head.appendChild(document.createElement('a'));
		achart.dialog = this;
		achart.title = "Area";
		achart.innerHTML = "&#x2244;";
		achart.onclick = () => chart.draw('Chart', 'achart');

		var pchart = head.appendChild(document.createElement('a'));
		pchart.dialog = this;
		pchart.title = "Pizza";
		pchart.innerHTML = "&#x2031;";
		pchart.onclick = () => chart.draw('Chart', 'pchart');

		var dchart = head.appendChild(document.createElement('a'));
		dchart.dialog = this;
		dchart.title = "Donut";
		dchart.innerHTML = "&#x2245;";
		dchart.onclick = () => chart.draw('Chart', 'dchart');

		var rchart = head.appendChild(document.createElement('a'));
		rchart.dialog = this;
		rchart.title = "Rose";
		rchart.innerHTML = "&#x2247;";
		rchart.onclick = () => chart.draw('Chart', 'rchart');

		head.appendChild(window.top.document.createElement('span'));

		var close = head.appendChild(document.createElement('a'));
		close.dialog = this;
		close.title = "Fechar";
		close.innerHTML = "&#x1011;";
		close.style.marginLeft = '16px';
		close.onclick = () => this.hide();

		this.show();

		if (!options.type)
			options.type = 'cchart';
		chart.draw('Chart', options.type);


		head.focus();
	}
}

ChartDialog.show = function (chart, series, action, title)
{
	if (!series)
		series = new URL(action).get();
	new ChartDialog({data: JSON.parse(series), type: chart, title: title});
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

