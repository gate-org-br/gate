/* global customElements */

class GChartDialog extends Window
{
	constructor()
	{
		super();
		this.caption = "Chart";
		this.classList.add("g-chart-dialog");

		this.body.appendChild(new GChart());

		var close = this.head.appendChild(new GCommand());
		close.dialog = this;
		close.title = "Fechar";
		close.innerHTML = "<i>&#x1011;</i>";
		close.onclick = () => this.hide();

		let commands = new GCommands();
		var cchart = commands.appendChild(document.createElement('a'));
		cchart.dialog = this;
		cchart.title = "Coluna";
		cchart.innerHTML = "Coluna<i>&#x2033;</i>";
		cchart.onclick = () => this.type = 'cchart';

		var bchart = commands.appendChild(document.createElement('a'));
		bchart.dialog = this;
		bchart.title = "Barra";
		bchart.innerHTML = "Barra<i>&#x2246;</i>";
		bchart.onclick = () => this.type = 'bchart';

		var lchart = commands.appendChild(document.createElement('a'));
		lchart.dialog = this;
		lchart.title = "Linha";
		lchart.innerHTML = "Linha<i>&#x2032;</i>";
		lchart.onclick = () => this.type = 'lchart';

		var achart = commands.appendChild(document.createElement('a'));
		achart.dialog = this;
		achart.title = "Área";
		achart.innerHTML = "Área<i>&#x2244;</i>";
		achart.onclick = () => this.type = 'achart';

		var pchart = commands.appendChild(document.createElement('a'));
		pchart.dialog = this;
		pchart.title = "Pizza";
		pchart.innerHTML = "Pizza<i>&#x2031;</i>";
		pchart.onclick = () => this.type = 'pchart';

		var dchart = commands.appendChild(document.createElement('a'));
		dchart.dialog = this;
		dchart.title = "Donut";
		dchart.innerHTML = "Donut<i>&#x2245;</i>";
		dchart.onclick = () => this.type = 'dchart';

		var rchart = commands.appendChild(document.createElement('a'));
		rchart.dialog = this;
		rchart.title = "Rose";
		rchart.innerHTML = "Rose<i>&#x2247;</i>";
		rchart.onclick = () => this.type = 'rchart';

		this.commands = commands;

		this.head.focus();
	}

	set type(type)
	{
		this.body.children[0].type = type;
		switch (type)
		{
			case 'cchart':
				this.caption = 'Coluna';
				break;
			case 'bchart':
				this.caption = 'Barra';
				break;
			case 'pchart':
				this.caption = 'Pizza';
				break;
			case 'dchart':
				this.caption = 'Donnut';
				break;
			case 'achart':
				this.caption = 'Área';
				break;
			case 'rchart':
				this.caption = 'Rose';
				break;
			case 'lchart':
				this.caption = 'Linha';
				break;
		}
	}

	set title(title)
	{
		this.body.children[0].title = title;
	}

	set data(data)
	{
		this.body.children[0].data = data;
	}
}

GChartDialog.show = function (chart, series, title)
{
	let dialog = new GChartDialog();
	dialog.show();

	dialog.type = chart;
	dialog.title = title;
	dialog.data = series;
};

customElements.define('g-chart-dialog', GChartDialog);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('tr.RChart, td.RChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("rchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.DChart, td.DChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("dchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.PChart, td.PChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("pchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.AChart, td.AChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("achart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});


	Array.from(document.querySelectorAll('tr.LChart, td.LChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("lchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.BChart, td.BChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("bchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.CChart, td.CChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("cchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('a[data-chart]')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show(this.getAttribute('data-chart'),
				this.getAttribute('data-series') || new URL(this.getAttribute('href')).get(),
				this.getAttribute("title"));
			return false;
		};
	});
});

