(function () {

	// nb. This is for IE10 and lower _only_.
	var supportCustomEvent = window.CustomEvent;
	if (!supportCustomEvent || typeof supportCustomEvent === 'object') {
		supportCustomEvent = function CustomEvent(event, x) {
			x = x || {};
			var ev = document.createEvent('CustomEvent');
			ev.initCustomEvent(event, !!x.bubbles, !!x.cancelable, x.detail || null);
			return ev;
		};
		supportCustomEvent.prototype = window.Event.prototype;
	}

	/**
	 * @param {Element} el to check for stacking context
	 * @return {boolean} whether this el or its parents creates a stacking context
	 */
	function createsStackingContext(el) {
		while (el && el !== document.body) {
			var s = window.getComputedStyle(el);
			var invalid = function (k, ok) {
				return !(s[k] === undefined || s[k] === ok);
			}
			if (s.opacity < 1 ||
				invalid('zIndex', 'auto') ||
				invalid('transform', 'none') ||
				invalid('mixBlendMode', 'normal') ||
				invalid('filter', 'none') ||
				invalid('perspective', 'none') ||
				s['isolation'] === 'isolate' ||
				s.position === 'fixed' ||
				s.webkitOverflowScrolling === 'touch') {
				return true;
			}
			el = el.parentElement;
		}
		return false;
	}

	/**
	 * Finds the nearest <dialog> from the passed element.
	 *
	 * @param {Element} el to search from
	 * @return {HTMLDialogElement} dialog found
	 */
	function findNearestDialog(el) {
		while (el) {
			if (el.localName === 'dialog') {
				return /** @type {HTMLDialogElement} */ (el);
			}
			el = el.parentElement;
		}
		return null;
	}

	/**
	 * Blur the specified element, as long as it's not the HTML body element.
	 * This works around an IE9/10 bug - blurring the body causes Windows to
	 * blur the whole application.
	 *
	 * @param {Element} el to blur
	 */
	function safeBlur(el) {
		if (el && el.blur && el !== document.body) {
			el.blur();
		}
	}

	/**
	 * @param {!NodeList} nodeList to search
	 * @param {Node} node to find
	 * @return {boolean} whether node is inside nodeList
	 */
	function inNodeList(nodeList, node) {
		for (var i = 0; i < nodeList.length; ++i) {
			if (nodeList[i] === node) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param {HTMLFormElement} el to check
	 * @return {boolean} whether this form has method="dialog"
	 */
	function isFormMethodDialog(el) {
		if (!el || !el.hasAttribute('method')) {
			return false;
		}
		return el.getAttribute('method').toLowerCase() === 'dialog';
	}

	/**
	 * @param {!HTMLDialogElement} dialog to upgrade
	 * @constructor
	 */
	function dialogPolyfillInfo(dialog) {
		this.dialog_ = dialog;
		this.replacedStyleTop_ = false;
		this.openAsModal_ = false;

		// Set a11y role. Browsers that support dialog implicitly know this already.
		if (!dialog.hasAttribute('role')) {
			dialog.setAttribute('role', 'dialog');
		}

		dialog.show = this.show.bind(this);
		dialog.showModal = this.showModal.bind(this);
		dialog.close = this.close.bind(this);

		if (!('returnValue' in dialog)) {
			dialog.returnValue = '';
		}

		if ('MutationObserver' in window) {
			var mo = new MutationObserver(this.maybeHideModal.bind(this));
			mo.observe(dialog, {attributes: true, attributeFilter: ['open']});
		} else {
			// IE10 and below support. Note that DOMNodeRemoved etc fire _before_ removal. They also
			// seem to fire even if the element was removed as part of a parent removal. Use the removed
			// events to force downgrade (useful if removed/immediately added).
			var removed = false;
			var cb = function () {
				removed ? this.downgradeModal() : this.maybeHideModal();
				removed = false;
			}.bind(this);
			var timeout;
			var delayModel = function (ev) {
				if (ev.target !== dialog) {
					return;
				}  // not for a child element
				var cand = 'DOMNodeRemoved';
				removed |= (ev.type.substr(0, cand.length) === cand);
				window.clearTimeout(timeout);
				timeout = window.setTimeout(cb, 0);
			};
			['DOMAttrModified', 'DOMNodeRemoved', 'DOMNodeRemovedFromDocument'].forEach(function (name) {
				dialog.addEventListener(name, delayModel);
			});
		}
		// Note that the DOM is observed inside DialogManager while any dialog
		// is being displayed as a modal, to catch modal removal from the DOM.

		Object.defineProperty(dialog, 'open', {
			set: this.setOpen.bind(this),
			get: dialog.hasAttribute.bind(dialog, 'open')
		});

		this.backdrop_ = document.createElement('div');
		this.backdrop_.className = 'backdrop';
		this.backdrop_.addEventListener('click', this.backdropClick_.bind(this));
	}

	dialogPolyfillInfo.prototype = {

		get dialog() {
			return this.dialog_;
		},

		/**
		 * Maybe remove this dialog from the modal top layer. This is called when
		 * a modal dialog may no longer be tenable, e.g., when the dialog is no
		 * longer open or is no longer part of the DOM.
		 */
		maybeHideModal: function () {
			if (this.dialog_.hasAttribute('open') && document.body.contains(this.dialog_)) {
				return;
			}
			this.downgradeModal();
		},

		/**
		 * Remove this dialog from the modal top layer, leaving it as a non-modal.
		 */
		downgradeModal: function () {
			if (!this.openAsModal_) {
				return;
			}
			this.openAsModal_ = false;
			this.dialog_.style.zIndex = '';

			// This won't match the native <dialog> exactly because if the user set top on a centered
			// polyfill dialog, that top gets thrown away when the dialog is closed. Not sure it's
			// possible to polyfill this perfectly.
			if (this.replacedStyleTop_) {
				this.dialog_.style.top = '';
				this.replacedStyleTop_ = false;
			}

			// Clear the backdrop and remove from the manager.
			this.backdrop_.parentNode && this.backdrop_.parentNode.removeChild(this.backdrop_);
			dialogPolyfill.dm.removeDialog(this);
		},

		/**
		 * @param {boolean} value whether to open or close this dialog
		 */
		setOpen: function (value) {
			if (value) {
				this.dialog_.hasAttribute('open') || this.dialog_.setAttribute('open', '');
			} else {
				this.dialog_.removeAttribute('open');
				this.maybeHideModal();  // nb. redundant with MutationObserver
			}
		},

		/**
		 * Handles clicks on the fake .backdrop element, redirecting them as if
		 * they were on the dialog itself.
		 *
		 * @param {!Event} e to redirect
		 */
		backdropClick_: function (e) {
			if (!this.dialog_.hasAttribute('tabindex')) {
				// Clicking on the backdrop should move the implicit cursor, even if dialog cannot be
				// focused. Create a fake thing to focus on. If the backdrop was _before_ the dialog, this
				// would not be needed - clicks would move the implicit cursor there.
				var fake = document.createElement('div');
				this.dialog_.insertBefore(fake, this.dialog_.firstChild);
				fake.tabIndex = -1;
				fake.focus();
				this.dialog_.removeChild(fake);
			} else {
				this.dialog_.focus();
			}

			var redirectedEvent = document.createEvent('MouseEvents');
			redirectedEvent.initMouseEvent(e.type, e.bubbles, e.cancelable, window,
				e.detail, e.screenX, e.screenY, e.clientX, e.clientY, e.ctrlKey,
				e.altKey, e.shiftKey, e.metaKey, e.button, e.relatedTarget);
			this.dialog_.dispatchEvent(redirectedEvent);
			e.stopPropagation();
		},

		/**
		 * Focuses on the first focusable element within the dialog. This will always blur the current
		 * focus, even if nothing within the dialog is found.
		 */
		focus_: function () {
			// Find element with `autofocus` attribute, or fall back to the first form/tabindex control.
			var target = this.dialog_.querySelector('[autofocus]:not([disabled])');
			if (!target && this.dialog_.tabIndex >= 0) {
				target = this.dialog_;
			}
			if (!target) {
				// Note that this is 'any focusable area'. This list is probably not exhaustive, but the
				// alternative involves stepping through and trying to focus everything.
				var opts = ['button', 'input', 'keygen', 'select', 'textarea'];
				var query = opts.map(function (el) {
					return el + ':not([disabled])';
				});
				// TODO(samthor): tabindex values that are not numeric are not focusable.
				query.push('[tabindex]:not([disabled]):not([tabindex=""])');  // tabindex != "", not disabled
				target = this.dialog_.querySelector(query.join(', '));
			}
			safeBlur(document.activeElement);
			target && target.focus();
		},

		/**
		 * Sets the zIndex for the backdrop and dialog.
		 *
		 * @param {number} dialogZ
		 * @param {number} backdropZ
		 */
		updateZIndex: function (dialogZ, backdropZ) {
			if (dialogZ < backdropZ) {
				throw new Error('dialogZ should never be < backdropZ');
			}
			this.dialog_.style.zIndex = dialogZ;
			this.backdrop_.style.zIndex = backdropZ;
		},

		/**
		 * Shows the dialog. If the dialog is already open, this does nothing.
		 */
		show: function () {
			if (!this.dialog_.open) {
				this.setOpen(true);
				this.focus_();
			}
		},

		/**
		 * Show this dialog modally.
		 */
		showModal: function () {
			if (this.dialog_.hasAttribute('open')) {
				throw new Error('Failed to execute \'showModal\' on dialog: The element is already open, and therefore cannot be opened modally.');
			}
			if (!document.body.contains(this.dialog_)) {
				throw new Error('Failed to execute \'showModal\' on dialog: The element is not in a Document.');
			}
			if (!dialogPolyfill.dm.pushDialog(this)) {
				throw new Error('Failed to execute \'showModal\' on dialog: There are too many open modal dialogs.');
			}

			if (createsStackingContext(this.dialog_.parentElement)) {
				console.warn('A dialog is being shown inside a stacking context. ' +
					'This may cause it to be unusable. For more information, see this link: ' +
					'https://github.com/GoogleChrome/dialog-polyfill/#stacking-context');
			}

			this.setOpen(true);
			this.openAsModal_ = true;

			// Optionally center vertically, relative to the current viewport.
			if (dialogPolyfill.needsCentering(this.dialog_)) {
				dialogPolyfill.reposition(this.dialog_);
				this.replacedStyleTop_ = true;
			} else {
				this.replacedStyleTop_ = false;
			}

			// Insert backdrop.
			this.dialog_.parentNode.insertBefore(this.backdrop_, this.dialog_.nextSibling);

			// Focus on whatever inside the dialog.
			this.focus_();
		},

		/**
		 * Closes this HTMLDialogElement. This is optional vs clearing the open
		 * attribute, however this fires a 'close' event.
		 *
		 * @param {string=} opt_returnValue to use as the returnValue
		 */
		close: function (opt_returnValue) {
			if (!this.dialog_.hasAttribute('open')) {
				throw new Error('Failed to execute \'close\' on dialog: The element does not have an \'open\' attribute, and therefore cannot be closed.');
			}
			this.setOpen(false);

			// Leave returnValue untouched in case it was set directly on the element
			if (opt_returnValue !== undefined) {
				this.dialog_.returnValue = opt_returnValue;
			}

			// Triggering "close" event for any attached listeners on the <dialog>.
			var closeEvent = new supportCustomEvent('close', {
				bubbles: false,
				cancelable: false
			});
			this.dialog_.dispatchEvent(closeEvent);
		}

	};

	var dialogPolyfill = {};

	dialogPolyfill.reposition = function (element) {
		var scrollTop = document.body.scrollTop || document.documentElement.scrollTop;
		var topValue = scrollTop + (window.innerHeight - element.offsetHeight) / 2;
		element.style.top = Math.max(scrollTop, topValue) + 'px';
	};

	dialogPolyfill.isInlinePositionSetByStylesheet = function (element) {
		for (var i = 0; i < document.styleSheets.length; ++i) {
			var styleSheet = document.styleSheets[i];
			var cssRules = null;
			// Some browsers throw on cssRules.
			try {
				cssRules = styleSheet.cssRules;
			} catch (e) {
			}
			if (!cssRules) {
				continue;
			}
			for (var j = 0; j < cssRules.length; ++j) {
				var rule = cssRules[j];
				var selectedNodes = null;
				// Ignore errors on invalid selector texts.
				try {
					selectedNodes = document.querySelectorAll(rule.selectorText);
				} catch (e) {
				}
				if (!selectedNodes || !inNodeList(selectedNodes, element)) {
					continue;
				}
				var cssTop = rule.style.getPropertyValue('top');
				var cssBottom = rule.style.getPropertyValue('bottom');
				if ((cssTop && cssTop !== 'auto') || (cssBottom && cssBottom !== 'auto')) {
					return true;
				}
			}
		}
		return false;
	};

	dialogPolyfill.needsCentering = function (dialog) {
		var computedStyle = window.getComputedStyle(dialog);
		if (computedStyle.position !== 'absolute') {
			return false;
		}

		// We must determine whether the top/bottom specified value is non-auto.  In
		// WebKit/Blink, checking computedStyle.top == 'auto' is sufficient, but
		// Firefox returns the used value. So we do this crazy thing instead: check
		// the inline style and then go through CSS rules.
		if ((dialog.style.top !== 'auto' && dialog.style.top !== '') ||
			(dialog.style.bottom !== 'auto' && dialog.style.bottom !== '')) {
			return false;
		}
		return !dialogPolyfill.isInlinePositionSetByStylesheet(dialog);
	};

	/**
	 * @param {!Element} element to force upgrade
	 */
	dialogPolyfill.forceRegisterDialog = function (element) {
		if (window.HTMLDialogElement || element.showModal) {
			console.warn('This browser already supports <dialog>, the polyfill ' +
				'may not work correctly', element);
		}
		if (element.localName !== 'dialog') {
			throw new Error('Failed to register dialog: The element is not a dialog.');
		}
		new dialogPolyfillInfo(/** @type {!HTMLDialogElement} */ (element));
	};

	/**
	 * @param {!Element} element to upgrade, if necessary
	 */
	dialogPolyfill.registerDialog = function (element) {
		if (!element.showModal) {
			dialogPolyfill.forceRegisterDialog(element);
		}
	};

	/**
	 * @constructor
	 */
	dialogPolyfill.DialogManager = function () {
		/** @type {!Array<!dialogPolyfillInfo>} */
		this.pendingDialogStack = [];

		var checkDOM = this.checkDOM_.bind(this);

		// The overlay is used to simulate how a modal dialog blocks the document.
		// The blocking dialog is positioned on top of the overlay, and the rest of
		// the dialogs on the pending dialog stack are positioned below it. In the
		// actual implementation, the modal dialog stacking is controlled by the
		// top layer, where z-index has no effect.
		this.overlay = document.createElement('div');
		this.overlay.className = '_dialog_overlay';
		this.overlay.addEventListener('click', function (e) {
			this.forwardTab_ = undefined;
			e.stopPropagation();
			checkDOM([]);  // sanity-check DOM
		}.bind(this));

		this.handleKey_ = this.handleKey_.bind(this);
		this.handleFocus_ = this.handleFocus_.bind(this);

		this.zIndexLow_ = 100000;
		this.zIndexHigh_ = 100000 + 150;

		this.forwardTab_ = undefined;

		if ('MutationObserver' in window) {
			this.mo_ = new MutationObserver(function (records) {
				var removed = [];
				records.forEach(function (rec) {
					for (var i = 0, c; (c = rec.removedNodes[i]); ++i) {
						if (!(c instanceof Element)) {
							continue;
						} else if (c.localName === 'dialog') {
							removed.push(c);
						}
						removed = removed.concat(c.querySelectorAll('dialog'));
					}
				});
				removed.length && checkDOM(removed);
			});
		}
	};

	/**
	 * Called on the first modal dialog being shown. Adds the overlay and related
	 * handlers.
	 */
	dialogPolyfill.DialogManager.prototype.blockDocument = function () {
		document.documentElement.addEventListener('focus', this.handleFocus_, true);
		document.addEventListener('keydown', this.handleKey_);
		this.mo_ && this.mo_.observe(document, {childList: true, subtree: true});
	};

	/**
	 * Called on the first modal dialog being removed, i.e., when no more modal
	 * dialogs are visible.
	 */
	dialogPolyfill.DialogManager.prototype.unblockDocument = function () {
		document.documentElement.removeEventListener('focus', this.handleFocus_, true);
		document.removeEventListener('keydown', this.handleKey_);
		this.mo_ && this.mo_.disconnect();
	};

	/**
	 * Updates the stacking of all known dialogs.
	 */
	dialogPolyfill.DialogManager.prototype.updateStacking = function () {
		var zIndex = this.zIndexHigh_;

		for (var i = 0, dpi; (dpi = this.pendingDialogStack[i]); ++i) {
			dpi.updateZIndex(--zIndex, --zIndex);
			if (i === 0) {
				this.overlay.style.zIndex = --zIndex;
			}
		}

		// Make the overlay a sibling of the dialog itself.
		var last = this.pendingDialogStack[0];
		if (last) {
			var p = last.dialog.parentNode || document.body;
			p.appendChild(this.overlay);
		} else if (this.overlay.parentNode) {
			this.overlay.parentNode.removeChild(this.overlay);
		}
	};

	/**
	 * @param {Element} candidate to check if contained or is the top-most modal dialog
	 * @return {boolean} whether candidate is contained in top dialog
	 */
	dialogPolyfill.DialogManager.prototype.containedByTopDialog_ = function (candidate) {
		while ((candidate = findNearestDialog(candidate))) {
			for (var i = 0, dpi; (dpi = this.pendingDialogStack[i]); ++i) {
				if (dpi.dialog === candidate) {
					return i === 0;  // only valid if top-most
				}
			}
			candidate = candidate.parentElement;
		}
		return false;
	};

	dialogPolyfill.DialogManager.prototype.handleFocus_ = function (event) {
		if (this.containedByTopDialog_(event.target)) {
			return;
		}

		if (document.activeElement === document.documentElement) {
			return;
		}

		event.preventDefault();
		event.stopPropagation();
		safeBlur(/** @type {Element} */ (event.target));

		if (this.forwardTab_ === undefined) {
			return;
		}  // move focus only from a tab key

		var dpi = this.pendingDialogStack[0];
		var dialog = dpi.dialog;
		var position = dialog.compareDocumentPosition(event.target);
		if (position & Node.DOCUMENT_POSITION_PRECEDING) {
			if (this.forwardTab_) {
				// forward
				dpi.focus_();
			} else if (event.target !== document.documentElement) {
				// backwards if we're not already focused on <html>
				document.documentElement.focus();
			}
		} else {
			// TODO: Focus after the dialog, is ignored.
		}

		return false;
	};

	dialogPolyfill.DialogManager.prototype.handleKey_ = function (event) {
		this.forwardTab_ = undefined;
		if (event.keyCode === 27) {
			event.preventDefault();
			event.stopPropagation();
			var cancelEvent = new supportCustomEvent('cancel', {
				bubbles: false,
				cancelable: true
			});
			var dpi = this.pendingDialogStack[0];
			if (dpi && dpi.dialog.dispatchEvent(cancelEvent)) {
				dpi.dialog.close();
			}
		} else if (event.keyCode === 9) {
			this.forwardTab_ = !event.shiftKey;
		}
	};

	/**
	 * Finds and downgrades any known modal dialogs that are no longer displayed. Dialogs that are
	 * removed and immediately readded don't stay modal, they become normal.
	 *
	 * @param {!Array<!HTMLDialogElement>} removed that have definitely been removed
	 */
	dialogPolyfill.DialogManager.prototype.checkDOM_ = function (removed) {
		// This operates on a clone because it may cause it to change. Each change also calls
		// updateStacking, which only actually needs to happen once. But who removes many modal dialogs
		// at a time?!
		var clone = this.pendingDialogStack.slice();
		clone.forEach(function (dpi) {
			if (removed.indexOf(dpi.dialog) !== -1) {
				dpi.downgradeModal();
			} else {
				dpi.maybeHideModal();
			}
		});
	};

	/**
	 * @param {!dialogPolyfillInfo} dpi
	 * @return {boolean} whether the dialog was allowed
	 */
	dialogPolyfill.DialogManager.prototype.pushDialog = function (dpi) {
		var allowed = (this.zIndexHigh_ - this.zIndexLow_) / 2 - 1;
		if (this.pendingDialogStack.length >= allowed) {
			return false;
		}
		if (this.pendingDialogStack.unshift(dpi) === 1) {
			this.blockDocument();
		}
		this.updateStacking();
		return true;
	};

	/**
	 * @param {!dialogPolyfillInfo} dpi
	 */
	dialogPolyfill.DialogManager.prototype.removeDialog = function (dpi) {
		var index = this.pendingDialogStack.indexOf(dpi);
		if (index === -1) {
			return;
		}

		this.pendingDialogStack.splice(index, 1);
		if (this.pendingDialogStack.length === 0) {
			this.unblockDocument();
		}
		this.updateStacking();
	};

	dialogPolyfill.dm = new dialogPolyfill.DialogManager();
	dialogPolyfill.formSubmitter = null;
	dialogPolyfill.useValue = null;

	/**
	 * Installs global handlers, such as click listers and native method overrides. These are needed
	 * even if a no dialog is registered, as they deal with <form method="dialog">.
	 */
	if (window.HTMLDialogElement === undefined) {

		/**
		 * If HTMLFormElement translates method="DIALOG" into 'get', then replace the descriptor with
		 * one that returns the correct value.
		 */
		var testForm = document.createElement('form');
		testForm.setAttribute('method', 'dialog');
		if (testForm.method !== 'dialog') {
			var methodDescriptor = Object.getOwnPropertyDescriptor(HTMLFormElement.prototype, 'method');
			if (methodDescriptor) {
				// nb. Some older iOS and older PhantomJS fail to return the descriptor. Don't do anything
				// and don't bother to update the element.
				var realGet = methodDescriptor.get;
				methodDescriptor.get = function () {
					if (isFormMethodDialog(this)) {
						return 'dialog';
					}
					return realGet.call(this);
				};
				var realSet = methodDescriptor.set;
				methodDescriptor.set = function (v) {
					if (typeof v === 'string' && v.toLowerCase() === 'dialog') {
						return this.setAttribute('method', v);
					}
					return realSet.call(this, v);
				};
				Object.defineProperty(HTMLFormElement.prototype, 'method', methodDescriptor);
			}
		}

		/**
		 * Global 'click' handler, to capture the <input type="submit"> or <button> element which has
		 * submitted a <form method="dialog">. Needed as Safari and others don't report this inside
		 * document.activeElement.
		 */
		document.addEventListener('click', function (ev) {
			dialogPolyfill.formSubmitter = null;
			dialogPolyfill.useValue = null;
			if (ev.defaultPrevented) {
				return;
			}  // e.g. a submit which prevents default submission

			var target = /** @type {Element} */ (ev.target);
			if (!target || !isFormMethodDialog(target.form)) {
				return;
			}

			var valid = (target.type === 'submit' && ['button', 'input'].indexOf(target.localName) > -1);
			if (!valid) {
				if (!(target.localName === 'input' && target.type === 'image')) {
					return;
				}
				// this is a <input type="image">, which can submit forms
				dialogPolyfill.useValue = ev.offsetX + ',' + ev.offsetY;
			}

			var dialog = findNearestDialog(target);
			if (!dialog) {
				return;
			}

			dialogPolyfill.formSubmitter = target;
		}, false);

		/**
		 * Replace the native HTMLFormElement.submit() method, as it won't fire the
		 * submit event and give us a chance to respond.
		 */
		var nativeFormSubmit = HTMLFormElement.prototype.submit;
		var replacementFormSubmit = function () {
			if (!isFormMethodDialog(this)) {
				return nativeFormSubmit.call(this);
			}
			var dialog = findNearestDialog(this);
			dialog && dialog.close();
		};
		HTMLFormElement.prototype.submit = replacementFormSubmit;

		/**
		 * Global form 'dialog' method handler. Closes a dialog correctly on submit
		 * and possibly sets its return value.
		 */
		document.addEventListener('submit', function (ev) {
			var form = /** @type {HTMLFormElement} */ (ev.target);
			if (!isFormMethodDialog(form)) {
				return;
			}
			ev.preventDefault();

			var dialog = findNearestDialog(form);
			if (!dialog) {
				return;
			}

			// Forms can only be submitted via .submit() or a click (?), but anyway: sanity-check that
			// the submitter is correct before using its value as .returnValue.
			var s = dialogPolyfill.formSubmitter;
			if (s && s.form === form) {
				dialog.close(dialogPolyfill.useValue || s.value);
			} else {
				dialog.close();
			}
			dialogPolyfill.formSubmitter = null;
		}, true);
	}

	dialogPolyfill['forceRegisterDialog'] = dialogPolyfill.forceRegisterDialog;
	dialogPolyfill['registerDialog'] = dialogPolyfill.registerDialog;

	if (typeof define === 'function' && 'amd' in define) {
		// AMD support
		define(function () {
			return dialogPolyfill;
		});
	} else if (typeof module === 'object' && typeof module['exports'] === 'object') {
		// CommonJS support
		module['exports'] = dialogPolyfill;
	} else {
		// all others
		window['dialogPolyfill'] = dialogPolyfill;
	}

	window.addEventListener("load", function ()
	{
		Array.from(document.getElementsByTagName("dialog")).forEach(function (dialog)
		{
			dialogPolyfill.registerDialog(dialog);
		});
	});
})();
if (!document.querySelectorAll)
	window.location = '../gate/NAVI.jsp';


///////////////////////////////////////////////////////////////////////////////////////////////////
// Gate
///////////////////////////////////////////////////////////////////////////////////////////////////

function select(id)
{
	return document
		.getElementById(id);
}

function search(css)
{
	return Array.from(document
		.querySelectorAll(css));
}

function $(e)
{
	return new Gate(e);
}

function resolve(string)
{
	var result = string;
	var parameters = string.match(/([?][{][^}]*[}])/g);
	if (parameters)
		for (var i = 0; i < parameters.length; i++)
		{
			var parameter = prompt(decodeURIComponent(parameters[i]
				.substring(2, parameters[i].length - 1)));
			if (parameter === null)
				return null;
			result = result.replace(parameters[i], encodeURIComponent(parameter));
		}

	var parameters = string.match(/([@][{][^}]*[}])/g);
	if (parameters)
		parameters.forEach(function (e)
		{
			var parameter = select(e.substring(2, e.length - 1));
			result = result.replace(e, parameter ? encodeURIComponent(parameter.value) : "");
		});

	return result;
}

