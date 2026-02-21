import MutationListener from './mutation-listener.js';

const connectedElements = new WeakSet();

const listener = new MutationListener(dispatcher, {
	childList: true,
	subtree: true,
	attributes: true,
	attributeOldValue: true
});

function connect(root)
{
	if (connectedElements.has(root))
		return;

	connectedElements.add(root);

	if (root.nodeType === Node.ELEMENT_NODE)
		root.dispatchEvent(new CustomEvent("connected", { bubbles: true, composed: true }));

	if (root.children)
		Array.from(root.children).forEach(connect);

	if (root.shadowRoot)
	{
		Array.from(root.shadowRoot.children).forEach(connect);
		listener.listen(root.shadowRoot);
	}
}

function disconnect(root)
{
	if (!connectedElements.has(root))
		return;

	connectedElements.delete(root);

	if (root.nodeType === Node.ELEMENT_NODE)
		root.dispatchEvent(new CustomEvent("disconnected", { bubbles: true, composed: true }));

	if (root.children)
		Array.from(root.children).forEach(disconnect);

	if (root.shadowRoot)
		Array.from(root.shadowRoot.children).forEach(disconnect);
}

function dispatcher(mutations)
{
	mutations.forEach(mutation =>
	{
		if (mutation.type === "childList")
		{
			let added = mutation.addedNodes;

			if (added.length === 0 && mutation.target.children.length > 0)
				added = Array.from(mutation.target.children);

			Array.from(added).forEach(connect);
			Array.from(mutation.removedNodes).forEach(disconnect);
		}
		else if (mutation.type === "attributes")
		{
			const { target, attributeName: attribute, oldValue } = mutation;

			if (!target.hasAttribute(attribute))
				target.dispatchEvent(new CustomEvent("attribute-removed", {
					bubbles: true,
					composed: true,
					detail: { attribute }
				}));
			else if (oldValue === null)
				target.dispatchEvent(new CustomEvent("attribute-created", {
					bubbles: true,
					composed: true,
					detail: { attribute }
				}));
			else
				target.dispatchEvent(new CustomEvent("attribute-changed", {
					bubbles: true,
					composed: true,
					detail: { attribute, oldValue }
				}));
		}
	});
}

listener.listen(document);
connect(document);