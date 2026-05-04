import assert from 'node:assert/strict';
import test from 'node:test';

class CustomEvent
{
	constructor(type, options = {})
	{
		this.type = type;
		this.detail = options.detail;
	}
}

class CustomElements
{
	#definitions = new Map();

	define(name, type)
	{
		this.#definitions.set(name, type);
	}

	create(name)
	{
		const type = this.#definitions.get(name);
		const element = type ? new type() : new HTMLElement(name);
		element.tagName = name;
		return element;
	}
}

class Node
{
	parentNode = null;
	children = [];

	appendChild(child)
	{
		this.children.push(child);
		child.parentNode = this;
		return child;
	}

	cloneNode(deep = false)
	{
		const clone = document.createElement(this.tagName);
		clone.id = this.id;
		clone.innerHTML = this.innerHTML;
		clone.attributes = {...this.attributes};
		if (deep)
			this.children.forEach(child => clone.appendChild(child.cloneNode(true)));
		return clone;
	}

	get lastElementChild()
	{
		return this.children.at(-1) || null;
	}
}

class Element extends Node
{
	constructor(tagName)
	{
		super();
		this.id = '';
		this.style = {};
		this.innerHTML = '';
		this.attributes = {};
		this.tagName = tagName;
	}

	setAttribute(name, value)
	{
		this.attributes[name] = value;
	}

	getAttribute(name)
	{
		return this.attributes[name] ?? null;
	}

	scrollIntoView()
	{
	}

	querySelector(selector)
	{
		return find(this, element => element.tagName === selector || ("#" + element.id) === selector);
	}

	getElementById(id)
	{
		return find(this, element => element.id === id);
	}

	attachShadow()
	{
		this.shadowRoot = new ShadowRoot();
		return this.shadowRoot;
	}
}

class HTMLElement extends Element
{
	constructor(tagName = '')
	{
		super(tagName);
	}
}

class ShadowRoot extends Element
{
	constructor()
	{
		super('#shadow-root');
	}
}

class DocumentFragment extends Element
{
	constructor()
	{
		super('#document-fragment');
	}
}

class TemplateElement extends Element
{
	constructor()
	{
		super('template');
		this.content = new DocumentFragment();
	}

	set innerHTML(value)
	{
		this._innerHTML = value;
		this.content = createTemplateContent(value);
	}

	get innerHTML()
	{
		return this._innerHTML;
	}
}

function find(node, predicate)
{
	for (const child of node.children)
	{
		if (predicate(child))
			return child;
		const found = find(child, predicate);
		if (found)
			return found;
	}
	return null;
}

function createTemplateContent(html)
{
	const content = new DocumentFragment();

	if (html.includes("id='title'"))
	{
		content.appendChild(element('label', 'title', '\n\t\tConectando ao servidor\n\t'));
		content.appendChild(element('progress'));
		content.appendChild(element('g-digital-clock'));
		content.appendChild(element('label', 'counter', '\n\t\t...\n\t'));
		content.appendChild(document.createElement('g-logger'));
		content.appendChild(element('style'));
		return content;
	}

	content.appendChild(element('style'));
	return content;
}

function element(tagName, id = '', innerHTML = '')
{
	const element = new HTMLElement(tagName);
	element.id = id;
	element.innerHTML = innerHTML;
	return element;
}

const listeners = new Map();
const customElements = new CustomElements();
const document = {
	createElement(name)
	{
		return name === 'template' ? new TemplateElement() : customElements.create(name);
	}
};
const window = {
	addEventListener(type, listener)
	{
		const typeListeners = listeners.get(type) || [];
		typeListeners.push(listener);
		listeners.set(type, typeListeners);
	},
	dispatchEvent(event)
	{
		(listeners.get(event.type) || []).forEach(listener => listener(event));
	}
};

globalThis.CustomEvent = CustomEvent;
globalThis.HTMLElement = HTMLElement;
globalThis.customElements = customElements;
globalThis.document = document;
globalThis.window = window;

await import('../../main/resources/META-INF/resources/gate/g-progress-status.js');

test('g-progress-status shows fatal process errors as a final failed state', () =>
{
	const status = document.createElement('g-progress-status');
	status.process = 'process-1';

	window.dispatchEvent(new CustomEvent('ProcessError', {
		detail: {
			id: 'process-1',
			text: 'Unable to reconnect to server',
			fatal: true
		}
	}));

	const title = status.shadowRoot.getElementById('title');
	const clock = status.shadowRoot.querySelector('g-digital-clock');
	const counter = status.shadowRoot.getElementById('counter');

	assert.equal(title.innerHTML, 'Unable to reconnect to server');
	assert.equal(title.style.color, '#660000');
	assert.equal(clock.style.color, '#660000');
	assert.equal(counter.style.color, '#660000');
	assert.equal(clock.getAttribute('paused'), 'paused');
});

test('g-progress-status keeps non fatal process errors as reconnecting state', () =>
{
	const status = document.createElement('g-progress-status');
	status.process = 'process-2';

	window.dispatchEvent(new CustomEvent('ProcessError', {
		detail: {
			id: 'process-2',
			text: 'Reconnecting to server'
		}
	}));

	const title = status.shadowRoot.getElementById('title');
	const clock = status.shadowRoot.querySelector('g-digital-clock');
	const counter = status.shadowRoot.getElementById('counter');

	assert.equal(title.innerHTML, 'Reconnecting to server');
	assert.equal(title.style.color, '#666666');
	assert.equal(clock.style.color, '#666666');
	assert.equal(counter.style.color, '#666666');
	assert.equal(clock.getAttribute('paused'), null);
});
