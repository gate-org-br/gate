if (!Array.prototype.flatMap)
	Object.defineProperties(Array.prototype,
		{
			'flatMap':
				{
					value: function (lambda)
					{
						return Array.prototype.concat.apply([], this.map(lambda));
					},
					writeable: false,
					enumerable: false
				}
		});
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
		var items = [];
		this.forEach(function (value, name)
		{
			items.push(name);
		});
		return iteratorFor(items);
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
/* global ENTER, ESC */

if (!document.querySelectorAll)
	window.location = '../gate/NAVI.jsp';

function resolve(string)
{
	var result = string;

	var parameters = string.match(/([@][{][^}]*[}])/g);
	if (parameters)
		parameters.forEach(function (e)
		{
			var parameter = document.getElementById(e.substring(2, e.length - 1));
			result = result.replace(e, parameter ? encodeURIComponent(parameter.value) : "");
		});

	var parameters = string.match(/([!][{][^}]*[}])/g);
	if (parameters)
		for (var i = 0; i < parameters.length; i++)
		{
			var parameter = confirm(decodeURIComponent(parameters[i]
				.substring(2, parameters[i].length - 1)));
			result = result.replace(parameters[i], parameter ? "true" : "false");
		}

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

	return result;
}

window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("select")).forEach(function (element)
	{
		element.onclick = function (event)
		{
			event = event ? event : window.event;
			if (event.stopPropagation)
				event.stopPropagation();
			else
				event.cancelBubble = true;
		};
	});

	Array.from(document.querySelectorAll("input.SELECTOR")).forEach(function (element)
	{
		element.onchange = function ()
		{
			var selector = 'input[type="checkbox"][name="' + this.getAttribute('data-target') + '"]';
			Array.from(document.querySelectorAll(selector)).forEach(target => target.checked = element.checked);
		};
	});
});
function DeskMenu(deskMenu)
{
	Array.from(deskMenu.getElementsByTagName("li"))
		.forEach(e => new DeskMenuIcon(e));

	function DeskMenuIcon(deskMenuIcon)
	{
		var icons = Array.from(deskMenuIcon.children)
			.filter(e => e.tagName.toLowerCase() === "ul")
			.flatMap(e => Array.from(e.children));

		if (icons.length > 0)
		{
			deskMenuIcon.onclick = function ()
			{
				var reset = deskMenu.appendChild
					(new Reset(Array.from(deskMenu.children).filter(e => e.tagName.toLowerCase() === "li"),
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
	Array.from(document.querySelectorAll("ul.DeskMenu"))
		.forEach(element => new DeskMenu(element));
});
function DeskPane(deskPane)
{
	Array.from(deskPane.children)
		.filter(li => li.tagName.toLowerCase() === "li")
		.forEach(function (li)
		{
			Array.from(li.children)
				.filter(a => a.tagName.toLowerCase() === "a")
				.forEach(a => new DeskPaneIcon(a));
		});

	function DeskPaneIcon(deskPaneIcon)
	{
		var icons = Array.from(deskPaneIcon.parentNode.children)
			.filter(e => e.tagName.toLowerCase() === "ul")
			.flatMap(e => Array.from(e.children));

		if (icons.length > 0)
		{
			deskPaneIcon.addEventListener("click", function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
				var reset = deskPane.appendChild
					(new Reset(Array.from(deskPane.children).filter(e => e.tagName.toLowerCase() === "li"),
						this.offsetWidth, this.offsetHeight));
				deskPane.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskPane.appendChild(icons[i]);
				deskPane.appendChild(reset);
			});
		}

		function Reset(icons, width, height)
		{
			var li = document.createElement("li");
			li.className = "Reset";
			li.style.width = width + "px";
			li.style.height = height + "px";
			li.style.color = "@G";

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";

			var i = a.appendChild(document.createElement("i"));
			i.innerHTML = "&#X2232";

			li.onclick = function ()
			{
				deskPane.innerHTML = "";
				for (var i = 0; i < icons.length; i++)
					deskPane.appendChild(icons[i]);
				return false;
			};
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskPane"))
		.forEach(element => new DeskPane(element));
});
var CSV =
	{
		parse: function (text)
		{
			var a = [];
			text.replace(/(?!\s*$)\s*(?:'([^'\\]*(?:\\[\S\s][^'\\]*)*)'|"([^"\\]*(?:\\[\S\s][^"\\]*)*)"|([^,;'"\s\\]*(?:\s+[^,;'"\s\\]+)*))\s*(?:[,;]|$)/g,
				function (g0, g1, g2, g3)
				{
					if (g1 !== undefined)
						a.push(g1.replace(/\\'/g, "'"));
					else if (g2 !== undefined)
						a.push(g2.replace(/\\"/g, '"'));
					else if (g3 !== undefined)
						a.push(g3);
					return '';
				});
			if (/,\s*$/.test(text))
				a.push('');
			return a;
		}
	};
function Populator(options)
{
	this.populate = function (element)
	{
		while (element.firstChild)
			element.removeChild(element.firstChild);

		switch (element.tagName.toLowerCase())
		{
			case "select":
				element.value = undefined;

				element.appendChild(document.createElement("option"))
					.setAttribute("value", "");

				for (var i = 0; i < options.length; i++)
				{
					var option = element.appendChild(document.createElement("option"));
					option.innerHTML = options[i].label;
					option.setAttribute('value', options[i].value);
				}

				break;

			case "datalist":
				for (var i = 0; i < options.length; i++)
				{
					var option = element.appendChild(document.createElement("option"));
					option.innerHTML = options[i].label;
					option.setAttribute('data-value', options[i].value);
				}

				break;

		}
		return this;
	};
}



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
window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input[list]")).forEach(function (element)
	{
		element.setAttribute("autocomplete", "off");
		element.addEventListener("input", function ()
		{
			if (typeof this.value === "string")
			{
				var datalist = document.getElementById(this.getAttribute("list"));
				if (datalist.hasAttribute("data-populate-url"))
				{
					var len = 3;
					if (datalist.hasAttribute("data-populate-len"))
						len = parseInt(datalist.getAttribute("data-populate-len"));

					if (this.value.length < len)
					{
						datalist.removeAttribute("data-populate-filter");
						new Populator([]).populate(datalist);
					} else if (!datalist.hasAttribute("data-populate-filter")
						|| !this.value.toLowerCase().includes(
						datalist.getAttribute("data-populate-filter")
						.toLowerCase()))
					{
						this.blur();
						this.disabled = true;
						new URL(resolve(datalist.getAttribute("data-populate-url")))
							.get(options =>
							{
								new Populator(JSON.parse(options)).populate(datalist);
								this.disabled = false;
								this.focus();
								this.click();
								datalist.setAttribute("data-populate-filter", this.value);
							});
					}
				}
			}
		});
	});

	Array.from(document.querySelectorAll("input[list][data-populate-field]")).forEach(function (element)
	{
		element.addEventListener("change", function ()
		{
			var field = document
				.getElementById(this.getAttribute("data-populate-field"));
			field.value = null;

			var datalist = document.getElementById(this.getAttribute("list"));
			Array.from(datalist.children).filter(option => option.innerHTML === this.value
					|| option.innerHTML.toLowerCase() === this.value.toLowerCase())
				.forEach(option =>
				{
					this.value = option.innerHTML;
					field.value = option.getAttribute("data-value");
					field.dispatchEvent(new CustomEvent('field-populated',
						{detail: {source: this}}));
				});
		});
	});


	Array.from(document.querySelectorAll("input[list][data-populate-field], input[list][data-require-list]")).forEach(function (element)
	{
		element.addEventListener("input", function ()
		{
			var datalist = document.getElementById(this.getAttribute("list"));
			if (this.value.length > 0)
				if (Array.from(datalist.children).some(e => element.value === e.innerHTML
						|| e.innerHTML.toLowerCase() === element.value.toLowerCase()))
					element.setCustomValidity("");
				else
					element.setCustomValidity("Entre com um dos valores da lista");
		});
	});
});
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
	};

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

			request.open('GET', resolve(this.value), true);
			request.send(null);
		} else
		{
			request.open('GET', resolve(this.value), false);
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

			request.open("POST", resolve(this.value), true);
			request.send(data);
		} else
		{
			request.open("POST", resolve(this.value), false);
			request.send(data);
			if (request.status === 200)
				return request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	};

	this.go = function ()
	{
		window.location.href = resolve(this.value);
		return this;
	};

	this.toString = function ()
	{
		return this.value;
	};

	this.populate = function (css)
	{
		this.get(function (options)
		{
			if (options)
			{
				options = JSON.parse(options);
				var populator = new Populator(options);
				Array.from(document.querySelectorAll(css))
					.forEach(element => populator.populate(element));
			}
		});
		return this;
	};
}
/* global Message, Block, ENTER, ESC */

function Link(link, creator)
{
	var navigator;

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
		if (this.hasAttribute("data-disabled"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	});

	link.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-confirm")
			&& !confirm(this.getAttribute("data-confirm")))
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
		if (this.href.match(/([@][{][^}]*[}])/g)
			|| this.href.match(/([!][{][^}]*[}])/g)
			|| this.href.match(/([?][{][^}]*[}])/g))
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
					} else {
						new Dialog({creator: creator || this,
							title: this.getAttribute("title"),
							target: this.getAttribute("href"),
							blocked: Boolean(this.getAttribute("data-blocked")),
							navigator: navigator})
							.show();
					}
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

				case "_popup":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();
					Array.from(this.children)
						.filter(e => e.tagName.toLowerCase() === "div")
						.forEach(e => new Popup(e));
					break;
			}
		}
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
		navigator = value;
		return this;
	};

	this.get = function ()
	{
		return link;
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("a"))
		.forEach(a => new Link(a));

	document.documentElement.addEventListener("keydown", function (event)
	{
		switch (event.keyCode)
		{
			case ENTER:
				Array.from(document.querySelectorAll("a.Action")).forEach(e => e.click());
				break;
			case ESC:
				Array.from(document.querySelectorAll("a.Cancel")).forEach(e => e.click());
				break;

		}
	});
});

