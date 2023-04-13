/* global echarts, customElements */

import URL from './url.mjs';
import Dataset from './dataset.mjs';
import * as echarts from './echarts.mjs';

const locale = document.documentElement.lang || navigator.language;
const grid = {top: '80px', left: '80px', right: '80px', bottom: '80px', containLabel: true};
const category = {type: 'category', axisLabel: {width: "100", interval: 0, overflow: "break"}};
const toolbox = {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}};
customElements.define('g-chart', class extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
	}

	set data(data)
	{
		this._private.data = data;
		this.refresh();
	}

	get data()
	{
		return this._private.data;
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

	parse(type, value)
	{
		switch (type)
		{
			case 'table':
				let matcher = value.match(/^(#[a-z]+)(\(([^)]*)\))?$/i);
				if (!matcher)
					throw new Error("Invalid table id");
				let table = document.querySelector(matcher[1]);
				let options = matcher[3] ? JSON.parse(matcher[3]) : {};
				this.data = Dataset.fromTable(table, options);
				break;
			case 'url':
				fetch(value).then(e => e.json()).then(e => this.data = e);
				break;
			case 'array':
				this.data = JSON.parse(value);
				break;
		}

	}

	set array(value)
	{
		this.data = JSON.parse(value);
	}

	set url(value)
	{
		fetch(value).then(e => e.json()).then(e => this.data = e);
	}

	attributeChangedCallback(name, _, value)
	{
		setTimeout(() =>
		{
			switch (name)
			{
				case 'url':
					Dataset.parse('url', value).then(e => this.data = e);
					break;
				case 'table':
					Dataset.parse('table', value).then(e => this.data = e);
					break;
				case 'array':
					Dataset.parse('array', value).then(e => this.data = e);
					break;
				case 'title':
					this.refresh();
					break;
				case 'type':
					this.refresh();
					break;
			}
		}, 0);
	}

	refresh()
	{
		if (this.type && this.title && this.data)
			this.draw(this.type, this.title, this.data);
		else
			echarts.init(this).clear();
	}

	draw(type, title, data)
	{
		switch (type)
		{
			case 'cchart':
				return this.cchart(title, data);
			case 'bchart':
				return this.bchart(title, data);
			case 'lchart':
				return this.lchart(title, data);
			case 'achart':
				return this.achart(title, data);
			case 'pchart':
				return this.pchart(title, data);
			case 'dchart':
				return this.dchart(title, data);
			case 'rchart':
				return this.rchart(title, data);
		}
	}

	cchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();
		chart.setOption({
			grid: grid,
			toolbox: toolbox,
			dataset: {source: data},
			title: {x: 'center', text: title},
			legend: {show: data[0].length > 2, y: 'bottom'},
			dataZoom: {height: 12, endValue: 9, startValue: 0, filterMode: 'empty'},
			xAxis: category,
			yAxis: {axisLabel: {formatter: value => value.toLocaleString(locale)}},
			series: Array(data[0].length - 1)
				.fill({type: 'bar',
					barGap: 0,
					seriesLayoutBy: 'column',
					label: {show: true, position: 'top', formatter: e => e.value[e.encode.y].toLocaleString(locale)}})
		});
	}

	bchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();
		chart.setOption({
			grid: grid,
			toolbox: toolbox,
			dataset: {source: data},
			title: {x: 'center', text: title},
			legend: {show: data[0].length > 2, y: 'bottom'},
			dataZoom: {right: 40, width: 12, endValue: 9, startValue: 0, orient: 'vertical', filterMode: 'empty'},
			yAxis: category,
			xAxis: {axisLabel: {formatter: value => value.toLocaleString(locale)}},
			series: Array(data[0].length - 1)
				.fill({type: 'bar',
					barGap: 0,
					seriesLayoutBy: 'column',
					label: {show: true, position: 'right', formatter: e => e.value[e.encode.x].toLocaleString(locale)}})
		});
	}

	lchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();
		chart.setOption({
			grid: grid,
			toolbox: toolbox,
			dataset: {source: data},
			title: {x: 'center', text: title},
			legend: {show: data[0].length > 2, y: 'bottom'},
			dataZoom: {height: 12, endValue: 9, startValue: 0, filterMode: 'empty'},
			xAxis: category,
			yAxis: {axisLabel: {formatter: value => value.toLocaleString(locale)}},
			series: Array(data[0].length - 1)
				.fill({type: 'line',
					seriesLayoutBy: 'column',
					label: {show: true, position: 'top', formatter: e => e.value[e.encode.y].toLocaleString(locale)}})
		});
	}

	achart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();
		chart.setOption({
			grid: grid,
			toolbox: toolbox,
			dataset: {source: data},
			title: {x: 'center', text: title},
			legend: {show: data[0].length > 2, y: 'bottom'},
			dataZoom: {height: 12, endValue: 9, startValue: 0, filterMode: 'empty'},
			xAxis: category,
			yAxis: {axisLabel: {formatter: value => value.toLocaleString(locale)}},
			series: Array(data[0].length - 1)
				.fill({type: 'line',
					seriesLayoutBy: 'column',
					label: {show: true, position: 'top', formatter: e => e.value[e.encode.y].toLocaleString(locale)},
					areaStyle: {type: 'default'}})
		});
	}

	pchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();

		let width = 100 / (data[0].length - 1);

		chart.setOption({
			toolbox: toolbox,
			legend: {bottom: 0},
			dataset: {source: data},
			title: {x: 'center', text: title},
			tooltip: {valueFormatter: (value) => value.toLocaleString(locale)},

			series: data[0].slice(1).map((value, index) =>
				({
					type: 'pie',
					top: 'center',
					height: '100%',
					width: width + '%',
					name: data[0][index + 1],
					left: (index * width) + '%',
					encode: {itemName: data[0][0], value: data[0][index + 1]},
					label: {position: 'inner', formatter: e => e.value[index + 1].toLocaleString(locale)}
				})
			)
		});
	}

	dchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();

		let width = 100 / (data[0].length - 1);

		chart.setOption({
			toolbox: toolbox,
			legend: {bottom: 0},
			dataset: {source: data},
			title: {x: 'center', text: title},
			tooltip: {valueFormatter: (value) => value.toLocaleString(locale)},

			series: data[0].slice(1).map((value, index) =>
				({
					type: 'pie',
					top: 'center',
					height: '100%',
					width: width + '%',
					radius: ['40%', '60%'],
					name: data[0][index + 1],
					left: (index * width) + '%',
					encode: {itemName: data[0][0], value: data[0][index + 1]},
					label: {position: 'inner', formatter: e => e.value[index + 1].toLocaleString(locale)}
				}))
		});
	}

	rchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();

		let width = 100 / (data[0].length - 1);

		chart.setOption({
			toolbox: toolbox,
			legend: {bottom: 0},
			dataset: {source: data},
			title: {x: 'center', text: title},
			tooltip: {valueFormatter: (value) => value.toLocaleString(locale)},

			series: data[0].slice(1).map((value, index) =>
				({
					type: 'pie',
					top: 'center',
					height: '100%',
					roseType: 'area',
					width: width + '%',
					name: data[0][index + 1],
					left: (index * width) + '%',
					encode: {itemName: data[0][0], value: data[0][index + 1]},
					label: {position: 'inner', formatter: e => e.value[index + 1].toLocaleString(locale)}
				}))
		});
	}

	static get observedAttributes()
	{
		return ['type', 'title', 'table', 'array', 'fetch'];
	}
});