function Gate(e)
{
	if (typeof e === 'string')
		e = document.querySelectorAll(e);
	if (e instanceof NodeList)
		e = Array.from(e);
	if (e.tagName)
	{
		this.children = function ()
		{
			var result = [e];
			for (var i = 0; i < arguments.length; i++)
				result = children(result, arguments[i]);
			return result;

			function children(array, tagName)
			{
				var result = [];
				for (var i = 0; i < array.length; i++)
					for (var j = 0; j < array[i].childNodes.length; j++)
						if (array[i].childNodes[j].tagName && array[i].childNodes[j].tagName.toLowerCase() === tagName.toLowerCase())
							result.push(array[i].childNodes[j]);
				return result;
			}
		};

		this.siblings = function (selector)
		{
			return selector ? Array.from(e.parentNode.childNodes)
				.filter(function (node)
				{
					return node.matches
						&& node.matches(selector);
				}) : Array.from(e.parentNode.childNodes);
		};
		this.get = function (name)
		{
			return e[name];
		};
		this.set = function (name, value)
		{
			e[name] = value;
		};
		this.def = function (name, value)
		{
			if (!e[name])
				e[name] = value;
		};
		this.addEventListener = function (name, value)
		{
			e.addEventListener(name, value);
		};
		this.getAttribute = function (name)
		{
			return e.getAttribute(name);
		};
		this.setAttribute = function (name, value)
		{
			e.setAttribute(name, value);
		};
		this.getForm = function ()
		{
			var form = e.parentNode;
			while (form
				&& form.tagName.toLowerCase()
				!== 'form')
				form = form.parentNode;
			return form;
		};
		this.getNext = function ()
		{
			var next = e.nextSibling;
			while (next && next.nodeType !== 1)
				next = next.nextSibling;
			return next;
		};
		this.getPrev = function ()
		{
			var prev = e.previousSibling;
			while (prev && prev.nodeType !== 1)
				prev = prev.previousSibling;
			return prev;
		};
		this.search = function (css)
		{
			return Array.from(e.querySelectorAll(css));
		};
	} else if (e instanceof Array)
	{
		this.get = function (name)
		{
			return e.map(function (node)
			{
				return node[name];
			});
		};
		this.set = function (name, value)
		{
			e.forEach(function (node)
			{
				node[name] = value;
			});
		};
		this.def = function (name, value)
		{
			e.forEach(function (node)
			{
				if (node[name])
					node[name] = value;
			});
		};
		this.addEventListener = function (name, value)
		{
			e.forEach(function (node)
			{
				node.addEventListener(name, value);
			});
		};
		this.getAttribute = function (name)
		{
			return e.map(function (node)
			{
				return node.getAttribute(name);
			});
		};
		this.setAttribute = function (name, value)
		{
			e.forEach(function (node)
			{
				node.setAttribute(name, value);
			});
		};
		this.children = function ()
		{
			var result = e;
			for (var i = 0; i < arguments.length; i++)
				result = children(result, arguments[i]);
			return result;

			function children(array, tagName)
			{
				var result = [];
				for (var i = 0; i < array.length; i++)
					for (var j = 0; j < array[i].childNodes.length; j++)
						if (array[i].childNodes[j].tagName && array[i].childNodes[j].tagName.toLowerCase() === tagName.toLowerCase())
							result.push(array[i].childNodes[j]);
				return result;
			}
		};
		this.filter = function (selector)
		{
			switch (typeof selector)
			{
				case "string":
					return e.filter(function (node)
					{
						return node && node.matches
							&& node.matches(selector);
					});
				case "boolean":
					return e.filter(function (node)
					{
						return selector ===
							(node !== null && node !== undefined);
					});
				case "function":
					return e.filter(selector);
			}
		};

		this.toArray = function ()
		{
			return e;
		};
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Loading
///////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener("load", function ()
{
	$('select').set("onclick", function (e)
	{
		e = e ? e : window.event;
		if (e.stopPropagation)
			e.stopPropagation();
		else
			e.cancelBubble = true;
	});
	$('input.SELECTOR').set("onchange", function ()
	{
		$('input[type="checkbox"][name="' + this.getAttribute('data-target') + '"]').set("checked", this.checked);
	});

	search("td > label > span, td > label > i").forEach(function (e)
	{
		e.parentNode.style.position = 'relative';
	});
	search("fieldset > label > span > span + input, fieldset > label > span > span + input").forEach(function (e)
	{
		e.style.paddingRight = "20px";
	});
});


function Duration(value)
{
	this.value = value;

	this.getHours = function ()
	{
		return Math.floor(value / 3600);
	};

	this.getMinutes = function ()
	{
		return Math.floor((value - (this.getHours() * 3600)) / 60);
	};

	this.getSeconds = function ()
	{
		return value - (this.getHours() * 3600) - (this.getMinutes() * 60);
	};

	this.toString = function ()
	{
		return "00".concat(String(this.getHours())).slice(-2)
				+ ':' + "00".concat(String(this.getMinutes())).slice(-2)
				+ ':' + "00".concat(String(this.getSeconds())).slice(-2);
	};
}
(function (self)
{
	'use strict';

	if (self.fetch)
	{
		return
	}

	var support = {
		searchParams: 'URLSearchParams' in self,
		iterable: 'Symbol' in self && 'iterator' in Symbol,
		blob: 'FileReader' in self && 'Blob' in self && (function ()
		{
			try
			{
				new Blob()
				return true
			} catch (e)
			{
				return false
			}
		})(),
		formData: 'FormData' in self,
		arrayBuffer: 'ArrayBuffer' in self
	}

	if (support.arrayBuffer)
	{
		var viewClasses = [
			'[object Int8Array]',
			'[object Uint8Array]',
			'[object Uint8ClampedArray]',
			'[object Int16Array]',
			'[object Uint16Array]',
			'[object Int32Array]',
			'[object Uint32Array]',
			'[object Float32Array]',
			'[object Float64Array]'
		]

		var isDataView = function (obj)
		{
			return obj && DataView.prototype.isPrototypeOf(obj)
		}

		var isArrayBufferView = ArrayBuffer.isView || function (obj)
		{
			return obj && viewClasses.indexOf(Object.prototype.toString.call(obj)) > -1
		}
	}

	function normalizeName(name)
	{
		if (typeof name !== 'string')
		{
			name = String(name)
		}
		if (/[^a-z0-9\-#$%&'*+.\^_`|~]/i.test(name))
		{
			throw new TypeError('Invalid character in header field name')
		}
		return name.toLowerCase()
	}

	function normalizeValue(value)
	{
		if (typeof value !== 'string')
		{
			value = String(value)
		}
		return value
	}

	// Build a destructive iterator for the value list
	function iteratorFor(items)
	{
		var iterator = {
			next: function ()
			{
				var value = items.shift()
				return {done: value === undefined, value: value}
			}
		}

		if (support.iterable)
		{
			iterator[Symbol.iterator] = function ()
			{
				return iterator
			}
		}

		return iterator
	}

	function Headers(headers)
	{
		this.map = {}

		if (headers instanceof Headers)
		{
			headers.forEach(function (value, name)
			{
				this.append(name, value)
			}, this)

		} else if (headers)
		{
			Object.getOwnPropertyNames(headers).forEach(function (name)
			{
				this.append(name, headers[name])
			}, this)
		}
	}

	Headers.prototype.append = function (name, value)
	{
		name = normalizeName(name)
		value = normalizeValue(value)
		var oldValue = this.map[name]
		this.map[name] = oldValue ? oldValue + ',' + value : value
	}

	Headers.prototype['delete'] = function (name)
	{
		delete this.map[normalizeName(name)]
	}

	Headers.prototype.get = function (name)
	{
		name = normalizeName(name)
		return this.has(name) ? this.map[name] : null
	}

	Headers.prototype.has = function (name)
	{
		return this.map.hasOwnProperty(normalizeName(name))
	}

	Headers.prototype.set = function (name, value)
	{
		this.map[normalizeName(name)] = normalizeValue(value)
	}

	Headers.prototype.forEach = function (callback, thisArg)
	{
		for (var name in this.map)
		{
			if (this.map.hasOwnProperty(name))
			{
				callback.call(thisArg, this.map[name], name, this)
			}
		}
	}

	Headers.prototype.keys = function ()
	{
		var items = []
		this.forEach(function (value, name)
		{
			items.push(name)
		})
		return iteratorFor(items)
	}

	Headers.prototype.values = function ()
	{
		var items = []
		this.forEach(function (value)
		{
			items.push(value)
		})
		return iteratorFor(items)
	}

	Headers.prototype.entries = function ()
	{
		var items = []
		this.forEach(function (value, name)
		{
			items.push([name, value])
		})
		return iteratorFor(items)
	}

	if (support.iterable)
	{
		Headers.prototype[Symbol.iterator] = Headers.prototype.entries
	}

	function consumed(body)
	{
		if (body.bodyUsed)
		{
			return Promise.reject(new TypeError('Already read'))
		}
		body.bodyUsed = true
	}

	function fileReaderReady(reader)
	{
		return new Promise(function (resolve, reject)
		{
			reader.onload = function ()
			{
				resolve(reader.result)
			}
			reader.onerror = function ()
			{
				reject(reader.error)
			}
		})
	}

	function readBlobAsArrayBuffer(blob)
	{
		var reader = new FileReader()
		var promise = fileReaderReady(reader)
		reader.readAsArrayBuffer(blob)
		return promise
	}

	function readBlobAsText(blob)
	{
		var reader = new FileReader()
		var promise = fileReaderReady(reader)
		reader.readAsText(blob)
		return promise
	}

	function readArrayBufferAsText(buf)
	{
		var view = new Uint8Array(buf)
		var chars = new Array(view.length)

		for (var i = 0; i < view.length; i++)
		{
			chars[i] = String.fromCharCode(view[i])
		}
		return chars.join('')
	}

	function bufferClone(buf)
	{
		if (buf.slice)
		{
			return buf.slice(0)
		} else
		{
			var view = new Uint8Array(buf.byteLength)
			view.set(new Uint8Array(buf))
			return view.buffer
		}
	}

	function Body()
	{
		this.bodyUsed = false

		this._initBody = function (body)
		{
			this._bodyInit = body
			if (!body)
			{
				this._bodyText = ''
			} else if (typeof body === 'string')
			{
				this._bodyText = body
			} else if (support.blob && Blob.prototype.isPrototypeOf(body))
			{
				this._bodyBlob = body
			} else if (support.formData && FormData.prototype.isPrototypeOf(body))
			{
				this._bodyFormData = body
			} else if (support.searchParams && URLSearchParams.prototype.isPrototypeOf(body))
			{
				this._bodyText = body.toString()
			} else if (support.arrayBuffer && support.blob && isDataView(body))
			{
				this._bodyArrayBuffer = bufferClone(body.buffer)
				// IE 10-11 can't handle a DataView body.
				this._bodyInit = new Blob([this._bodyArrayBuffer])
			} else if (support.arrayBuffer && (ArrayBuffer.prototype.isPrototypeOf(body) || isArrayBufferView(body)))
			{
				this._bodyArrayBuffer = bufferClone(body)
			} else
			{
				throw new Error('unsupported BodyInit type')
			}

			if (!this.headers.get('content-type'))
			{
				if (typeof body === 'string')
				{
					this.headers.set('content-type', 'text/plain;charset=UTF-8')
				} else if (this._bodyBlob && this._bodyBlob.type)
				{
					this.headers.set('content-type', this._bodyBlob.type)
				} else if (support.searchParams && URLSearchParams.prototype.isPrototypeOf(body))
				{
					this.headers.set('content-type', 'application/x-www-form-urlencoded;charset=UTF-8')
				}
			}
		}

		if (support.blob)
		{
			this.blob = function ()
			{
				var rejected = consumed(this)
				if (rejected)
				{
					return rejected
				}

				if (this._bodyBlob)
				{
					return Promise.resolve(this._bodyBlob)
				} else if (this._bodyArrayBuffer)
				{
					return Promise.resolve(new Blob([this._bodyArrayBuffer]))
				} else if (this._bodyFormData)
				{
					throw new Error('could not read FormData body as blob')
				} else
				{
					return Promise.resolve(new Blob([this._bodyText]))
				}
			}

			this.arrayBuffer = function ()
			{
				if (this._bodyArrayBuffer)
				{
					return consumed(this) || Promise.resolve(this._bodyArrayBuffer)
				} else
				{
					return this.blob().then(readBlobAsArrayBuffer)
				}
			}
		}

		this.text = function ()
		{
			var rejected = consumed(this)
			if (rejected)
			{
				return rejected
			}

			if (this._bodyBlob)
			{
				return readBlobAsText(this._bodyBlob)
			} else if (this._bodyArrayBuffer)
			{
				return Promise.resolve(readArrayBufferAsText(this._bodyArrayBuffer))
			} else if (this._bodyFormData)
			{
				throw new Error('could not read FormData body as text')
			} else
			{
				return Promise.resolve(this._bodyText)
			}
		}

		if (support.formData)
		{
			this.formData = function ()
			{
				return this.text().then(decode)
			}
		}

		this.json = function ()
		{
			return this.text().then(JSON.parse)
		}

		return this
	}

	// HTTP methods whose capitalization should be normalized
	var methods = ['DELETE', 'GET', 'HEAD', 'OPTIONS', 'POST', 'PUT']

	function normalizeMethod(method)
	{
		var upcased = method.toUpperCase()
		return (methods.indexOf(upcased) > -1) ? upcased : method
	}

	function Request(input, options)
	{
		options = options || {}
		var body = options.body

		if (typeof input === 'string')
		{
			this.url = input
		} else
		{
			if (input.bodyUsed)
			{
				throw new TypeError('Already read')
			}
			this.url = input.url
			this.credentials = input.credentials
			if (!options.headers)
			{
				this.headers = new Headers(input.headers)
			}
			this.method = input.method
			this.mode = input.mode
			if (!body && input._bodyInit != null)
			{
				body = input._bodyInit
				input.bodyUsed = true
			}
		}

		this.credentials = options.credentials || this.credentials || 'omit'
		if (options.headers || !this.headers)
		{
			this.headers = new Headers(options.headers)
		}
		this.method = normalizeMethod(options.method || this.method || 'GET')
		this.mode = options.mode || this.mode || null
		this.referrer = null

		if ((this.method === 'GET' || this.method === 'HEAD') && body)
		{
			throw new TypeError('Body not allowed for GET or HEAD requests')
		}
		this._initBody(body)
	}

	Request.prototype.clone = function ()
	{
		return new Request(this, {body: this._bodyInit})
	}

	function decode(body)
	{
		var form = new FormData()
		body.trim().split('&').forEach(function (bytes)
		{
			if (bytes)
			{
				var split = bytes.split('=')
				var name = split.shift().replace(/\+/g, ' ')
				var value = split.join('=').replace(/\+/g, ' ')
				form.append(decodeURIComponent(name), decodeURIComponent(value))
			}
		})
		return form
	}

	function parseHeaders(rawHeaders)
	{
		var headers = new Headers()
		rawHeaders.split(/\r?\n/).forEach(function (line)
		{
			var parts = line.split(':')
			var key = parts.shift().trim()
			if (key)
			{
				var value = parts.join(':').trim()
				headers.append(key, value)
			}
		})
		return headers
	}

	Body.call(Request.prototype)

	function Response(bodyInit, options)
	{
		if (!options)
		{
			options = {}
		}

		this.type = 'default'
		this.status = 'status' in options ? options.status : 200
		this.ok = this.status >= 200 && this.status < 300
		this.statusText = 'statusText' in options ? options.statusText : 'OK'
		this.headers = new Headers(options.headers)
		this.url = options.url || ''
		this._initBody(bodyInit)
	}

	Body.call(Response.prototype)

	Response.prototype.clone = function ()
	{
		return new Response(this._bodyInit, {
			status: this.status,
			statusText: this.statusText,
			headers: new Headers(this.headers),
			url: this.url
		})
	}

	Response.error = function ()
	{
		var response = new Response(null, {status: 0, statusText: ''})
		response.type = 'error'
		return response
	}

	var redirectStatuses = [301, 302, 303, 307, 308]

	Response.redirect = function (url, status)
	{
		if (redirectStatuses.indexOf(status) === -1)
		{
			throw new RangeError('Invalid status code')
		}

		return new Response(null, {status: status, headers: {location: url}})
	}

	self.Headers = Headers
	self.Request = Request
	self.Response = Response

	self.fetch = function (input, init)
	{
		return new Promise(function (resolve, reject)
		{
			var request = new Request(input, init)
			var xhr = new XMLHttpRequest()

			xhr.onload = function ()
			{
				var options = {
					status: xhr.status,
					statusText: xhr.statusText,
					headers: parseHeaders(xhr.getAllResponseHeaders() || '')
				}
				options.url = 'responseURL' in xhr ? xhr.responseURL : options.headers.get('X-Request-URL')
				var body = 'response' in xhr ? xhr.response : xhr.responseText
				resolve(new Response(body, options))
			}

			xhr.onerror = function ()
			{
				reject(new TypeError('Network request failed'))
			}

			xhr.ontimeout = function ()
			{
				reject(new TypeError('Network request failed'))
			}

			xhr.open(request.method, request.url, true)

			if (request.credentials === 'include')
			{
				xhr.withCredentials = true
			}

			if ('responseType' in xhr && support.blob)
			{
				xhr.responseType = 'blob'
			}

			request.headers.forEach(function (value, name)
			{
				xhr.setRequestHeader(name, value)
			})

			xhr.send(typeof request._bodyInit === 'undefined' ? null : request._bodyInit)
		})
	}
	self.fetch.polyfill = true
})(typeof self !== 'undefined' ? self : this);
const ESC = 27;
const ENTER = 13;
const RIGHT = 39;
const LEFT = 37;
const UP = 38;
const DOWN = 40;
const END = 35;
const HOME = 36;
const PAGE_UP = 33;
const PAGE_DOWN = 34;

function URL(value)
{
	this.value = value;

	this.setParameter = function (name, value)
	{
		if (this.value.indexOf("?") === -1)
			this.value += "?";
		if (this.value[this.value.length - 1] !== '?')
			this.value += "&";
		this.value += name + "=" + value;
		return this;
	}
	;

	this.setModule = function (module)
	{
		this.setParameter("MODULE", module);
		return this;
	};

	this.setScreen = function (screen)
	{
		this.setParameter("SCREEN", screen);
		return this;
	};

	this.setAction = function (action)
	{
		this.setParameter("ACTION", action);
		return this;
	};

	this.get = function (callback)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");

		if (callback)
		{
			request.onreadystatechange = function ()
			{
				switch (this.readyState)
				{
					case 4:
						switch (this.status)
						{
							case 200:
								callback(this.responseText);
								break;
							case 420:
								alert(this.responseText);
								break;
							default:
								alert("Erro ao obter dados do servidor");
								break;
						}
						break;
				}
			};

			request.open('GET', this.value, true);
			request.send(null);
		} else
		{
			request.open('GET', this.value, false);
			request.send(null);
			if (request.status === 200)
				return request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	};

	this.post = function (data, callback)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");
		if (callback)
		{
			request.onreadystatechange = function ()
			{
				switch (this.readyState)
				{
					case 4:
						switch (this.status)
						{
							case 200:
								callback(this.responseText);
								break;
							case 420:
								alert(this.statusText);
								break;
							default:
								alert("Erro ao obter dados do servidor");
								break;
						}
						break;
				}
			};

			request.open("POST", this.value, true);
			request.send(data);
		} else
		{
			request.open("POST", this.value, false);
			request.send(data);
			if (request.status === 200)
				return request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	};

	this.populate = function (css)
	{
		var selects = Array.from(document.querySelectorAll(css));
		for (var i = 0; i < selects.length; i++)
		{
			selects[i].value = undefined;
			selects[i].innerHTML = "<option value=''></option>";
		}

		this.get(function (options)
		{
			if (options)
			{
				options = JSON.parse(options);
				for (var i = 0; i < selects.length; i++)
					for (var j = 0; j < options.length; j++)
					{
						var option = selects[i].appendChild(document.createElement("option"));
						option.innerHTML = options[j].label;
						option.setAttribute('value', options[j].value);
					}
			}
		});
		return this;
	};


	this.go = function ()
	{
		window.location.href = this.toString();
		return this;
	};

	this.check = function ()
	{
		return !this.value.match(/([?][{][^}]*[}])/g)
			&& !this.value.match(/([@][{][^}]*[}])/g);
	};

	this.resolve = function ()
	{
		var result = this.value;
		var parameters = this.value.match(/([?][{][^}]*[}])/g);
		if (parameters)
			for (var i = 0; i < parameters.length; i++)
			{
				var parameter = decodeURIComponent
					(prompt(decodeURIComponent(parameters[i].substring(2, parameters[i].length - 1))));
				if (!parameter)
					return false;
				result = result.replace(parameters[i], encodeURIComponent(parameter));
			}

		var parameters = this.value.match(/([@][{][^}]*[}])/g);
		if (parameters)
			for (var i = 0; i < parameters.length; i++)
			{
				var parameter = select(parameters[i].substring(2, parameters[i].length - 1));
				if (!parameter || !parameter.value)
					return false;
				result = result.replace(parameters[i], encodeURIComponent(parameter.value));
			}

		this.value = result;
		return true;
	};

	this.toString = function ()
	{
		return this.value;
	};
}
function Calendar(month)
{
    var result = new Array();
    var ini = getFDOW(getFDOM(month));
    var end = getLDOW(getLDOM(month));
    for (var date = ini; date <= end; date.setDate(date.getDate() + 1))
        result.push(new Date(date));
    while (result.length < 42)
    {
        result.push(new Date(date));
        date.setDate(date.getDate() + 1);
    }
    return result;

    function getFDOM(date)
    {
        var fdom = new Date(date);
        fdom.setDate(1);
        return fdom;
    }

    function getLDOM(date)
    {
        var ldom = new Date(date);
        ldom.setMonth(ldom.getMonth() + 1);
        ldom.setDate(0);
        return ldom;
    }

    function getFDOW(date)
    {
        var fdow = new Date(date);
        while (fdow.getDay() !== 0)
            fdow.setDate(fdow.getDate() - 1);
        return fdow;
    }

    function getLDOW(date)
    {
        var ldow = new Date(date);
        while (ldow.getDay() !== 6)
            ldow.setDate(ldow.getDate() + 1);
        return ldow;
    }
}
function Link(link)
{
	link.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-cancel"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();

			Message.error(this.getAttribute("data-cancel"), 2000);
		}
	});

	link.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-confirm") && !confirm(this.getAttribute("data-confirm")))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	});

	link.addEventListener("click", function ()
	{
		if (this.hasAttribute("data-alert"))
			alert(this.getAttribute("data-alert"));
	});


	link.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-disabled"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	});

	link.addEventListener("click", function (event)
	{
		if (this.href.match(/([?][{][^}]*[}])/g) || this.href.match(/([@][{][^}]*[}])/g))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();

			var resolved = resolve(this.href);
			if (resolved !== null)
			{
				var href = this.href;
				this.href = resolved;
				this.click();
				this.href = href;
			}
		}
	});

	link.addEventListener("click", function (event)
	{
		if (this.getAttribute("target"))
		{
			switch (this.getAttribute("target").toLowerCase())
			{
				case "_dialog":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					if (event.ctrlKey)
					{
						this.setAttribute("target", "_blank");
						this.click();
						this.setAttribute("target", "_dialog");
					} else
						new Dialog()
							.setTitle(this.getAttribute("title"))
							.setOnHide(this.getAttribute("data-onHide"))
							.setNavigator(this.getAttribute("data-navigator") ?
								eval(this.getAttribute("data-navigator")) : null)
							.setTarget(this.getAttribute("href"))
							.setCloseable(!this.hasAttribute("data-closeable")
								|| JSON.parse(this.getAttribute("data-closeable")))
							.show();

					break;
				case "_message":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							Message.show(status, 2000);
						} finally
						{
							link.removeAttribute("data-cancel");
						}
					});

					break;
				case "_none":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type !== "SUCCESS")
								Message.show(status, 2000);
						} finally
						{
							link.removeAttribute("data-cancel");
						}
					});

					break;
				case "_this":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type === "SUCCESS")
								this.innerHTML = status.value;
							else
								Message.show(status, 2000);
						} finally
						{
							link.removeAttribute("data-cancel");
						}
					});
					break;
				case "_alert":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						alert(status);
						link.removeAttribute("data-cancel");
					});

					break;
				case "_hide":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();
					if (window.frameElement
						&& window.frameElement.dialog
						&& window.frameElement.dialog.hide)
						window.frameElement.dialog.hide();
					else
						window.close();
					break;
			}
		}
	});

	link.addEventListener("click", function ()
	{
		if (this.getAttribute("data-block"))
			Block.show(this.getAttribute("data-block"));
	});

	link.addEventListener("keydown", function (event)
	{
		if (event.keyCode === 32)
		{
			this.click();
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	this.setAlert = function (value)
	{
		if (value)
			link.setAttribute("data-alert", value);
		else if (link.getAttribute("data-alert"))
			link.removeAttribute("data-alert");
		return this;
	};

	this.setConfirm = function (value)
	{
		if (value)
			link.setAttribute("data-confirm", value);
		else if (link.getAttribute("data-confirm"))
			link.removeAttribute("data-confirm");
		return this;
	};

	this.setBlock = function (value)
	{
		if (value)
			link.setAttribute("data-block", value);
		else if (link.getAttribute("data-block"))
			link.removeAttribute("data-block");
		return this;
	};

	this.setAction = function (value)
	{
		if (value)
			link.setAttribute("href", value);
		else if (link.getAttribute("href"))
			link.removeAttribute("href");
		return this;
	};

	this.setTarget = function (value)
	{
		if (value)
			link.setAttribute("target", value);
		else if (link.getAttribute("target"))
			link.removeAttribute("target");
		return this;
	};

	this.setTitle = function (value)
	{
		if (value)
			link.setAttribute("title", value);
		else if (link.getAttribute("title"))
			link.removeAttribute("title");
		return this;
	};

	this.setNavigator = function (value)
	{
		if (value)
			link.setAttribute("data-navigator", value);
		else if (link.getAttribute("data-navigator"))
			link.removeAttribute("data-navigator");
		return this;
	};

	this.setOnHide = function (value)
	{
		if (value)
			link.setAttribute("data-onHide", value);
		else if (link.getAttribute("data-onHide"))
			link.removeAttribute("data-onHide");
		return this;
	};

	this.get = function ()
	{
		return link;
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("a")).forEach(function (a)
	{
		new Link(a);
	});
});

function Button(button)
{
	button.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-cancel"))
		{
			Message.error(this.getAttribute("data-cancel"), 2000);
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-confirm")
			&& !confirm(this.getAttribute("data-confirm")))
		{
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function ()
	{
		if (this.hasAttribute("data-alert"))
			alert(this.getAttribute("data-alert"));
	});


	button.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-disabled"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (event)
	{
		if (this.getAttribute("formaction") &&
			(this.getAttribute("formaction").match(/([?][{][^}]*[}])/g)
				|| this.getAttribute("formaction").match(/([@][{][^}]*[}])/g)))
		{
			var resolved = resolve(this.getAttribute("formaction"));

			if (resolved !== null)
			{
				var formaction = this.getAttribute("formaction");
				this.setAttribute("formaction", resolved);
				this.click();
				this.setAttribute("formaction", formaction);
			}

			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (event)
	{
		if (this.getAttribute("formtarget"))
		{
			switch (this.getAttribute("formtarget").toLowerCase())
			{
				case "_dialog":
					if (event.ctrlKey)
					{
						event.preventDefault();
						event.stopPropagation();
						event.stopImmediatePropagation();
						this.setAttribute("formtarget", "_blank");
						this.click();
						this.setAttribute("formtarget", "_dialog");
					} else if (this.form.getAttribute("target") !== "_dialog")
						new Dialog()
							.setTitle(this.getAttribute("title"))
							.setOnHide(this.getAttribute("data-onHide"))
							.setNavigator(this.getAttribute("data-navigator") ?
								eval(this.getAttribute("data-navigator")) : null)
							.setCloseable(!this.hasAttribute("data-closeable")
								|| JSON.parse(this.getAttribute("data-closeable")))
							.show();
					break;
				case "_message":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							try
							{
								status = JSON.parse(status);
								Message.show(status, 2000);
							} finally
							{
								button.disabled = false;
							}
						});
					break;
				case "_none":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							try
							{
								status = JSON.parse(status);
								if (status.type !== "SUCCESS")
									Message.show(status, 2000);
							} finally
							{
								button.disabled = false;
							}
						});
					break;
				case "_this":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							try
							{
								status = JSON.parse(status);
								if (status.type === "SUCCESS")
									button.innerHTML = status.value;
								else
									Message.show(status, 2000);
							} finally
							{
								button.disabled = false;
							}
						});
					break;
				case "_alert":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							alert(status);
							button.disabled = false;
						});
					break;
				case "_hide":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();
					if (window.frameElement
						&& window.frameElement.dialog
						&& window.frameElement.dialog.hide)
						window.frameElement.dialog.hide();
					else
						window.close();
					break;
			}
		}
	});

	button.addEventListener("click", function ()
	{
		if (this.getAttribute("data-block"))
		{
			this.form.addEventListener("submit", function (e)
			{
				if (!this.getAttribute("data-block"))
					Block.show(button.getAttribute("data-block"));
				e.target.removeEventListener(e.type, arguments.callee);
			});
		}
	});

	this.setAlert = function (value)
	{
		if (value)
			button.setAttribute("data-alert", value);
		else if (button.getAttribute("data-alert"))
			button.removeAttribute("data-alert");
		return this;
	};

	this.setConfirm = function (value)
	{
		if (value)
			button.setAttribute("data-confirm", value);
		else if (button.getAttribute("data-confirm"))
			button.removeAttribute("data-confirm");
		return this;
	};

	this.setBlock = function (value)
	{
		if (value)
			button.setAttribute("data-block", value);
		else if (button.getAttribute("data-block"))
			button.removeAttribute("data-block");
		return this;
	};

	this.setAction = function (value)
	{
		if (value)
			button.setAttribute("formaction", value);
		else if (button.getAttribute("formaction"))
			button.removeAttribute("formaction");
		return this;
	};

	this.setTarget = function (value)
	{
		if (value)
			button.setAttribute("formtarget", value);
		else if (button.getAttribute("formtarget"))
			button.removeAttribute("formtarget");
		return this;
	};

	this.setTitle = function (value)
	{
		if (value)
			button.setAttribute("title", value);
		else if (button.getAttribute("title"))
			button.removeAttribute("title");
		return this;
	};

	this.setNavigator = function (value)
	{
		if (value)
			button.setAttribute("data-navigator", value);
		else if (button.getAttribute("data-navigator"))
			button.removeAttribute("data-navigator");
		return this;
	};

	this.setOnHide = function (value)
	{
		if (value)
			button.setAttribute("data-onHide", value);
		else if (button.getAttribute("data-onHide"))
			button.removeAttribute("data-onHide");
		return this;
	};

	this.get = function ()
	{
		return button;
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("button")).forEach(function (button)
	{
		new Button(button);
	});
});