/* global Message, Block, ENTER, ESC */

function Button(button, creator)
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
		if (this.hasAttribute("data-disabled"))
		{
			event.preventDefault();
			event.stopPropagation();
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
		if (this.getAttribute("formaction") &&
			(this.getAttribute("formaction").match(/([@][{][^}]*[}])/g)
				|| this.getAttribute("formaction").match(/([!][{][^}]*[}])/g)
				|| this.getAttribute("formaction").match(/([?][{][^}]*[}])/g)))
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
					{
						new Dialog({creator: creator || this,
							title: this.getAttribute("title"),
							blocked: Boolean(this.getAttribute("data-blocked"))})
							.show();
					}

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

	this.get = function ()
	{
		return button;
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("button"))
		.forEach(button => new Button(button));

	document.documentElement.addEventListener("keydown", function (event)
	{
		switch (event.keyCode)
		{
			case ENTER:
				Array.from(document.querySelectorAll("button.Action")).forEach(e => e.click());
				break;
			case ESC:
				Array.from(document.querySelectorAll("button.Cancel")).forEach(e => e.click());
				break;

		}
	});
});

/* global ENTER, HOME, END, DOWN, UP */

function ActionHandler(element)
{
	element.setAttribute("tabindex", 1);
	element.onmouseover = () => element.focus();

	element.onkeydown = function (event)
	{
		event = event ? event : window.event;
		switch (event.keyCode)
		{
			case ENTER:
				this.onclick(event);
				return false;

			case HOME:
				var siblings = Array.from(this.parentNode.childNodes)
					.filter(node => node.tagName.toLowerCase() === "tr");
				if (siblings.length !== 0
					&& siblings[0].getAttribute("tabindex"))
					siblings[0].focus();
				return false;

			case END:
				var siblings = Array.from(this.parentNode.childNodes)
					.filter(node => node.tagName.toLowerCase() === "tr");
				if (siblings.length !== 0
					&& siblings[siblings.length - 1].getAttribute("tabindex"))
					siblings[siblings.length - 1].focus();
				return false;

			case UP:
				if (this.previousElementSibling &&
					this.previousElementSibling.getAttribute("tabindex"))
					this.previousElementSibling.focus();
				return false;

			case DOWN:
				if (this.nextElementSibling &&
					this.nextElementSibling.getAttribute("tabindex"))
					this.nextElementSibling.focus();
				return false;

			default:
				return true;
		}
	};

	if (!element.onclick)
		element.onclick = function (event)
		{
			this.blur();
			event = event || window.event;
			for (var parent = event.target || event.srcElement;
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

					if (!event.ctrlKey && this.getAttribute("data-target"))
						var navigator = Array.from(this.parentNode.children)
							.map(e => e.getAttribute("data-action"))
							.filter(e => e);

					var link = new Link(document.createElement("a"), element)
						.setAction(this.getAttribute("data-action"))
						.setTarget(event.ctrlKey ? "_blank" : this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
						.setNavigator(navigator)
						.get();
					document.body.appendChild(link);

					link.click();
					document.body.removeChild(link);
					break;
				case "post":
					var form = this.parentNode;
					while (form
						&& form.tagName.toLowerCase()
						!== 'form')
						form = form.parentNode;

					var button = new Button(document.createElement("button"), element)
						.setAction(this.getAttribute("data-action"))
						.setTarget(event.ctrlKey ? "_blank" : this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
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
	Array.from(document.querySelectorAll('*[data-action]')).forEach(element => new ActionHandler(element));
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
					.setBlock(this.getAttribute("data-block"))
					.setAlert(this.getAttribute("data-alert"))
					.setConfirm(this.getAttribute("data-confirm"))
					.get();
				document.body.appendChild(a);
				a.click();
				document.body.removeChild(a);
				break;
			case "post":
				var form = this.parentNode;
				while (form
					&& form.tagName.toLowerCase()
					!== 'form')
					form = form.parentNode;

				var button = new Button(document.createElement("button"))
					.setAction(this.getAttribute("data-action"))
					.setTarget(this.getAttribute("data-target"))
					.setTitle(this.getAttribute("title"))
					.setBlock(this.getAttribute("data-block"))
					.setAlert(this.getAttribute("data-alert"))
					.setConfirm(this.getAttribute("data-confirm"))
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
	Array.from(document.querySelectorAll('input[data-method], input[data-action], input[data-target]')).forEach(function (element)
	{
		new ChangeHandler(element);
	});
	Array.from(document.querySelectorAll('select[data-method], select[data-action], select[data-target]')).forEach(function (element)
	{
		new ChangeHandler(element);
	});
});

class NavBar
{
	constructor(links, element)
	{
		var index = 0;

		if (!element)
			element = document.createElement("div");
		element.className = "NavBar";
		this.element = () => element;

		var frst = element.appendChild(document.createElement("a"));
		var prev = element.appendChild(document.createElement("a"));
		var text = element.appendChild(document.createElement("label"));
		var next = element.appendChild(document.createElement("a"));
		var last = element.appendChild(document.createElement("a"));

		frst.setAttribute("href", "#");
		prev.setAttribute("href", "#");
		next.setAttribute("href", "#");
		last.setAttribute("href", "#");

		frst.innerHTML = "&#x2213;";
		prev.innerHTML = "&#x2212;";
		next.innerHTML = "&#x2211;";
		last.innerHTML = "&#x2216;";

		frst.addEventListener("click", () => this.go(links[0]));
		prev.addEventListener("click", () => this.go(links[index - 1]));
		next.addEventListener("click", () => this.go(links[index + 1]));
		last.addEventListener("click", () => this.go(links[links.length - 1]));

		this.go = function (url)
		{
			if (element.dispatchEvent(new CustomEvent('go', {cancelable: true, detail: {navbar: this, target: url}})))
				index = Math.max(links.indexOf(url), 0);

			frst.setAttribute("navbar-disabled", String(index === 0));
			prev.setAttribute("navbar-disabled", String(index === 0));
			next.setAttribute("navbar-disabled", String(index === links.length - 1));
			last.setAttribute("navbar-disabled", String(index === links.length - 1));
			text.innerHTML = "Registro " + (index + 1) + " de " + links.length;
		};
	}
}
function Mask(element)
{
	var changed = false;
	element.setAttribute("autocomplete", "off");
	const MASKS = {'#': "[0-9]", '_': "[a-zA-Z]", '*': "[0-9a-zA-Z]"};

	var mask = element.getAttribute("data-mask");
	var placeholder = element.getAttribute("placeholder");
	if (!placeholder || placeholder.length !== mask.length)
	{
		placeholder = "";
		for (var i = 0; i < mask.length; i++)
			placeholder += MASKS[mask.charAt(i)] ? "_" : mask.charAt(i);
		element.setAttribute("placeholder", placeholder);
	}

	element.getCursor = function ()
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
			document.write("Navegador muito antigo. Favor atualizar.");
	};

	element.setCursor = function (i)
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
			document.write("Navegador muito antigo. Favor atualizar.");
		return false;
	};

	element.onkeydown = function (event)
	{
		event = event ? event : window.event;
		switch (event.keyCode)
		{
			// ///////////////////////////////////////////////////////
			// ESQUERDA
			// ///////////////////////////////////////////////////////
			case 37:

				for (var i = this.getCursor() - 1; i >= 0; i--)
					if (MASKS[mask.charAt(i)])
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// DIREITA
				// ///////////////////////////////////////////////////////
			case 39:

				for (var i = this.getCursor() + 1; i <= mask.length; i++)
					if (i === mask.length || MASKS[mask.charAt(i)])
						return this.setCursor(i);
				return false;
				// ///////////////////////////////////////////////////////
				// BACKSPACE
				// ///////////////////////////////////////////////////////
			case 8:

				for (var i = this.getCursor() - 1; i >= 0; i--)
				{
					if (MASKS[mask.charAt(i)])
					{
						this.value = this.value.substr(0, i) + placeholder[i] + this.value.substr(i + 1);
						this.setCursor(i);
						changed = true;
						break;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// Delete
				// ///////////////////////////////////////////////////////
			case 46:

				for (var i = this.getCursor(); i < mask.length; i++)
				{
					if (MASKS[mask.charAt(i)])
					{
						this.value = this.value.substr(0, i) + placeholder[i] + this.value.substr(i + 1);

						this.setCursor(i + 1);
						while (this.getCursor() < mask.length
							&& !MASKS[mask.charAt(this.getCursor())])
							this.setCursor(this.getCursor() + 1);
						changed = true;
						break;
					}
				}
				return false;
				// ///////////////////////////////////////////////////////
				// END
				// ///////////////////////////////////////////////////////
			case 35:
				return this.setCursor(mask.length);
				// ///////////////////////////////////////////////////////
				// HOME
				// ///////////////////////////////////////////////////////
			case 36:
				return this.setCursor(0);
		}

		return true;
	};

	element.onkeypress = function (event)
	{
		event = event ? event : window.event;
		switch (event.keyCode)
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

				var c = String.fromCharCode(event.which ? event.which : event.keyCode);
				if (event.ctrlKey)
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

				for (var i = this.getCursor(); i < mask.length; i++)
				{
					if (MASKS[mask.charAt(i)] && c.match(MASKS[mask.charAt(i)]))
					{
						this.value = this.value.substr(0, i) + c + this.value.substr(i + 1);
						this.setCursor(i + 1);

						while (this.getCursor() < mask.length
							&& !MASKS[mask.charAt(this.getCursor())])
							this.setCursor(this.getCursor() + 1);

						changed = true;
						return false;
					}
				}

				return false;
		}
	};

	element.onpaste = function (event)
	{
		var data = event.clipboardData.getData("text");
		if (data)
		{
			var index = 0;
			var cursor = 0;

			while (index < data.length && cursor < mask.length)
			{
				while (cursor < mask.length && !MASKS[mask.charAt(cursor)])
					cursor++;
				while (cursor < mask.length && index < data.length && !data[index].match(MASKS[mask.charAt(cursor)]))
					index++;

				if (cursor < mask.length && index < data.length)
				{
					this.value = this.value.substr(0, cursor) + data[index] + this.value.substr(cursor + 1);
					changed = true;
					cursor++;
					index++;
				}
			}
		}
		return false;
	};

	element.onfocus = function ()
	{
		if (!this.value && placeholder)
			this.value = placeholder;

		setTimeout(() =>
		{
			this.setCursor(0);
			while (this.getCursor() < mask.length
				&& !MASKS[mask.charAt(this.getCursor())])
				this.setCursor(this.getCursor() + 1);
		}, 0);
	};

	element.onblur = function ()
	{
		if (this.value === placeholder)
			this.value = '';

		if (changed)
			this.dispatchEvent(new Event('change'));
		changed = false;
	};

	element.oncut = () => false;
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('input[data-mask]'))
		.forEach(element => new Mask(element));
});
class Modal
{
	constructor(options)
	{
		var element = window.top.document.createElement('div');
		element.className = "Modal";
		this.element = () => element;

		var blocked = options ? options.blocked : null;
		this.blocked = () => blocked;

		var creator = options ? options.creator : null;
		this.creator = () => creator ? creator : element;

		if (!blocked)
			element.addEventListener("click", event =>
				(event.target === element || event.srcElement === element) && this.hide());
	}

