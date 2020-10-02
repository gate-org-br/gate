/* global echarts, customElements */

class GChart extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
	}

	set data(data)
	{
		this.setAttribute("data", data);
	}

	get data()
	{
		return this.getAttribute("data");
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	get type()
	{
		return this.getAttribute("type");
	}

	set title(title)
	{
		this.setAttribute("title", title);
	}

	get title()
	{
		return this.getAttribute("title");
	}

	attributeChangedCallback()
	{
		if (!this.data
			|| !this.type
			|| !this.title)
			return;

		let data = this.data;

		try {
			data = JSON.parse(this.data);
		} catch (ex) {
			data = JSON.parse(new URL(data).get());
		}

		let categories = new Array();
		for (var i = 1; i < data.length; i++)
			categories.push(data[i][0]);

		let groups = new Array();
		for (var j = 1; j < data[0].length; j++)
		{
			var group = {'label': data[0][j], 'values': new Array()};
			for (var i = 1; i < data.length; i++)
				group.values.push(data[i][j]);
			groups.push(group);
		}


		let color = () => '#'
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10));
		let tooltipFormater = params => params.name + ': ' + params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
		let axisLabelFormater = value => value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
		let itemStyleFormater = params => params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");

		switch (this.type)
		{
			case 'cchart':
				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, xAxis: {type: 'category', data: categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'top', formatter: itemStyleFormater}}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = color;
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'bchart':

				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, yAxis: {type: 'category', data: categories,
						axisLabel: {rotate: -70}}, xAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'right', formatter: itemStyleFormater}}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = color;
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'lchart':

				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, xAxis: {type: 'category', data: categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					}
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'achart':

				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, xAxis: {type: 'category', data: categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {areaStyle: {type: 'default'}}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					}
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'pchart':

				var options =
					{calculable: true, tooltip: {show: true, formatter: tooltipFormater},
						legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
						toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
						series: [{type: 'pie', roseType: false, radius: this.title ? '60%' : '80%', data: [],
								center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};

				options.title = {x: 'center', text: this.title};

				if (groups.length > 1)
				{
					for (var i = 0; i < groups.length; i++)
					{
						options.legend.data.push(groups[i].label);
						var sum = 0;
						for (var j = 0; j < groups[i].values.length; j++)
							sum = sum + groups[i].values[j];
						options.series[0].data.push({name: groups[i].label, value: sum});
						if (groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < categories.length; i++)
					{
						options.legend.data.push(categories[i]);
						options.series[0].data.push({name: categories[i], value: groups[0].values[i]});
					}
				}

				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'dchart':

				var options =
					{calculable: true, tooltip: {show: true, formatter: tooltipFormater},
						legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
						toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
						series: [{type: 'pie', roseType: false, radius: this.title ? ['40%', '60%'] : ['60%', '80%'], data: [],
								center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};

				options.title = {x: 'center', text: this.title};

				if (groups.length > 1)
				{
					for (var i = 0; i < groups.length; i++)
					{
						options.legend.data.push(groups[i].label);
						var sum = 0;
						for (var j = 0; j < groups[i].values.length; j++)
							sum = sum + groups[i].values[j];
						options.series[0].data.push({name: groups[i].label, value: sum});
						if (groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < categories.length; i++)
					{
						options.legend.data.push(categories[i]);
						options.series[0].data.push({name: categories[i], value: groups[0].values[i]});
					}
				}

				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'rchart':
				var options =
					{calculable: true, tooltip: {show: true, formatter: tooltipFormater},
						legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
						toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
						series: [{type: 'pie', roseType: 'area', radius: this.title ? [20, '60%'] : [20, '80%'], data: [],
								center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};

				options.title = {x: 'center', text: this.title};

				if (groups.length > 1)
				{
					for (var i = 0; i < groups.length; i++)
					{
						options.legend.data.push(groups[i].label);
						var sum = 0;
						for (var j = 0; j < groups[i].values.length; j++)
							sum = sum + groups[i].values[j];
						options.series[0].data.push({name: groups[i].label, value: sum});
						if (groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < categories.length; i++)
					{
						options.legend.data.push(categories[i]);
						options.series[0].data.push({name: categories[i], value: groups[0].values[i]});
					}
				}

				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
		}
	}

	static get observedAttributes() {
		return ['type', 'data', 'title'];
	}
}

customElements.define('g-chart', GChart);