function ActionHandler(e)
{
	e.setAttribute("tabindex", 1);
	e.onmouseover = function ()
	{
		this.focus();
	};

	e.onkeydown = function (e)
	{
		e = e ? e : window.event;
		switch (e.keyCode)
		{
			case ENTER:
				this.onclick(e);
				return false;

			case HOME:
				var siblings = $(this).siblings("tr");
				if (siblings.length !== 0
					&& siblings[0].getAttribute("tabindex"))
					siblings[0].focus();
				return false;

			case END:
				var siblings = $(this).siblings("tr");
				if (siblings.length !== 0
					&& siblings[siblings.length - 1].getAttribute("tabindex"))
					siblings[siblings.length - 1].focus();
				return false;

			case UP:
				var prev = $(this).getPrev();
				if (prev &&
					prev.getAttribute("tabindex"))
					prev.focus();
				return false;

			case DOWN:
				var next = $(this).getNext();
				if (next &&
					next.getAttribute("tabindex"))
					next.focus();
				return false;

			default:
				return true;
		}
	};

	if (!e.onclick)
		e.onclick = function (e)
		{
			this.blur();
			e = e || window.event;
			for (var parent = e.target || e.srcElement;
				parent !== this;
				parent = parent.parentNode)
				if (parent.onclick
					|| parent.tagName.toLowerCase() === 'a'
					|| parent.tagName.toLowerCase() === 'button')
					return;
			switch (this.getAttribute("data-method") ?
				this.getAttribute("data-method")
				.toLowerCase() : "get")
			{
				case "get":
					var a = new Link(document.createElement("a"))
						.setAction(this.getAttribute("data-action"))
						.setTarget(e.ctrlKey ? "_blank" : this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setOnHide(this.getAttribute("data-onHide"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
						.setNavigator(this.getAttribute("data-navigator"))
						.get();
					document.body.appendChild(a);
					a.click();
					document.body.removeChild(a);
					break;
				case "post":
					var form = $(this).getForm();
					var button = new Button(document.createElement("button"))
						.setAction(this.getAttribute("data-action"))
						.setTarget(e.ctrlKey ? "_blank" : this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setOnHide(this.getAttribute("data-onHide"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
						.setNavigator(this.getAttribute("data-navigator"))
						.get();
					form.appendChild(button);
					button.click();
					form.removeChild(button);
					break;
			}

			return false;
		};
}

window.addEventListener("load", function ()
{
	search('*[data-action]').forEach(function (e)
	{
		new ActionHandler(e);
	});
});
function ChangeHandler(e)
{
	e.onchange = function ()
	{
		switch (this.getAttribute("data-method") ?
				this.getAttribute("data-method")
				.toLowerCase() : "get")
		{
			case "get":
				var a = new Link(document.createElement("a"))
						.setAction(this.getAttribute("data-action"))
						.setTarget(this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setOnHide(this.getAttribute("data-onHide"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
						.setNavigator(this.getAttribute("data-navigator"))
						.get();
				document.body.appendChild(a);
				a.click();
				document.body.removeChild(a);
				break;
			case "post":
				var form = $(this).getForm();
				var button = new Button(document.createElement("button"))
						.setAction(this.getAttribute("data-action"))
						.setTarget(this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setOnHide(this.getAttribute("data-onHide"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
						.setNavigator(this.getAttribute("data-navigator"))
						.get();
				form.appendChild(button);
				button.click();
				form.removeChild(button);
				break;
		}
	};
}

window.addEventListener("load", function ()
{
	search('input[data-method], input[data-action], input[data-target]').forEach(function (e)
	{
		new ChangeHandler(e)
	});
	search('select[data-method], select[data-action], select[data-target]').forEach(function (e)
	{
		new ChangeHandler(e)
	});
});
  
function Mask(e)
{
	e.placeholder = new String();
	e.mask = e.getAttribute("data-mask");
	for (var i = 0; i < e.mask.length; i++)
		e.placeholder += e.mask.charAt(i).match('[#*_]') ? "_" : e.mask.charAt(i);
	e.setAttribute('placeholder', e.placeholder);

	e.getCursor = function ()
	{
		if (this.selectionStart >= 0)
		{
			return this.selectionStart;
		} else if (document.selection)
		{
			this.focus();
			var selection = document.selection.createRange();
			selection.moveStart('character', -this.value.length);
			return selection.text.length;
		} else
		{
			document.write("Navegador muito antigo. Favor atualizar.");
		}
	};

	e.setCursor = function (i)
	{
		if (this.selectionStart >= 0)
		{
			this.focus();
			this.setSelectionRange(i, i);
		} else if (this.createTextRange)
		{
			var selection = this.createTextRange();
			selection.move('character', i);
			selection.select();
		} else
		{
			document.write("Navegador muito antigo. Favor atualizar.");
		}
		return false;
	};

	e.setValue = function (value)
	{
		this.value = value;
		if (!this.value)
			this.value = this.prevValue ? this.prevValue : this.placeholder;
	};


	e.check_mask = function ()
	{
		var ph = this.getAttribute('data-mask');
		for (var i = 0; i < this.mask.length && i < this.value.length; i++)
			if (ph.charAt(i) === '#')
				if (this.value.charAt(i).match("[_0-9]"))
					continue;
				else
					return false;
			else if (ph.charAt(i) === '_')
				if (this.value.charAt(i).match("[_a-zA-Z]"))
					continue;
				else
					return false;
			else if (ph.charAt(i) === '*')
				if (this.value.charAt(i).match("[_a-zA-Z0-9]"))
					continue;
				else
					return false;
			else if (this.value.charAt(i) === ph.charAt(i))
				continue;
			else
				return false;
		return true;
	};

	e.onkeydown = function (e)
	{
		e = e ? e : window.event;
		switch (e.keyCode)
		{
			// ///////////////////////////////////////////////////////
			// ESQUERDA
			// ///////////////////////////////////////////////////////
			case 37:

				for (var i = this.getCursor() - 1; i >= 0; i--)
					if (this.mask.charAt(i).match("[#_*]"))
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// DIREITA
				// ///////////////////////////////////////////////////////
			case 39:

				for (var i = this.getCursor() + 1; i <= this.mask.length; i++)
					if (i === this.mask.length || this.mask.charAt(i).match("[#_*]"))
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// BACKSPACE
				// ///////////////////////////////////////////////////////
			case 8:

				for (var i = this.getCursor() - 1; i >= 0; i--)
				{
					if (this.mask.charAt(i).match("[#_*]"))
					{
						this.value = this.value.substr(0, i) + "__" + this.value.substr(i + 1);
						return this.setCursor(i + 1) || true;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// Delete
				// ///////////////////////////////////////////////////////
			case 46:

				for (var i = this.getCursor(); i < this.mask.length; i++)
				{
					if (this.mask.charAt(i).match("[#_*]"))
					{
						this.value = this.value.substr(0, i) + "__" + this.value.substr(i + 1);
						return this.setCursor(i + 1) || true;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// END
				// ///////////////////////////////////////////////////////
			case 35:
				return this.setCursor(this.mask.length);
				// ///////////////////////////////////////////////////////
				// HOME
				// ///////////////////////////////////////////////////////
			case 36:
				return this.setCursor(0);
		}

		return true;
	};

	e.onkeypress = function (e)
	{
		e = e ? e : window.event;
		switch (e.keyCode)
		{
			case 8:
			case 9:
			case 13:
			case 35:
			case 36:
			case 37:
			case 39:
			case 46:
				return true;
			default:

				var c = String.fromCharCode(e.which ? e.which : e.keyCode);
				if (e.ctrlKey)
				{
					switch (c)
					{
						case 'c':
						case 'v':
							this.select();
							return true;
						default:
							return false;
					}
				}

				for (var i = this.getCursor(); i < this.mask.length; i++)
				{
					if (this.mask.charAt(i).match("[#_*]"))
					{
						switch (this.mask.charAt(i))
						{
							case '#':
								if (c.match("[0-9]"))
								{
									this.value = this.value.substr(0, i) + this.value.substr(i + 1);
									this.setCursor(i);
									return true;
								}
								break;
							case '_':
								if (c.match("[a-zA-Z]")) {
									this.value = this.value.substr(0, i) + this.value.substr(i + 1);
									this.setCursor(i);
									return true;
								}
								break;
							case '*':
								if (c.match("[0-9a-zA-Z]"))
								{
									this.value = this.value.substr(0, i) + this.value.substr(i + 1);
									this.setCursor(i);
									return true;
								}
								break;
						}
					}
				}

				return false;
		}
	};

	e.oninput = function ()
	{
		if (this.check_mask())
		{
			for (var i = this.getCursor(); i < this.mask.length; i++)
			{
				if (this.mask.charAt(i).match("[#_*]"))
				{
					this.setCursor(i);
					break;
				}
			}
			return true;
		}

		this.setValue('');
		this.setCursor(0);
		return false;
	};
	e.onpaste = function ()
	{
		this.prevValue = this.value;
		this.select();
		return true;
	};
	e.onfocus = function ()
	{
		if (!this.value && this.placeholder)
			this.value = this.placeholder;

		var self = this;
		setTimeout(function ()
		{
			self.setCursor(0);
			for (var i = self.getCursor(); i < self.mask.length; i++)
				if (self.mask.charAt(i).match("[#_*]"))
					return self.setCursor(i);
		}, 0);
	};
	e.onblur = function ()
	{
		if (this.value === this.placeholder)
			this.value = '';
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('input[data-mask]')).forEach(function (e)
	{
		new Mask(e);
	});

	document.oncontextmenu = function (e)
	{
		return e && e.target && !e.target.getAttribute('data-mask');
	};
});


function Modal()
{
	var body = window.top.document.body;
	var overflow = body.style.overflow;

	var modal = window.top.document.createElement('div');
	modal.className = "Modal";

	modal.show = function ()
	{
		body.style.overflow = "hidden";
		body.appendChild(this);
		return this;
	};

	modal.setOnHide = function (onHide)
	{
		this.onHide = onHide;
		return this;
	};

	modal.hide = function ()
	{
		if (this.parentNode)
		{
			body.style.overflow = overflow;
			if (this.onHide)
				eval(this.onHide);
			this.parentNode.removeChild(this);
		}
		return this;
	};

	return modal;
}
function Block(text)
{
	var modal = new Modal();
	modal.style.display = "flex";
	modal.style.alignItems = "center";
	modal.style.justifyContent = "center";

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.style.width = "50%";
	dialog.style.display = "flex";
	dialog.style.flexWrap = "wrap";
	dialog.style.borderRadius = "5px";
	dialog.style.alignItems = "center";
	dialog.style.justifyContent = "center";
	dialog.style.border = "4px solid #767d90";

	var head = dialog.appendChild(window.top.document.createElement('div'));
	head.style.width = "100%";
	head.style.height = "40px";
	head.style.backgroundImage = "linear-gradient(to bottom, #767D90 100%, #AAB3BD 100%)";
	head.setAttribute("tabindex", "1");
	head.focus();

	var caption = head.appendChild(window.top.document.createElement('label'));
	caption.style.color = "white";
	caption.style.padding = "10px";
	caption.style.fontSize = "18px";
	caption.style['float'] = "left";
	caption.innerHTML = "Aguarde";

	var body = dialog.appendChild(window.top.document.createElement('div'));
	body.style.width = "100%";
	body.style.display = "flex";
	body.style.height = "180px";
	body.style.flexWrap = "wrap";
	body.style.alignItems = "center";
	body.style.justifyContent = "center";
	body.style.background = "linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%)";

	var label = body.appendChild(window.top.document.createElement('label'));
	label.style.width = "calc(100% - 20px)";
	label.style.height = "50px";
	label.style.fontSize = "20px";
	label.style.display = "flex";
	label.style.alignItems = "center";
	label.style.justifyContent = "center";
	label.innerHTML = text;

	var progress = body.appendChild(window.top.document.createElement('progress'));
	progress.style.width = "calc(100% - 20px)";
	progress.style.height = "50px";

	var foot = body.appendChild(window.top.document.createElement('label'));
	foot.style.width = "calc(100% - 20px)";
	foot.style.height = "40px";
	foot.style.fontSize = "20px";
	foot.style.display = "flex";
	foot.style.alignItems = "center";
	foot.style.justifyContent = "right";
	foot.innerHTML = "00:00:00";
	foot.setAttribute("data-clock", '0');

	modal.show();
	return modal;
}

Block.show = function (text)
{
	if (!window.top._block)
		window.top._block = new Block(text);
};

Block.hide = function ()
{
	if (window.top._block)
	{
		window.top._block.hide();
		window.top._block = null;
	}
};

window.addEventListener("load", function ()
{
	Block.hide();
	search("form[data-block]").forEach(function (e)
	{
		e.addEventListener("submit", function ()
		{
			Block.show(this.getAttribute("data-block"));
		});
	});
});
function DateDialog(callback, month)
{
	var dialog = new Modal(this);

	month = month ? month : new Date();
	var monthFormat = new DateFormat("MM/yyyy");
	var dateFormat = new DateFormat("dd/MM/yyyy");

	var table = dialog.appendChild(document.createElement("table"));

	var caption = table.createCaption();
	caption.appendChild(document.createTextNode(monthFormat.format(month)));

	var td = table.appendChild(document.createElement("tfoot"))
		.appendChild(document.createElement("tr"))
		.appendChild(document.createElement("td"));
	td.setAttribute("colspan", 7);

	var prev = td.appendChild(document.createElement("span"));
	prev.style['float'] = "left";
	prev.style.cursor = "pointer";
	prev.onclick = function ()
	{
		dialog.setDate(prev.date);
	};
	prev.onmouseover = function ()
	{
		prev.style.fontWeight = "bold";
	};
	prev.onmouseout = function ()
	{
		prev.style.fontWeight = "normal";
	};

	var next = td.appendChild(document.createElement("span"));
	next.style['float'] = "right";
	next.style.cursor = "pointer";
	next.onclick = function ()
	{
		dialog.setDate(next.date);
	};
	next.onmouseover = function ()
	{
		next.style.fontWeight = "bold";
	};
	next.onmouseout = function ()
	{
		next.style.fontWeight = "normal";
	};

	var week = ["14%", "13%", "13%", "13%", "13%", "13%", "14%"];
	for (var i = 0; i < week.length; i++)
	{
		var col = table.appendChild(document.createElement("col"));
		col.style.width = week[i];
	}

	var thead = table.appendChild(document.createElement("thead"));
	var week = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"];
	for (var i = 0; i < week.length; i++)
	{
		var th = thead.appendChild(document.createElement("th"));
		th.appendChild(document.createTextNode(week[i]));
		th.style.textAlign = "center";
	}

	var tbody = table.appendChild(document.createElement("tbody"));
	dialog.setDate = function (month)
	{
		tbody.innerHTML = "";
		var calendar = new Calendar(month);
		caption.innerHTML = monthFormat.format(month);
		prev.date = new Date(month);
		prev.date.setMonth(prev.date.getMonth() - 1);
		prev.innerHTML = "&nbsp;<&nbsp;" + monthFormat.format(prev.date);
		next.date = new Date(month);
		next.date.setMonth(next.date.getMonth() + 1);
		next.innerHTML = monthFormat.format(next.date) + "&nbsp;>&nbsp;";
		for (var i = 0; i < calendar.length; i += 7)
		{
			var tr = tbody.appendChild(document.createElement("tr"));
			for (var j = 0; j < 7; j++)
			{
				var td = tr.appendChild(document.createElement("td"));
				td.style.cursor = "pointer";
				td.style.textAlign = "center";
				td.value = calendar[i + j];
				td.title = dateFormat.format(calendar[i + j]);
				td.appendChild(document.createTextNode(calendar[i + j].getDate()));
				if (calendar[i + j].getMonth() !== month.getMonth())
					td.style.color = "#CCCCCC";

				var date = new Date();
				if (td.value.getDate() === date.getDate()
					&& td.value.getMonth() === date.getMonth()
					&& td.value.getYear() === date.getYear())
					td.style.backgroundColor = "#F0E68C";

				td.onmouseover = function ()
				{
					this.style.fontWeight = "bold";
					this.prev = this.style.backgroundColor;
					this.style.backgroundColor = "#CCCCCC";
				};
				td.onclick = function ()
				{
					callback(this.value);
					dialog.hide();
				};
				td.onmouseout = function ()
				{
					this.style.fontWeight = "normal";
					this.style.backgroundColor = this.prev;
				};
			}
		}
	};

	dialog.setDate(month);
	dialog.show();

	dialog.onclick = function (e)
	{
		if (e.target === this
			|| e.srcElement === this)
			this.hide();
	};
}


window.addEventListener("load", function ()
{
	search("input.Date").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new DateDialog(function (e)
				{
					input.value = new DateFormat("dd/MM/yyyy").format(e);
				});
			return false;
		};
	});
});
function TimeDialog(callback)
{
	var dialog = new Modal();

	var hour = dialog.appendChild(document.createElement("table"));
	hour.appendChild(document.createElement("caption"))
		.appendChild(document.createTextNode("Selecione a Hora"));
	var columns = ["9%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "9%"];
	for (var i = 0; i < columns.length; i++)
	{
		var col = hour.appendChild(document.createElement("col"));
		col.style.width = columns[i];
	}

	var tbody = hour.appendChild(document.createElement("tbody"));
	for (var i = 0; i < 24; i += 12)
	{
		var tr = tbody.appendChild(document.createElement("tr"));
		for (var j = 0; j < 12; j++)
		{
			var td = tr.appendChild(document.createElement("td"));
			td.dialog = this;
			td.style.cursor = "pointer";
			td.style.textAlign = "center";
			td.title = "00".concat(new String(i + j)).slice(-2);
			td.appendChild(document.createTextNode(td.title));
			td.onmouseover = function ()
			{
				this.style.fontWeight = "bold";
				this.prev = this.style.backgroundColor;
				this.style.backgroundColor = "#CCCCCC";
			};
			td.onmouseout = function ()
			{
				this.style.fontWeight = "normal";
				this.style.backgroundColor = this.prev;
			};
			td.onclick = function ()
			{
				hour.minutes.innerHTML = "";
				var minutes = hour.minutes.appendChild(document.createElement("table"));
				minutes.appendChild(document.createElement("caption"))
					.appendChild(document.createTextNode("Selecione os Minutos"));
				var tbody = minutes.appendChild(document.createElement("tbody"));
				for (var i = 0; i < 60; i += 12)
				{
					var tr = tbody.appendChild(document.createElement("tr"));
					for (var j = 0; j < 12; j++)
					{
						var td = tr.appendChild(document.createElement("td"));
						td.dialog = this.dialog;
						td.style.cursor = "pointer";
						td.style.textAlign = "center";
						td.title = this.title + ":" + "00".concat(new String(i + j)).slice(-2);
						td.appendChild(document.createTextNode(td.title));
						td.onmouseover = function ()
						{
							this.prev = this.style.backgroundColor;
							this.style.backgroundColor = "#CCCCCC";
						};
						td.onmouseout = function ()
						{
							this.style.backgroundColor = this.prev;
						};
						td.onclick = function ()
						{
							dialog.hide();
							callback(this.title);
						};
					}
				}
			};
		}
	}

	hour.minutes = tbody.appendChild(document.createElement("tr"))
		.appendChild(document.createElement("td"));
	hour.minutes.style.padding = '06px';
	hour.minutes.style.height = '200px';
	hour.minutes.style.textAlign = 'center';
	hour.minutes.setAttribute("colspan", 12);
	hour.minutes.innerHTML = "Selecione a Hora";

	dialog.show();

	dialog.onclick = function (e)
	{
		if (e.target === this
			|| e.srcElement === this)
			this.hide();
	};
}

window.addEventListener("load", function ()
{
	search("input.Time").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new TimeDialog(function (e)
				{
					input.value = e;
				});
			return false;
		};
	});
});
window.addEventListener("load", function ()
{
	search("input.DateTime").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new DateDialog(function (date)
				{
					new TimeDialog(function (time)
					{
						input.value = new DateFormat("dd/MM/yyyy").format(date) + " " + time;
					});
				});
			return false;
		};
	});
});

window.addEventListener("load", function ()
{
	search("input.DateInterval").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new DateDialog(function (date1)
				{
					new DateDialog(function (date2)
					{
						var format = new DateFormat("dd/MM/yyyy");
						input.value = format.format(date1) + " - " + format.format(date2);
					}, date1);
				});

			return false;
		};
	});
});

window.addEventListener("load", function ()
{
	search("input.TimeInterval").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new TimeDialog(function (time1)
				{
					new TimeDialog(function (time2)
					{
						input.value = time1 + " - " + time2;
					});
				});
			return false;
		};
	});
});

window.addEventListener("load", function ()
{
	search("input.DateTimeInterval").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";
		link.onclick = function ()
		{
			if (!input.value)
				input.value = '';
			else
				new DateDialog(function (date1)
				{
					new TimeDialog(function (time1)
					{
						new DateDialog(function (date2)
						{
							new TimeDialog(function (time2)
							{
								var format = new DateFormat("dd/MM/yyyy");
								input.value = format.format(date1) + " " + time1 + " - " + format.format(date2) + " " + time2;
							});
						}, date1);
					});
				});

			return false;
		};
	});
});
function Chart(data, title)
{
	this.categories = new Array();
	for (var i = 1; i < data.length; i++)
		this.categories.push(data[i][0]);
	this.groups = new Array();
	for (var j = 1; j < data[0].length; j++)
	{
		var group = {'label': data[0][j], 'values': new Array()};
		for (var i = 1; i < data.length; i++)
			group.values.push(data[i][j]);
		this.groups.push(group);
	}

	function cc()
	{
		return '#'
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10));
	}

	function f1(params)
	{
		return params.name + ': ' + params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}

	function f2(value)
	{
		return value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}

	function f3(params)
	{
		return params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
	}

	this.draw = function (id, type)
	{
		switch (type.toLowerCase())
		{
			case 'cchart':
				var options = {calculable: true, tooltip: {show: true, formatter: f1}, xAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'top', formatter: f3}}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = cc;
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'bchart':

				var options = {calculable: true, tooltip: {show: true, formatter: f1}, yAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: -70}}, xAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'right', formatter: f3}}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = cc;
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'lchart':

				var options = {calculable: true, tooltip: {show: true, formatter: f1}, xAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					}
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'achart':

				var options = {calculable: true, tooltip: {show: true, formatter: f1}, xAxis: {type: 'category', data: this.categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: f2}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (this.categories.length > 0 ? Math.ceil((this.categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};
				if (title)
					options.title = {x: 'center', text: title};
				for (var i = 0; i < this.groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {areaStyle: {type: 'default'}}}, data: this.groups[i].values});
					options.series[options.series.length - 1].name = this.groups[i].label;
					if (this.groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = this.groups[i].color;
					if (this.groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(this.groups[i].label);
					}
				}
				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'pchart':

				var options =
						{calculable: true, tooltip: {show: true, formatter: f1},
							legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
							toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
							series: [{type: 'pie', roseType: false, radius: this.title ? '60%' : '80%', data: [],
									center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};
				if (title)
					options.title = {x: 'center', text: title};
				if (this.groups.length > 1)
				{
					for (var i = 0; i < this.groups.length; i++)
					{
						options.legend.data.push(this.groups[i].label);
						var sum = 0;
						for (var j = 0; j < this.groups[i].values.length; j++)
							sum = sum + this.groups[i].values[j];
						options.series[0].data.push({name: this.groups[i].label, value: sum});
						if (this.groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: this.groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < this.categories.length; i++)
					{
						options.legend.data.push(this.categories[i]);
						options.series[0].data.push({name: this.categories[i], value: this.groups[0].values[i]});
					}
				}

				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'dchart':

				var options =
						{calculable: true, tooltip: {show: true, formatter: f1},
							legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
							toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
							series: [{type: 'pie', roseType: false, radius: this.title ? ['40%', '60%'] : ['60%', '80%'], data: [],
									center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};
				if (title)
					options.title = {x: 'center', text: title};
				if (this.groups.length > 1)
				{
					for (var i = 0; i < this.groups.length; i++)
					{
						options.legend.data.push(this.groups[i].label);
						var sum = 0;
						for (var j = 0; j < this.groups[i].values.length; j++)
							sum = sum + this.groups[i].values[j];
						options.series[0].data.push({name: this.groups[i].label, value: sum});
						if (this.groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: this.groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < this.categories.length; i++)
					{
						options.legend.data.push(this.categories[i]);
						options.series[0].data.push({name: this.categories[i], value: this.groups[0].values[i]});
					}
				}

				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
			case 'rchart':
				var options =
						{calculable: true, tooltip: {show: true, formatter: f1},
							legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
							toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
							series: [{type: 'pie', roseType: 'area', radius: this.title ? [20, '60%'] : [20, '80%'], data: [],
									center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};
				if (title)
					options.title = {x: 'center', text: title};
				if (this.groups.length > 1)
				{
					for (var i = 0; i < this.groups.length; i++)
					{
						options.legend.data.push(this.groups[i].label);
						var sum = 0;
						for (var j = 0; j < this.groups[i].values.length; j++)
							sum = sum + this.groups[i].values[j];
						options.series[0].data.push({name: this.groups[i].label, value: sum});
						if (this.groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: this.groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < this.categories.length; i++)
					{
						options.legend.data.push(this.categories[i]);
						options.series[0].data.push({name: this.categories[i], value: this.groups[0].values[i]});
					}
				}

				var chart = echarts.init(document.getElementById(id));
				chart.clear();
				chart.setOption(options);
				break;
		}
		return this;
	};
}

function ChartDialog(data, type, title)
{
	var modal = new Modal();

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.className = "Dialog";

	var head = dialog.appendChild(document.createElement("div"));
	head.setAttribute("tabindex", "1");

	var caption = head.appendChild(window.top.document.createElement('label'));
	caption.innerHTML = title;
	caption.style['float'] = "left";

	var body = dialog.appendChild(document.createElement("div"));
	body.style.backgroundColor = "white";

	var canvas = body.appendChild(document.createElement("div"));
	canvas.setAttribute("id", 'Chart');
	canvas.setAttribute("tabindex", "1");

	canvas.onmouseenter = function ()
	{
		this.focus();
	};

	head.onmouseenter = function ()
	{
		this.focus();
	};

	var close = head.appendChild(document.createElement('a'));
	close.style['float'] = "right";
	close.dialog = this;
	close.title = "Fechar";
	close.innerHTML = "&#x1011;";
	close.style.marginLeft = '16px';
	close.style.fontFamily = "gate";
	close.onclick = function ()
	{
		modal.hide();
	};

	var chart = new Chart(data, title);

	var rchart = head.appendChild(document.createElement('a'));
	rchart.style['float'] = "right";
	rchart.dialog = this;
	rchart.title = "Rose";
	rchart.innerHTML = "&#x2247;";
	rchart.style.fontFamily = "gate";
	rchart.onclick = function ()
	{
		chart.draw('Chart', 'rchart');
	};

	var dchart = head.appendChild(document.createElement('a'));
	dchart.style['float'] = "right";
	dchart.dialog = this;
	dchart.title = "Donut";
	dchart.innerHTML = "&#x2245;";
	dchart.style.fontFamily = "gate";
	dchart.onclick = function ()
	{
		chart.draw('Chart', 'dchart');
	};

	var pchart = head.appendChild(document.createElement('a'));
	pchart.style['float'] = "right";
	pchart.dialog = this;
	pchart.title = "Pizza";
	pchart.innerHTML = "&#x2031;";
	pchart.style.fontFamily = "gate";
	pchart.onclick = function ()
	{
		chart.draw('Chart', 'pchart');
	};

	var achart = head.appendChild(document.createElement('a'));
	achart.style['float'] = "right";
	achart.dialog = this;
	achart.title = "Area";
	achart.innerHTML = "&#x2244;";
	achart.style.fontFamily = "gate";
	achart.onclick = function ()
	{
		chart.draw('Chart', 'achart');
	};

	var lchart = head.appendChild(document.createElement('a'));
	lchart.style['float'] = "right";
	lchart.dialog = this;
	lchart.title = "Linha";
	lchart.innerHTML = "&#x2032;";
	lchart.style.fontFamily = "gate";
	lchart.onclick = function ()
	{
		chart.draw('Chart', 'lchart');
	};

	var bchart = head.appendChild(document.createElement('a'));
	bchart.style['float'] = "right";
	bchart.dialog = this;
	bchart.title = "Barra";
	bchart.innerHTML = "&#x2246;";
	bchart.style.fontFamily = "gate";
	bchart.onclick = function ()
	{
		chart.draw('Chart', 'bchart');
	};

	var cchart = head.appendChild(document.createElement('a'));
	cchart.style['float'] = "right";
	cchart.dialog = this;
	cchart.title = "Coluna";
	cchart.innerHTML = "&#x2033;";
	cchart.style.fontFamily = "gate";
	cchart.onclick = function ()
	{
		chart.draw('Chart', 'cchart');
	};

	modal.show();

	if (!type)
		type = 'cchart';
	chart.draw('Chart', type);

	modal.onclick = function (e)
	{
		if (e.target === this
			|| e.srcElement === this)
			modal.hide();
	};

	head.focus();
}

ChartDialog.show = function (chart, series, action, title)
{
	if (!series)
		series = new URL(action).get();
	new ChartDialog(JSON.parse(series), chart, title);
};

window.addEventListener("load", function ()
{
	search('tr.RChart, td.RChart').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("rchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	search('tr.DChart, td.DChart').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("dchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	search('tr.PChart, td.PChart').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("pchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	search('tr.AChart, td.AChart').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("achart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});


	search('tr.LChart, td.LChart').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("lchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	search('tr.BChart, td.BChart').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("bchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	search('tr.CChart, td.CChart').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("cchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	search('a[data-chart]').forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show(this.getAttribute('data-chart'),
				this.getAttribute('data-series'),
				this.getAttribute('href'),
				this.getAttribute("title"));
			return false;
		};
	});
});


function PageControl(pageControl)
{
    if (!pageControl.getAttribute("data-type"))
        pageControl.setAttribute("data-type", "Frame");

    var pages = $(pageControl).children("ul", "li");
    if (pages.length > 0 && pages.every(function (e)
    {
        return !e.getAttribute("data-selected")
                || e.getAttribute("data-selected")
                .toLowerCase() !== "true";
    }))
        pages[0].setAttribute("data-selected", "true");


    for (var i = 0; i < pages.length
            && i < pages.length; i++)
        new Page(pages[i]);

    function reset()
    {
        for (var i = 0; i < pages.length; i++)
            pages[i].setAttribute("data-selected", "false");

        for (var i = 0; i < pageControl.children.length; i++)
            if (pageControl.children[i].tagName.toLowerCase() === 'div')
                pageControl.children[i].style.display = "";
    }

    function Page(page)
    {
        if (!page.getAttribute("data-type"))
            page.setAttribute("data-type",
                    pageControl.getAttribute("data-type"));

        var link = undefined;
        for (var i = 0; i < page.children.length; i++)
            if (page.children[i].tagName.toLowerCase() === 'a')
                link = page.children[i];

        var body = undefined;
        for (var i = 0; i < page.children.length; i++)
            if (page.children[i].tagName.toLowerCase() === 'div')
                body = page.children[i];

        if (!body)
            body = document.createElement("div");
        pageControl.appendChild(body);

        link.onclick = function ()
        {
            reset();
            body.style.display = "block";
            page.setAttribute("data-selected", "true");

            if (!body.innerHTML.replace(/^\s+|\s+$/g, ''))
                switch (page.getAttribute("data-type"))
                {
                    case 'Fetch':
                        fetch();
                        break;
                    case 'Frame':
                        frame();
                        break;
                }

            return false;
        }


        if (page.getAttribute("data-selected") &&
                page.getAttribute("data-selected").toLowerCase() === "true")
            link.onclick();


        function fetch()
        {
            new URL(link.getAttribute('href')).get(function (text)
            {
                body.innerHTML = text;
            });
        }

        function frame()
        {
            var iframe = body.appendChild(document.createElement("iframe"));
            iframe.setAttribute("allowfullscreen", "true");
            iframe.style.backgroundPosition = "center";
            iframe.style.backgroundRepeat = "no-repeat";
            iframe.scrolling = "no";
            iframe.style.backgroundImage = "url('../gate/imge/back/LOADING.gif')";
            iframe.setAttribute("src", link.getAttribute('href'));

            var observer = new MutationObserver(function ()
            {
                iframe.height = 0;
                iframe.height = iframe.contentWindow.document
                        .body.scrollHeight + "px";
            });

            iframe.onload = function ()
            {
                observer.disconnect();

                this.height = 0;
                this.height = this.contentWindow.document
                        .body.scrollHeight + "px";

		this.style.backgroundImage = "";
                var elements = iframe.contentWindow.document.querySelectorAll("*");
                for (var i = 0; i < elements.length; i++)
                    observer.observe(elements[i], {attributes: true, childList: true, characterData: true});
            };
            iframe.refresh = function ()
            {
                var divs = $(this.parentNode.parentNode).children("div");
                for (i = 0; i < divs.length; i++)
                {
                    if (divs[i].childNodes[0] !== this)
                        if (divs[i] !== this.parenNode)
                            divs[i].innerHTML = '';
                }
            };
        }
    }
}

window.addEventListener("load", function ()
{
    search('div.PageControl').forEach(function (e)
    {
        new PageControl(e);
    });
});
function LinkControl(linkControl)
{
	var links = $(linkControl).children("ul", "li", "a");

	if (links.length > 0 && links.every(function (e)
	{
		return !e.parentNode.getAttribute("data-selected")
				|| e.parentNode.getAttribute("data-selected")
				.toLowerCase() !== "true";
	}))
		links[0].parentNode.setAttribute("data-selected", "true");
}

window.addEventListener("load", function ()
{
	search('div.LinkControl').forEach(function (e)
	{
		new LinkControl(e);
	});
});
function Dialog()
{
	var modal = new Modal();

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.className = "Dialog";
	dialog.closeable = true;

	var head = dialog.appendChild(window.top.document.createElement('div'));
	head.setAttribute("tabindex", "1");
	head.focus();

	var caption = head.appendChild(window.top.document.createElement('label'));
	caption.style['float'] = "left";

	var close = head.appendChild(window.top.document.createElement("a"));
	close.title = 'Fechar janela';
	close.style['float'] = "right";
	close.innerHTML = "&#x1011;";

	close.onclick = function ()
	{
		modal.hide();
	};

	var last = head.appendChild(window.top.document.createElement("a"));
	last.title = 'Ir para o último registro';
	last.style['float'] = "right";
	last.innerHTML = "&#x2216;";
	last.style.display = "none";

	var next = head.appendChild(window.top.document.createElement("a"));
	next.title = 'Ir para o próximo registro';
	next.style['float'] = "right";
	next.innerHTML = "&#x2211;";
	next.style.display = "none";

	var navigator = head.appendChild(window.top.document.createElement('label'));
	navigator.title = 'Ir para o registro atual';
	navigator.style['float'] = "right";
	navigator.style.cursor = "pointer";
	navigator.style.display = "none";

	var prev = head.appendChild(window.top.document.createElement("a"));
	prev.title = 'Ir para o registro anterior';
	prev.style['float'] = "right";
	prev.innerHTML = "&#x2212;";
	prev.style.display = "none";

	var frst = head.appendChild(window.top.document.createElement("a"));
	frst.title = 'Ir para o primeiro registro';
	frst.style['float'] = "right";
	frst.innerHTML = "&#x2213;";
	frst.style.display = "none";

	var body = dialog.appendChild(window.top.document.createElement('div'));

	var iframe = body.appendChild(window.top.document.createElement('iframe'));
	iframe.dialog = modal;
	iframe.setAttribute('name', '_dialog');

	head.onmouseenter = function ()
	{
		this.focus();
	};

	iframe.onmouseenter = function ()
	{
		this.focus();
	};

	iframe.onload = function ()
	{
		iframe.name = "_frame";
		iframe.setAttribute("name", "_frame");

		head.onkeydown = undefined;
		head.addEventListener("keydown",
			function (e)
			{
				e = e ? e : window.event;
				switch (e.keyCode)
				{
					case ESC:
						if (dialog.closeable)
							modal.hide();
						break;
					case ENTER:
						iframe.focus();
						break;
				}

				e.preventDefault();
				e.stopPropagation();
			});

		iframe.contentWindow
			.addEventListener("keydown", function (e)
			{
				e = e ? e : window.event;
				switch (e.keyCode)
				{
					case ESC:
						head.focus();
						e.preventDefault();
						e.stopPropagation();
						break;
				}
			});

		iframe.addEventListener("focus", function ()
		{
			autofocus(this.contentWindow.document);
		});

		if (modal.navigator)
		{
			for (i = 0; i < modal.navigator.length; i++)
			{
				if (this.contentWindow.location.href.endsWith(modal.navigator[i]))
				{
					var index = i;
					if (index > 0)
					{
						frst.style.display = "block";
						prev.style.display = "block";
						frst.onclick = function ()
						{
							iframe.src = modal.navigator[0];
							return false;
						};

						prev.onclick = function ()
						{
							iframe.src = modal.navigator[index - 1];
							return false;
						};
					} else
					{
						frst.onclick = null;
						prev.onclick = null;
						frst.style.display = "none";
						prev.style.display = "none";
					}

					navigator.innerHTML = "Registro " + (i + 1) + " de " + modal.navigator.length;
					navigator.style.display = "block";
					navigator.onclick = function ()
					{
						iframe.src = modal.navigator[index];
					}

					if (index < modal.navigator.length - 1)
					{
						next.onclick = function ()
						{
							iframe.src = modal.navigator[index + 1];
							return false;
						};

						last.onclick = function ()
						{
							iframe.src = modal.navigator[modal.navigator.length - 1];
							return false;
						};

						next.style.display = "block";
						last.style.display = "block";
					} else
					{
						next.onclick = null;
						last.onclick = null;
						next.style.display = "none";
						last.style.display = "none";
					}

					head.onkeydown = function (e)
					{
						e = e ? e : window.event;
						switch (e.keyCode)
						{
							case END:
								if (index < modal.navigator.length - 1)
									iframe.src = modal.navigator[modal.navigator.length - 1];
								break;
							case HOME:
								if (index > 0)
									iframe.src = modal.navigator[0];
								break;
							case UP:
							case LEFT:
								if (index > 0)
									iframe.src = modal.navigator[index - 1];
								break;
							case DOWN:
							case RIGHT:
								if (index < modal.navigator.length - 1)
									iframe.src = modal.navigator[index + 1];
								break;
						}
						e.preventDefault();
						e.stopPropagation();
					}
					;

					break;
				}
			}
		}
	};

	modal.setCloseable = function (closeable)
	{
		dialog.closeable = closeable;
		close.style.display = closeable ? "" : "none";
		return this;
	};

	modal.setTitle = function (title)
	{
		caption.innerHTML = title;
		return this;
	};

	modal.setTarget = function (target)
	{
		iframe.setAttribute('src', target);
		return this;
	};

	modal.setSize = function (width, height)
	{
		dialog.style.width = width;
		dialog.style.height = height;
		return this;
	};

	modal.setNavigator = function (navigator)
	{
		this.navigator = navigator;
		return this;
	};

	modal.getWindow = function ()
	{
		return iframe.contentWindow;
	};

	modal.get = function ()
	{
		this.arguments = arguments;
		this.show();
	};

	modal.ret = function ()
	{
		for (var i = 0; i < Math.min(arguments.length, this.arguments.length); i++)
		{
			if (this.arguments[i])
			{
				switch (this.arguments[i].tagName.toLowerCase())
				{
					case "input":
						this.arguments[i].value = arguments[i];
						break;
					case "textarea":
						this.arguments[i].innerHTML = arguments[i];
						break;
					case "select":
						this.arguments[i].value = arguments[i];
						break;
				}
			}
		}
		this.hide();
	};

	return modal;
}

window.addEventListener("load", function ()
{
	search('a[data-get]').forEach(function (a)
	{
		a.get = a.getAttribute('data-get').split(",")
			.map(function (e)
			{
				return e.trim();
			})
			.map(function (e)
			{
				return e !== 'null' ? select(e) : null;
			});

		a.onclick = function ()
		{
			if (this.get.some(function (e)
			{
				return e && e.value;
			}))
			{
				this.get
					.filter(function (e)
					{
						return e && e.value;
					})
					.forEach(function (e)
					{
						e.value = "";
					});
				return false;
			}

			var dialog = new Dialog();
			dialog.setTitle(this.getAttribute("title"))
				.setTarget(this.href)
				.get.apply(dialog, this.get);
			return false;
		};
	});

	search('a[data-ret], tr[data-ret], li[data-ret], td[data-ret]').forEach(function (e)
	{
		e.onmouseover = function ()
		{
			this.focus();
		};

		e.onmouseout = function ()
		{
			this.blur();
		};

		e.onclick = function ()
		{
			var ret = this.getAttribute("data-ret")
				.split(this.getAttribute("data-sep") ?
					this.getAttribute("data-sep") : ",")
				.map(function (e)
				{
					return e.trim();
				});
			window.frameElement.dialog.ret.apply(window.frameElement.dialog, ret);
			return false;
		};

		e.onkeydown = function (e)
		{
			e = e ? e : window.event;
			if (e.keyCode === 13)
				this.onclick();
			return true;
		};
	});

	search('a.Hide').forEach(function (a)
	{
		a.onclick = function ()
		{
			if (window.frameElement
				&& window.frameElement.dialog
				&& window.frameElement.dialog.hide)
				window.frameElement.dialog.hide();
			else
				window.close();
		};
	});
});


function registerTreeView(table)
{
	function depth(tr)
	{
		return parseInt(tr.getAttribute("data-depth"));
	}

	function expands(tr)
	{
		if (tr.children[0].innerHTML === '+')
			tr.children[0].innerHTML = '-';
		for (var next = tr.nextElementSibling;
			next && depth(next) > depth(tr);
			next = next.nextElementSibling)
			if (depth(tr) === depth(next) - 1)
				next.style.display = 'table-row';
	}

	function colapse(tr)
	{
		if (tr.children[0].innerHTML === '-')
			tr.children[0].innerHTML = '+';

		for (var next = tr.nextElementSibling;
			next && depth(next) > depth(tr);
			next = next.nextElementSibling)
		{
			next.style.display = 'none';
			if (next.children[0].innerHTML === '-')
				next.children[0].innerHTML = '+';
		}
	}

	function parent(tr)
	{
		for (var p = tr; p; p = p.previousSibling)
			if (depth(p) < depth(tr))
				return p;
	}

	function colorize(table)
	{
		Array.from(table.children).filter(e => e.tagName.toLowerCase() === "tbody").forEach(function (tbody)
		{
			Array.from(tbody.children).filter(e => e.style.display === 'table-row').forEach(function (tr, index)
			{
				tr.className = index % 2 ? 'odd' : 'even';
			});
		});
	}

	Array.from(table.children)
		.filter(e => e.tagName.toLowerCase() === "tbody")
		.forEach(function (tbody)
		{
			Array.from(tbody.children).forEach(function (tr)
			{
				if (depth(tr) === 0)
					tr.style.display = 'table-row';
				if (tr.nextElementSibling && depth(tr) < depth(tr.nextElementSibling))
				{
					tr.children[0].innerHTML = '+';
					tr.children[0].style.cursor = 'pointer';
					tr.children[0].onclick = function (e)
					{
						e = e ? e : window.event;
						if (e.stopPropagation)
							e.stopPropagation();
						else
							e.cancelBubble = true;

						if (this.innerHTML === '+')
							expands(this.parentNode);
						else if (this.innerHTML === '-')
							colapse(this.parentNode);

						colorize(table);
					};
				} else
					tr.children[0].innerHTML = ' ';

				if (tr.getAttribute("data-expanded"))
				{
					for (var p = tr; p; p = parent(p))
						expands(p);
					setTimeout(() => tr.scrollIntoView(), 0);
				}
			});
		});

	var selector = Array.from(table.children)
		.filter(e => e.tagName.toLowerCase() === "thead")
		.map(e => e.children[0])
		.map(e => e.children[0])[0];
	selector.innerHTML = "+";
	selector.style.cursor = "pointer";

	selector.onclick = function (e)
	{
		e = e ? e : window.event;
		if (e.stopPropagation)
			e.stopPropagation();
		else
			e.cancelBubble = true;

		switch (this.innerHTML)
		{
			case '+':
				this.innerHTML = '-';
				Array.from(table.children).filter(e => e.tagName.toLowerCase() === "tbody").forEach(function (tbody)
				{
					Array.from(tbody.children).forEach(e => expands(e));
				});

				break;
			case '-':
				this.innerHTML = '+';
				Array.from(table.children).filter(e => e.tagName.toLowerCase() === "tbody").forEach(function (tbody)
				{
					Array.from(tbody.children).forEach(e => colapse(e));
				});

				break;
		}
		colorize(table);
	};

	colorize(table);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('table.TREEVIEW, table.treeview, table.TreeView, table.Treeview, table.treeView'))
		.forEach(e => registerTreeView(e));
});
function DateFormat(format)
{
	this.format = function (date)
	{
		var result = "";
		var regex = /d+|M+|y+|H+|m+|s+|./g;
		while ((e = regex.exec(format)))
		{
			switch (e[0][0])
			{
				case 'd':
					var value = date.getDate().toString();
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				case 'M':
					var value = (date.getMonth() + 1).toString();
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				case 'y':
					var value = date.getFullYear().toString();
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				case 'H':
					var value = date.getHours().toString();
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				case 'm':
					var value = date.getMinutes().toString();
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				case 's':
					var value = date.getSeconds().toString();
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				default:
					result += e[0];
			}
		}
		return result;
	};

	this.parse = function(string)
	{
		var d = 0;
		var M = 0;
		var y = 0;
		var H = 0;
		var m = 0;
		var s = 0;
		var regex = /d+|M+|y+|H+|m+|s+|./g;
		while ((e = regex.exec(format)))
		{
			switch (e[0][0])
			{
				case 'd':
					var value = string.slice(0, e[0].length);
					d = Number(value);
					string = string.slice(e[0].length);
					break;
				case 'M':
					var value = string.slice(0, e[0].length);
					M = Number(value);
					string = string.slice(e[0].length);
					break;
				case 'y':
					var value = string.slice(0, e[0].length);
					y = Number(value);
					string = string.slice(e[0].length);
					break;
				case 'H':
					var value = string.slice(0, e[0].length);
					H = Number(value);
					string = string.slice(e[0].length);
					break;
				case 'm':
					var value = string.slice(0, e[0].length);
					m = Number(value);
					string = string.slice(e[0].length);
					break;
				case 's':
					var value = string.slice(0, e[0].length);
					s = Number(value);
					string = string.slice(e[0].length);
					break;
				default:
					string = string.slice(1);
			}
		}
		return new Date(y, M - 1, d, H, m, s);
	};
}
function Desktop(desktop)
{
	var desktopIcons = Array.from(desktop.getElementsByTagName("li"));

	desktopIcons.forEach(function (e)
	{
		new DesktopIcon(e);
	});

	desktopIcons.forEach(function (e)
	{
		e.style.visibility = "visible";
	});

	function DesktopIcon(desktopIcon)
	{
		var icons = $(desktopIcon).children("ul", "li");

		if (icons.length > 0)
		{
			desktopIcon.onclick = function ()
			{
				var reset = desktop.appendChild
						(new Reset($(desktop).children("li")));
				desktop.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					desktop.appendChild(icons[i]);
				desktop.appendChild(reset);
				return false;
			};
		}

		function Reset(icons)
		{
			var li = document.createElement("li");
			li.style.visibility = "visible";

			var i = li.appendChild(document.createElement("i"));
			i.innerHTML = "&#x2023";
			i.style.color = "#432F21";

			var label = li.appendChild(document.createElement("label"));
			label.innerHTML = "Retornar";
			label.style.color = "#432F21";

			li.onclick = function ()
			{
				desktop.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					desktop.appendChild(icons[i]);
				return false;
			};
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	search("ul.DESKTOP").forEach(function (e)
	{
		new Desktop(e);
	});
});

window.addEventListener("load", function ()
{
	window.setInterval(function ()
	{
		search("*[data-switch]").forEach(function (node)
		{
			var innerHTML = node.innerHTML;
			var dataSwitch = node.getAttribute('data-switch');
			node.innerHTML = dataSwitch;
			node.setAttribute('data-switch', innerHTML);
		});
	}, 500);
});

function clock()
{
	for (var i = 0; i < document.all.length; i++)
	{
		var node = document.all[i];
		if (node.getAttribute("data-clock")
				&& node.getAttribute("data-paused") !== 'true')
		{
			var time = Number(node.getAttribute("data-clock"));
			node.setAttribute("data-clock", time + 1);
			node.innerHTML = new Duration(time).toString();
		}

		if (node.onClockTick)
			node.onClockTick();
	}
}

window.addEventListener("load", function ()
{
	clock();
	window.setInterval(clock, 1000);
});


window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("textarea.Rich")).forEach(function (e)
	{
		CKEDITOR.config.toolbar =
				[
					{name: 'Font', items: ['Font', 'FontSize']},
					{name: 'Format', items: ['Bold', 'Italic', 'Underline', 'Strike', '-', 'Subscript', 'Superscript']},
					{name: 'Color', items: ['TextColor', 'BGColor']},
					{name: 'Align', items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', '-', 'JustifyBlock']},
					{name: 'Identation', items: ['Outdent', 'Indent']},
					{name: 'Clear', items: ['RemoveFormat']},
					{name: 'List', items: ['NumberedList', 'BulletedList']},
					{name: 'Links', items: ['Link', 'Unlink']},
					{name: 'Insert', items: ['HorizontalRule', 'Table', 'Smiley']},
					{name: 'Tools', items: ['Maximize']}
				];

		var tabindex = e.getAttribute("tabindex");
		if (tabindex)
		{
			e.removeAttribute("tabindex");
			CKEDITOR.config.tabIndex = tabindex;
		}

		if (e.getAttribute("autofocus"))
			e.removeAttribute("autofocus");

		if (document.activeElement === e)
			CKEDITOR.config.startupFocus = true;

		CKEDITOR.config.language = 'pt-br';
		CKEDITOR.config.allowedContent = true;
		CKEDITOR.config.extraPlugins = 'maximize';
		CKEDITOR.config.width = "100%";
		CKEDITOR.config.height = e.parentNode.clientHeight + "px";
		CKEDITOR.config.resize_enabled = false;
		CKEDITOR.config.contentsCss = '../gate/Gate.css';
		CKEDITOR.replace(e.getAttribute("id"));
	});
});
window.addEventListener("load", function ()
{
	document.addEventListener("fullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.fullscreen ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	document.addEventListener("mozfullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.mozFullScreen ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	document.addEventListener("webkitfullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.webkitIsFullScreen ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	document.addEventListener("msfullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.msFullscreenElement ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
	{
		e.innerHTML =
				!document.fullscreen && !document.mozFullScreen
				&& !document.webkitIsFullScreen && !document.msFullscreenElement
				? "<i >&#x2250;</i>" : "<i >&#x2251;</i>";

		e.addEventListener("click", function ()
		{
			if (!document.fullscreen && !document.mozFullScreen
					&& !document.webkitIsFullScreen && !document.msFullscreenElement)
			{
				if (document.documentElement.requestFullscreen)
					document.documentElement.requestFullscreen();
				else if (document.documentElement.mozRequestFullScreen)
					document.documentElement.mozRequestFullScreen();
				else if (document.documentElement.webkitRequestFullScreen)
					document.documentElement.webkitRequestFullScreen();
			} else
			{
				if (document.exitFullscreen)
					document.exitFullscreen();
				else if (document.webkitExitFullscreen)
					document.webkitExitFullscreen();
				else if (document.mozCancelFullScreen)
					document.mozCancelFullScreen();
			}
		});
	});
});

window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("form")).forEach(function (form)
	{
		form.addEventListener("submit", function (e)
		{
			if (this.hasAttribute("data-cancel"))
			{
				Message.error(this.getAttribute("data-cancel"), 2000);
				e.preventDefault();
				e.stopImmediatePropagation();

			} else if (this.hasAttribute("data-confirm") && !confirm(this.getAttribute("data-confirm")))
			{
				e.preventDefault();
				e.stopImmediatePropagation();
			} else if (this.target === "_dialog")
			{
				new Dialog()
					.setTitle(this.getAttribute("title"))
					.setOnHide(this.getAttribute("data-onHide"))
					.setNavigator(this.getAttribute("data-navigator") ?
						eval(this.getAttribute("data-navigator")) : null)
					.show();
			}
		});

		form.addEventListener("keydown", function (event)
		{
			event = event ? event : window.event;

			switch (event.keyCode)
			{
				case ENTER:
					var element = document.activeElement;
					switch (element.tagName.toLowerCase())
					{
						case "a":
						case "button":
							element.click();
							event.preventDefault();
							event.stopImmediatePropagation();
							break;
						case "select":
						case "textarea":
							if (!event.ctrlKey)
								break;
						case "input":
							var commit = this.querySelector(".Commit");
							if (commit)
							{
								commit.focus();
								commit.click();
								event.preventDefault();
								event.stopImmediatePropagation();
							} else if (this.hasAttribute("action"))
							{
								let button = document.createElement("button");
								button.style.display = "none";
								this.appendChild(button);
								button.click();
								this.removeChild(button);
								event.preventDefault();
								event.stopImmediatePropagation();
							}
							break;
					}
					break;
				case ESC:
					var element = document.activeElement;
					switch (element.tagName.toLowerCase())
					{
						case "select":
							if (!event.ctrlKey)
								break;
						case "input":
						case "a":
						case "button":
						case "textarea":
							var cancel = this.querySelector(".Cancel");
							if (cancel)
							{
								cancel.focus();
								cancel.click();
								event.preventDefault();
								event.stopImmediatePropagation();
							}
							break;
					}
					break;

			}
		});
	});
});



