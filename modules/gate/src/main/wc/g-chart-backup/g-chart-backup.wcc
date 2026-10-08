/* global customElements */

import Dataset from './dataset.js';
import NumberFormat from './number-format.js';

const format = new NumberFormat();
const locale = document.documentElement.lang || navigator.language;
const tooltip = {valueFormatter: value => value.toLocaleString(locale)};
const values = {axisLabel: {formatter: value => value.toLocaleString(locale)}};
const grid = {top: '80px', left: '80px', right: '80px', bottom: '80px', containLabel: true};
const category = {type: 'category', axisLabel: {width: "100", interval: 0, overflow: "break"}};

function load(chart)
{
	return import('./echarts.js').then(() =>
	{
		return Promise.all([import('./echarts.js'),
			import('./icon-data.js')])
			.then(([echarts, icons]) => [echarts,
					toolbox(chart, icons.default)]);
	});
}

function toolbox(chart, icons)
{
	return chart.toolbox ? {
		show: true,
		top: 'middle',
		orient: 'vertical',
		feature: {
			myPChart: {
				title: 'Pie',
				onclick: () => chart.type = "pchart",
				icon: `image://${icons.get('2031')}`
			}, myDChart: {
				title: 'Donut',
				onclick: () => chart.type = "dchart",
				icon: `image://${icons.get('2245')}`
			}, myRChart: {
				title: 'Rose',
				onclick: () => chart.type = "rchart",
				icon: `image://${icons.get('2247')}`
			}, myLChart: {
				title: 'Line',
				onclick: () => chart.type = "lchart",
				icon: `image://${icons.get('2032')}`
			}, myCChart: {
				title: 'Column',
				onclick: () => chart.type = "cchart",
				icon: `image://${icons.get('2033')}`
			}, myAChart: {
				title: 'Area',
				onclick: () => chart.type = "achart",
				icon: `image://${icons.get('2244')}`
			}, myBChart: {
				title: 'Bar',
				onclick: () => chart.type = "bchart",
				icon: `image://${icons.get('2246')}`
			}, myReverse: {
				title: 'Reverse',
				onclick: () => chart.reversed = !chart.reversed,
				icon: `image://${icons.get('2025')}`
			},
			saveAsImage: {title: 'Salvar'}
		}
	} : {};
}

function pie(title, toolbox, value, radius, roseType)
{
	let labels = value[0].slice(1);
	let width = 100 / labels.length;

	let series = labels.map((_, index) => ({
			radius,
			roseType,
			type: 'pie',
			top: 'center',
			height: '100%',
			width: width + '%',
			name: value[0][index + 1],
			left: (index * width) + '%',
			encode: {itemName: value[0][0], value: value[0][index + 1]},
			label: {position: 'inner', formatter: e => e.value[index + 1].toLocaleString(locale)}
		}));

	title = title ? [{text: title, x: "center"}] : [];
	if (labels.length > 1)
		labels.map((e, index) => ({subtext: e, left: (index * width) + width / 2 + '%', top: 25, textAlign: 'center'}))
			.forEach(e => title.push(e));

	return {title,
		series,
		tooltip,
		toolbox,
		legend: {bottom: 0},
		dataset: {source: value}};
}

