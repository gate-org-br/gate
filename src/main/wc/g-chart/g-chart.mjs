/* global echarts, customElements */

import URL from './url.mjs';
import Dataset from './dataset.mjs';
import * as echarts from './echarts.mjs';

let grid = {top: '80px', left: '80px', right: '80px', bottom: '80px', containLabel: true}
let category = {type: 'category', axisLabel: {width: "100", interval: 0, overflow: "break"}};
let toolbox = {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}};

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
		if (this.type && this.title && this.data)
			this.draw(this.type, this.title, this.dataset);
	}

	get data()
	{
		return this._private.data;
	}

	set type(type)
	{
		this._private.type = type;
		if (this.type && this.title && this.data)
			this.draw(this.type, this.title, this.dataset);
	}

	get type()
	{
		return this._private.type;
	}

	set title(title)
	{
		this._private.title = title;
		if (this.type && this.title && this.data)
			this.draw(this.type, this.title, this.dataset);
	}

	get title()
	{
		return this._private.title;
	}

	get dataset()
	{
		if (typeof this.data === 'object')
			return this.data;
		else if (typeof this.data === 'string')
			if (this.data.length && this.data[0] === "#")
				return Dataset.fromTable(document.getElementById(this.data.substring(1)));
			else if (this.data.includes("[") && this.data.includes("]"))
				return JSON.parse(this.data);
			else
				return JSON.parse(new URL(this.data).get());
	}

	attributeChangedCallback(name)
	{
		switch (name)
		{
			case 'type':
				this.type = this.getAttribute("type");
				break;
			case 'title':
				this.title = this.getAttribute("title");
				break;
			case 'data':
				this.data = this.getAttribute("data");
				break;
		}
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
			yAxis: {},
			series: Array(data[0].length - 1)
				.fill({type: 'bar',
					barGap: 0,
					seriesLayoutBy: 'column',
					itemStyle: {normal: {label: {show: true, position: 'top'}}}})
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
			xAxis: {},
			series: Array(data[0].length - 1)
				.fill({type: 'bar',
					barGap: 0,
					seriesLayoutBy: 'column',
					itemStyle: {normal: {label: {show: true, position: 'right'}}}})
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
			yAxis: {},
			series: Array(data[0].length - 1)
				.fill({type: 'line',
					seriesLayoutBy: 'column',
					itemStyle: {normal: {label: {show: true, position: 'top'}}}})
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
			yAxis: {},
			series: Array(data[0].length - 1)
				.fill({type: 'line',
					seriesLayoutBy: 'column',
					itemStyle: {normal: {label: {show: true, position: 'top'}, areaStyle: {type: 'default'}}}})
		});
	}

	pchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();
		chart.setOption({
			toolbox: toolbox,
			tooltip: {show: true},
			dataset: {source: data},
			legend: {show: true, y: 'bottom'},
			title: {x: 'center', text: title},
			xAxis: {type: 'category', gridIndex: 0},
			yAxis: {type: 'value', gridIndex: 0},
			series: {type: 'pie', itemStyle: {normal: {label: {show: false}}}}
		});
	}

	dchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();
		chart.setOption({
			toolbox: toolbox,
			tooltip: {show: true},
			dataset: {source: data},
			legend: {show: true, y: 'bottom'},
			title: {x: 'center', text: title},
			xAxis: {type: 'category', gridIndex: 0},
			yAxis: {type: 'value', gridIndex: 0},
			series: {type: 'pie', radius: ['40%', '60%'], itemStyle: {normal: {label: {show: false}}}}
		});
	}

	rchart(title, data)
	{
		let chart = echarts.init(this);
		chart.clear();
		chart.setOption({
			toolbox: toolbox,
			tooltip: {show: true},
			dataset: {source: data},
			legend: {show: true, y: 'bottom'},
			title: {x: 'center', text: title},
			xAxis: {type: 'category', gridIndex: 0},
			yAxis: {type: 'value', gridIndex: 0},
			series: {type: 'pie', roseType: 'area', itemStyle: {normal: {label: {show: false}}}}
		});
	}

	static get observedAttributes()
	{
		return ['type', 'data', 'title'];
	}
});