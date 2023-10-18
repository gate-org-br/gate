export function getAllTextNodes(fragment)
{
	fragment.normalize();

	if (fragment.nodeType === Node.TEXT_NODE)
		return [fragment];

	return Array.from(fragment.childNodes).flatMap(e => {
		if (e.nodeType === Node.TEXT_NODE)
			return [e];
		else
			return getAllTextNodes(e);
	});
}

export function getAllTextStyles(fragment)
{
	return getAllTextNodes(fragment).map(text => {
		if (text.parentNode.tagName === "SPAN")
			return text.parentNode;
		let style = document.createElement("span");
		text.replaceWith(style);
		style.appendChild(text);
		return style;
	});
}

export function equals(element1, element2)
{
	if (!element1 || !element2)
		return false;
	if (element1.tagName !== element2.tagName)
		return false;

	const style1 = element1.getAttribute("style");
	const style2 = element2.getAttribute("style");
	const sortedStyle1 = style1.split(";").map(style => style.trim()).filter(Boolean).sort().join(";");
	const sortedStyle2 = style2.split(";").map(style => style.trim()).filter(Boolean).sort().join(";");
	return sortedStyle1 === sortedStyle2;
}

export function mergeStyles(editor)
{
	let elements = Array.from(editor.querySelectorAll("*"));
	elements.filter(e => e.tagName === "SPAN")
		.filter(e => equals(e, e.nextElementSibling))
		.forEach(e =>
		{
			e.childNodes.forEach(node => e.nextElementSibling.appendChild(node));
			e.remove();
		});
}

export function removeEmptyElements(editor)
{
	let elements = Array.from(editor.querySelectorAll("*"));
	let empty = elements.filter(e => e.innerText.trim() === "");
	if (empty.length)
	{
		empty.forEach(e => e.remove());
		removeEmptyElements(editor);
	}
}

export function compact(editor)
{
	mergeStyles(editor);
	removeEmptyElements(editor);
	editor.normalize();
}

export function insertIcon(editor, code)
{
	let icon = document.createElement("i");
	icon.innerHTML = `<i>&#X${code}</i>`;
	insert(editor, icon);
}

export function getRange()
{
	let selection = window.getSelection();
	if (selection.rangeCount)
	{
		let range = selection.getRangeAt(0);
		if (range.commonAncestorContainer.tagName === "SPAN"
			&& range.commonAncestorContainer.childNodes.length === 1)
			range.selectNode(range.commonAncestorContainer);
		else if (range.commonAncestorContainer.nodeType === Node.TEXT_NODE
			&& range.commonAncestorContainer.parentNode.tagName === "SPAN"
			&& range.commonAncestorContainer.parentNode.childNodes.length === 1)
			range.selectNode(range.commonAncestorContainer.parentNode);
		return range;
	}
}

export function removeSytles(editor)
{
	let range = getRange();
	if (range && editor.contains(range.commonAncestorContainer))
	{
		let fragment = range.extractContents();
		let styles = getAllTextStyles(fragment);
		range.insertNode(fragment);
		styles.forEach(e => e.replaceWith(...e.childNodes));
	}

	compact(editor);
}

export function insert(editor, element)
{
	let range = getRange();
	if (range && editor.contains(range.commonAncestorContainer))
	{
		let fragment = range.extractContents();
		fragment.prepend(element);
		range.insertNode(fragment);
	}

	compact(editor);
}

export function apply(editor, style, value)
{
	let range = getRange();
	if (range && editor.contains(range.commonAncestorContainer))
	{
		let fragment = range.extractContents();
		let styles = getAllTextStyles(fragment);
		range.insertNode(fragment);
		styles.forEach(e => e.style[style] = value);
	}

	compact(editor);
}

export function toggle(editor, style, apply, clear)
{
	let range = getRange();
	if (range && editor.contains(range.commonAncestorContainer))
	{
		let fragment = range.extractContents();
		let styles = getAllTextStyles(fragment);
		range.insertNode(fragment);

		if (styles.every(e => window.getComputedStyle(e)[style] === apply))
			styles.forEach(e => e.style[style] = clear);
		else
			styles.forEach(e => e.style[style] = apply);
	}

	compact(editor);
}