	show()
	{
		if (this.creator().dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
		{
			window.top.document.body.style.overflow = "hidden";
			window.top.document.body.appendChild(this.element());
			this.element().dispatchEvent(new CustomEvent('show', {detail: {modal: this}}));
		}

		return this;
	}

	hide()
	{
		if (this.element().parentNode
			&& this.creator().dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}})))
		{
			window.top.document.body.style.overflow = "";
			this.element().parentNode.removeChild(this.element());
		}

		return this;
	}
}
class Block extends Modal
{
	constructor(text)
	{
		super({blocked: true});

		var dialog = this.element()
			.appendChild(window.top.document.createElement('div'));
		dialog.className = "Block";

		var head = dialog.appendChild(window.top.document.createElement('div'));
		head.setAttribute("tabindex", "1");
		head.focus();

		head.appendChild(window.top.document.createElement('label'))
			.innerHTML = "Aguarde";

		var body = dialog.appendChild(window.top.document.createElement('div'));

		body.appendChild(window.top.document.createElement('label'))
			.innerHTML = text;

		body.appendChild(window.top.document.createElement('progress'));

		var foot = dialog.appendChild(window.top.document.createElement('div'));

		foot.innerHTML = "00:00:00";
		foot.setAttribute("data-clock", '0');

		this.show();
	}
}

Block.show = function (text)
{
	if (!Block.instance)
		Block.instance = new Block(text);
};

Block.hide = function ()
{
	if (Block.block)
	{
		Block.instance.hide();
		Block.instance = null;
	}
};