function Progress(process)
{
	var ws = new WebSocket("ws://" + window.location.host + "/" +
		window.location.pathname.split("/")[1] + "/Progress/" + process);
	ws.onmessage = function (event)
	{
		event = JSON.parse(event.data);

		event.toString = function ()
		{
			if (this.done && this.done !== -1)
				if (this.todo && this.todo !== -1)
					return this.done + "/" + this.todo;
				else
					return this.done.toString();
			else
				return "...";
		};

		switch (event.event)
		{
			case "Progress":
				for (var i = 0; i < document.all.length; i++)
					if (document.all[i].onProgressEvent)
						document.all[i].onProgressEvent(event);
				break;

			case "Redirect":
				window.location.href = event.url;
				break;
		}
	};
}
function autofocus(d)
{
	var elements = Array.from(d.querySelectorAll('[autofocus]'));
	if (elements.length !== 0)
		return elements[0].focus();

	var elements = Array.from(d.querySelectorAll('[tabindex]'))
		.filter(function (e)
		{
			return Number(e.getAttribute("tabindex")) > 0;
		});

	if (elements.length === 0)
		return -1;

	var element = elements.reverse().reduce(function (a, b)
	{
		return  Number(a.getAttribute("tabindex")) < Number(b.getAttribute("tabindex")) ? a : b;
	});

	if (element)
	{
		element.focus();
		if (element.onfocus)
			element.onfocus();
	}
}

