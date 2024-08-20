import DOM from './dom.js';

const shadowRootObservers = new WeakMap();

function dispatcher(mutations)
{
	mutations.forEach(mutation => {
		if (mutation.type === "childList")
		{
			Array.from(mutation.addedNodes).forEach(root => {
				DOM.traverse(root, target => target.nodeType === Node.ELEMENT_NODE, target => {
					target.dispatchEvent(new CustomEvent("connected", {bubbles: true, composed: true}));

					if (target.shadowRoot && !shadowRootObservers.has(target.shadowRoot))
					{
						const observer = new MutationObserver(dispatcher);
						shadowRootObservers.set(target.shadowRoot, observer);
						observer.observe(target.shadowRoot, {childList: true, subtree: true, attributes: true});
					}
				});
			});

			Array.from(mutation.removedNodes).forEach(root => {
				DOM.traverse(root, target => target.nodeType === Node.ELEMENT_NODE, target => {
					target.dispatchEvent(new CustomEvent("disconnected", {bubbles: true, composed: true}));

					if (target.shadowRoot && shadowRootObservers.has(target.shadowRoot))
					{
						const observer = shadowRootObservers.get(target.shadowRoot);
						observer.disconnect();
						shadowRootObservers.delete(target.shadowRoot);
					}
				});
			});
		} else if (mutation.type === "attributes")
		{
			const {target, attributeName: attribute, oldValue} = mutation;
			if (!target.hasAttribute(attribute))
				target.dispatchEvent(new CustomEvent("attribute-removed", {bubbles: true, composed: true, detail: {attribute}}));
			else if (oldValue === null)
				target.dispatchEvent(new CustomEvent("attribute-created", {bubbles: true, composed: true, detail: {attribute}}));
			else
				target.dispatchEvent(new CustomEvent("attribute-changed", {bubbles: true, composed: true, detail: {attribute, oldValue}}));
		}
	});
}

window.addEventListener("load", () => {
	DOM.traverse(document, node => node.nodeType === Node.ELEMENT_NODE, node => {
		node.dispatchEvent(new CustomEvent("connected", {bubbles: true, composed: true}));

		if (node.shadowRoot && !shadowRootObservers.has(node.shadowRoot))
		{
			const observer = new MutationObserver(dispatcher);
			shadowRootObservers.set(node.shadowRoot, observer);
			observer.observe(node.shadowRoot, {childList: true, subtree: true, attributes: true});
		}
	});
});

new MutationObserver(dispatcher).observe(document, {childList: true, subtree: true, attributes: true});