window.addEventListener("load", function ()
{
	Block.hide();

	Array.from(document.querySelectorAll("form")).forEach(function (element)
	{
		element.addEventListener("submit", function ()
		{
			if (this.getAttribute("data-block"))
				Block.show(this.getAttribute("data-block"));
		});
	});

	Array.from(document.querySelectorAll("a")).forEach(function (element)
	{
		element.addEventListener("click", function ()
		{
			if (this.getAttribute("data-block"))
				Block.show(this.getAttribute("data-block"));
		});
	});

	Array.from(document.querySelectorAll("button")).forEach(function (button)
	{
		button.addEventListener("click", function ()
		{
			if (button.getAttribute("data-block"))
				if (button.form)
					button.form.addEventListener("submit", function (event)
					{
						Block.show(button.getAttribute("data-block"));
						event.target.removeEventListener(event.type, arguments.callee);
					});
				else
					Block.show(this.getAttribute("data-block"));
		});
	});
});
function Slider(element, value, next, prev, format)
{
	element.classList.add("Slider");
	element.setAttribute('tabindex', 0);
	element.addEventListener("mouseover", () => element.focus());
	element.addEventListener("mouseoust", () => element.blur());

	if (!format)
		format = e => e;

	element.addEventListener((/Firefox/i.test(navigator.userAgent))
		? "DOMMouseScroll" : "mousewheel",
		function (event)
		{
			event.preventDefault();

			if (event.detail)
				if (event.detail > 0)
					gonext.click();
				else
					goprev.click();
			else if (event.wheelDelta)
				if (event.wheelDelta > 0)
					gonext.click();
				else
					goprev.click();
		});


	element.addEventListener("keydown", function (event)
	{
		switch (event.keyCode)
		{
			case 38:
				goprev.click();
				break;
			case 40:
				gonext.click();
				break;
		}
		event.preventDefault();
	});

	var goprev = element.appendChild(document.createElement("a"));
	goprev.href = "#";
	goprev.appendChild(document.createElement("i")).innerHTML = "&#X2278;";
	goprev.addEventListener("click", () => update(prev(value)));

	var prev2 = element.appendChild(document.createElement("label"));
	var prev1 = element.appendChild(document.createElement("label"));
	var main = element.appendChild(document.createElement("label"));
	var next1 = element.appendChild(document.createElement("label"));
	var next2 = element.appendChild(document.createElement("label"));

	var gonext = element.appendChild(document.createElement("a"));
	gonext.href = "#";
	gonext.appendChild(document.createElement("i")).innerHTML = "&#X2276;";
	gonext.addEventListener("click", () => update(next(value)));

	this.value = () => value;

	var update = function (val)
	{
		if (val !== null)
		{
			if (!element.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {slider: this, value: val}})))
				return false;

			value = val;
			main.innerHTML = format(value);
			prev1.innerHTML = '-';
			prev2.innerHTML = '-';
			next1.innerHTML = '-';
			next2.innerHTML = '-';

			var data = prev(value);
			if (data !== null)
			{
				prev1.innerHTML = format(data);
				var data = prev(data);
				if (data !== null)
					prev2.innerHTML = format(data);
			}

			var data = next(value);
			if (data !== null)
			{
				next1.innerHTML = format(data);
				var data = next(data);
				if (data !== null)
					next2.innerHTML = format(data);
			}

			element.dispatchEvent(new CustomEvent('update', {detail: {slider: this}}));
		}
	};

	this.element = () => element;
	update(value);
}
/* global DateFormat */

var calendars = {};
function Calendar(element, init)
{
	var selection = [];

	var max = undefined;
	if (init && init.max)
		max = init.max;

	var month = new Date();
	if (init && init.month)
		month = init.month;
	month.setHours(0, 0, 0, 0);

	element.className = "Calendar";
	this.element = () => element;

	var update = () =>
	{
		while (element.firstChild)
			element.removeChild(element.firstChild);

		var body = element.appendChild(document.createElement("div"));
		["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"]
			.forEach(e => body.appendChild(document.createElement("label"))
					.appendChild(document.createTextNode(e)));

		var dates = [];
		var ini = new Date(month);
		ini.setDate(1);
		while (ini.getDay() !== 0)
			ini.setDate(ini.getDate() - 1);
		var end = new Date(month);
		end.setMonth(end.getMonth() + 1);
		end.setDate(0);
		while (end.getDay() !== 6)
			end.setDate(end.getDate() + 1);
		for (var date = ini; date <= end; date.setDate(date.getDate() + 1))
			dates.push(new Date(date));
		while (dates.length < 42)
		{
			dates.push(new Date(date));
			date.setDate(date.getDate() + 1);
		}

		var now = new Date();
		now.setHours(0, 0, 0, 0);
		dates.forEach(date =>
		{
			var link = body.appendChild(createLink(date.getDate(), () => this.action(date)));
			if (date.getTime() === now.getTime())
				link.classList.add("current");
			if (date.getMonth() !== month.getMonth())
				link.classList.add("disabled");
			if (selection.some(e => e.getTime() === date.getTime()))
				link.classList.add("selected");
		});

		var head = body.appendChild(document.createElement("div"));
		head.appendChild(createLink("<<", prevYear));
		head.appendChild(createLink("<", prevMonth));
		head.appendChild(document.createElement("label")).appendChild(document.createTextNode(DateFormat.MONTH.format(month)));
		head.appendChild(createLink(">", nextMonth));
		head.appendChild(createLink(">>", nextYear));

		function createLink(text, callback)
		{
			var link = document.createElement("a");
			link.setAttribute("href", "#");
			link.appendChild(document.createTextNode(text));
			link.addEventListener("click", callback);
			return link;
		}
	};

	var prevYear = function ()
	{
		month.setFullYear(month.getFullYear() - 1);
		update();
	};

	var prevMonth = function ()
	{
		month.setMonth(month.getMonth() - 1);
		update();
	};

	var nextMonth = function ()
	{
		month.setMonth(month.getMonth() + 1);
		update();
	};


	var nextYear = function ()
	{
		month.setFullYear(month.getFullYear() + 1);
		update();
	};


	element.addEventListener((/Firefox/i.test(navigator.userAgent))
		? "DOMMouseScroll" : "mousewheel",
		function (event)
		{
			event.preventDefault();

			if (event.ctrlKey)
			{
				if (event.detail)
					if (event.detail > 0)
						nextYear();
					else
						prevYear();
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						nextYear();
					else
						prevYear();
			} else {
				if (event.detail)
					if (event.detail > 0)
						nextMonth();
					else
						prevMonth();
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						nextMonth();
					else
						prevMonth();
			}
		});


	this.clear = () =>
	{
		if (!element.dispatchEvent(new CustomEvent('clear', {cancelable: true, detail: {calendar: this}})))
			return false;

		selection = [];

		update();

		element.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
		return true;
	};


	this.select = (date) =>
	{
		date = new Date(date);
		date.setHours(0, 0, 0, 0);

		if (selection.some(e => e.getTime() === date.getTime()))
			return false;

		if (!element.dispatchEvent(new CustomEvent('select', {cancelable: true, detail: {calendar: this, date: date}})))
			return false;

		if (max !== undefined
			&& max <= selection.length)
			selection.shift();

		selection.push(date);

		update();

		element.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
		return true;
	};

	this.remove = (date) =>
	{
		date = new Date(date);
		date.setHours(0, 0, 0, 0);

		if (!selection.some(e => e.getTime() === date.getTime()))
			return false;

		if (!element.dispatchEvent(new CustomEvent('remove', {cancelable: true, detail: {calendar: this, date: date}})))
			return false;

		selection = selection.filter(e => e.getTime() !== date.getTime());
		update();

		element.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
		return true;
	};

	this.action = (date) =>
	{
		date = new Date(date);
		date.setHours(0, 0, 0, 0);

		if (!element.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {calendar: this, date: date}})))
			return false;

		return selection.some(e => e.getTime() === date.getTime()) ?
			this.remove(date) : this.select(date);
	};

	this.length = () => selection.length;
	this.selection = () => Array.from(selection);
	this.element = () => element;
	update();
}