customElements.define('g-chart-backup', class extends HTMLElement
{
	#chart;
	#value;

	constructor()
	{
		super();
		window.addEventListener('resize', () => this.resize());
		new IntersectionObserver(entries =>
			entries.filter(e => e.isIntersecting).forEach(() => this.resize()))
			.observe(this);
	}

	resize()
	{
		if (this.#chart)
			this.#chart.resize();
	}

	set value(value)
	{
		this.#value = [value[0],
			...value.slice(1)
				.map(row => row.map((col, index) => index
							? format.parse(col)
							: col))];
		this.refresh();
	}

	get value()
	{
		return this.#value;
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

	get reversed()
	{
		return this.hasAttribute("reversed");
	}

	set reversed(reversed)
	{
		if (reversed)
			this.setAttribute("reversed", "");
		else
			this.removeAttribute("reversed");
	}

	attributeChangedCallback(name, _, value)
	{
		switch (name)
		{
			case 'value':
				fetch(value)
					.then(e => e.json())
					.then(e => this.value = e);
				break;

			case 'type':
			case 'title':
			case 'reversed':
			case 'toolbox':
				this.refresh();
				break;
		}
	}

	refresh()
	{
		setTimeout(() =>
		{
			if (this.type && this.value)
				this.draw(this.type, this.title, this.reversed
					? Dataset.reverse(this.value) : this.value);
			else
				load(this).then(echarts => echarts.init(this).clear());
		}, 0);
	}

	draw(type, title, value)
	{
		switch (type)
		{
			case 'column':
			case 'cchart':
				return this.cchart(title, value);
			case 'bar':
			case 'bchart':
				return this.bchart(title, value);
			case 'line':
			case 'lchart':
				return this.lchart(title, value);
			case 'area':
			case 'achart':
				return this.achart(title, value);
			case 'pie':
			case 'pchart':
				return this.pchart(title, value);
			case 'donut':
			case 'dchart':
				return this.dchart(title, value);
			case 'rose':
			case 'rchart':
				return this.rchart(title, value);
		}
	}

	cchart(title, value)
	{
		title = title ? {x: 'center', text: title} : null;

		load(this).then(([echarts, toolbox]) =>
		{
			this.#chart = echarts.init(this);
			this.#chart.clear();
			this.#chart.setOption({
				grid,
				title,
				toolbox,
				dataset: {source: value},
				legend: {show: value[0].length > 2, y: 'bottom'},
				dataZoom: {height: 12, endValue: 9, startValue: 0, filterMode: 'empty'},
				xAxis: category,
				yAxis: values,
				series: Array(value[0].length - 1)
					.fill({type: 'bar',
						barGap: 0,
						seriesLayoutBy: 'column',
						label: {show: true, position: 'top', formatter: e => e.value[e.encode.y].toLocaleString(locale)}})
			});
		});
	}

	bchart(title, value)
	{
		title = title ? {x: 'center', text: title} : null;

		load(this).then(([echarts, toolbox]) =>
		{
			this.#chart = echarts.init(this);
			this.#chart.clear();
			this.#chart.setOption({
				grid,
				title,
				toolbox,
				dataset: {source: value},
				legend: {show: value[0].length > 2, y: 'bottom'},
				dataZoom: {right: 40, width: 12, endValue: 9, startValue: 0, orient: 'vertical', filterMode: 'empty'},
				yAxis: category,
				xAxis: values,
				series: Array(value[0].length - 1)
					.fill({type: 'bar',
						barGap: 0,
						seriesLayoutBy: 'column',
						label: {show: true, position: 'right', formatter: e => e.value[e.encode.x].toLocaleString(locale)}})
			});
		});

	}

	lchart(title, value)
	{

		title = title ? {x: 'center', text: title} : null;

		load(this).then(([echarts, toolbox]) =>
		{
			this.#chart = echarts.init(this);
			this.#chart.clear();
			this.#chart.setOption({
				grid,
				title,
				toolbox,
				dataset: {source: value},
				legend: {show: value[0].length > 2, y: 'bottom'},
				dataZoom: {height: 12, endValue: 9, startValue: 0, filterMode: 'empty'},
				xAxis: category,
				yAxis: values,
				series: Array(value[0].length - 1)
					.fill({type: 'line',
						seriesLayoutBy: 'column',
						label: {show: true, position: 'top', formatter: e => e.value[e.encode.y].toLocaleString(locale)}})
			});
		});
	}

	achart(title, value)
	{
		title = title ? {x: 'center', text: title} : null;

		load(this).then(([echarts, toolbox]) =>
		{
			this.#chart = echarts.init(this);
			this.#chart.clear();
			this.#chart.setOption({
				grid,
				title,
				toolbox,
				dataset: {source: value},
				legend: {show: value[0].length > 2, y: 'bottom'},
				dataZoom: {height: 12, endValue: 9, startValue: 0, filterMode: 'empty'},
				xAxis: category,
				yAxis: values,
				series: Array(value[0].length - 1)
					.fill({type: 'line',
						seriesLayoutBy: 'column',
						label: {show: true, position: 'top', formatter: e => e.value[e.encode.y].toLocaleString(locale)},
						areaStyle: {type: 'default'}})
			});
		});
	}

	pchart(title, value)
	{
		load(this).then(([echarts, toolbox]) =>
		{
			this.#chart = echarts.init(this);
			this.#chart.clear();
			this.#chart.setOption(pie(title, toolbox, value, "80%", null));
		});
	}

	dchart(title, value)
	{
		load(this).then(([echarts, toolbox]) =>
		{
			this.#chart = echarts.init(this);
			this.#chart.clear();
			this.#chart.setOption(pie(title, toolbox, value, ["60%", "80%"], null));
		});
	}

	rchart(title, value)
	{
		load(this).then(([echarts, toolbox]) =>
		{
			this.#chart = echarts.init(this);
			this.#chart.clear();
			this.#chart.setOption(pie(title, toolbox, value, "80%", "area"));
		});
	}

	set toolbox(toolbox)
	{
		if (toolbox)
			this.setAttribute("toolbox", "");
		else
			this.removeAttribute("toolbox");
	}

	get toolbox()
	{
		return this.hasAttribute("toolbox");
	}

	static get observedAttributes()
	{
		return ['type', 'title',
			'value',
			'reversed', 'toolbox'];
	}
});