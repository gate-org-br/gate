/* global fetch */

import './trigger.js';
import GBlock from './g-block.js';
import navigate from './navigate.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

window.addEventListener("_self", function (event)
{
	event.preventDefault();

	GBlock.show("...");
	if (event.detail.method === "get")
		window.location = event.detail.action;
	else
		fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
			.then(ResponseHandler.text)
			.then(html => document.documentElement.outerHTML = html)
			.catch(GMessageDialog.error)
			.finally(() => GBlock.hide());
});

window.addEventListener("_top", function (event)
{
	event.preventDefault();

	GBlock.show("...");
	if (event.detail.method === "get")
		window.top.location = event.detail.action;
	else
		fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
			.then(ResponseHandler.text)
			.then(html => window.top.document.documentElement.outerHTML = html)
			.catch(GMessageDialog.error)
			.finally(() => GBlock.hide());
});

window.addEventListener("_parent", function (event)
{
	event.preventDefault();

	GBlock.show("...");
	if (event.detail.method === "get")
		window.parent.location = event.detail.action;
	else
		fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
			.then(ResponseHandler.text)
			.then(html => window.parent.document.documentElement.outerHTML = html)
			.catch(GMessageDialog.error)
			.finally(() => GBlock.hide());
});

window.addEventListener("_blank", function (event)
{
	event.preventDefault();

	let target = window.open();

	if (event.detail.method === "get")
		target.location = event.detail.action;
	else
		fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
			.then(ResponseHandler.text)
			.then(html => target.document.documentElement.outerHTML = html)
			.catch(GMessageDialog.error);
});

window.addEventListener("@outerHTML", function (event)
{
	event.preventDefault();
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let target = navigate(source, path).orElseThrow("Invalid target element");

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(html => target.outerHTML = html)
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());

});

window.addEventListener("@innerHTML", function (event)
{
	event.preventDefault();
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let target = navigate(source, path).orElseThrow("Invalid target element");

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(html => target.innerHTML = html)
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());
});

window.addEventListener("@beforebegin", function (event)
{
	event.preventDefault();
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let target = navigate(source, path).orElseThrow("Invalid target element");

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(html => target.insertAdjacentHTML("beforebegin", html))
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());
});

window.addEventListener("@beforeend", function (event)
{
	event.preventDefault();
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let target = navigate(source, path).orElseThrow("Invalid target element");

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(html => target.insertAdjacentHTML("beforeend", html))
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());
});

window.addEventListener("@afterbegin", function (event)
{
	event.preventDefault();
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let target = navigate(source, path).orElseThrow("Invalid target element");

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(html => target.insertAdjacentHTML("afterbegin", html))
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());
});

window.addEventListener("@afterend", function (event)
{
	event.preventDefault();
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let target = navigate(source, path).orElseThrow("Invalid target element");

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(html => target.insertAdjacentHTML("afterend", html))
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());
});

window.addEventListener("@remove", function (event)
{
	event.preventDefault();
	let path = event.detail.parameters[0];
	let source = event.composedPath()[0] || event.target;
	let target = navigate(source, path).orElseThrow("Invalid target element");

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(() => target.remove())
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());
});

window.addEventListener("@none", function (event)
{
	event.preventDefault();

	GBlock.show("...");
	fetch(RequestBuilder.build(event.detail.action, event.detail.method, event.detail.form))
		.then(ResponseHandler.text)
		.then(() => undefined)
		.catch(GMessageDialog.error)
		.finally(() => GBlock.hide());
});