Object.defineProperty(Calendar, "of", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: (id, init) => calendars[id] = new Calendar(document.getElementById(id), init)
});
Object.defineProperty(Calendar, "get", {
	writable: false,
	enumerable: false,
	configurable: false,
	value: id => calendars[id]
});
window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.Calendar"))
		.forEach(element => Calendar.of(element.id, new Date()));
});
/* global DateFormat */

function DateSelector(element)
{
	element.classList.add("DateSelector");

	var date = new Date();
	var calendar = new Calendar(element.appendChild(document.createElement("div")), {month: new Date(), max: 1});
	calendar.select(date);

	calendar.element().addEventListener("remove", event => event.preventDefault());
	calendar.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () => new Date(calendar.selection()[0]);

	this.element = () => element;

	this.toString = () => DateFormat.DATE.format(this.selection());
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateSelector"))
		.forEach(element => new DateSelector(element));
});
function TimeSelector(element)
{
	var date = new Date();
	element.classList.add("TimeSelector");
	var h = new Slider(element.appendChild(document.createElement("div")), date.getHours(), e => e > 0 ? e - 1 : 23, e => e < 23 ? e + 1 : 0, e => "00".concat(String(e)).slice(-2));
	var m = new Slider(element.appendChild(document.createElement("div")), date.getMinutes(), e => e > 0 ? e - 1 : 59, e => e < 59 ? e + 1 : 0, e => "00".concat(String(e)).slice(-2));

	h.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	m.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {h: h.value(), m: m.value()};
	};

	this.element = () => element;

	this.toString = () => "00".concat(String(h.value())).slice(-2) + ":" + "00".concat(String(m.value())).slice(-2);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.TimeSelector"))
		.forEach(e => new TimeSelector(e));
});
function MonthSelector(element)
{
	var date = new Date();
	element.classList.add("MonthSelector");
	var months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];
	var m = new Slider(element.appendChild(document.createElement("div")), date.getMonth(), e => e > 0 ? e - 1 : 11, e => e < 11 ? e + 1 : 0, e => months[e]);
	var y = new Slider(element.appendChild(document.createElement("div")), date.getFullYear(), e => e - 1, e => e + 1, e => "0000".concat(String(e)).slice(-4));

	m.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	y.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {m: m.value() + 1, y: y.value()};
	};

	this.element = () => element;

	this.toString = () => "00".concat(String(m.value() + 1)).slice(-2) + "/" + "0000".concat(String(y.value())).slice(-4);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.MonthSelector"))
		.forEach(e => new MonthSelector(e));
});
/* global DateFormat */

function DateTimeSelector(element)
{
	element.classList.add("DateTimeSelector");

	var dateSelector = new DateSelector(element.appendChild(document.createElement("div")));
	var timeSelector = new TimeSelector(element.appendChild(document.createElement("div")));

	dateSelector.element().addEventListener("remove", event => event.preventDefault());
	dateSelector.element().addEventListener("select", event => event.detail.calendar.clear());
	dateSelector.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	timeSelector.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		var date = dateSelector.selection();
		if (!date)
			return undefined;

		var time = timeSelector.selection();
		date.setHours(time.h, time.m, 0, 0);
		return date;
	};

	this.element = () => element;

	this.toString = () => DateFormat.DATETIME.format(this.selection());
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateTimeSelector"))
		.forEach(element => new DateTimeSelector(element));
});
/* global DateFormat */

function DateIntervalSelector(element)
{
	element.classList.add("DateIntervalSelector");

	var date1 = new DateSelector(element.appendChild(document.createElement("div")));
	var date2 = new DateSelector(element.appendChild(document.createElement("div")));

	date1.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	date2.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.element = () => element;

	this.selection = () =>
	{
		return {date1: date1.selection(), date2: date2.selection()};
	};

	this.toString = () => DateFormat.DATE.format(date1.selection()) + " - "
			+ DateFormat.DATE.format(date2.selection());
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateIntervalSelector"))
		.forEach(element => new DateIntervalSelector(element));
});
function TimeIntervalSelector(element)
{
	element.classList.add("TimeIntervalSelector");

	var time1 = new TimeSelector(element.appendChild(document.createElement("div")));
	var time2 = new TimeSelector(element.appendChild(document.createElement("div")));

	time1.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	time2.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {time1: time1.selection(), time2: time2.selection()};
	};

	this.element = () => element;

	this.toString = () => time1.toString() + " - " + time2.toString();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.TimeIntervalSelector"))
		.forEach(e => new TimeIntervalSelector(e));
});
function MonthIntervalSelector(element)
{
	element.classList.add("MonthIntervalSelector");

	var time1 = new MonthSelector(element.appendChild(document.createElement("div")));
	var time2 = new MonthSelector(element.appendChild(document.createElement("div")));

	time1.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	time2.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {time1: time1.selection(), time2: time2.selection()};
	};

	this.element = () => element;

	this.toString = () => time1.toString() + " - " + time2.toString();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.MonthIntervalSelector"))
		.forEach(e => new MonthIntervalSelector(e));
});
function DateTimeIntervalSelector(element)
{
	element.classList.add("DateTimeIntervalSelector");

	var dateTime1 = new DateTimeSelector(element.appendChild(document.createElement("div")));
	var dateTime2 = new DateTimeSelector(element.appendChild(document.createElement("div")));

	dateTime1.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));
	dateTime2.element().addEventListener("update", () => element.dispatchEvent(new CustomEvent('update', {detail: {selector: this}})));

	this.selection = () =>
	{
		return {dateTime1: dateTime1.selection(), dateTime2: dateTime2.selection()};
	};

	this.element = () => element;

	this.toString = () => dateTime1.toString() + " - " + dateTime2.toString();
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("div.DateTimeIntervalSelector"))
		.forEach(e => new TimeIntervalSelector(e));
});
class Picker extends Modal
{
	constructor()
	{
		super();
		var main = this.element().appendChild(document.createElement("div"));
		main.className = "Picker";
		this.main = () => main;

		var head = main.appendChild(document.createElement("div"));
		this.head = () => head;

		var body = main.appendChild(document.createElement("div"));
		this.body = () => body;

		var foot = main.appendChild(document.createElement("div"));
		this.foot = () => foot;

		var cancel = foot.appendChild(document.createElement("a"));
		cancel.addEventListener("click", () => this.hide());
		cancel.appendChild(document.createTextNode("Cancelar"));
		cancel.href = "#";
		this.cancel = () => cancel;

		var commit = foot.appendChild(document.createElement("a"));
		commit.appendChild(document.createTextNode("Concluir"));
		commit.href = "#";
		this.commit = () => commit;
	}
}
/* global DateFormat */

class DatePicker extends Picker
{
	constructor(callback)
	{
		super();

		this.main().classList.add("DatePicker");
		this.head().appendChild(document.createTextNode("Selecione uma data"));

		var selector = new Calendar(this.body().appendChild(document.createElement("div"), {max: 1}));

		selector.element().addEventListener("update", () =>
		{
			if (selector.selection().length === 1)
				callback(DateFormat.DATE.format(selector.selection()[0]));
			this.hide();
		});

		this.commit().addEventListener("click", () => alert("selecione uma data"));

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Date")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new DatePicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});
class TimePicker extends Picker
{
	constructor(callback)
	{
		super();

		this.main().classList.add("TimePicker");

		var selector = new TimeSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () => callback(selector.toString()) | this.hide());

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Time")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new TimePicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});
class MonthPicker extends Picker
{
	constructor(callback)
	{
		super();

		this.main().classList.add("MonthPicker");

		var selector = new MonthSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () => callback(selector.toString()) | this.hide());

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Month")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new MonthPicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});
class DateTimePicker extends Picker
{
	constructor(callback)
	{
		super();

		this.main().classList.add("DateTimePicker");

		var selector = new DateTimeSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () => callback(selector.toString()) | this.hide());

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.DateTime")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new DateTimePicker(dateTime =>
				{
					input.value = dateTime;
					link.focus();
				});

			link.blur();
		});
	});
});
/* global DateFormat */

