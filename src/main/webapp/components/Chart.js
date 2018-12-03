function Chart(data, title)
{
	this.categories = new Array();
	for (var i = 1; i < data.length; i++)
		this.categories.push(data[i][0]);
	this.groups = new Array();
	for (var j = 1; j < data[0].length; j++)
	{
		var group = {'label': data[0][j], 'values': new Array()};
		for (var i = 1; i < data.length; i++)
			group.values.push(data[i][j]);
		this.groups.push(group);
	}

	function cc()
	{
		return '#'
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10));
	}

	function f1(params)
	{
		return params.name + ': ' + params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}

	function f2(value)
	{
		return value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}

	function f3(params)
	{
		return params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}

	this.draw = function (id, type)
	{
		switch (type.toLowerCase())
		{
			case 'cchart':
				var options = {calculable: true, tooltip: {show: true, formatter: f1}, xAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'top', formatter: f3}}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = cc;
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'bchart':

				var options = {calculable: true, tooltip: {show: true, formatter: f1}, yAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: -70}}, xAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'right', formatter: f3}}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = cc;
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'lchart':

				var options = {calculable: true, tooltip: {show: true, formatter: f1}, xAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					}
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'achart':

				var options = {calculable: true, tooltip: {show: true, formatter: f1}, xAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {areaStyle: {type: 'default'}}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					}
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'pchart':

				var options =
						{calculable: true, tooltip: {show: true, formatter: f1},
							legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
							toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
							series: [{type: 'pie', roseType: false, radius: this.title ? '60%' : '80%', data: [],
									center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};
				if (title)
					options.title = {x: 'center', text: title};
				if (this.groups.length > 1)
				{
					for (var i = 0; i < this.groups.length; i++)
					{
						options.legend.data.push(this.groups[i].label);
						var sum = 0;
						for (var j = 0; j < this.groups[i].values.length; j++)
							sum = sum + this.groups[i].values[j];
						options.series[0].data.push({name: this.groups[i].label, value: sum});
						if (this.groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: this.groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < this.categories.length; i++)
					{
						options.legend.data.push(this.categories[i]);
						options.series[0].data.push({name: this.categories[i], value: this.groups[0].values[i]});
					}
				}

				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'dchart':

				var options =
						{calculable: true, tooltip: {show: true, formatter: f1},
							legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
							toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
							series: [{type: 'pie', roseType: false, radius: this.title ? ['40%', '60%'] : ['60%', '80%'], data: [],
									center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};
				if (title)
					options.title = {x: 'center', text: title};
				if (this.groups.length > 1)
				{
					for (var i = 0; i < this.groups.length; i++)
					{
						options.legend.data.push(this.groups[i].label);
						var sum = 0;
						for (var j = 0; j < this.groups[i].values.length; j++)
							sum = sum + this.groups[i].values[j];
						options.series[0].data.push({name: this.groups[i].label, value: sum});
						if (this.groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: this.groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < this.categories.length; i++)
					{
						options.legend.data.push(this.categories[i]);
						options.series[0].data.push({name: this.categories[i], value: this.groups[0].values[i]});
					}
				}

				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'rchart':
				var options =
						{calculable: true, tooltip: {show: true, formatter: f1},
							legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
							toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
							series: [{type: 'pie', roseType: 'area', radius: this.title ? [20, '60%'] : [20, '80%'], data: [],
									center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};
				if (title)
					options.title = {x: 'center', text: title};
				if (this.groups.length > 1)
				{
					for (var i = 0; i < this.groups.length; i++)
					{
						options.legend.data.push(this.groups[i].label);
						var sum = 0;
						for (var j = 0; j < this.groups[i].values.length; j++)
							sum = sum + this.groups[i].values[j];
						options.series[0].data.push({name: this.groups[i].label, value: sum});
						if (this.groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: this.groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < this.categories.length; i++)
					{
						options.legend.data.push(this.categories[i]);
						options.series[0].data.push({name: this.categories[i], value: this.groups[0].values[i]});
					}
				}

				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
		}
		return this;
	};
}
