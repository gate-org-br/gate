/* global customElements */

class ChartDialog extends Window
{
	constructor(options)
	{
		super(options);
		this.classList.add("g-chart-dialog");

		var canvas = this.body.appendChild(document.createElement("div"));
		canvas.setAttribute("id", 'Chart');
		canvas.setAttribute("tabindex", "1");

		canvas.onmouseenter = () => canvas.focus();
		this.head.onmouseenter = () => this.head.focus();

		var chart = new Chart(options.data, options.title ? options.title : "");

		let overflow = this.commands.add(document.createElement("g-overflow"));
		overflow.innerHTML = "<i>&#X3018;</i>";

		var close = this.commands.appendChild(document.createElement('a'));
		close.dialog = this;
		close.title = "Fechar";
		close.innerHTML = "<i>&#x1011;</i>";
		close.style.marginLeft = '16px';
		close.onclick = () => this.hide();

		var cchart = this.commands.appendChild(document.createElement('a'));
		cchart.dialog = this;
		cchart.title = "Coluna";
		cchart.innerHTML = "<i>&#x2033;</i>";
		cchart.onclick = () => chart.draw('Chart', 'cchart');

		var bchart = this.commands.appendChild(document.createElement('a'));
		bchart.dialog = this;
		bchart.title = "Barra";
		bchart.innerHTML = "<i>&#x2246;</i>";
		bchart.onclick = () => chart.draw('Chart', 'bchart');

		var lchart = this.commands.appendChild(document.createElement('a'));
		lchart.dialog = this;
		lchart.title = "Linha";
		lchart.innerHTML = "<i>&#x2032;</i>";
		lchart.onclick = () => chart.draw('Chart', 'lchart');

		var achart = this.commands.appendChild(document.createElement('a'));
		achart.dialog = this;
		achart.title = "Area";
		achart.innerHTML = "<i>&#x2244;</i>";
		achart.onclick = () => chart.draw('Chart', 'achart');

		var pchart = this.commands.appendChild(document.createElement('a'));
		pchart.dialog = this;
		pchart.title = "Pizza";
		pchart.innerHTML = "<i>&#x2031;</i>";
		pchart.onclick = () => chart.draw('Chart', 'pchart');

		var dchart = this.commands.appendChild(document.createElement('a'));
		dchart.dialog = this;
		dchart.title = "Donut";
		dchart.innerHTML = "<i>&#x2245;</i>";
		dchart.onclick = () => chart.draw('Chart', 'dchart');

		var rchart = this.commands.appendChild(document.createElement('a'));
		rchart.dialog = this;
		rchart.title = "Rose";
		rchart.innerHTML = "<i>&#x2247;</i>";
		rchart.onclick = () => chart.draw('Chart', 'rchart');



		this.show();

		if (!options.type)
			options.type = 'cchart';
		chart.draw('Chart', options.type);


		this.head.focus();
	}
}

ChartDialog.show = function (chart, series, action, title)
{
	if (!series)
		series = new URL(action).get();
	new ChartDialog({data: JSON.parse(series), type: chart, title: title});
};

customElements.define('g-chart-dialog', ChartDialog);

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