class DateIntervalPicker extends Picker
{
	constructor(callback)
	{
		super();
		this.main().classList.add("DateIntervalPicker");

		var selector = new DateIntervalSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () =>
		{
			if (selector.selection())
			{
				callback(selector.toString());
				this.hide();
			}
		});

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.DateInterval")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new DateIntervalPicker(selection =>
				{
					input.value = selection;
					link.focus();
				});

			link.blur();
		});
	});
});
class TimeIntervalPicker extends Picker
{
	constructor(callback)
	{
		super();
		this.main().classList.add("TimeIntervalPicker");

		var selector = new TimeIntervalSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () => callback(selector.toString()) | this.hide());

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.TimeInterval")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new TimeIntervalPicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});
class MonthIntervalPicker extends Picker
{
	constructor(callback)
	{
		super();

		this.main().classList.add("MonthIntervalPicker");

		var selector = new MonthIntervalSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () => callback(selector.toString()) | this.hide());

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.MonthInterval")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new MonthIntervalPicker(time =>
				{
					input.value = time;
					link.focus();
				});

			link.blur();
		});
	});
});
class DateTimeIntervalPicker extends Picker
{
	constructor(callback)
	{
		super();
		this.main().classList.add("DateTimeIntervalPicker");

		var selector = new DateTimeIntervalSelector(this.body().appendChild(document.createElement("div")));
		selector.element().addEventListener("update", () => this.head().innerHTML = selector.toString());

		this.head().appendChild(document.createTextNode(selector.toString()));

		this.commit().addEventListener("click", () => callback(selector.toString()) | this.hide());

		this.element().addEventListener("show", () => this.commit().focus());

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.DateTimeInterval")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new DateTimeIntervalPicker(dateTime =>
				{
					input.value = dateTime;
					link.focus();
				});

			link.blur();
		});
	});
});
/* global DateFormat */

