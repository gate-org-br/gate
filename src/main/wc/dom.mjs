import Parser from './parser.js';
import Optional from './optional.js';

let REGISTRY = [];
new MutationObserver(mutations => mutations
		.flatMap(e => Array.from(e.addedNodes))
		.forEach(root => REGISTRY.forEach(e => DOM.traverse(root, e.filter, e.method))))
	.observe(document, {childList: true, subtree: true});


/**
 * Utility class for DOM manipulation and traversal.
 *
 * @class
 */
export default class DOM
{
	/**
	 * Traverse the DOM starting from the specified root element, applying a filter
	 * and invoking a consumer function on elements that pass the filter. This function
	 * also recursively traverses shadow roots.
	 *
	 * @param {Element} root - The root element from which to start the traversal.
	 * @param {function} filter - A function that determines whether an element should be processed.
	 * @param {function} consumer - A function to be invoked on elements that pass the filter.
	 */
	static traverse(root, filter, consumer)
	{
		if (root.querySelectorAll)
		{
			Array.from(root.querySelectorAll("*")).forEach(element =>
			{
				if (filter(element))
					consumer(element);
				if (element.shadowRoot)
					DOM.traverse(element.shadowRoot, filter, consumer);
			});
		} else if (filter(root))
			consumer(root);
	}

	/**
	 * Navigates the DOM based on the provided selector or numeric count.
	 *
	 * @param {Element} source - The source element to start navigation.
	 * @param {string} selector - The selector or numeric count to determine the navigation steps.
	 * @returns {Element|Optional} - The resulting element or an Optional containing the element.
	 */
	static parent(source, selector)
	{
		if (selector.match(/^\d+$/))
		{
			selector = Number(selector);
			for (let i = 0; source && i <= selector; i++)
				source = source.parentNode;
			return source;
		} else if (selector && selector.length)
		{
			for (source = source.parentNode; source; source = source.parentNode)
				if (!source.matches)
					return null;
				else if (source.matches(selector))
					return source;
		} else
			return source.parentNode;
	}

	/**
	 * Moves to the previous sibling element based on the provided selector or numeric count.
	 *
	 * @param {Element} source - The source element to start navigation.
	 * @param {string} selector - The selector or numeric count to determine the navigation steps.
	 * @returns {Element|Optional} - The resulting element or an Optional containing the element.
	 */
	static prev(source, selector)
	{
		if (selector.match(/^\d+$/))
		{
			selector = Number(selector);
			for (let i = 0; i <= selector; i++)
				source = source.previousElementSibling;
		} else if (selector.length)
		{
			do
			{
				source = source.previousElementSibling;
			} while (!source.matches(selector));
		} else
			source = source.previousElementSibling;
		return source;
	}

	/**
	 * Moves to the next sibling element based on the provided selector or numeric count.
	 *
	 * @param {Element} source - The source element to start navigation.
	 * @param {string} selector - The selector or numeric count to determine the navigation steps.
	 * @returns {Element|Optional} - The resulting element or an Optional containing the element.
	 */
	static next(source, selector)
	{
		if (selector.match(/^\d+$/))
		{
			selector = Number(selector);
			for (let i = 0; i <= selector; i++)
				source = source.nextElementSibling;
		} else if (selector.length)
		{
			do
			{
				source = source.nextElementSibling;
			} while (!source.matches(selector));
		} else
			source = source.nextElementSibling;
		return source;
	}

	/**
	 * Navigates the DOM based on the provided path string.
	 *
	 * @param {Element|Event} source - The source element to start navigation. If an Event is provided, the first element in its composed path is used.
	 * @param {string} path - The path string representing the navigation steps.
	 * @returns {Optional} - An Optional containing the resulting element or empty if the source is not provided or if the path is null or blank.
	 *
	 * @description
	 * If the path does not start with "this", document.querySelector is used.
	 * If the path starts with "this", the navigation begins from the specified source element.
	 *
	 * Navigation Steps:
	 * - "this": Skips the current step and continues to the next one.
	 * - "parent(number)": Navigates to the nth parent of the element.
	 * - "parent(selector)": Navigates to the first parent of the element that matches the selector.
	 * - "prev(number)": Navigates to the nth previous sibling of the element.
	 * - "prev(selector)": Navigates to the first previous sibling of the element that matches the selector.
	 * - "next(number)": Navigates to the nth next sibling of the element.
	 * - "next(selector)": Navigates to the first next sibling of the element that matches the selector.
	 * - "[+n]": Navigates to the nth child element of the current source element.
	 * - "[-n]": Navigates to the nth child element of the current source element in reverse order.
	 * - "selector": Navigates to the first child element that matches the provided selector.
	 *
	 * Steps can be separated by "." or by "[]" and can be quoted with double or single quotes.
	 */
	static navigate(source, path)
	{
		if (!source)
			return Optional.empty();
		if (source instanceof Event)
			source = source.composedPath()[0] || source.target;
		path = (path || "").trim();
		if (!path || path === "this")
			return new Optional(source);

		if (!path.startsWith("this"))
			return new Optional(source.getRootNode().querySelector(path) || document.querySelector(path));

		for (let step of Parser.path(path.substring(4)))
		{
			if (!source)
				return new Optional(source);
			else if (step === "this")
				continue;
			else if (step.startsWith("parent('") && step.endsWith("')"))
				source = DOM.parent(source, step.slice(8, -2).trim());
			else if (step.startsWith('parent("') && step.endsWith('")'))
				source = DOM.parent(source, step.slice(8, -2).trim());
			else if (step.startsWith("parent(") && step.endsWith(')'))
				source = DOM.parent(source, step.slice(7, -1).trim());
			else if (step.startsWith("prev('") && step.endsWith("')"))
				source = DOM.prev(source, step.slice(6, -2).trim());
			else if (step.startsWith('prev("') && step.endsWith('")'))
				source = DOM.prev(source, step.slice(6, -2).trim());
			else if (step.startsWith("prev(") && step.endsWith(')'))
				source = DOM.prev(source, step.slice(5, -1).trim());
			else if (step.startsWith("next('") && step.endsWith("')"))
				source = DOM.next(source, step.slice(6, -2).trim());
			else if (step.startsWith('next("') && step.endsWith('")'))
				source = DOM.next(source, step.slice(6, -2).trim());
			else if (step.startsWith("next(") && step.endsWith(')'))
				source = DOM.next(source, step.slice(5, -1).trim());
			else if (step.startsWith("search(") && step.endsWith(')'))
				source = source.querySelector(step.slice(7, -1).trim());
			else if (/^\+?\d+$/.test(step))
				source = source.children[Number(step)];
			else if (/^-\d+$/.test(step))
				source = source.children[source.children.length + Number(step)];
			else
				source = Array.from(source.children).filter(e => e.tagName === step.toUpperCase())[0];
		}

		return Optional.of(source);
	}

	static forEveryElement(filter, method)
	{
		REGISTRY.push({filter, method});
		DOM.traverse(document, filter, method);
	}
}