window.addEventListener("load", function ()
{
	autofocus(document);
});
window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("tbody")).forEach(function (element)
	{
		element.sort = function (index, mode)
		{
			var children = Array.from(this.children);
			children.sort(function (e1, e2)
			{
				e1 = e1.children[index];
				var s1 = e1.hasAttribute("data-value") ?
					Number(e1.getAttribute("data-value")) :
					e1.innerHTML.trim();

				e2 = e2.children[index];
				var s2 = e2.hasAttribute("data-value") ?
					Number(e2.getAttribute("data-value")) :
					e2.innerHTML.trim();

				if (mode === "U")
					return s1 > s2 ? 1 : s1 < s2 ? -1 : 0;
				else if (mode === "D")
					return s1 > s2 ? -1 : s1 < s2 ? 1 : 0;
			});
			for (var i = 0; i < children.length; i++)
				this.appendChild(children[i]);
		};
	});

	Array.from(document.querySelectorAll("th[data-sortable] > a")).forEach(function (link)
	{
		link.parentNode.setAttribute("data-sortable", "N");
		link.addEventListener("click", function ()
		{
			switch (this.parentNode.getAttribute("data-sortable"))
			{
				case "N":
					Array.from(this.parentNode.parentNode.children)
						.forEach(e => e.setAttribute("data-sortable", "N"));
					this.parentNode.setAttribute("data-sortable", "U");
					break;
				case "U":
					this.parentNode.setAttribute("data-sortable", "D");
					break;
				case "D":
					this.parentNode.setAttribute("data-sortable", "U");
					break;
			}

			Array.from(this.parentNode.parentNode.parentNode.parentNode.children)
				.filter(e => e.tagName.toUpperCase() === "TBODY")
				.forEach(e => e.sort(Array.prototype.indexOf
						.call(this.parentNode.parentNode.children, this.parentNode),
						this.parentNode.getAttribute("data-sortable")));
		});
	});
});
function Message(type, message, timeout)
{
	var modal = new Modal();
	modal.style.display = "flex";
	modal.style.alignItems = "center";
	modal.style.justifyContent = "center";

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.style.width = "800px";
	dialog.style.height = "260px";
	dialog.style.display = "flex";
	dialog.style.borderRadius = "5px";
	dialog.style.alignItems = "center";
	dialog.style.justifyContent = "center";
	dialog.style.border = "4px solid #767d90";
	dialog.style.boxShadow = "3px 10px 5px 0px rgba(0,0,0,0.75)";

	var icon = dialog.appendChild(window.top.document.createElement('div'));
	icon.style.width = "240px";
	icon.style.height = "100%";
	icon.style.fontFamily = "gate";
	icon.style.fontSize = "120px";
	icon.style.display = "flex";
	icon.style.alignItems = "center";
	icon.style.justifyContent = "center";
	icon.style.background = "linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%)";

	var body = dialog.appendChild(window.top.document.createElement('div'));
	body.innerHTML = message;
	body.style.width = "100%";
	body.style.height = "100%";
	body.style.padding = "8px";
	body.style.display = "flex";
	body.style.flexWrap = "wrap";
	body.style.fontSize = "20px";
	body.style.alignItems = "center";
	body.style.justifyContent = "center";
	body.style.backgroundColor = "#FFFFFF";

	modal.onclick = dialog.onclick = icon.onclick = body.onclick = function ()
	{
		modal.hide();
	};

	switch (type)
	{
		case "SUCCESS":
			icon.innerHTML = "&#X1000";
			icon.style.color = "#006600";
			body.style.color = "#006600";
			break;
		case "WARNING":
			icon.innerHTML = "&#X1007";
			icon.style.color = "#666600";
			body.style.color = "#666600";
			break;
		case "ERROR":
			icon.innerHTML = "&#X1001";
			icon.style.color = "#660000";
			body.style.color = "#660000";
			break;
	}

	if (timeout)
		window.top.setTimeout(function ()
		{
			modal.hide();
		}, timeout);


	modal.show();
}