class IconPicker extends Picker
{
	constructor(callback)
	{
		super();

		this.main().classList.add("IconPicker");
		this.head().appendChild(document.createTextNode("Selecione um ícone"));

		var selector = this.body()
			.appendChild(document.createElement("div"));

		icon("1000");
		icon("1001");
		icon("1002");
		icon("1003");
		icon("1006");
		icon("1007");
		icon("1010");
		icon("1011");
		icon("1012");
		icon("1013");
		icon("1014");
		icon("1020");
		icon("1021");
		icon("1022");
		icon("1023");
		icon("1024");
		icon("2000");
		icon("2001");
		icon("2002");
		icon("2003");
		icon("2004");
		icon("2005");
		icon("2006");
		icon("2007");
		icon("2008");
		icon("2009");
		icon("200A");
		icon("2010");
		icon("2011");
		icon("2012");
		icon("2013");
		icon("2014");
		icon("2015");
		icon("2016");
		icon("2017");
		icon("2018");
		icon("2019");
		icon("2020");
		icon("2021");
		icon("2022");
		icon("2023");
		icon("2024");
		icon("2025");
		icon("2026");
		icon("2027");
		icon("2030");
		icon("2031");
		icon("2032");
		icon("2033");
		icon("2034");
		icon("2035");
		icon("2036");
		icon("2037");
		icon("2038");
		icon("2039");
		icon("2040");
		icon("2041");
		icon("2042");
		icon("2043");
		icon("2044");
		icon("2045");
		icon("2046");
		icon("2047");
		icon("2048");
		icon("2049");
		icon("2050");
		icon("2051");
		icon("2052");
		icon("2053");
		icon("2054");
		icon("2055");
		icon("2056");
		icon("2057");
		icon("2058");
		icon("2059");
		icon("2070");
		icon("2071");
		icon("2072");
		icon("2073");
		icon("2074");
		icon("2075");
		icon("2076");
		icon("2077");
		icon("2078");
		icon("2079");
		icon("2080");
		icon("2081");
		icon("2082");
		icon("2083");
		icon("2084");
		icon("2085");
		icon("2086");
		icon("2087");
		icon("2088");
		icon("2089");
		icon("2090");
		icon("2091");
		icon("2092");
		icon("2093");
		icon("2094");
		icon("2095");
		icon("2096");
		icon("2097");
		icon("2098");
		icon("2099");
		icon("2100");
		icon("2101");
		icon("2102");
		icon("2103");
		icon("2104");
		icon("2105");
		icon("2106");
		icon("2107");
		icon("2108");
		icon("2109");
		icon("2110");
		icon("2111");
		icon("2112");
		icon("2113");
		icon("2114");
		icon("2115");
		icon("2116");
		icon("2117");
		icon("2118");
		icon("2119");
		icon("2120");
		icon("2121");
		icon("2122");
		icon("2123");
		icon("2124");
		icon("2125");
		icon("2126");
		icon("2127");
		icon("2128");
		icon("2129");
		icon("2130");
		icon("2131");
		icon("2132");
		icon("2133");
		icon("2134");
		icon("2135");
		icon("2136");
		icon("2137");
		icon("2138");
		icon("2139");
		icon("2140");
		icon("2141");
		icon("2142");
		icon("2143");
		icon("2144");
		icon("2145");
		icon("2146");
		icon("2147");
		icon("2148");
		icon("2149");
		icon("2150");
		icon("2151");
		icon("2152");
		icon("2153");
		icon("2154");
		icon("2155");
		icon("2156");
		icon("2157");
		icon("2158");
		icon("2159");
		icon("2160");
		icon("2161");
		icon("2162");
		icon("2163");
		icon("2164");
		icon("2165");
		icon("2166");
		icon("2167");
		icon("2168");
		icon("2169");
		icon("2170");
		icon("2171");
		icon("2172");
		icon("2173");
		icon("2174");
		icon("2175");
		icon("2176");
		icon("2177");
		icon("2178");
		icon("2179");
		icon("2180");
		icon("2181");
		icon("2182");
		icon("2183");
		icon("2187");
		icon("2188");
		icon("2189");
		icon("2190");
		icon("2191");
		icon("2192");
		icon("2193");
		icon("2194");
		icon("2195");
		icon("2196");
		icon("2197");
		icon("2198");
		icon("2199");
		icon("2200");
		icon("2201");
		icon("2202");
		icon("2203");
		icon("2204");
		icon("2205");
		icon("2206");
		icon("2207");
		icon("2208");
		icon("2209");
		icon("2210");
		icon("2211");
		icon("2212");
		icon("2213");
		icon("2214");
		icon("2215");
		icon("2216");
		icon("2217");
		icon("2218");
		icon("2219");
		icon("2220");
		icon("2221");
		icon("2222");
		icon("2223");
		icon("2224");
		icon("2225");
		icon("2226");
		icon("2227");
		icon("2228");
		icon("2229");
		icon("2230");
		icon("2231");
		icon("2232");
		icon("2233");
		icon("2234");
		icon("2235");
		icon("2236");
		icon("2237");
		icon("2238");
		icon("2239");
		icon("2240");
		icon("2241");
		icon("2242");
		icon("2243");
		icon("2244");
		icon("2245");
		icon("2246");
		icon("2247");
		icon("2248");
		icon("2249");
		icon("2250");
		icon("2251");
		icon("2252");
		icon("2253");
		icon("2254");
		icon("2255");
		icon("2256");
		icon("2257");
		icon("2258");
		icon("2259");
		icon("2260");
		icon("2261");
		icon("2262");
		icon("2263");
		icon("2264");
		icon("2265");
		icon("2266");
		icon("2267");
		icon("2268");
		icon("2269");
		icon("2270");
		icon("2271");
		icon("2272");
		icon("2273");
		icon("2274");
		icon("2275");
		icon("2276");
		icon("2277");
		icon("2278");
		icon("2279");
		icon("2280");
		icon("2281");
		icon("2282");
		icon("2283");

		this.commit().addEventListener("click", () => alert("selecione um ícone"));

		this.show();

		var element = this;
		function icon(code)
		{
			var icon = selector.appendChild(document.createElement("a"));
			icon.innerHTML = "&#X" + code + ";";
			icon.href = "#";
			icon.addEventListener("click", () => callback(code) | element.hide());
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Icon")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2009;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
				input.value = '';
			else
				new IconPicker(code =>
				{
					input.value = code;
					link.focus();
				});

			link.blur();
		});
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

class ChartDialog extends Modal
{
	constructor(options)
	{
		super(options);

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.classList.add("ChartDialog");

		var head = dialog.appendChild(document.createElement("div"));
		head.setAttribute("tabindex", "1");

		var body = dialog.appendChild(document.createElement("div"));
		body.style.backgroundColor = "white";

		var canvas = body.appendChild(document.createElement("div"));
		canvas.setAttribute("id", 'Chart');
		canvas.setAttribute("tabindex", "1");

		canvas.onmouseenter = () => canvas.focus();
		head.onmouseenter = () => head.focus();

		var chart = new Chart(options.data, options.title ? options.title : "");

		var cchart = head.appendChild(document.createElement('a'));
		cchart.dialog = this;
		cchart.title = "Coluna";
		cchart.innerHTML = "&#x2033;";
		cchart.onclick = () => chart.draw('Chart', 'cchart');

		var bchart = head.appendChild(document.createElement('a'));
		bchart.dialog = this;
		bchart.title = "Barra";
		bchart.innerHTML = "&#x2246;";
		bchart.onclick = () => chart.draw('Chart', 'bchart');

		var lchart = head.appendChild(document.createElement('a'));
		lchart.dialog = this;
		lchart.title = "Linha";
		lchart.innerHTML = "&#x2032;";
		lchart.onclick = () => chart.draw('Chart', 'lchart');

		var achart = head.appendChild(document.createElement('a'));
		achart.dialog = this;
		achart.title = "Area";
		achart.innerHTML = "&#x2244;";
		achart.onclick = () => chart.draw('Chart', 'achart');

		var pchart = head.appendChild(document.createElement('a'));
		pchart.dialog = this;
		pchart.title = "Pizza";
		pchart.innerHTML = "&#x2031;";
		pchart.onclick = () => chart.draw('Chart', 'pchart');

		var dchart = head.appendChild(document.createElement('a'));
		dchart.dialog = this;
		dchart.title = "Donut";
		dchart.innerHTML = "&#x2245;";
		dchart.onclick = () => chart.draw('Chart', 'dchart');

		var rchart = head.appendChild(document.createElement('a'));
		rchart.dialog = this;
		rchart.title = "Rose";
		rchart.innerHTML = "&#x2247;";
		rchart.onclick = () => chart.draw('Chart', 'rchart');

		head.appendChild(window.top.document.createElement('span'));

		var close = head.appendChild(document.createElement('a'));
		close.dialog = this;
		close.title = "Fechar";
		close.innerHTML = "&#x1011;";
		close.style.marginLeft = '16px';
		close.onclick = () => this.hide();

		this.show();

		if (!options.type)
			options.type = 'cchart';
		chart.draw('Chart', options.type);


		head.focus();
	}
}

ChartDialog.show = function (chart, series, action, title)
{
	if (!series)
		series = new URL(action).get();
	new ChartDialog({data: JSON.parse(series), type: chart, title: title});
};

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('tr.RChart, td.RChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("rchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.DChart, td.DChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("dchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.PChart, td.PChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("pchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.AChart, td.AChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("achart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});


	Array.from(document.querySelectorAll('tr.LChart, td.LChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("lchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.BChart, td.BChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("bchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.CChart, td.CChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			ChartDialog.show("cchart",
				this.getAttribute('data-series'),
				this.getAttribute('data-action'),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('a[data-chart]')).forEach(function (e)
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

	var pages = Array.from(pageControl.children)
		.filter(e => e.tagName.toLowerCase() === "ul")
		.flatMap(e => Array.from(e.children));

	if (pages.length > 0
		&& pages.every(e => !e.getAttribute("data-selected")
				|| e.getAttribute("data-selected").toLowerCase() !== "true"))
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

		var link;
		for (var i = 0; i < page.children.length; i++)
			if (page.children[i].tagName.toLowerCase() === 'a')
				link = page.children[i];

		var body;
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
		};


		if (page.getAttribute("data-selected") &&
			page.getAttribute("data-selected").toLowerCase() === "true")
			link.onclick();


		function fetch()
		{
			new URL(link.getAttribute('href')).get(text => body.innerHTML = text);
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
				var divs = Array.from(this.parentNode.parentNode.children).filter(e => e.tagName.toLowerCase() === "div");
				for (i = 0; i < divs.length; i++)
					if (divs[i].childNodes[0] !== this)
						if (divs[i] !== this.parenNode)
							divs[i].innerHTML = '';
			};
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('div.PageControl'))
		.forEach(element => new PageControl(element));
});
function LinkControl(linkControl)
{
	var links = [];
	Array.from(linkControl.children).forEach(function (ul)
	{
		if (ul.tagName.toLowerCase() === "ul")
			Array.from(ul.children).forEach(function (li)
			{
				if (li.tagName.toLowerCase() === "li")
					links.push(li);
			});
	});

	if (links.length > 0 && links.every(e => !e.getAttribute("data-selected")
			|| e.getAttribute("data-selected").toLowerCase() !== "true"))
		links[0].setAttribute("data-selected", "true");
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('div.LinkControl')).forEach(function (e)
	{
		new LinkControl(e);
	});
});
/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV */

