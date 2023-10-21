function getAllTextNodes(fragment)
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

function getAllTextStyles(fragment)
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

export default class GTextSelection
{
	constructor(editor, range)
	{
		this._private = {editor, range};
	}

	updateStyle(style, value)
	{
		if (this.range && value)
		{
			let fragment = this.range.extractContents();
			let styles = getAllTextStyles(fragment);
			styles.forEach(e => e.style[style] = value);
			this.range.insertNode(fragment);
			this.editor.compact();
		}

		return this;
	}

	toggleStyle(style, apply, clear)
	{
		if (this.range && apply && clear)
		{
			let fragment = this.range.extractContents();
			let styles = getAllTextStyles(fragment);
			this.range.insertNode(fragment);
			if (styles.every(e => window.getComputedStyle(e)[style] === apply))
				styles.forEach(e => e.style[style] = clear);
			else
				styles.forEach(e => e.style[style] = apply);
			this.editor.compact();
		}

		return this;
	}

	clearStyles()
	{
		if (this.range)
		{
			let fragment = this.range.extractContents();
			let styles = getAllTextStyles(fragment);
			this.range.insertNode(fragment);
			styles.forEach(e => e.replaceWith(...e.childNodes));
			this.editor.compact();
		}

		return this;
	}

	updateClass(value)
	{
		if (this.range && value)
		{
			let fragment = this.range.extractContents();
			let styles = getAllTextStyles(fragment);
			styles.forEach(e => e.className = value || "");
			this.range.insertNode(fragment);
			this.editor.compact();
		}

		return this;
	}

	appendText(value)
	{
		if (this.range && value)
		{
			let fragment = this.range.extractContents();
			fragment.append(document.createTextNode(value));
			this.range.insertNode(fragment);
			this.editor.compact();
		}

		return this;
	}

	get range()
	{
		return this._private.range;
	}

	get editor()
	{
		return this._private.editor;
	}
}