Message.success = function (message, timeout)
{
	Message("SUCCESS", message, timeout);
};

Message.warning = function (message, timeout)
{
	Message("WARNING", message, timeout);
};

Message.error = function (message, timeout)
{
	Message("ERROR", message, timeout);
};

Message.show = function (status, timeout)
{
	switch (status.type)
	{
		case "SUCCESS":
			Message.success(status.message, timeout);
			break;
		case "WARNING":
			Message.warning(status.message, timeout);
			break;
		case "ERROR":
			Message.error(status.message, timeout);
			break;
	}

};

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("a.Return")).forEach(function (e)
	{
		e.onclick = function ()
		{
			window.history.go(-1);
		};
	});
});
function DeskMenu(deskMenu)
{
	var desktopIcons = Array.from(deskMenu.getElementsByTagName("li"));

	desktopIcons.forEach(function (e)
	{
		new DeskMenuIcon(e);
	});

	function DeskMenuIcon(deskMenuIcon)
	{
		var icons = $(deskMenuIcon).children("ul", "li");

		if (icons.length > 0)
		{
			deskMenuIcon.onclick = function ()
			{
				var reset = deskMenu.appendChild
					(new Reset($(deskMenu).children("li"),
						this.offsetWidth, this.offsetHeight));
				deskMenu.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskMenu.appendChild(icons[i]);
				deskMenu.appendChild(reset);
				return false;
			};
		}

		function Reset(icons, width, height)
		{
			var li = document.createElement("li");
			li.className = "Reset";
			li.style.width = width + "px";
			li.style.height = height + "px";

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";
			a.setAttribute("data-icon", "\u2232");

			li.onclick = function ()
			{
				deskMenu.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskMenu.appendChild(icons[i]);
				return false;
			};
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskMenu")).forEach(function (e)
	{
		new DeskMenu(e);
	});
});
function DeskPane(deskMenu)
{
	var desktopIcons = Array.from(deskMenu.getElementsByTagName("li"));

	desktopIcons.forEach(function (e)
	{
		new DeskPaneIcon(e);
	});

	function DeskPaneIcon(deskMenuIcon)
	{
		var icons = $(deskMenuIcon).children("ul", "li");

		if (icons.length > 0)
		{
			deskMenuIcon.onclick = function ()
			{
				var reset = deskMenu.appendChild
					(new Reset($(deskMenu).children("li"),
						this.offsetWidth, this.offsetHeight));
				deskMenu.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskMenu.appendChild(icons[i]);
				deskMenu.appendChild(reset);
				return false;
			};
		}

		function Reset(icons, width, height)
		{
			var li = document.createElement("li");
			li.className = "Reset";
			li.style.width = width + "px";
			li.style.height = height + "px";
			li.style.color = "#006600";

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";

			var i = a.appendChild(document.createElement("i"));
			i.innerHTML = "&#X2232";

			li.onclick = function ()
			{
				deskMenu.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskMenu.appendChild(icons[i]);
				return false;
			};
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskPane")).forEach(function (e)
	{
		new DeskPane(e);
	});
});