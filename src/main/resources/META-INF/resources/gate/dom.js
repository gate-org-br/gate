import Optional from './optional.js';
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
		Array.from(root.querySelectorAll("*")).forEach(element =>
		{
			if (filter(element))
				consumer(element);
			if (element.shadowRoot)
				DOM.traverse(element.shadowRoot, filter, consumer);
		});
	}

	/**
	 * Navigate through the DOM based on a specified path.
	 *
	 * @param {Element|Event} source - The source element or event from which to start the navigation.
	 * @param {string} path - The path specifying the navigation steps.
	 *
	 * @returns {Optional} An Optional object wrapping the result of the navigation.
	 * If the result is present, it contains the navigated element; otherwise, it is empty.
	 */
	static navigate(source, path)
	{
		if (!source)
			return Optional.empty();
		if (source instanceof Event)
			source = source.composedPath()[0] || source.target;
		path = (path || "").trim();
		if (!path)
			return new Optional(source);
		if (!path.startsWith("this"))
			return new Optional(source.getRootNode().querySelector(path)
				|| document.querySelector(path));
		for (let step of path.split(/\.(?=(?:[^"']*["'][^"']*["'])*[^"']*$)/g))
		{
			if (!source)
				return new Optional(source);
			else if (step === "parentNode")
				source = source.parentNode;
			else if (step === "nextElementSibling")
				source = source.nextElementSibling;
			else if (step === "previousElementSibling")
				source = source.previousElementSibling;
			else if (step === "firstElementChild")
				source = source.firstElementChild;
			else if (step === "lastElementChild")
				source = source.lastElementChild;
			else if (step.startsWith("closest(")
				&& step.endsWith(")"))
				source = source.closest(step.slice(8, -1));
			else if (step.startsWith("querySelector(")
				&& step.endsWith(")"))
				source = source.querySelector(step.slice(14, -1));
			else if (step.startsWith("children[")
				&& step.endsWith("]"))
				source = source.children[Number(step.slice(9, -1))];
			else if (step !== "this")
				throw new Error(`${path} is not a valid path`);
		}

		return Optional.of(source);
	}
}