class Dialog extends Modal
{
	constructor(options)
	{
		super(options);

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.className = "Dialog";
		if (options && options.size && options.size.w)
			dialog.style.width = options.size.w;
		if (options && options.size && options.size.h)
			dialog.style.height = options.size.h;

		var head = dialog.appendChild(window.top.document.createElement('div'));
		head.onmouseenter = () => head.focus();
		head.setAttribute("tabindex", "1");
		head.focus();

		var caption = head.appendChild(window.top.document.createElement('label'));
		if (options && options.title)
			caption.innerHTML = options.title;

		if (!this.blocked())
		{
			var close = head.appendChild
				(window.top.document.createElement("a"));
			close.title = 'Fechar janela';
			close.innerHTML = "&#x1011;";
			close.onclick = () => this.hide();
		}

		var body = dialog.appendChild(window.top.document.createElement('div'));

		var iframe = body.appendChild(window.top.document.createElement('iframe'));
		iframe.dialog = this;
		iframe.setAttribute('name', '_dialog');
		iframe.onmouseenter = () => iframe.focus();

		iframe.addEventListener("load", () =>
		{
			iframe.name = "_frame";
			iframe.setAttribute("name", "_frame");

			head.onkeydown = undefined;
			head.addEventListener("keydown", event =>
			{
				event = event ? event : window.event;
				switch (event.keyCode)
				{
					case ESC:
						if (!this.blocked())
							this.hide();
						break;
					case ENTER:
						iframe.focus();
						break;
				}

				event.preventDefault();
				event.stopPropagation();
			});

			iframe.addEventListener("keydown", event =>
			{
				event = event ? event : window.event;
				if (event.keyCode === ESC)
				{
					head.focus();
					event.preventDefault();
					event.stopPropagation();
				}
			});

			iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));
		});




		if (options && options.navigator)
		{
			var navigator = new NavBar(options.navigator);
			head.appendChild(navigator.element());
			navigator.element().addEventListener("go",
				event => iframe.setAttribute('src', event.detail.target));
			if (options.target)
				navigator.go(options.target);
		} else if (options && options.target)
			iframe.setAttribute('src', options.target);
	}

	get()
	{
		this.arguments = arguments;
		this.show();
	}

	ret()
	{
		for (var i = 0; i < Math.min(arguments.length, this.arguments.length); i++)
		{
			if (this.arguments[i])
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
		this.hide();
	}

}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('a[data-get]')).forEach(function (element)
	{
		element.addEventListener("click", function (event)
		{
			var parameters =
				CSV.parse(this.getAttribute('data-get'))
				.map(e => e.trim())
				.map(e => e !== null ? document.getElementById(e) : null);
			if (parameters.some(e => e && e.value))
			{
				parameters
					.filter(e => e)
					.filter(e => e.value)
					.forEach(e => e.value = "");
			} else {
				var dialog = new Dialog({target: this.href,
					title: this.getAttribute("title")});
				dialog.get.apply(dialog, parameters);
			}

			event.preventDefault();
			event.stopPropagation();
		});
	});
	Array.from(document.querySelectorAll('input[data-getter]')).forEach(function (element)
	{
		element.addEventListener("change", function ()
		{
			var getter = document.getElementById(this.getAttribute("data-getter"));
			var url = resolve(getter.href);
			var parameters =
				CSV.parse(getter.getAttribute('data-get'))
				.map(id => id.trim())
				.map(id => id !== null ? document.getElementById(id) : null);
			if (this.value)
			{
				parameters
					.filter(e => e)
					.filter(e => e.value)
					.forEach(e => e.value = "");
				var dialog = new Dialog({target: url,
					title: getter.getAttribute("title")})
				dialog.get.apply(dialog, parameters);
			} else
				parameters
					.filter(e => e)
					.filter(e => e.value)
					.forEach(e => e.value = "");
			event.preventDefault();
			event.stopPropagation();
		});
	});

	Array.from(document.querySelectorAll('*[data-ret]')).forEach(function (element)
	{
		element.onmouseover = () => element.focus();
		element.onmouseout = () => element.blur();

		element.onclick = function ()
		{
			var ret = CSV.parse(this.getAttribute("data-ret")).map(e => e.trim());
			window.frameElement.dialog.ret.apply(window.frameElement.dialog, ret);
			return false;
		};
		element.onkeydown = function (e)
		{
			e = e ? e : window.event;
			if (e.keyCode === 13)
				this.onclick();
			return true;
		};
	});

	Array.from(document.querySelectorAll('a.Hide')).forEach(function (element)
	{
		element.onclick = function ()
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
class Popup extends Modal
{
	constructor(element)
	{
		super(false);
		var parent = element.parentNode;

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.classList.add("Popup");

		var head = dialog.appendChild(window.top.document.createElement('div'));
		head.setAttribute("tabindex", "1");
		head.focus();

		var caption = head.appendChild(window.top.document.createElement('label'));
		if (element.hasAttribute("title"))
			caption.innerHTML = element.getAttribute("title");

		var close = head.appendChild(window.top.document.createElement("a"));
		close.title = 'Fechar janela';
		close.innerHTML = "&#x1011;";
		close.onclick = () => this.hide();

		var body = dialog.appendChild(window.top.document.createElement('div'));
		body.appendChild(element);

		if (parent)
			this.creator().addEventListener("hide", () => parent.appendChild(element));

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("*[popup]")).forEach(element => new Popup(element));
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
			Array
				.from(tbody.children)
				.filter(e => e.style.display === 'table-row')
				.forEach((tr, index) => tr.className = index % 2 ? 'odd' : 'even');
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
	Array.from(document.querySelectorAll('table.TreeView, table.TREEVIEW'))
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

	this.parse = function (string)
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

Object.defineProperty(DateFormat, "YEAR", {
	writable: false,
	enumerable: false,
	configurable: true,
	value: new DateFormat("yyyy")
});

Object.defineProperty(DateFormat, "DATE", {
	writable: false,
	enumerable: false,
	configurable: true,
	value: new DateFormat("dd/MM/yyyy")
});

Object.defineProperty(DateFormat, "MONTH", {
	writable: false,
	enumerable: false,
	configurable: true,
	value: new DateFormat("MM/yyyy")
});

Object.defineProperty(DateFormat, "DATETIME", {
	writable: false,
	enumerable: false,
	configurable: true,
	value: new DateFormat("dd/MM/yyyy HH:mm")
});

Object.defineProperty(DateFormat, "INSTANT", {
	writable: false,
	enumerable: false,
	configurable: true,
	value: new DateFormat("dd/MM/yyyy HH:mm:ss")
});
window.addEventListener("load", function ()
{
	window.setInterval(function ()
	{
		Array.from(document.querySelectorAll("*[data-switch]")).forEach(function (node)
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

/* global ENTER, ESC, Message */

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
				var dialog = new Dialog({creator: creator || this,
					title: this.getAttribute("title"),
					blocked: Boolean(this.getAttribute("data-blocked"))})
					.show();

				dialog.element().addEventListener("show", event => this.dispatchEvent(event));
				dialog.element().addEventListener("hide", event => this.dispatchEvent(event));
			}
		});

		form.addEventListener("keydown", function (event)
		{
			event = event ? event : window.event;

			if (!event.ctrlKey && event.keyCode === ENTER)
			{
				var element = document.activeElement;
				switch (element.tagName.toLowerCase())
				{
					case "select":
					case "textarea":
						break;
					case "a":
					case "button":
						element.click();
						event.preventDefault();
						event.stopImmediatePropagation();
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
class Message extends Modal
{
	constructor(options)
	{
		super();

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.classList.add("Message");
		dialog.classList.add(options.type);

		var icon = dialog.appendChild(window.top.document.createElement('label'));
		switch (options.type)
		{
			case "SUCCESS":
				icon.innerHTML = "&#X1000;";
				break;
			case "WARNING":
				icon.innerHTML = "&#X1007;";
				break;
			case "ERROR":
				icon.innerHTML = "&#X1001;";
				break;
		}

		var message = dialog.appendChild(window.top.document.createElement('label'));
		message.innerHTML = options.message;

		if (options.timeout)
			window.top.setTimeout(() => this.hide(), options.timeout);

		this.show();
	}
}

Message.success = function (message, timeout)
{
	new Message({type: "SUCCESS", message: message, timeout: timeout});
};

Message.warning = function (message, timeout)
{
	new Message({type: "WARNING", message: message, timeout: timeout});
};

Message.error = function (message, timeout)
{
	new Message({type: "ERROR", message: message, timeout: timeout});
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
function copy(data)
{
	const textarea = document.createElement('textarea');
	textarea.value = data;
	textarea.removeAttribute('readonly');
	textarea.style.position = 'absolute';
	textarea.style.left = '-9999px';
	document.body.appendChild(textarea);
	textarea.select();
	document.execCommand('copy');
	document.body.removeChild(textarea);
	Message.success("O texto " + data + " foi copiado com sucesso para a área de transferência.", 1000);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("[data-copy-onclick]")).forEach(function (element)
	{
		element.addEventListener("click", () => copy(element.getAttribute("data-copy-onclick")));
	});
});