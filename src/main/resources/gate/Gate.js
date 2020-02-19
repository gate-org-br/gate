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
/* global */

class GSelection
{
	static getSelectedLink(nodes)
	{
		let href = window.location.href;
		if (href[href.length - 1] === '#')
			href = href.slice(0, -1);
		let parameters = URL.parse_query_string(href);
		let elements = Array.from(nodes)
			.filter(e => (e.href && e.href.includes('?'))
					|| (e.formaction && e.formaction.includes('?')));

		var q = elements.filter(e =>
		{
			let args = URL.parse_query_string(e.href || e.formaction);
			return args.MODULE === parameters.MODULE
				&& args.SCREEN === parameters.SCREEN
				&& args.ACTION === parameters.ACTION;
		});

		if (q.length === 0)
		{
			var q = elements.filter(e =>
			{
				var args = URL.parse_query_string(e.href || e.formaction);
				return args.MODULE === parameters.MODULE
					&& args.SCREEN === parameters.SCREEN;
			});

			if (q.length === 0)
			{
				q = elements.filter(e =>
				{
					var args = URL.parse_query_string(e.href || e.formaction);
					return args.MODULE === parameters.MODULE;
				});
			}
		}

		if (q.length !== 0)
			return q[0];
	}
}
class Proxy
{
	static create(element)
	{
		let clone = element.cloneNode(true);
		clone.onclick = event =>
		{
			element.click();
			event.preventDefault();
		};
		return clone;
	}

	static	copyStyle(source, target)
	{
		target = target.style;
		source = getComputedStyle(source);
		for (var i = source.length; i-- > 0; ) {
			var name = source[i];
			alert(name);
			target.setProperty(name, source.getPropertyValue(name));
		}
	}
}
/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class WindowList extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.windows = [];
	}

	static get instance()
	{
		if (!WindowList._private)
			WindowList._private = {};
		if (!WindowList._private.instance)
			WindowList._private.instance =
				window.top.document.documentElement
				.appendChild(new WindowList());
		return WindowList._private.instance;
	}
}

customElements.define('g-window-list', WindowList);
/* global customElements, Proxy, GSelection */

class GOverflow extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		var container = document.createElement("div");
		container.style.width = "auto";
		container.style.flex = "1 1 0px";
		container.style.display = "flex";

		container.style.whiteSpace = "nowrap";
		container.style.flexDirection = "row-reverse";
		this.shadowRoot.appendChild(container);
		let slot = container.appendChild(document.createElement("slot"));
		slot.addEventListener("slotchange", () => this._private.update());

		var more = document.createElement("a");
		more.href = "#";
		more.innerHTML = "&#X3018;";

		more.style.padding = "0";
		more.style.width = "32px";
		more.style.flexGrow = "0";
		more.style.height = "100%";
		more.style.outline = "none";
		more.style.display = "none";
		more.style.flexShrink = "0";
		more.style.fontSize = "20px";
		more.style.color = "inherit";
		more.style.cursor = "pointer";
		more.style.fontFamily = "gate";
		more.style.marginRight = "auto";
		more.style.alignItems = "center";
		more.style.textDecoration = "none";
		more.style.justifyContent = "center";

		container.appendChild(more);

		more.addEventListener("click", () =>
		{
			let elements = Array.from(this.children)
				.filter(e => e.tagName !== "HR")
				.filter(e => e.style.display === "none")
				.map(element => Proxy.create(element));
			elements.forEach(e => e.style.display = "");
			document.documentElement.appendChild(new GSideMenu(elements)).show(more);
		});

		this._private = {more: more, container: container, update: () =>
			{
				let selected = GSelection.getSelectedLink(this.children);
				if (selected)
					selected.setAttribute("aria-selected", "true");

				Array.from(this.children).forEach(e => e.style.display = "flex");

				more.style.display = "none";
				if (container.clientWidth > this.clientWidth)
					more.style.display = "flex";

				for (let e = this.lastElementChild; e; e = e.previousElementSibling)
					if (container.clientWidth > this.clientWidth)
						if (!e.hasAttribute("aria-selected"))
							e.style.display = "none";
			}};

		window.addEventListener("load", this._private.update);
	}

	connectedCallback()
	{
		window.addEventListener("resize", this._private.update);
	}

	disconnectedCallback()
	{
		window.removeEventListener("resize", this._private.update);
	}

	static isOverflowed(element)
	{
		return element.scrollWidth > element.clientWidth
			|| element.scrollHeight > element.clientHeight;
	}

	static disable(element)
	{
		element.setAttribute("data-scroll-disabled", "data-scroll-disabled");
		Array.from(element.children).forEach(e => GOverflow.disable(e));
		window.top.document.documentElement.addEventListener("touchmove", GOverflow.PREVENT_BODY_SCROLL, false);
	}

	static enable(element)
	{
		element.removeAttribute("data-scroll-disabled");
		Array.from(element.children).forEach(e => GOverflow.enable(e));
		window.top.document.documentElement.removeEventListener("touchmove", GOverflow.PREVENT_BODY_SCROLL, false);
	}

	static determineOverflow(component, container)
	{
		container = container || component;

		if (!component.firstElementChild)
			return "none";

		var containerMetrics = container.getBoundingClientRect();
		var containerMetricsRight = Math.floor(containerMetrics.right);
		var containerMetricsLeft = Math.floor(containerMetrics.left);

		var left = Math.floor(component.firstElementChild.getBoundingClientRect().left);
		var right = Math.floor(component.lastElementChild.getBoundingClientRect().right);

		if (containerMetricsLeft > left
			&& containerMetricsRight < right)
			return "both";
		else if (left < containerMetricsLeft)
			return "left";
		else if (right > containerMetricsRight)
			return "right";
		else
			return "none";
	}
}

customElements.define('g-overflow', GOverflow);

GOverflow.PREVENT_BODY_SCROLL = e => e.preventDefault();

window.addEventListener("load", () => Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.scrollWidth > e.clientWidth
				|| e.scrollHeight > e.clientHeight)
		.forEach(e => e.setAttribute("data-overflow", "true")));

window.addEventListener("resize", () => {
	Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.hasAttribute("data-overflow"))
		.forEach(e => e.removeAttribute("data-overflow"));

	Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.scrollWidth > e.clientWidth
				|| e.scrollHeight > e.clientHeight)
		.forEach(e => e.setAttribute("data-overflow", "true"));
});
/* global customElements, GOverflow, Proxy, GDialog */

class GDialogCommands extends GOverflow
{
	constructor()
	{
		super();
		window.addEventListener("load", () =>
			GDialog.commands = this);
	}
}

customElements.define('g-dialog-commands', GDialogCommands);
/* global customElements */

class DigitalClock extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {}
		this._private.listener = () =>
		{
			if (!this.hasAttribute("paused"))
			{
				var time = this.hasAttribute("time") ?
					Number(this.getAttribute("time")) : 0;
				this.setAttribute("time", time + 1);
			}
		};
	}

	static get observedAttributes()
	{
		return ['time', 'paused'];
	}

	attributeChangedCallback()
	{
		this.innerHTML = new Duration(Number(this.getAttribute("time")))
			.format(this.getAttribute("format") || "hh:mm:ss");
	}

	connectedCallback()
	{
		window.addEventListener("ClockTick", this._private.listener);
	}

	disconnectedCallback()
	{
		window.removeEventListener("ClockTick", this._private.listener);
	}
}

customElements.define('digital-clock', DigitalClock);

window.addEventListener("load", () =>
{
	window.setInterval(() => this.dispatchEvent(new CustomEvent("ClockTick")), 1000);
});
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

	Array.from(document.querySelectorAll("input.SELECTOR, input[type='checkbox'][data-target]")).forEach(function (element)
	{
		element.addEventListener("change", function ()
		{
			var selector = 'input[type="checkbox"][name="' + this.getAttribute('data-target') + '"]';
			Array.from(document.querySelectorAll(selector)).forEach(target => target.checked = element.checked);
		});
	});

	setInterval(() => window.dispatchEvent(new CustomEvent("refresh_size")), 500);
});
class Objects
{
	static isEmpty(value)
	{
		return !value && value !== 0
			&& value !== false;
	}

	static compare(s1, s2)
	{
		if (Objects.isEmpty(s1)
			&& Objects.isEmpty(s2))
			return 0;

		if (Objects.isEmpty(s1))
			return 1;

		if (Objects.isEmpty(s2))
			return -1;

		return s1 > s2 ? 1 : s1 < s2 ? -1 : 0;
	}
}
class Colorizer
{
	static colorize(table)
	{
		var type = "odd";
		Array.from(table.children)
			.filter(e => e.tagName.toUpperCase() === "TBODY")
			.flatMap(e => Array.from(e.children)).forEach(row =>
		{
			row.classList.remove("odd");
			row.classList.remove("even");
			if (window.getComputedStyle(row).display !== "none")
			{
				row.classList.add(type);
				type = type === "even" ? "odd" : "even";
			}
		});
	}
}
class DataFormat
{
	static format(bytes, decimals = 2)
	{
		if (bytes === 0)
			return '0 Bytes';

		const k = 1024;
		const dm = decimals < 0 ? 0 : decimals;
		const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

		const i = Math.floor(Math.log(bytes) / Math.log(k));

		return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];

	}
}
function DeskMenu(deskMenu)
{
	Array.from(deskMenu.getElementsByTagName("a"))
		.forEach(element => new DeskMenuIcon(element));

	function DeskMenuIcon(deskMenuIcon)
	{
		var icons = Array.from(deskMenuIcon.parentNode.children)
			.filter(e => e.tagName.toLowerCase() === "ul")
			.flatMap(e => Array.from(e.children));

		if (icons.length > 0)
		{
			deskMenuIcon.addEventListener("click", function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				var children = Array.from(deskMenu.children);
				children.forEach(e => deskMenu.removeChild(e));
				icons.forEach(e => deskMenu.appendChild(e));
				deskMenu.appendChild(new Reset(children));
			});
		}

		function Reset(icons)
		{
			var li = document.createElement("li");
			li.className = "Reset";

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";

			a.appendChild(document.createElement("i"))
				.innerHTML = "&#X2232";

			a.addEventListener("click", function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				Array.from(deskMenu.children).forEach(e => deskMenu.removeChild(e));
				icons.forEach(e => deskMenu.appendChild(e));
			});
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
	Array.from(deskPane.getElementsByTagName("a"))
		.forEach(element => new DeskPaneIcon(element));

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

				var children = Array.from(deskPane.children);
				children.forEach(e => deskPane.removeChild(e));
				icons.forEach(e => deskPane.appendChild(e));
				deskPane.appendChild(new Reset(children));
			});
		}

		function Reset(icons)
		{
			var li = document.createElement("li");
			li.className = "Reset";

			var a = li.appendChild(document.createElement("a"));
			a.setAttribute("href", "#");
			a.innerHTML = "Retornar";

			a.appendChild(document.createElement("i"))
				.innerHTML = "&#X2232";

			a.addEventListener("click", function (event)
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				Array.from(deskPane.children).forEach(e => deskPane.removeChild(e));
				icons.forEach(e => deskPane.appendChild(e));
			});
			return li;
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul.DeskPane"))
		.forEach(element => new DeskPane(element));
});


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

/* global customElements, GOverflow, WindowList */

class GModal extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.classList.add("g-modal");

		this.addEventListener("contextmenu", event =>
		{
			event.preventDefault();
			this.hide();
		});

		this.addEventListener("click", event => !this.blocked
				&& (event.target === this || event.srcElement === this)
				&& this.hide());
	}

	get blocked()
	{
		return this._private.blocked || false;
	}

	set blocked(blocked)
	{
		this._private.blocked = blocked;
	}

	show()
	{

		if (window.dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}}))
			&& this.dispatchEvent(new CustomEvent('show', {cancelable: true, detail: {modal: this}})))
		{
			GOverflow.disable(window.top.document.documentElement);

			window.top.document.documentElement.appendChild(this);

			this.dispatchEvent(new CustomEvent('show', {detail: {modal: this}}));
		}

		return this;
	}

	hide()
	{
		if (this.parentNode
			&& window.dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}}))
			&& this.dispatchEvent(new CustomEvent('hide', {cancelable: true, detail: {modal: this}})))
		{
			GOverflow.enable(window.top.document.documentElement);
			this.parentNode.removeChild(this);
		}

		return this;
	}

	minimize()
	{
		if (!this.parentNode)
			return;

		this.style.display = "none";
		GOverflow.enable(window.top.document.documentElement);

		var link = WindowList.instance.appendChild(document.createElement("a"));
		link.href = "#";
		link.innerHTML = this.caption || "Janela";

		link.addEventListener("click", () =>
		{
			this.style.display = "";
			link.parentNode.removeChild(link);
			GOverflow.disable(window.top.document.documentElement);
		});
	}
}

customElements.define('g-modal', GModal);
/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class GWindow extends GModal
{
	constructor()
	{
		super();
		this.classList.add("g-window");
		this.main.addEventListener("click", e => e.stopPropagation());
		this.main.addEventListener("contextmenu", e => e.stopPropagation());
	}

	get main()
	{
		if (!this._private.main)
			this._private.main = this
				.appendChild(document.createElement("main"));
		return this._private.main;
	}

	get head()
	{
		if (!this._private.head)
			this._private.head = this.main
				.appendChild(document.createElement("header"));
		return this._private.head;
	}

	get caption()
	{
		if (!this._private.caption)
			this._private.caption = this.head
				.appendChild(document.createElement("label"));
		return this._private.caption.innerText;
	}

	set caption(caption)
	{
		if (!this._private.caption)
			this._private.caption = this.head
				.appendChild(document.createElement("label"));
		this._private.caption.innerText = caption;
	}

	get body()
	{
		if (!this._private.body)
			this._private.body = this.main
				.appendChild(document.createElement("section"));
		return this._private.body;
	}

	get foot()
	{
		if (!this._private.foot)
			this._private.foot = this.main
				.appendChild(document.createElement("footer"));
		return this._private.foot;
	}

	get commands()
	{
		if (!this._private.commands)
			this._private.commands =
				this.head.appendChild(new GDialogCommands());
		return this._private.commands;
	}

	set commands(commands)
	{
		if (this._private.commands)
			this.head.removeChild(this._private.commands);
		this.head.appendChild(this._private.commands = commands);
	}
}

customElements.define('g-window', GWindow);
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



class Duration
{
	constructor(value)
	{
		this.value = value;
	}

	getHours()
	{
		return Math.floor(this.value / 3600);
	}

	getMinutes()
	{
		return Math.floor((this.value - (this.getHours() * 3600)) / 60);
	}

	getSeconds()
	{
		return this.value - (this.getHours() * 3600) - (this.getMinutes() * 60);
	}

	format(format)
	{
		var e;
		var result = "";
		var regex = /h+|m+|s+|H+|M+|S+|./g;
		while ((e = regex.exec(format)))
		{
			switch (e[0][0])
			{
				case 'h':
				case 'H':
					var value = String(this.getHours());
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				case 'm':
				case 'M':
					var value = String(this.getMinutes());
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				case 's':
				case 'S':
					var value = String(this.getSeconds());
					var padding = "0".repeat(e[0].length);
					result += (padding + value).slice(-Math.max(padding.length, value.length));
					break;
				default:
					result += e[0];
			}
		}
		return result;
	}

	toString()
	{
		return this.format("hh:mm:ss");
	}
}
window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input[list]")).forEach(function (element)
	{
		element.setAttribute("autocomplete", "off");
		element.addEventListener("input", process);
		process.call(element);

		function process()
		{
			var datalist = document.getElementById(this.getAttribute("list"));

			if (typeof this.value === "string"
				&& datalist.hasAttribute("data-options"))
			{
				var len = 3;
				if (datalist.hasAttribute("data-options-len"))
					len = parseInt(datalist.getAttribute("data-options-len"));

				if (this.value.length < len)
				{
					this.filter = null;
					new Populator([]).populate(datalist);
				} else if (!this.filter || !this.value.toLowerCase().includes(this.filter.toLowerCase()))
				{
					if (document.activeElement === this)
					{
						this.blur();
						this.disabled = true;
						new URL(resolve(datalist.getAttribute("data-options"))).get(options =>
						{
							new Populator(JSON.parse(options)).populate(datalist);
							this.disabled = false;
							this.focus();
							this.click();
							this.filter = this.value;
						});
					} else {
						this.disabled = true;
						new URL(resolve(datalist.getAttribute("data-options"))).get(options =>
						{
							new Populator(JSON.parse(options)).populate(datalist);
							this.disabled = false;
							this.filter = this.value;
						});
					}
				}
			}


			var hidden = null;
			if (this.nextElementSibling
				&& this.nextElementSibling.tagName.toLowerCase() === "input"
				&& this.nextElementSibling.getAttribute("type")
				&& this.nextElementSibling.getAttribute("type").toLowerCase() === "hidden")
				hidden = this.nextElementSibling;

			if (hidden && hidden.value)
			{
				hidden.value = "";
				hidden.dispatchEvent(new Event('change', {bubbles: true}));
			}

			if (this.value && this.value.length > 0)
			{
				var option = Array.from(datalist.children)
					.find(option => option.innerHTML === this.value
							|| option.innerHTML.toLowerCase() === this.value.toLowerCase());
				if (option)
				{
					this.setCustomValidity("");
					this.value = option.innerHTML;

					if (hidden)
					{
						hidden.value = option.getAttribute("data-value");
						hidden.dispatchEvent(new Event('change', {bubbles: true}));
					}
				} else
					this.setCustomValidity("Entre com um dos valores da lista");
			} else
				this.setCustomValidity("");
		}
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

class URL
{
	constructor(value)
	{
		this.value = value;
	}

	setParameter(name, value)
	{
		if (this.value.indexOf("?") === -1)
			this.value += "?";
		if (this.value[this.value.length - 1] !== '?')
			this.value += "&";
		this.value += name + "=" + value;
		return this;
	}

	setModule(module)
	{
		this.setParameter("MODULE", module);
		return this;
	}

	setScreen(screen)
	{
		this.setParameter("SCREEN", screen);
		return this;
	}

	setAction(action)
	{
		this.setParameter("ACTION", action);
		return this;
	}

	setContentType(contentType)
	{
		this.contentType = contentType;
		return this;
	}

	get(callback, dwload)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");

		if (dwload)
			request.addEventListener("progress", dwload, false);

		if (this.contentType)
			request.setRequestHeader('Content-type', contentType);

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
	}

	post(data, callback, upload, dwload)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");

		if (dwload)
			request.addEventListener("progress", dwload, false);
		if (upload)
			request.upload.addEventListener("progress", upload, false);

		if (this.contentType)
			request.setRequestHeader('Content-type', contentType);

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
	}

	go()
	{
		window.location.href = resolve(this.value);
		return this;
	}

	toString()
	{
		return this.value;
	}

	populate(css)
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
	}

	static parse_query_string(query)
	{
		var query_string = {};
		if (query.includes('?'))
			query = query.split('?')[1];
		var vars = query.split("&");
		for (var i = 0; i < vars.length; i++) {
			var pair = vars[i].split("=");
			var key = decodeURIComponent(pair[0]);
			var value = decodeURIComponent(pair[1]);
			if (typeof query_string[key] === "undefined")
				query_string[key] = decodeURIComponent(value);
			else if (typeof query_string[key] === "string")
				query_string[key] = [query_string[key], decodeURIComponent(value)];
			else
				query_string[key].push(decodeURIComponent(value));
		}
		return query_string;
	}
}
/* global Message, Block, ENTER, ESC, GDialog, GStackFrame */

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;
	let link = event.target;
	if (link.tagName !== 'A')
		return;
	if (link.hasAttribute("data-cancel"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		Message.error(link.getAttribute("data-cancel"), 2000);
		return;
	}

	if (link.hasAttribute("data-disabled"))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	if (link.hasAttribute("data-confirm")
		&& !confirm(link.getAttribute("data-confirm")))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		return;
	}

	if (link.hasAttribute("data-alert"))
		alert(link.getAttribute("data-alert"));
	else if (link.href.match(/([@][{][^}]*[}])/g)
		|| link.href.match(/([!][{][^}]*[}])/g)
		|| link.href.match(/([?][{][^}]*[}])/g))
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		var resolved = resolve(link.href);
		if (resolved !== null)
		{
			var href = link.href;
			link.href = resolved;
			link.click();
			link.href = href;
		}
		return;
	}

	if (link.getAttribute("target"))
	{
		switch (link.getAttribute("target").toLowerCase())
		{
			case "_dialog":
				event.preventDefault();
				event.stopPropagation();
				if (event.ctrlKey)
				{
					link.setAttribute("target", "_blank");
					link.click();
					link.setAttribute("target", "_dialog");
				} else
				{
					let dialog = GDialog.create();
					dialog.navigator = link.navigator;
					dialog.target = link.getAttribute("href");
					dialog.caption = link.getAttribute("title");
					dialog.blocked = Boolean(link.getAttribute("data-blocked"));


					if (link.hasAttribute("data-reload-on-hide"))
						dialog.addEventListener("hide", () => window.location = window.location.href);
					else if (link.hasAttribute("data-submit-on-hide"))
						dialog.addEventListener("hide", () => document.getElementById(link.getAttribute("data-submit-on-hide")).submit());

					dialog.show();
				}
				break;
			case "_stack":
				event.preventDefault();
				event.stopPropagation();
				if (event.ctrlKey)
				{
					link.setAttribute("target", "_blank");
					link.click();
					link.setAttribute("target", "_dialog");
				} else
				{
					let stackFrame = GStackFrame.create();
					stackFrame.target = link.getAttribute("href");

					if (link.hasAttribute("data-reload-on-hide"))
						stackFrame.addEventListener("hide", () => window.location = window.location.href);
					else if (link.hasAttribute("data-submit-on-hide"))
						stackFrame.addEventListener("hide", () => document.getElementById(link.getAttribute("data-submit-on-hide")).submit());

					stackFrame.show();
				}
				break;
			case "_message":
				event.preventDefault();
				event.stopPropagation();
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
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
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
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
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
				{
					try
					{
						status = JSON.parse(status);
						if (status.type === "SUCCESS")
							link.innerHTML = status.value;
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
				link.setAttribute("data-cancel", "Processando");
				new URL(link.href).get(function (status)
				{
					alert(status);
					link.removeAttribute("data-cancel");
				});
				break;
			case "_hide":
				event.preventDefault();
				event.stopPropagation();
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
				Array.from(link.children)
					.filter(e => e.tagName.toLowerCase() === "div")
					.forEach(e => new GPopup(e));
				break;
			case "_progress-dialog":
				event.preventDefault();
				event.stopPropagation();
				new URL(link.href).get(process =>
				{
					process = JSON.parse(process);
					new GProgressDialog(process,
						{title: link.getAttribute("title")}).show();
				});
				break;
			case "_progress-window":
				event.preventDefault();
				event.stopPropagation();
				new URL(link.href).get(function (process)
				{
					process = JSON.parse(process);
					document.body.appendChild(new GProgressWindow(process));
				});
				break;
			case "_report":
			case "_report-dialog":
				event.preventDefault();
				event.stopPropagation();
				let dialog = new GReportDialog();
				dialog.blocked = true;
				dialog.caption = link.getAttribute("title") || "Imprimir";
				dialog.get(link.href);
				break;
		}
	}
});
window.addEventListener("keydown", function (event)
{
	let link = event.target;
	if (link.tagName === 'A' && event.keyCode === 32)
	{
		event.target.click();
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
});
/* global Message, Block, ENTER, ESC, link, GDialog, GStackFrame */

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;

	let button = event.target;
	if (button.tagName === 'BUTTON')
	{
		if (button.hasAttribute("data-cancel"))
		{
			Message.error(event.target.getAttribute("data-cancel"), 2000);
			event.preventDefault();
			event.stopImmediatePropagation();
			return;
		}


		if (button.hasAttribute("data-disabled"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			return;
		}

		if (button.hasAttribute("data-confirm")
			&& !confirm(button.getAttribute("data-confirm")))
		{
			event.preventDefault();
			event.stopImmediatePropagation();
			return;
		}

		if (button.hasAttribute("data-alert"))
			alert(button.getAttribute("data-alert"));


		if (button.getAttribute("formaction") &&
			(button.getAttribute("formaction").match(/([@][{][^}]*[}])/g)
				|| button.getAttribute("formaction").match(/([!][{][^}]*[}])/g)
				|| button.getAttribute("formaction").match(/([?][{][^}]*[}])/g)))
		{
			let resolved = resolve(button.getAttribute("formaction"));
			if (resolved !== null)
			{
				var formaction = button.getAttribute("formaction");
				button.setAttribute("formaction", resolved);
				button.click();
				button.setAttribute("formaction", formaction);
			}

			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			return;
		}

		if (button.getAttribute("formtarget"))
		{
			switch (button.getAttribute("formtarget").toLowerCase())
			{
				case "_dialog":
					if (button.form.checkValidity())
					{
						if (event.ctrlKey)
						{
							event.preventDefault();
							event.stopPropagation();
							button.setAttribute("formtarget", "_blank");
							button.click();
							button.setAttribute("formtarget", "_dialog");
						} else if (event.target.form.getAttribute("target") !== "_dialog")
						{
							let dialog = GDialog.create();
							dialog.caption = event.target.getAttribute("title");
							dialog.blocked = Boolean(event.target.getAttribute("data-blocked"));

							if (button.hasAttribute("data-reload-on-hide"))
								dialog.addEventListener(() => window.location = window.location.href);
							else if (button.hasAttribute("data-submit-on-hide"))
								dialog.addEventListener(() => document.getElementById(button.getAttribute("data-submit-on-hide")).submit());

							dialog.show();
						}
					}
					break;
				case "_stack":
					if (button.form.checkValidity())
					{
						if (event.ctrlKey)
						{
							event.preventDefault();
							event.stopPropagation();
							button.setAttribute("formtarget", "_blank");
							button.click();
							button.setAttribute("formtarget", "_dialog");
						} else if (event.target.form.getAttribute("target") !== "_dialog")
						{
							let stackFrame = GStackFrame.create();

							if (button.hasAttribute("data-reload-on-hide"))
								stackFrame.addEventListener("hide", () => window.location = window.location.href);
							else if (button.hasAttribute("data-submit-on-hide"))
								stackFrame.addEventListener("hide", () => document.getElementById(button.getAttribute("data-submit-on-hide")).submit());

							stackFrame.show();
						}
					}
					break;
				case "_message":
					event.preventDefault();
					event.stopPropagation();

					button.disabled = true;
					new URL(button.getAttribute("formaction"))
						.post(new FormData(button.form), function (status)
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

					button.disabled = true;
					new URL(button.getAttribute("formaction"))
						.post(new FormData(button.form), function (status)
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

					button.disabled = true;
					new URL(button.getAttribute("formaction"))
						.post(new FormData(button.form), function (status)
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

					if (button.form.reportValidity())
					{
						button.disabled = true;
						new URL(button.getAttribute("formaction"))
							.post(new FormData(button.form), function (status)
							{
								alert(status);
								button.disabled = false;
							});
					}
					break;
				case "_hide":
					event.preventDefault();
					event.stopPropagation();
					if (window.frameElement
						&& window.frameElement.dialog
						&& window.frameElement.dialog.hide)
						window.frameElement.dialog.hide();
					else
						window.close();
					break;

				case "_progress-dialog":
					event.preventDefault();
					event.stopPropagation();

					if (button.form.reportValidity())
					{
						button.disabled = true;
						new URL(button.getAttribute("formaction"))
							.post(new FormData(button.form), process =>
							{
								process = JSON.parse(process);
								new GProgressDialog(process,
									{title: button.getAttribute("title")}).show();
								button.disabled = false;
							});
					}

					break;

				case "_progress-window":
					event.preventDefault();
					event.stopPropagation();

					if (button.form.reportValidity())
					{
						new URL(button.getAttribute("formaction"))
							.post(new FormData(button.form), function (process)
							{
								process = JSON.parse(process);
								document.body.appendChild(new GProgressWindow(process));
							});
					}

					break;

				case "_report":
				case "_report-dialog":
					event.preventDefault();
					event.stopPropagation();

					if (button.form.reportValidity())
					{
						let dialog = new GReportDialog();
						dialog.blocked = true;
						dialog.caption = button.getAttribute("title") || "Imprimir";
						dialog.post(button.getAttribute("formaction") || button.form.action,
							new FormData(button.form));
						button.disabled = false;
					}

					break;
			}

			return;
		}
	}
});


window.addEventListener("keydown", function (event)
{
	let link = event.target;
	if (link.tagName === 'BUTTON' && event.keyCode === 32)
	{
		event.target.click();
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
});
/* global customElements */

class GSideMenu extends GModal
{
	constructor(elements)
	{
		super();
		this.classList.add("g-side-menu");
		var menu = this.appendChild(document.createElement("div"));
		elements.forEach(e => menu.appendChild(e));
		this.addEventListener("click", e => e.stopPropagation() | this.hide());
	}

	showL()
	{
		setTimeout(() => this.classList.remove("R"), 100);
		setTimeout(() => this.classList.add("L"), 100);
	}

	showR()
	{
		setTimeout(() => this.classList.remove("L"), 100);
		setTimeout(() => this.classList.add("R"), 100);
	}

	show(element)
	{
		var center = element.getBoundingClientRect();
		center = center.left + (center.width / 2);
		if (center <= window.innerWidth / 2)
			this.showL();
		else
			this.showR();
	}
}

customElements.define('g-side-menu', GSideMenu);
/* global Message */

class Clipboard
{
	static copy(data, silent = false)
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
		if (!silent)
			Message.success("O texto " + data + " foi copiado com sucesso para a área de transferência.", 1000);
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("[data-copy-onclick]")).forEach(function (element)
	{
		element.addEventListener("click", () => Clipboard.copy(element.getAttribute("data-copy-onclick")));
	});
}
);
/* global NodeList, Clipboard, customElements */

class GContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.addEventListener("click", () => this.hide());
		this._private.mousedown = e => !this.contains(e.target) && this.hide();
	}

	show(context, target, x, y)
	{
		this._private.target = target;
		this._private.context = context;
		this.style.display = "flex";

		if (x + this.clientWidth > window.innerWidth)
			x = x >= this.clientWidth
				? x - this.clientWidth
				: x = window.innerWidth / 2 - this.clientWidth / 2;

		if (y + this.clientHeight > window.innerHeight)
			y = y >= this.clientHeight
				? y - this.clientHeight
				: y = window.innerHeight / 2 - this.clientHeight / 2;


		this.style.top = y + "px";
		this.style.left = x + "px";
		this.style.visibility = "visible";
	}

	hide()
	{
		this._private.target = null;
		this._private.context = null;
		this.style.display = "none";
		this.style.visibility = "hidden";
	}

	connectedCallback()
	{
		this.classList.add('g-context-menu');
		window.addEventListener("mousedown", this._private.mousedown);
	}

	disconnectedCallback()
	{
		window.removeEventListener("mousedown", this._private.mousedown);
	}

	get context()
	{
		return this._private.context;
	}

	get target()
	{
		return this._private.target;
	}
}

customElements.define('g-context-menu', GContextMenu);

window.addEventListener("contextmenu", event =>
{
	event = event || window.event;
	let action = event.target || event.srcElement;

	action = action.closest("[data-context-menu]");
	if (action)
	{
		document.getElementById(action.getAttribute("data-context-menu"))
			.show(action, event.target, event.clientX, event.clientY);
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
}, true);
/* global CopyTextMenuItem, CopyLinkMenuItem, OpenLinkMenuItem, Clipboard, customElements */

class GActionContextMenu extends GContextMenu
{
	connectedCallback()
	{
		super.connectedCallback();

		let copyText = this.appendChild(document.createElement("a"));
		copyText.innerHTML = "Copiar texto <i>&#X2217;</i>";
		copyText.addEventListener("click", () => Clipboard.copy(this.target.innerText, true));

		let copyLink = this.appendChild(document.createElement("a"));
		copyLink.innerHTML = "Copiar endereço <i>&#X2159;</i>";
		copyLink.addEventListener("click", () => Clipboard.copy(this.context.getAttribute("data-action"), true));

		let openLink = this.appendChild(document.createElement("a"));
		openLink.innerHTML = "Abrir em nova aba <i>&#X2256;</i>";
		openLink.addEventListener("click", () =>
		{
			let context = this.context;
			let target = this.context.getAttribute("data-target");
			this.context.setAttribute("data-target", "_blank");
			this.context.click();
			this.context.setAttribute("data-target", target);
		});
	}
}

customElements.define('g-action-context-menu', GActionContextMenu);


window.addEventListener("contextmenu", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-action]");
	if (!action)
		return;

	let menu = document.querySelector("g-action-context-menu")
		|| document.body.appendChild(new GActionContextMenu());
	menu.show(action, event.target, event.clientX, event.clientY);

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});


/* global customElements */

class GCheckableContextMenu extends GContextMenu
{
	connectedCallback()
	{
		super.connectedCallback();

		let inverterSelecao = this.appendChild(document.createElement("a"));
		inverterSelecao.innerHTML = "Inverter seleção<i>&#X2205;</i>";
		inverterSelecao.addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = !input.checked));

		let selecionarTudo = this.appendChild(document.createElement("a"));
		selecionarTudo.innerHTML = "Selecionar tudo<i>&#X1011;</i>";
		selecionarTudo.addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = true));

		let desmarcarSelecionados = this.appendChild(document.createElement("a"));
		desmarcarSelecionados.innerHTML = "Desmarcar tudo<i>&#X1014;</i>";
		desmarcarSelecionados.addEventListener("click", () => Array.from(this.context.getElementsByTagName("input")).forEach(input => input.checked = false));
	}
}

customElements.define('g-checkable-context-menu', GCheckableContextMenu);


window.addEventListener("contextmenu", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("ul.Checkable");
	if (!action)
		return;

	let menu = document.querySelector("g-checkable-context-menu")
		|| document.body.appendChild(new GCheckableContextMenu());
	menu.show(action, event.target, event.clientX, event.clientY);

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});
/* global ENTER, HOME, END, DOWN, UP, Clipboard, ActionContextMenu */

window.addEventListener("click", event => {
	event = event || window.event;

	if (event.button !== 0)
		return;

	let action = event.target || event.srcElement;
	action = action.closest("tr[data-action], td[data-action], li[data-action], div[data-action]");
	if (!action)
		return;

	action.blur();

	switch (action.getAttribute("data-method") ?
		action.getAttribute("data-method").toLowerCase() : "get")
	{
		case "get":

			let link = document.createElement("a");

			if (event.ctrlKey)
				link.setAttribute("target", "_blank");
			else if (action.hasAttribute("data-target"))
				link.setAttribute("target", action.getAttribute("data-target"));

			if (action.hasAttribute("data-action"))
				link.setAttribute("href", action.getAttribute("data-action"));

			if (action.hasAttribute("data-reload-on-hide"))
				link.setAttribute("data-reload-on-hide", action.getAttribute("data-reload-on-hide"));
			if (action.hasAttribute("data-submit-on-hide"))
				link.setAttribute("data-submit-on-hide", action.getAttribute("data-submit-on-hide"));

			if (action.hasAttribute("title"))
				link.setAttribute("title", action.getAttribute("title"));

			if (action.hasAttribute("data-block"))
				link.setAttribute("data-block", action.getAttribute("data-block"));

			if (action.hasAttribute("data-alert"))
				link.setAttribute("data-alert", action.getAttribute("data-alert"));

			if (action.hasAttribute("data-confirm"))
				link.setAttribute("data-confirm", action.getAttribute("data-confirm"));

			if (!event.ctrlKey && action.getAttribute("data-target") === "_dialog")
				link.navigator = Array.from(action.parentNode.children)
					.map(e => e.getAttribute("data-action"))
					.filter(e => e);

			document.body.appendChild(link);
			link.click();
			document.body.removeChild(link);
			break;
		case "post":

			let button = document.createElement("button");

			if (event.ctrlKey)
				button.setAttribute("target", "_blank");
			else if (action.hasAttribute("data-target"))
				button.setAttribute("target", action.getAttribute("data-target"));

			if (action.hasAttribute("data-action"))
				button.setAttribute("formaction", action.getAttribute("formaction"));

			if (action.hasAttribute("data-reload-on-hide"))
				button.setAttribute("data-reload-on-hide", action.getAttribute("data-reload-on-hide"));
			if (action.hasAttribute("data-submit-on-hide"))
				button.setAttribute("data-submit-on-hide", action.getAttribute("data-submit-on-hide"));

			if (action.hasAttribute("title"))
				button.setAttribute("title", action.getAttribute("title"));

			if (action.hasAttribute("data-block"))
				button.setAttribute("data-block", action.getAttribute("data-block"));

			if (action.hasAttribute("data-alert"))
				button.setAttribute("data-alert", action.getAttribute("data-alert"));

			if (action.hasAttribute("data-confirm"))
				button.setAttribute("data-confirm", action.getAttribute("data-confirm"));

			var form = action.closest("form");
			form.appendChild(button);
			button.click();
			form.removeChild(button);
			break;
	}

	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});

window.addEventListener("keydown", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-action]");
	if (!action)
		return;

	switch (event.keyCode)
	{
		case ENTER:
			action.click();
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;

		case HOME:
			var siblings = Array.from(action.parentNode.childNodes)
				.filter(node => node.tagName.toLowerCase() === "tr");
			if (siblings.length !== 0
				&& siblings[0].getAttribute("tabindex"))
				siblings[0].focus();


			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
		case END:
			var siblings = Array.from(action.parentNode.childNodes)
				.filter(node => node.tagName.toLowerCase() === "tr");
			if (siblings.length !== 0
				&& siblings[siblings.length - 1].getAttribute("tabindex"))
				siblings[siblings.length - 1].focus();

			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
		case UP:
			if (action.previousElementSibling &&
				action.previousElementSibling.getAttribute("tabindex"))
				action.previousElementSibling.focus();

			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
		case DOWN:
			if (action.nextElementSibling &&
				action.nextElementSibling.getAttribute("tabindex"))
				action.nextElementSibling.focus();
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
			break;
	}
});

window.addEventListener("mouseover", event => {
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-action]");
	if (!action)
		return;

	action.focus();
	event.preventDefault();
	event.stopPropagation();
	event.stopImmediatePropagation();
});

window.addEventListener("load", () => Array.from(document.querySelectorAll('tr[data-action], td[data-action], li[data-action], div[data-action]'))
		.forEach(action => action.setAttribute("tabindex", "1")));
/////////////////////////////////////////////////////////////////////////////
// ChangeHandler.js
/////////////////////////////////////////////////////////////////////////////

window.addEventListener("change", event =>
	{
		let action = event.target || event.srcElement;

		if ((!action.tagName === "INPUT"
			&& !action.tagName === "SELECT")
			|| (!action.hasAttribute("data-method")
				&& !action.hasAttribute("data-action")
				&& !action.hasAttribute("data-target")))
			return;

		action.blur();

		switch (action.getAttribute("data-method") ? action.getAttribute("data-method").toLowerCase() : "get")
		{
			case "get":

				let link = document.createElement("a");

				if (action.hasAttribute("data-target"))
					link.setAttribute("target", action.getAttribute("data-target"));

				if (action.hasAttribute("data-action"))
					link.setAttribute("href", action.getAttribute("data-action"));

				if (action.hasAttribute("data-reload-on-hide"))
					link.setAttribute("data-reload-on-hide", action.getAttribute("data-reload-on-hide"));
				if (action.hasAttribute("data-submit-on-hide"))
					link.setAttribute("data-submit-on-hide", action.getAttribute("data-submit-on-hide"));

				if (action.hasAttribute("title"))
					link.setAttribute("title", action.getAttribute("title"));

				if (action.hasAttribute("data-block"))
					link.setAttribute("data-block", action.getAttribute("data-block"));

				if (action.hasAttribute("data-alert"))
					link.setAttribute("data-alert", action.getAttribute("data-alert"));

				if (action.hasAttribute("data-confirm"))
					link.setAttribute("data-confirm", action.getAttribute("data-confirm"));

				document.body.appendChild(link);
				link.click();
				document.body.removeChild(link);
				break;
			case "post":
				let button = document.createElement("button");

				if (action.hasAttribute("data-target"))
					button.setAttribute("target", action.getAttribute("data-target"));

				if (action.hasAttribute("data-action"))
					button.setAttribute("formaction", action.getAttribute("formaction"));

				if (action.hasAttribute("data-reload-on-hide"))
					button.setAttribute("data-reload-on-hide", action.getAttribute("data-reload-on-hide"));
				if (action.hasAttribute("data-submit-on-hide"))
					button.setAttribute("data-submit-on-hide", action.getAttribute("data-submit-on-hide"));

				if (action.hasAttribute("title"))
					button.setAttribute("title", action.getAttribute("title"));

				if (action.hasAttribute("data-block"))
					button.setAttribute("data-block", action.getAttribute("data-block"));

				if (action.hasAttribute("data-alert"))
					button.setAttribute("data-alert", action.getAttribute("data-alert"));

				if (action.hasAttribute("data-confirm"))
					button.setAttribute("data-confirm", action.getAttribute("data-confirm"));


				var form = action.closest("form");
				form.appendChild(button);
				button.click();
				form.removeChild(button);
				break;
		}
	});
/* global customElements */

class GNavBar extends HTMLElement
{
	constructor(links, url)
	{
		super();
		this._private = {};
		this._private.index = 0;
		this._private.links = links;

		this._private.text = document.createElement("label");
		this._private.text.addEventListener("click", () => this.update(this._private.links[this._private.index]));

		this._private.frst = document.createElement("a");
		this._private.frst.innerHTML = "&#x2277;";
		this._private.frst.setAttribute("href", "#");
		this._private.frst.addEventListener("click", () => this.update(this._private.links[0]));

		this._private.prev = document.createElement("a");
		this._private.prev.innerHTML = "&#x2273;";
		this._private.prev.setAttribute("href", "#");
		this._private.prev.addEventListener("click", () => this.update(this._private.links[this._private.index - 1]));

		this._private.next = document.createElement("a");
		this._private.next.innerHTML = "&#x2275;";
		this._private.next.setAttribute("href", "#");
		this._private.next.addEventListener("click", () => this.update(this._private.links[this._private.index + 1]));
		this._private.last = document.createElement("a");

		this._private.last.innerHTML = "&#x2279;";
		this._private.last.setAttribute("href", "#");
		this._private.last.addEventListener("click", () => this.update(this._private.links[this._private.links.length - 1]));

		this.update(url);
	}

	connectedCallback()
	{
		this.appendChild(this._private.frst);
		this.appendChild(this._private.prev);
		this.appendChild(this._private.text);
		this.appendChild(this._private.next);
		this.appendChild(this._private.last);
	}

	disconnectedCallback()
	{
		this.removeChild(this._private.frst);
		this.removeChild(this._private.prev);
		this.removeChild(this._private.text);
		this.removeChild(this._private.next);
		this.removeChild(this._private.last);
	}

	update(url)
	{
		if (!this.dispatchEvent(new CustomEvent('update', {cancelable: true, detail: {navbar: this, target: url}})))
			return this;

		this._private.index = Math.max(this._private.links.indexOf(url), 0);
		this._private.frst.setAttribute("navbar-disabled", String(this._private.index === 0));
		this._private.prev.setAttribute("navbar-disabled", String(this._private.index === 0));
		this._private.text.innerHTML = "" + (this._private.index + 1) + " de " + this._private.links.length;
		this._private.next.setAttribute("navbar-disabled", String(this._private.index === this._private.links.length - 1));
		this._private.last.setAttribute("navbar-disabled", String(this._private.index === this._private.links.length - 1));
		return this;
	}
}

customElements.define('g-navbar', GNavBar);
/* global customElements */

class GSlider extends HTMLElement
{
	constructor(value, next, prev, format, size = 5)
	{
		super();
		this.classList.add("g-slider");
		this.addEventListener("mouseover", () => this.focus());
		this.addEventListener("mouseoust", () => this.blur());

		if (!format)
			format = e => e;
		this.addEventListener((/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel", function (event)
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
		this.addEventListener("keydown", function (event)
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

		var goprev = this.appendChild(document.createElement("a"));
		goprev.href = "#";
		goprev.appendChild(document.createElement("i")).innerHTML = "&#X2278;";
		goprev.addEventListener("click", () => update(prev(value)));

		function execute(val, func, count)
		{
			for (let i = 0; i < count; i++)
				val = func(val);
			return val;
		}


		var prevs = [];
		for (let i = size - 1; i >= 0; i--)
		{
			prevs[i] = this.appendChild(document.createElement("label"));
			prevs[i].onclick = () => update(execute(value, prev, i + 1));
		}

		var main = this.appendChild(document.createElement("label"));
		main.style.fontWeight = "bold";

		var nexts = []
		for (let i = 0; i < size; i++)
		{
			nexts[i] = this.appendChild(document.createElement("label"));
			nexts[i].onclick = () => update(execute(value, next, i + 1));
		}

		var gonext = this.appendChild(document.createElement("a"));
		gonext.href = "#";
		gonext.appendChild(document.createElement("i")).innerHTML = "&#X2276;";
		gonext.addEventListener("click", () => update(next(value)));
		this.value = () => value;
		var update = val =>
		{
			if (val !== null)
			{
				if (!this.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {slider: this, value: val}})))
					return false;
				value = val;
				prevs[0].innerHTML = '-';
				prevs[1].innerHTML = '-';
				prevs[2].innerHTML = '-';
				prevs[3].innerHTML = '-';
				prevs[4].innerHTML = '-';
				main.innerHTML = format(value);
				nexts[0].innerHTML = '-';
				nexts[1].innerHTML = '-';
				nexts[2].innerHTML = '-';
				nexts[1].innerHTML = '-';
				nexts[0].innerHTML = '-';


				for (let i = 0; i < prevs.length; i++)
					prevs[i].innerHTML = format(execute(val, prev, i + 1));

				for (let i = 0; i < nexts.length; i++)
					nexts[i].innerHTML = format(execute(val, next, i + 1));
				this.dispatchEvent(new CustomEvent('update', {detail: {slider: this}}));
			}
		};

		update(value);
	}
}



customElements.define('g-slider', GSlider);
/* global DateFormat, customElements */

var calendars = {};
class GCalendar extends HTMLElement
{
	constructor(init)
	{
		super();

		var selection = [];

		var max = undefined;
		if (init && init.max)
			max = init.max;

		var month = new Date();
		if (init && init.month)
			month = init.month;
		month.setHours(0, 0, 0, 0);


		var update = () =>
		{
			while (this.firstChild)
				this.removeChild(this.firstChild);

			var body = this.appendChild(document.createElement("div"));
			body.appendChild(document.createElement("label")).innerText = "Dom";
			body.appendChild(document.createElement("label")).innerText = "Seg";
			body.appendChild(document.createElement("label")).innerText = "Ter";
			body.appendChild(document.createElement("label")).innerText = "Qua";
			body.appendChild(document.createElement("label")).innerText = "Qui";
			body.appendChild(document.createElement("label")).innerText = "Sex";
			body.appendChild(document.createElement("label")).innerText = "Sab";

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
				var link = body.appendChild(document.createElement("a"));
				link.setAttribute("href", "#");
				link.innerText = date.getDate();
				link.onclick = () => this.action(date);

				if (date.getTime() === now.getTime())
					link.classList.add("current");
				if (date.getMonth() !== month.getMonth())
					link.classList.add("disabled");
				if (selection.some(e => e.getTime() === date.getTime()))
					link.classList.add("selected");
			});

			var foot = body.appendChild(document.createElement("div"));

			var prevYear = foot.appendChild(document.createElement("a"));
			prevYear.setAttribute("href", "#");
			prevYear.innerText = "<<";
			prevYear.onclick = () => month.setFullYear(month.getFullYear() - 1) | update();

			var prevMonth = foot.appendChild(document.createElement("a"));
			prevMonth.setAttribute("href", "#");
			prevMonth.innerText = "<";
			prevMonth.onclick = () => month.setMonth(month.getMonth() - 1) | update();

			foot.appendChild(document.createElement("label")).innerText = DateFormat.MONTH.format(month);

			var nextMonth = foot.appendChild(document.createElement("a"));
			nextMonth.setAttribute("href", "#");
			nextMonth.innerText = ">";
			nextMonth.onclick = () => month.setMonth(month.getMonth() + 1) | update();

			var nextYear = foot.appendChild(document.createElement("a"));
			nextYear.setAttribute("href", "#");
			nextYear.innerText = ">>";
			nextYear.onclick = () => month.setFullYear(month.getFullYear() + 1) | update();
		};

		this.addEventListener((/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel", event =>
		{
			event.preventDefault();

			if (event.ctrlKey)
			{
				if (event.detail)
					if (event.detail > 0)
						month.setFullYear(month.getFullYear() + 1);
					else
						month.setFullYear(month.getFullYear() - 1);
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						month.setFullYear(month.getFullYear() + 1);
					else
						month.setFullYear(month.getFullYear() - 1);
			} else {
				if (event.detail)
					if (event.detail > 0)
						month.setMonth(month.getMonth() + 1);
					else
						month.setMonth(month.getMonth() - 1);
				else if (event.wheelDelta)
					if (event.wheelDelta > 0)
						month.setMonth(month.getMonth() + 1);
					else
						month.setMonth(month.getMonth() - 1);
			}

			update();
		});


		this.clear = () =>
		{
			if (!this.dispatchEvent(new CustomEvent('clear', {cancelable: true, detail: {calendar: this}})))
				return false;

			selection = [];

			update();

			this.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
			return true;
		};


		this.select = (date) =>
		{
			date = new Date(date);
			date.setHours(0, 0, 0, 0);

			if (selection.some(e => e.getTime() === date.getTime()))
				return false;

			if (!this.dispatchEvent(new CustomEvent('select', {cancelable: true, detail: {calendar: this, date: date}})))
				return false;

			if (max !== undefined
				&& max <= selection.length)
				selection.shift();

			selection.push(date);

			update();

			this.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
			return true;
		};

		this.remove = (date) =>
		{
			date = new Date(date);
			date.setHours(0, 0, 0, 0);

			if (!selection.some(e => e.getTime() === date.getTime()))
				return false;

			if (!this.dispatchEvent(new CustomEvent('remove', {cancelable: true, detail: {calendar: this, date: date}})))
				return false;

			selection = selection.filter(e => e.getTime() !== date.getTime());
			update();

			this.dispatchEvent(new CustomEvent('update', {detail: {calendar: this}}));
			return true;
		};

		this.action = (date) =>
		{
			date = new Date(date);
			date.setHours(0, 0, 0, 0);

			if (!this.dispatchEvent(new CustomEvent('action', {cancelable: true, detail: {calendar: this, date: date}})))
				return false;

			return selection.some(e => e.getTime() === date.getTime()) ?
				this.remove(date) : this.select(date);
		};

		this.length = () => selection.length;
		this.selection = () => Array.from(selection);
		update();
	}
}

customElements.define('g-calendar', GCalendar);
/* global DateFormat, customElements */

class DateSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		var date = new Date();
		this._private.calendar = this.appendChild(new GCalendar({month: new Date(), max: 1}));
		this._private.calendar.select(date);
		this._private.calendar.addEventListener("remove", event => event.preventDefault());
		this._private.calendar.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected',
				{detail: this.selection})));
	}

	get selection()
	{
		return DateFormat.DATE.format(this._private.calendar.selection()[0]);
	}
}


customElements.define('g-date-selector', DateSelector);
/* global customElements */

class TimeSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		this._private.h = this.appendChild(new GSlider(0, e => (e + 23) % 24, e => (e + 1) % 24, e => "00".concat(String(e)).slice(-2), 5));
		this._private.h.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		this._private.m = this.appendChild(new GSlider(0, e => (e + 50) % 60, e => (e + 10) % 60, e => "00".concat(String(e)).slice(-2), 5));
		this._private.m.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return "00".concat(String(this._private.h.value())).slice(-2) + ":" + "00".concat(String(this._private.m.value())).slice(-2);
	}
}

customElements.define('g-time-selector', TimeSelector);
/* global customElements */

class MonthSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};

		var date = new Date();
		var months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];
		this._private.m = this.appendChild(new GSlider(date.getMonth(), e => e > 0 ? e - 1 : 11, e => e < 11 ? e + 1 : 0, e => months[e]));
		this._private.m.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		this._private.y = this.appendChild(new GSlider(date.getFullYear(), e => e - 1, e => e + 1, e => "0000".concat(String(e)).slice(-4)));
		this._private.y.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return "00".concat(String(this._private.m.value() + 1)).slice(-2) + "/" + "0000".concat(String(this._private.y.value())).slice(-4);
	}
}

customElements.define('g-month-selector', MonthSelector);
/* global DateFormat, customElements */

class DateTimeSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.date = this.appendChild(new DateSelector());
		this._private.time = this.appendChild(new TimeSelector());
		this._private.date.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.time.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return this._private.date.selection + " " + this._private.time.selection;
	}
}


customElements.define('g-datetime-selector', DateTimeSelector);
/* global customElements */

class DateIntervalSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.min = this.appendChild(new DateSelector());
		this._private.max = this.appendChild(new DateSelector());
		this._private.min.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.max.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return this._private.min.selection + " - " + this._private.max.selection;
	}
}


customElements.define('g-date-interval-selector', DateIntervalSelector);
/* global customElements */

class TimeIntervalSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.min = this.appendChild(new TimeSelector());
		this._private.max = this.appendChild(new TimeSelector());
		this._private.min.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.max.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return this._private.min.selection + " - " + this._private.max.selection;
	}
}


customElements.define('g-time-interval-selector', TimeIntervalSelector);
/* global customElements */

class MonthIntervalSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.min = this.appendChild(new MonthSelector());
		this._private.max = this.appendChild(new MonthSelector());
		this._private.min.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.max.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return this._private.min.selection + " - " + this._private.max.selection;
	}
}


customElements.define('g-month-interval-selector', MonthIntervalSelector);
/* global customElements */

class DateTimeIntervalSelector extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.min = this.appendChild(new DateTimeSelector());
		this._private.max = this.appendChild(new DateTimeSelector());
		this._private.min.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this._private.max.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		return this._private.min.selection + " - " + this._private.max.selection;
	}
}


customElements.define('g-datetime-interval-selector', DateTimeIntervalSelector);
/* global DateFormat, customElements */

class IconSelector extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		this.add("1000");
		this.add("1001");
		this.add("1002");
		this.add("1003");
		this.add("1006");
		this.add("1007");
		this.add("1010");
		this.add("1011");
		this.add("1012");
		this.add("1013");
		this.add("1014");
		this.add("1020");
		this.add("1021");
		this.add("1022");
		this.add("1023");
		this.add("1024");
		this.add("2000");
		this.add("2001");
		this.add("2002");
		this.add("2003");
		this.add("2004");
		this.add("2005");
		this.add("2006");
		this.add("2007");
		this.add("2008");
		this.add("2009");
		this.add("200A");
		this.add("2010");
		this.add("2011");
		this.add("2012");
		this.add("2013");
		this.add("2014");
		this.add("2015");
		this.add("2016");
		this.add("2017");
		this.add("2018");
		this.add("2019");
		this.add("2020");
		this.add("2021");
		this.add("2022");
		this.add("2023");
		this.add("2024");
		this.add("2025");
		this.add("2026");
		this.add("2027");
		this.add("2030");
		this.add("2031");
		this.add("2032");
		this.add("2033");
		this.add("2034");
		this.add("2035");
		this.add("2036");
		this.add("2037");
		this.add("2038");
		this.add("2039");
		this.add("2040");
		this.add("2041");
		this.add("2042");
		this.add("2043");
		this.add("2044");
		this.add("2045");
		this.add("2046");
		this.add("2047");
		this.add("2048");
		this.add("2049");
		this.add("2050");
		this.add("2051");
		this.add("2052");
		this.add("2053");
		this.add("2054");
		this.add("2055");
		this.add("2056");
		this.add("2057");
		this.add("2058");
		this.add("2059");
		this.add("2070");
		this.add("2071");
		this.add("2072");
		this.add("2073");
		this.add("2074");
		this.add("2075");
		this.add("2076");
		this.add("2077");
		this.add("2078");
		this.add("2079");
		this.add("2080");
		this.add("2081");
		this.add("2082");
		this.add("2083");
		this.add("2084");
		this.add("2085");
		this.add("2086");
		this.add("2087");
		this.add("2088");
		this.add("2089");
		this.add("2090");
		this.add("2091");
		this.add("2092");
		this.add("2093");
		this.add("2094");
		this.add("2095");
		this.add("2096");
		this.add("2097");
		this.add("2098");
		this.add("2099");
		this.add("2100");
		this.add("2101");
		this.add("2102");
		this.add("2103");
		this.add("2104");
		this.add("2105");
		this.add("2106");
		this.add("2107");
		this.add("2108");
		this.add("2109");
		this.add("2110");
		this.add("2111");
		this.add("2112");
		this.add("2113");
		this.add("2114");
		this.add("2115");
		this.add("2116");
		this.add("2117");
		this.add("2118");
		this.add("2119");
		this.add("2120");
		this.add("2121");
		this.add("2122");
		this.add("2123");
		this.add("2124");
		this.add("2125");
		this.add("2126");
		this.add("2127");
		this.add("2128");
		this.add("2129");
		this.add("2130");
		this.add("2131");
		this.add("2132");
		this.add("2133");
		this.add("2134");
		this.add("2135");
		this.add("2136");
		this.add("2137");
		this.add("2138");
		this.add("2139");
		this.add("2140");
		this.add("2141");
		this.add("2142");
		this.add("2143");
		this.add("2144");
		this.add("2145");
		this.add("2146");
		this.add("2147");
		this.add("2148");
		this.add("2149");
		this.add("2150");
		this.add("2151");
		this.add("2152");
		this.add("2153");
		this.add("2154");
		this.add("2155");
		this.add("2156");
		this.add("2157");
		this.add("2158");
		this.add("2159");
		this.add("2160");
		this.add("2161");
		this.add("2162");
		this.add("2163");
		this.add("2164");
		this.add("2165");
		this.add("2166");
		this.add("2167");
		this.add("2168");
		this.add("2169");
		this.add("2170");
		this.add("2171");
		this.add("2172");
		this.add("2173");
		this.add("2174");
		this.add("2175");
		this.add("2176");
		this.add("2177");
		this.add("2178");
		this.add("2179");
		this.add("2180");
		this.add("2181");
		this.add("2182");
		this.add("2183");
		this.add("2187");
		this.add("2188");
		this.add("2189");
		this.add("2190");
		this.add("2191");
		this.add("2192");
		this.add("2193");
		this.add("2194");
		this.add("2195");
		this.add("2196");
		this.add("2197");
		this.add("2198");
		this.add("2199");
		this.add("2200");
		this.add("2201");
		this.add("2202");
		this.add("2203");
		this.add("2204");
		this.add("2205");
		this.add("2206");
		this.add("2207");
		this.add("2208");
		this.add("2209");
		this.add("2210");
		this.add("2211");
		this.add("2212");
		this.add("2213");
		this.add("2214");
		this.add("2215");
		this.add("2216");
		this.add("2217");
		this.add("2218");
		this.add("2219");
		this.add("2220");
		this.add("2221");
		this.add("2222");
		this.add("2223");
		this.add("2224");
		this.add("2225");
		this.add("2226");
		this.add("2227");
		this.add("2228");
		this.add("2229");
		this.add("2230");
		this.add("2231");
		this.add("2232");
		this.add("2233");
		this.add("2234");
		this.add("2235");
		this.add("2236");
		this.add("2237");
		this.add("2238");
		this.add("2239");
		this.add("2240");
		this.add("2241");
		this.add("2242");
		this.add("2243");
		this.add("2244");
		this.add("2245");
		this.add("2246");
		this.add("2247");
		this.add("2248");
		this.add("2249");
		this.add("2250");
		this.add("2251");
		this.add("2252");
		this.add("2253");
		this.add("2254");
		this.add("2255");
		this.add("2256");
		this.add("2257");
		this.add("2258");
		this.add("2259");
		this.add("2260");
		this.add("2261");
		this.add("2262");
		this.add("2263");
		this.add("2264");
		this.add("2265");
		this.add("2266");
		this.add("2267");
		this.add("2268");
		this.add("2269");
		this.add("2270");
		this.add("2271");
		this.add("2272");
		this.add("2273");
		this.add("2274");
		this.add("2275");
		this.add("2276");
		this.add("2277");
		this.add("2278");
		this.add("2279");
		this.add("2280");
		this.add("2281");
		this.add("2282");
		this.add("2283");
		this.add("3000");
		this.add("3001");
		this.add("3002");
		this.add("3003");
		this.add("3004");
		this.add("3005");
		this.add("3006");
		this.add("3007");
		this.add("3008");
		this.add("3009");
		this.add("3010");
		this.add("3011");
		this.add("3012");
		this.add("3013");
		this.add("3014");
		this.add("3015");
		this.add("3016");
		this.add("3017");
		this.add("3018");
		this.add("3019");
	}

	add(code)
	{
		let icon = this.appendChild(document.createElement("a"));
		icon.innerHTML = "&#X" + code + ";";
		icon.href = "#";
		icon.addEventListener("click", () =>
			this.dispatchEvent(new CustomEvent('selected',
				{detail: {selector: this, icon: code}})));
	}
}

customElements.define('g-icon-selector', IconSelector);
/* global customElements */

class ReportSelector extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		this.add("PDF", "&#x2218;");
		this.add("XLS", "&#x2221;");
		this.add("DOC", "&#x2220;");
		this.add("CSV", "&#x2222;");
	}

	add(type, icon)
	{
		var link = this.appendChild(document.createElement("a"));
		link.appendChild(document.createTextNode(type));
		link.appendChild(document.createElement("i")).innerHTML = icon;
		link.addEventListener("click", () => this.dispatchEvent(new CustomEvent('selected', {cancelable: false, detail: type})));
		return link;
	}
}

window.addEventListener("load", () => customElements.define('g-report-selector', ReportSelector));
/* global customElements */

class Picker extends GWindow
{
	constructor()
	{
		super();
		let close = document.createElement("a");
		close.href = "#";
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);
	}

	connectedCallback()
	{
		this.classList.add("g-picker");
	}

	get commit()
	{
		if (!this._private.commit)
		{
			this._private.commit = this.foot.appendChild(document.createElement("a"));
			this._private.commit.innerText = "Concluir";
			this._private.commit.href = "#";
		}
		return this._private.commit;
	}
}

customElements.define('g-picker', Picker);
/* global DateFormat, customElements */

class DatePicker extends Picker
{
	constructor()
	{
		super();
		this.classList.add("g-date-picker");
		this.caption = "Selecione uma data";
		this._private.date = this.body.appendChild(new DateSelector());
		this._private.date.addEventListener("selected", e => this.dispatchEvent(new CustomEvent('picked', {detail: e.detail})) | this.hide());
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

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new DatePicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	});
});

customElements.define('g-date-picker', DatePicker);
/* global customElements */

class TimePicker extends Picker
{
	constructor()
	{
		super();
		this.classList.add("g-time-picker");
		this.caption = "Selecione uma hora";
		var selector = this.body.appendChild(new TimeSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("picked", {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}


customElements.define('g-time-picker', TimePicker);

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
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new TimePicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			link.focus();
			link.blur();
		});
	});
});

/* global customElements */

class MonthPicker extends Picker
{
	constructor()
	{
		super();
		this.classList.add("g-month-picker");
		this.caption = "Selecione uma hora";
		var selector = this.body.appendChild(new MonthSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("picked", {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}


customElements.define('g-month-picker', MonthPicker);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.Month")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new MonthPicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			link.focus();
			link.blur();
		});
	});
});

/* global customElements */

class DateTimePicker extends Picker
{
	constructor()
	{
		super();
		this.caption = "Selecione uma data e hora";
		var selector = this.body.appendChild(new DateTimeSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent('picked', {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}

customElements.define('g-datetime-picker', DateTimePicker);

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

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new DateTimePicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	});
});
/* global customElements */

class DateIntervalPicker extends Picker
{
	constructor()
	{
		super();
		this.caption = "Selecione um período";
		var selector = this.body.appendChild(new DateIntervalSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent('picked', {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}

customElements.define('g-date-interval-picker', DateIntervalPicker);

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

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new DateIntervalPicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	});
});
/* global customElements */

class TimeIntervalPicker extends Picker
{
	constructor()
	{
		super();
		this.caption = "Selecione um período";
		this.classList.add("g-time-interval-picker");
		var selector = this.body.appendChild(new TimeIntervalSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent('picked', {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}

customElements.define('g-time-interval-picker', TimeIntervalPicker);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.TimeInterval")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new TimeIntervalPicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	});
});
/* global customElements */

class MonthIntervalPicker extends Picker
{
	constructor()
	{
		super();
		this.caption = "Selecione um período";
		var selector = this.body.appendChild(new MonthIntervalSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent('picked', {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}

customElements.define('g-month-interval-picker', MonthIntervalPicker);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input.MonthInterval")).forEach(function (input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new MonthIntervalPicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	});
});
/* global customElements */

class DateTimeIntervalPicker extends Picker
{
	constructor()
	{
		super();
		this.caption = "Selecione um período";
		var selector = this.body.appendChild(new DateTimeIntervalSelector());
		this.commit.innerText = selector.selection;
		this.addEventListener("show", () => this.commit.focus());
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent('picked', {detail: this.commit.innerText})) | this.hide());
		this.show();
	}
}

customElements.define('g-datetime-interval-picker', DateTimeIntervalPicker);

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

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new DateTimeIntervalPicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	});
});
/* global DateFormat, customElements */

class IconPicker extends Picker
{
	constructor()
	{
		super();

		this.classList.add("g-picker");
		this.head.appendChild(document.createTextNode("Selecione um ícone"));
		var selector = this.body.appendChild(document.createElement("g-icon-selector"));
		selector.addEventListener("selected", e => this.dispatchEvent(new CustomEvent('picked', {detail: e.detail.icon})) | this.hide());
		this.show();
	}
}

customElements.define('g-icon-picker', IconPicker);

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

			if (input.value) {
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				new IconPicker().addEventListener("picked", e =>
				{
					input.value = e.detail;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				});


			link.focus();
			link.blur();
		});
	});
});
/* global customElements */

class GBlock extends GWindow
{
	constructor()
	{
		super();
		this.blocked = true;
		this.classList.add("g-block");
		this.body.appendChild(window.top.document.createElement('progress'));
		this.foot.appendChild(window.top.document.createElement('digital-clock'));
	}
}

customElements.define('g-block', GBlock);

GBlock.show = function (text)
{
	if (!window.top.GateBlockDialog)
	{
		window.top.GateBlockDialog = new GBlock(text);
		window.top.GateBlockDialog.caption = text || "Aguarde";
		window.top.GateBlockDialog.show();
	}
};

GBlock.hide = function ()
{
	if (window.top.GateBlockDialog)
	{
		window.top.GateBlockDialog.hide();
		window.top.GateBlockDialog = null;
	}
};

window.addEventListener("load", function ()
{
	GBlock.hide();

	Array.from(document.querySelectorAll("form[data-block]")).forEach(function (element)
	{
		element.addEventListener("submit", function ()
		{
			if (this.getAttribute("data-block"))
				GBlock.show(this.getAttribute("data-block"));
		});
	});

	Array.from(document.querySelectorAll("a")).forEach(function (element)
	{
		element.addEventListener("click", function ()
		{
			if (this.getAttribute("data-block"))
				GBlock.show(this.getAttribute("data-block"));
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
						GBlock.show(button.getAttribute("data-block"));
						event.target.removeEventListener(event.type, arguments.callee);
					});
				else
					GBlock.show(this.getAttribute("data-block"));
		});
	});
});



/* global echarts, customElements */

class GChart extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
	}

	set data(data)
	{
		this.setAttribute("data", data);
	}

	get data()
	{
		return this.getAttribute("data");
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	get type()
	{
		return this.getAttribute("type");
	}

	set title(title)
	{
		this.setAttribute("title", title);
	}

	get title()
	{
		return this.getAttribute("title");
	}

	attributeChangedCallback()
	{
		if (!this.data
			|| !this.type
			|| !this.title)
			return;

		var data = JSON.parse(this.data);

		let categories = new Array();
		for (var i = 1; i < data.length; i++)
			categories.push(data[i][0]);

		let groups = new Array();
		for (var j = 1; j < data[0].length; j++)
		{
			var group = {'label': data[0][j], 'values': new Array()};
			for (var i = 1; i < data.length; i++)
				group.values.push(data[i][j]);
			groups.push(group);
		}


		let color = () => '#'
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10))
				+ '0123456789'.charAt(Math.floor(Math.random() * 10));
		let tooltipFormater = params => params.name + ': ' + params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
		let axisLabelFormater = value => value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");
		let itemStyleFormater = params => params.value.toString().replace(".", ',').replace(/\B(?=(\d{3})+(?!\d))/g, ".");

		switch (this.type)
		{
			case 'cchart':
				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, xAxis: {type: 'category', data: categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'top', formatter: itemStyleFormater}}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = color;
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'bchart':

				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, yAxis: {type: 'category', data: categories,
						axisLabel: {rotate: -70}}, xAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "bar", itemStyle: {normal: {label: {show: true, position: 'right', formatter: itemStyleFormater}}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					} else
						options.series[options.series.length - 1].itemStyle.normal.color = color;
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'lchart':

				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, xAxis: {type: 'category', data: categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					}
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'achart':

				var options = {calculable: true, tooltip: {show: true, formatter: tooltipFormater}, xAxis: {type: 'category', data: categories,
						axisLabel: {rotate: 10}}, yAxis: {type: 'value', axisLabel: {formatter: axisLabelFormater}},
					dataZoom: {show: true, y: 26, height: 12, start: 0, end: 100 / (categories.length > 0 ? Math.ceil((categories.length / 8)) : 1)},
					toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
					series: []};

				options.title = {x: 'center', text: this.title};

				for (var i = 0; i < groups.length; i++)
				{
					options.series.push({type: "line", itemStyle: {normal: {areaStyle: {type: 'default'}}}, data: groups[i].values});
					options.series[options.series.length - 1].name = groups[i].label;
					if (groups[i].color)
						options.series[options.series.length - 1].itemStyle.normal.color = groups[i].color;
					if (groups.length > 1)
					{
						if (!options.legend)
							options.legend = {x: 'center', y: 'bottom', data: []};
						options.legend.data.push(groups[i].label);
					}
				}
				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'pchart':

				var options =
					{calculable: true, tooltip: {show: true, formatter: tooltipFormater},
						legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
						toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
						series: [{type: 'pie', roseType: false, radius: this.title ? '60%' : '80%', data: [],
								center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};

				options.title = {x: 'center', text: this.title};

				if (groups.length > 1)
				{
					for (var i = 0; i < groups.length; i++)
					{
						options.legend.data.push(groups[i].label);
						var sum = 0;
						for (var j = 0; j < groups[i].values.length; j++)
							sum = sum + groups[i].values[j];
						options.series[0].data.push({name: groups[i].label, value: sum});
						if (groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < categories.length; i++)
					{
						options.legend.data.push(categories[i]);
						options.series[0].data.push({name: categories[i], value: groups[0].values[i]});
					}
				}

				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'dchart':

				var options =
					{calculable: true, tooltip: {show: true, formatter: tooltipFormater},
						legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
						toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
						series: [{type: 'pie', roseType: false, radius: this.title ? ['40%', '60%'] : ['60%', '80%'], data: [],
								center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};

				options.title = {x: 'center', text: this.title};

				if (groups.length > 1)
				{
					for (var i = 0; i < groups.length; i++)
					{
						options.legend.data.push(groups[i].label);
						var sum = 0;
						for (var j = 0; j < groups[i].values.length; j++)
							sum = sum + groups[i].values[j];
						options.series[0].data.push({name: groups[i].label, value: sum});
						if (groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < categories.length; i++)
					{
						options.legend.data.push(categories[i]);
						options.series[0].data.push({name: categories[i], value: groups[0].values[i]});
					}
				}

				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
			case 'rchart':
				var options =
					{calculable: true, tooltip: {show: true, formatter: tooltipFormater},
						legend: {x: 'center', orient: 'horizontal', y: 'bottom', data: []},
						toolbox: {show: true, feature: {restore: {show: true, title: 'Restaurar'}, saveAsImage: {show: true, title: 'Salvar'}}},
						series: [{type: 'pie', roseType: 'area', radius: this.title ? [20, '60%'] : [20, '80%'], data: [],
								center: ['50%', '50%'], itemStyle: {normal: {label: {show: false}, labelLine: {show: false}}}}]};

				options.title = {x: 'center', text: this.title};

				if (groups.length > 1)
				{
					for (var i = 0; i < groups.length; i++)
					{
						options.legend.data.push(groups[i].label);
						var sum = 0;
						for (var j = 0; j < groups[i].values.length; j++)
							sum = sum + groups[i].values[j];
						options.series[0].data.push({name: groups[i].label, value: sum});
						if (groups[i].color)
							options.series[0].data[options.series[0].data.length - 1].itemStyle = {normal: {color: groups[i].color}};
					}
				} else
				{
					for (var i = 0; i < categories.length; i++)
					{
						options.legend.data.push(categories[i]);
						options.series[0].data.push({name: categories[i], value: groups[0].values[i]});
					}
				}

				var chart = echarts.init(this);
				chart.clear();
				chart.setOption(options);
				break;
		}
	}

	static get observedAttributes() {
		return ['type', 'data', 'title'];
	}
}

customElements.define('g-chart', GChart);




/* global customElements */

class GChartDialog extends GWindow
{
	constructor()
	{
		super();
		this.caption = "Chart";
		this.classList.add("g-chart-dialog");

		this.body.appendChild(new GChart());

		var close = this.head.appendChild(document.createElement('a'));
		close.href = "#";
		close.dialog = this;
		close.title = "Fechar";
		close.innerHTML = "<i>&#x1011;</i>";
		close.onclick = () => this.hide();

		let commands = new GDialogCommands();
		var cchart = commands.appendChild(document.createElement('a'));
		cchart.href = "#";
		cchart.dialog = this;
		cchart.title = "Coluna";
		cchart.innerHTML = "Coluna<i>&#x2033;</i>";
		cchart.onclick = () => this.type = 'cchart';

		var bchart = commands.appendChild(document.createElement('a'));
		bchart.href = "#";
		bchart.dialog = this;
		bchart.title = "Barra";
		bchart.innerHTML = "Barra<i>&#x2246;</i>";
		bchart.onclick = () => this.type = 'bchart';

		var lchart = commands.appendChild(document.createElement('a'));
		lchart.href = "#";
		lchart.dialog = this;
		lchart.title = "Linha";
		lchart.innerHTML = "Linha<i>&#x2032;</i>";
		lchart.onclick = () => this.type = 'lchart';

		var achart = commands.appendChild(document.createElement('a'));
		achart.href = "#";
		achart.dialog = this;
		achart.title = "Área";
		achart.innerHTML = "Área<i>&#x2244;</i>";
		achart.onclick = () => this.type = 'achart';

		var pchart = commands.appendChild(document.createElement('a'));
		pchart.href = "#";
		pchart.dialog = this;
		pchart.title = "Pizza";
		pchart.innerHTML = "Pizza<i>&#x2031;</i>";
		pchart.onclick = () => this.type = 'pchart';

		var dchart = commands.appendChild(document.createElement('a'));
		dchart.href = "#";
		dchart.dialog = this;
		dchart.title = "Donut";
		dchart.innerHTML = "Donut<i>&#x2245;</i>";
		dchart.onclick = () => this.type = 'dchart';

		var rchart = commands.appendChild(document.createElement('a'));
		rchart.href = "#";
		rchart.dialog = this;
		rchart.title = "Rose";
		rchart.innerHTML = "Rose<i>&#x2247;</i>";
		rchart.onclick = () => this.type = 'rchart';

		this.commands = commands;

		this.head.focus();
	}

	set type(type)
	{
		this.body.children[0].type = type;
		switch (type)
		{
			case 'cchart':
				this.caption = 'Coluna';
				break;
			case 'bchart':
				this.caption = 'Barra';
				break;
			case 'pchart':
				this.caption = 'Pizza';
				break;
			case 'dchart':
				this.caption = 'Donnut';
				break;
			case 'achart':
				this.caption = 'Área';
				break;
			case 'rchart':
				this.caption = 'Rose';
				break;
			case 'lchart':
				this.caption = 'Linha';
				break;
		}
	}

	set title(title)
	{
		this.body.children[0].title = title;
	}

	set data(data)
	{
		this.body.children[0].data = data;
	}
}

GChartDialog.show = function (chart, series, title)
{
	let dialog = new GChartDialog();
	dialog.show();

	dialog.type = chart;
	dialog.title = title;
	dialog.data = series;
};

customElements.define('g-chart-dialog', GChartDialog);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('tr.RChart, td.RChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("rchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.DChart, td.DChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("dchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.PChart, td.PChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("pchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.AChart, td.AChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("achart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});


	Array.from(document.querySelectorAll('tr.LChart, td.LChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("lchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.BChart, td.BChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("bchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('tr.CChart, td.CChart')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show("cchart",
				this.getAttribute('data-series') || new URL(this.getAttribute('data-action')).get(),
				this.getAttribute("title"));
		};
	});

	Array.from(document.querySelectorAll('a[data-chart]')).forEach(function (e)
	{
		e.onclick = function ()
		{
			GChartDialog.show(this.getAttribute('data-chart'),
				this.getAttribute('data-series') || new URL(this.getAttribute('href')).get(),
				this.getAttribute("title"));
			return false;
		};
	});
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
/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class GDialog extends GWindow
{
	constructor()
	{
		super();
		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-dialog");

		let minimize = document.createElement("a");
		minimize.href = "#";
		this.head.appendChild(minimize);
		minimize.innerHTML = "<i>&#x3019;<i/>";
		minimize.onclick = () => this.minimize();

		let fullScreen = document.createElement("a");
		fullScreen.href = "#";
		this.head.appendChild(fullScreen);
		fullScreen.innerHTML = (FullScreen.status() ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");
		fullScreen.onclick = element => element.innerHTML = (FullScreen.switch(this.main) ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");

		let close = document.createElement("a");
		close.href = "#";
		this.head.appendChild(close);
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011;<i/>";

		this.head.addEventListener("keydown", event =>
		{
			event = event ? event : window.event;
			switch (event.keyCode)
			{
				case ESC:
					if (!this.blocked())
						this.hide();
					break;
				case ENTER:
					this.focus();
					break;
			}

			event.preventDefault();
			event.stopPropagation();
		});


		this._private.iframe = this.body.appendChild(window.top.document.createElement('iframe'));

		this.iframe.dialog = this;
		this.iframe.scrolling = "no";
		this.iframe.setAttribute('name', '_dialog');
		this.iframe.onmouseenter = () => this.iframe.focus();

		this.iframe.addEventListener("load", () =>
		{
			this.iframe.name = "_frame";
			this.iframe.setAttribute("name", "_frame");
			this.iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));

			var resize = () =>
			{
				if (!this.iframe.contentWindow
					|| !this.iframe.contentWindow.document
					|| !this.iframe.contentWindow.document.body
					|| !this.iframe.contentWindow.document.body.scrollHeight)
					return false;

				let height = Math.max(this.iframe.contentWindow.document.body.scrollHeight,
					this.body.offsetHeight) + "px";
				if (this.iframe.height !== height)
				{
					this.iframe.height = "0";
					this.iframe.height = height;
				}
				return true;
			};

			resize();
			window.addEventListener("refresh_size", resize);
			this.iframe.backgroundImage = "none";
		});

	}

	get iframe()
	{
		return this._private.iframe;
	}

	set navigator(navigator)
	{
		if (navigator && navigator.length > 1)
		{
			let navbar = new GNavBar(navigator, navigator.target);
			navbar.addEventListener("update", event => this._private.iframe.setAttribute('src', event.detail.target));
			this.foot.appendChild(navbar);
		}

	}

	set target(target)
	{
		this.iframe.setAttribute('src', target);
	}

	get()
	{
		this.arguments = arguments;
		this.show();
	}

	ret()
	{
		let size = Math.min(arguments.length, this.arguments.length);
		for (var i = 0; i < size; i++)
			if (this.arguments[i])
				if (this.arguments[i].tagName.toLowerCase() === "textarea")
					this.arguments[i].innerHTML = arguments[i];
				else
					this.arguments[i].value = arguments[i];

		for (var i = 0; i < size; i++)
			if (this.arguments[i])
				this.arguments[i].dispatchEvent(new Event('change', {bubbles: true}));

		this.hide();
	}

	static hide()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
	}

	static set caption(caption)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.caption = caption;
	}

	static set commands(commands)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.commands = commands;
	}

	static get caption()
	{
		if (window.frameElement && window.frameElement.dialog)
			return window.frameElement.dialog.caption;
	}

	static get commands()
	{
		if (window.frameElement && window.frameElement.dialog)
			return window.frameElement.dialog.commands;
	}

	static create()
	{
		return 	document === window.top.document ? new GDialog()
			: window.top.document.importNode(new GDialog());
	}
}

customElements.define('g-dialog', GDialog);

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('a[data-get]')).forEach(function (element)
	{
		element.addEventListener("click", function (event)
		{
			event.preventDefault();
			event.stopPropagation();
			var parameters =
				CSV.parse(this.getAttribute('data-get'))
				.map(e => e.trim())
				.map(e => e !== null ? document.getElementById(e) : null);
			if (parameters.some(e => e && e.value))
			{
				parameters = parameters.filter(e => e && e.value);
				parameters.forEach(e => e.value = "");
				parameters.forEach(e => e.dispatchEvent(new Event('change', {bubbles: true})));
			} else {
				let dialog = GDialog.create();
				dialog.target = this.href;
				dialog.caption = this.getAttribute("title");
				dialog.get.apply(dialog, parameters);
			}
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

				let dialog = GDialog.create();
				dialog.target = url;
				dialog.caption = getter.getAttribute("title");
				dialog.get.apply(dialog, parameters);

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
/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class GStackFrame extends GModal
{
	constructor()
	{
		super();

		this._private.iframe = this.appendChild(window.top.document.createElement('iframe'));

		this.iframe.dialog = this;
		this.iframe.scrolling = "no";
		this.iframe.setAttribute('name', '_dialog');
		this.iframe.onmouseenter = () => this.iframe.focus();

		this.iframe.addEventListener("load", () =>
		{
			this.iframe.name = "_frame";
			this.iframe.setAttribute("name", "_frame");
			this.iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));
			this.iframe.backgroundImage = "none";
		});

	}

	get iframe()
	{
		return this._private.iframe;
	}

	set target(target)
	{
		this.iframe.setAttribute('src', target);
	}

	static hide()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
	}

	static create()
	{
		return 	document === window.top.document ? new GStackFrame()
			: window.top.document.importNode(new GStackFrame());
	}
}

customElements.define('g-stack-frame', GStackFrame);
/* global customElements */

class GPopup extends GWindow
{
	constructor(element)
	{
		super();
		this.head.focus();
		this.head.tabindex = 1;
		this.classList.add("g-popup");

		this.caption = "Erro de sistema";

		let close = document.createElement("a");
		close.href = "#";
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011;<i/>";
		this.head.appendChild(close);

		this.body.appendChild(element);
	}
}

window.addEventListener("load",
	() => Array.from(document.querySelectorAll('template[data-popup]'))
		.forEach(element => new GPopup(element.content.cloneNode(true)).show()));


customElements.define('g-popup', GPopup);
/* global Colorizer */

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

						Colorizer.colorize(table);
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
		Colorizer.colorize(table);
	};

	Colorizer.colorize(table);
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
		var e;
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

class FullScreen
{
	static status()
	{
		return false
			|| document.fullscreenElement
			|| document.mozFullScreenElement
			|| document.webkitFullscreenElement;
	}

	static switch (element)
	{
		return FullScreen.status()
			? FullScreen.exit()
			: FullScreen.enter(element);
	}

	static enter(element)
	{
		if (element.requestFullscreen)
			element.requestFullscreen();
		else if (element.mozRequestFullScreen)
			element.mozRequestFullScreen();
		else if (element.webkitRequestFullScreen)
			element.webkitRequestFullScreen();
		return true;
	}

	static exit()
	{
		if (document.exitFullscreen)
			document.exitFullscreen();
		else if (document.webkitExitFullscreen)
			document.webkitExitFullscreen();
		else if (document.mozCancelFullScreen)
			document.mozCancelFullScreen();
		return false;
	}
}
/* global ENTER, ESC, Message, GDialog */

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
				let dialog = GDialog.create();
				dialog.caption = this.getAttribute("title");
				dialog.blocked = Boolean(this.getAttribute("data-blocked"));
				dialog.addEventListener("show", event => this.dispatchEvent(event));
				dialog.addEventListener("hide", event => this.dispatchEvent(event));
				dialog.show();
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



function autofocus(d)
{
	var elements = Array.from(d.querySelectorAll('[autofocus]'));
	if (elements.length !== 0)
		return elements[0].focus();

	var elements = Array.from(d.querySelectorAll('[tabindex]')).filter(e => Number(e.getAttribute("tabindex")) > 0);
	if (elements.length === 0)
		return -1;

	var element = elements.reverse().reduce((a, b) => Number(a.getAttribute("tabindex")) < Number(b.getAttribute("tabindex")) ? a : b);

	if (element)
	{
		element.focus();
		if (element.onfocus)
			element.onfocus();
	}
}

window.addEventListener("load", () => autofocus(document));
/* global Colorizer, Objects */

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
				var s1 = e1.innerHTML.trim();
				if (e1.hasAttribute("data-value"))
					s1 = e1.getAttribute("data-value").trim() ?
						Number(e1.getAttribute("data-value")) : null;

				e2 = e2.children[index];
				var s2 = e2.innerHTML.trim();
				if (e2.hasAttribute("data-value"))
					s2 = e2.getAttribute("data-value").trim() ?
						Number(e2.getAttribute("data-value")) : null;

				if (mode === "U")
					return Objects.compare(s1, s2);
				else if (mode === "D")
					return Objects.compare(s2, s1);
			});
			for (var i = 0; i < children.length; i++)
				this.appendChild(children[i]);
		};
	});

	Array.from(document.querySelectorAll("th[data-sortable]")).forEach(function (link)
	{
		link.setAttribute("data-sortable", "N");
		link.addEventListener("click", function ()
		{
			switch (this.getAttribute("data-sortable"))
			{
				case "N":
					Array.from(this.parentNode.children)
						.filter(e => e.hasAttribute("data-sortable"))
						.forEach(e => e.setAttribute("data-sortable", "N"));
					this.setAttribute("data-sortable", "U");
					break;
				case "U":
					this.setAttribute("data-sortable", "D");
					break;
				case "D":
					this.setAttribute("data-sortable", "U");
					break;
			}

			var table = this.closest("TABLE");
			Array.from(table.children)
				.filter(e => e.tagName.toUpperCase() === "TBODY")
				.forEach(e => e.sort(Array.prototype.indexOf
						.call(this.parentNode.children, this),
						this.getAttribute("data-sortable")));
			Colorizer.colorize(table);
		});
	});
});


/* global customElements */

class Message extends GWindow
{
	constructor(options)
	{
		super();
		this.classList.add("g-message");
		this.classList.add(options.type);
		this.caption = options.title || "";

		let close = document.createElement("a");
		close.href = "#";
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);

		this.body.appendChild(window.top.document.createElement('label')).innerHTML = options.message;

		if (options.timeout)
			window.top.setTimeout(() => this.hide(), options.timeout);

		this.show();
	}
}

customElements.define('g-message', Message);

Message.success = function (message, timeout)
{
	new Message({type: "SUCCESS", title: "Sucesso", message: message, timeout: timeout});
};

Message.warning = function (message, timeout)
{
	new Message({type: "WARNING", title: "Alerta", message: message, timeout: timeout});
};

Message.error = function (message, timeout)
{
	new Message({type: "ERROR", title: "Erro", message: message, timeout: timeout});
};

Message.info = function (message, timeout)
{
	new Message({type: "INFO", title: "Informação", message: message, timeout: timeout});
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
		case "INFO":
			Message.info(status.message, timeout);
			break;
	}

};

/* global Message, customElements */

class GProgressStatus extends HTMLElement
{
	constructor(process)
	{
		super();
		if (process)
			this.setAttribute("process", process);
	}

	connectedCallback()
	{
		var colors = new Object();
		colors["PENDING"] = '#000000';
		colors["COMMITED"] = '#006600';
		colors["CANCELED"] = '#660000';
		colors["CLOSED"] = '#666666';

		var title = this.appendChild(document.createElement("label"));
		title.style.fontSize = "20px";
		title.style.flexBasis = "40px";
		title.innerHTML = "Conectando ao servidor";

		var progress = this.appendChild(document.createElement("progress"));
		progress.style.width = "100%";
		progress.style.flexBasis = "40px";

		var div = this.appendChild(document.createElement("div"));
		div.style.flexBasis = "40px";
		div.style.display = "flex";
		div.style.alignItems = "center";

		var clock = div.appendChild(document.createElement("digital-clock"));
		clock.style.flexGrow = "1";
		clock.style.fontSize = "12px";
		clock.style.display = "flex";
		clock.style.alignItems = "center";
		clock.style.justifyContent = "flex-start";
		clock.innerHTML = "00:00:00";

		var counter = div.appendChild(document.createElement("label"));
		counter.style.flexGrow = "1";
		counter.style.fontSize = "12px";
		counter.style.display = "flex";
		counter.style.alignItems = "center";
		counter.style.justifyContent = "flex-end";
		counter.innerHTML = "...";

		var div = this.appendChild(document.createElement("div"));
		div.style.padding = "4px";
		div.style.display = "flex";
		div.style.overflow = "auto";
		div.style.flexBasis = "120px";
		div.style.borderRadius = "5px";
		div.style.alignItems = "stretch";
		div.style.justifyContent = "center";
		div.style.backgroundColor = "white";

		var logger = div.appendChild(document.createElement("ul"));
		logger.style.flexGrow = "1";
		logger.style.margin = "0";
		logger.style.padding = "0";
		logger.style.listStyleType = "none";

		function log(message)
		{
			var log = logger.firstElementChild ?
				logger.insertBefore(document.createElement("li"),
					logger.firstElementChild)
				: logger.appendChild(document.createElement("li"));
			log.innerHTML = message;
			log.style.height = "16px";
			log.style.display = "flex";
			log.style.alignItems = "center";
		}

		var ws = new WebSocket("ws://" + window.location.host + "/" +
			window.location.pathname.split("/")[1] + "/Progress/" + this.getAttribute('process'));

		ws.onerror = e => log(e.data);

		ws.onclose = e =>
		{
			if (e.code !== 1000)
			{
				title.style.color = colors["CLOSED"];
				clock.style.color = colors["CLOSED"];
				counter.style.color = colors["CLOSED"];
				log(e.reason ? e.reason : "Conexão perdida com o servidor");
			}
		};

		ws.onmessage = (event) =>
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

					if (event.text !== title.innerHTML)
					{
						log(event.text);
						title.innerHTML = event.text;
					}

					counter.innerHTML = event.toString();

					title.style.color = colors[event.status];
					clock.style.color = colors[event.status];
					counter.style.color = colors[event.status];

					switch (event.status)
					{
						case "COMMITED":
							if (!progress.max)
								progress.max = 100;
							if (!progress.value)
								progress.value = 100;

							this.dispatchEvent(new CustomEvent('commited'));

							clock.setAttribute("paused", "paused");
							break;
						case "CANCELED":
							if (!progress.max)
								progress.max = 100;
							if (!progress.value)
								progress.value = 0;

							this.dispatchEvent(new CustomEvent('canceled'));

							clock.setAttribute("paused", "paused");
							break;
					}

					if (event.todo !== -1)
					{
						progress.max = event.todo;
						if (event.done !== -1)
							progress.value = event.done;
					}

					break;

				case "Redirect":
					this.dispatchEvent(new CustomEvent('redirected', {detail: event.url}));
					break;
			}
		};
	}
}

customElements.define('g-progress-status', GProgressStatus);



/* global customElements */

class GProgressDialog extends Picker
{
	constructor(process, options)
	{
		super(options);

		var status = "Pending";
		this.classList.add("g-progress-dialog");

		this.caption = options && options.title ? options.title : "Progresso";

		var progress = this.body.appendChild(document.createElement("g-progress-status"));
		progress.setAttribute("process", process);

		this.commit.innerText = "Processando";
		this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--b')

		this.addEventListener("hide", function (event)
		{
			if (status === "Pending"
				&& !confirm("Tem certeza de que deseja fechar o progresso?"))
				event.preventDefault();
		});

		this.commit.onclick = () => this.hide();

		progress.addEventListener("commited", () =>
		{
			status = "Commited";
			this.commit.innerHTML = "OK";
			this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--g');
		});

		progress.addEventListener("canceled", () =>
		{
			status = "Canceled";
			this.commit.innerHTML = "OK";
			this.commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--r');
		});

		progress.addEventListener("redirected", url =>
		{
			this.addEventListener("hide", event =>
			{
				this.hide();
				event.preventDefault();
				window.location.href = url.detail;
			});
		});
	}
}

customElements.define('g-progress-dialog', GProgressDialog);
/* global customElements */

class GProgressWindow extends HTMLElement
{
	constructor(process)
	{
		super();
		Array.from(document.body.children)
			.forEach(e => e.style.display = "none");
		if (window.frameElement)
			window.frameElement.height = "0";
		if (process)
			this.setAttribute("process", process);
	}

	connectedCallback()
	{
		var body = this.appendChild(document.createElement("div"));

		var progress = body.appendChild(new GProgressStatus(this.getAttribute("process")));

		var coolbar = body.appendChild(document.createElement("div"));
		coolbar.className = "Coolbar";

		var action = coolbar.appendChild(document.createElement("a"));
		action.appendChild(document.createTextNode("Processando"));
		action.innerHTML = "Processando<i>&#X2017;</i>";
		action.href = "#";

		action.onclick = () =>
		{
			if (confirm("Tem certeza de que deseja fechar o progresso?"))
			{
				Array.from(document.body.children)
					.forEach(e => e.style.display = "");
				document.body.removeChild(this);
			}
		};

		progress.addEventListener("commited", () =>
		{
			action.innerHTML = "Ok<i>&#X1000;</i>";
			action.style.color = "#006600";
		});

		progress.addEventListener("canceled", () =>
		{
			action.innerHTML = "OK";
			action.style.color = "#660000";
		});

		progress.addEventListener("redirected", url =>
			action.onclick = () => window.location.href = url.detail);
	}
}

window.addEventListener("load", () =>
	customElements.define('g-progress-window', GProgressWindow));
/* global Message, DataFormat, customElements */

class GDownloadStatus extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		var title = this.appendChild(document.createElement("label"));
		title.style.fontSize = "20px";
		title.style.flexBasis = "40px";

		var progress = this.appendChild(document.createElement("progress"));
		progress.style.width = "100%";
		progress.style.flexBasis = "40px";

		var div = this.appendChild(document.createElement("div"));
		div.style.flexBasis = "40px";
		div.style.display = "flex";
		div.style.alignItems = "center";

		var clock = div.appendChild(document.createElement("digital-clock"));
		clock.style.flexGrow = "1";
		clock.style.fontSize = "12px";
		clock.style.display = "flex";
		clock.style.alignItems = "center";
		clock.style.justifyContent = "flex-start";

		var counter = div.appendChild(document.createElement("label"));
		counter.style.flexGrow = "1";
		counter.style.fontSize = "12px";
		counter.style.display = "flex";
		counter.style.alignItems = "center";
		counter.style.justifyContent = "flex-end";
		counter.innerHTML = "...";

		this.abort = function ()
		{
			if (this.request)
				this.request.abort();
		};

		this.get = function (url)
		{
			this.download("GET", url, null);
		};

		this.post = function (url, data)
		{
			this.download("GET", url, data);
		};

		this.download = function (method, url, data)
		{
			var title = this.children[0];
			var progress = this.children[1];
			var clock = this.children[2].children[0];
			var counter = this.children[2].children[1];
			this.request = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");

			this.request.addEventListener("load", () =>
			{
				clock.setAttribute("paused", "paused");

				if (this.request.status === 200)
				{
					if (!progress.max && !progress.value)
						progress.max = progress.value = 100;
					title.style.color = "#006600";
					title.innerHTML = "Download efetuado com sucesso";

					var disposition = this.request.getResponseHeader('content-disposition');
					var matches = /"([^"]*)"/.exec(disposition);
					var filename = (matches !== null && matches[1] ? matches[1] : 'file');
					var blob = new Blob([this.request.response], {type: 'application/octet-stream'});
					var link = document.createElement('a');
					link.href = window.URL.createObjectURL(blob);
					link.download = filename;
					document.body.appendChild(link);
					link.click();
					document.body.removeChild(link);
					setTimeout(() => window.URL.revokeObjectURL(link.href), 60 * 1000);

					this.dispatchEvent(new CustomEvent('done', {cancelable: false}));
				} else
				{
					var reader = new FileReader();
					reader.addEventListener("loadend", function ()
					{
						title.style.color = "#660000";
						title.innerHTML = reader.result;
						this.dispatchEvent(new CustomEvent('error', {cancelable: false, 'detail': reader.result}));
					});
					reader.readAsText(new Blob([this.request.response], {type: 'application/octet-stream'}));
				}
			});

			this.request.addEventListener("loadend", () => this.request = null);

			this.request.addEventListener("progress", event =>
			{
				title.innerHTML = "Efetuando download";
				if (event.loaded)
				{
					progress.value = event.loaded;
					counter.innerHTML = DataFormat.format(event.loaded);

					if (event.total)
					{
						progress.max = event.total;
						counter.innerHTML = counter.innerHTML + " de " + DataFormat.format(event.total);
					}
				}
			});

			this.request.addEventListener("error", () =>
			{
				clock.setAttribute("paused", "paused");
				title.style.color = "#660000";
				title.innerHTML = "Erro ao efetuar download";
				this.dispatchEvent(new CustomEvent('error', {cancelable: false}));
			});

			title.innerHTML = "Conectando ao servidor";
			this.request.responseType = 'blob';
			this.request.open(method, resolve(url), true);
			this.request.send(data);

			return this;
		};
	}
}

customElements.define('g-download-status', GDownloadStatus);
/* global customElements */

class GReportDialog extends GWindow
{
	constructor()
	{
		super();
		this.classList.add("g-report-dialog");
		this._private.downloadStatus = new GDownloadStatus();
		this._private.selector = this.body.appendChild(new ReportSelector());

		addEventListener("hide", () => this._private.downloadStatus.abort());

		let close = document.createElement("a");
		close.href = "#";
		close.addEventListener("click", () => this.hide());
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);
	}

	get(url)
	{
		this._private.selector.addEventListener("selected", event =>
		{
			this.body.removeChild(this._private.selector);
			this.body.appendChild(this._private.downloadStatus);
			url = new URL(url).setParameter("type", event.detail).toString();
			this._private.downloadStatus.download("get", url, null);
			this._private.downloadStatus.addEventListener("done", () => this.hide());
		});
		this.show();
	}

	post(url, data)
	{
		this._private.selector.addEventListener("selected", event =>
		{
			this.body.removeChild(this._private.selector);
			this.body.appendChild(this._private.downloadStatus);
			url = new URL(url).setParameter("type", event.detail).toString();
			this._private.downloadStatus.download("post", url, data);
			this._private.downloadStatus.addEventListener("done", () => this.hide());
		});
		this.show();
	}
}



customElements.define('g-report-dialog', GReportDialog);
/* global Colorizer */

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("input[data-filter]")).forEach(element =>
	{
		var table = element.getAttribute("data-filter") ?
			document.getElementById(element.getAttribute("data-filter"))
			: element.closest("TABLE");

		element.addEventListener("input", function ()
		{
			Array.from(table.children)
				.filter(e => e.tagName.toUpperCase() === "TBODY")
				.flatMap(e => Array.from(e.children)).forEach(row =>
				row.style.display = !this.value || row.innerHTML.toUpperCase()
					.indexOf(this.value.toUpperCase()) !== -1 ? "" : "none");
			Colorizer.colorize(table);
		});

		Array.from(table.children)
			.filter(e => e.tagName.toUpperCase() === "TBODY")
			.flatMap(e => Array.from(e.children)).forEach(row =>
			row.style.display = !this.value || row.innerHTML.toUpperCase()
				.indexOf(this.value.toUpperCase()) !== -1 ? "" : "none");
		Colorizer.colorize(table);
	});
});
/* global customElements */

customElements.define('g-coolbar', class extends GOverflow
{
});
window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("*[data-autoclick]")).forEach(a => a.click());
});
class DefinitionList
{
	static of(entries)
	{
		var dd = document.createElement("dd");
		dd.style.display = "grid";
		dd.style.gridTemplateColumns = "auto auto";
		dd.style.gridGap = "8px";


		for (var key in entries)
		{
			dd.appendChild(document.createElement("dt")).innerHTML = key;
			dd.appendChild(document.createElement("dd")).innerHTML = entries[key];


		}
		return dd;
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("dl[data-entries]")).forEach(function (e)
	{
		entries = JSON.parse(e.getAttribute("data-entries"));

		for (var key in entries)
		{
			e.appendChild(document.createElement("dt")).innerHTML = key;
			e.appendChild(document.createElement("dd")).innerHTML = entries[key];
		}
	});
});
class AppEvents
{
	static listen()
	{
		var url = "wss://" + /.*:\/\/(.*\/.*)\//.exec(window.location.href)[1] + "/AppEvents";

		if (!AppEvents.connection || AppEvents.connection.readyState === 3)
		{
			AppEvents.connection = new WebSocket(url);

			AppEvents.connection.onmessage = function (event)
			{
				event = JSON.parse(event.data);
				window.dispatchEvent(new CustomEvent(event.type, {detail: event.detail}));
			};

			AppEvents.connection.onopen = function ()
			{
			};

			AppEvents.connection.onclose = function ()
			{
				AppEvents.connection = new WebSocket(url);
			};
		}
	}
}
/* global customElements, DefinitionList, Table, HTMLElement */

class GTooltip extends HTMLElement
{
	constructor(element, orientation, content)
	{
		super();
		this._private = {};
		this._private.element = element;
		this._orientation = orientation;

		switch (typeof content)
		{
			case "string":
				var label = document.createElement("label");
				label.innerHTML = content;
				this._content = label;
				break;
			case "object":
				if (content instanceof HTMLElement)
					this._content = content;
				else
					this._content = DefinitionList.of(content);
				break;
		}
	}

	connectedCallback()
	{
		this.appendChild(this._content);
		let tooltip = this.getBoundingClientRect();
		let element = this._private.element.getBoundingClientRect();
		element.center = {x: element.left + (element.width / 2), y: element.top + (element.height / 2)};

		switch (this._orientation || "vertical")
		{

			case "vertical":
				if (element.center.y >= (window.innerHeight / 2))
					this.show(element.center.x - tooltip.width / 2, element.top - tooltip.height - 10, "top");
				else
					this.show(element.center.x - tooltip.width / 2, element.bottom + 10, "bottom");
				break;
			case "horizontal":
				if (element.center.x >= (window.innerWidth / 2))
					this.show(element.left - tooltip.width - 10, element.center.y - (tooltip.height / 2), "left");
				else
					this.show(element.x + element.width + 10, element.center.y - tooltip.height / 2, "right");
				break;
		}
	}

	show(x, y, arrow)
	{
		this.style.top = y + "px";
		this.style.left = x + "px";
		this.setAttribute("arrow", arrow);
		this.style.visibility = "visible";
	}

	static show(element, orientation, content)
	{
		if (!this._private)
			this._private = {};

		GTooltip.hide();
		this._private.instance = document.body
			.appendChild(new GTooltip(element, orientation, content));
	}

	static hide()
	{
		if (this._private && this._private.instance)
		{
			this._private.instance.parentNode
				.removeChild(this._private.instance);
			this._private.instance = null;
		}
	}
}

customElements.define('g-tooltip', GTooltip);


window.addEventListener("mouseover", e =>
{
	e = e.target;
	e = e.closest("*[data-tooltip]");
	if (e)
	{
		var object = e.getAttribute("data-tooltip");
		if (/ *[{"[].*[}"\]] */.test(object))
			object = JSON.parse(object);
		GTooltip.show(e, e.getAttribute("data-tooltip-orientation"), object);
	} else
		GTooltip.hide();
});
/* global customElements, GOverflow, Proxy */

customElements.define('g-tabbar', class extends GOverflow
{
	constructor()
	{
		super();
		this._private.container.style.flexDirection = "row";
	}
});
/* global customElements, GOverflow, GSelection */

class GScrollTabBar extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});

		var div = this.shadowRoot.appendChild(document.createElement("div"));
		div.style.width = "100%";
		div.style.height = "auto";
		div.style.border = "none";
		div.style.display = "flex";
		div.style.overflowX = "hidden";
		div.style.whiteSpace = "nowrap";

		this.addEventListener("mouseenter", () => div.style.overflowX = "auto");
		this.addEventListener("mouseleave", () => div.style.overflowX = "hidden");
		this.addEventListener("touchstart", () => div.style.overflowX = "auto");
		this.addEventListener("touchend", () => div.style.overflowX = "hidden");
		this.addEventListener("touchmove", e => div.style.overflowX = this.contains(e.target) ? "auto" : "hidden");
		div.appendChild(document.createElement("slot"));

		div.addEventListener("scroll", () => this.setAttribute("data-overflowing",
				GOverflow.determineOverflow(this, this.shadowRoot.firstElementChild)));

		window.addEventListener("load", () =>
		{
			this.setAttribute("data-overflowing",
				GOverflow.determineOverflow(this, this.shadowRoot.firstElementChild));
			Array.from(this.children).filter(e => e.getAttribute("aria-selected"))
				.forEach(e => e.scrollIntoView({inline: "center", block: "nearest"}));
		});
	}
}

customElements.define("g-scroll-tabbar", GScrollTabBar);
/* global customElements, GOverflow, Proxy, Dialog */

customElements.define('g-dialog-caption', class extends HTMLElement
{
	constructor()
	{
		super();
		window.addEventListener("load", () =>
			GDialog.caption = this.innerText);
	}
});

/* global customElements */

class GTabControl extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		let head = this.shadowRoot.appendChild(document.createElement("div"));
		head.style = "display: flex; align-items: center; justify-content: flex-start; flex-wrap: wrap";

		head.appendChild(document.createElement("slot"));

		this.shadowRoot.appendChild(document.createElement("div"))
			.appendChild(document.createElement("slot")).name = "body";
	}

	get type()
	{
		return this.getAttribute("type") || "frame";
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	connectedCallback()
	{
		window.setTimeout(() =>
		{
			let slot = this.shadowRoot.firstChild.firstChild;

			Array.from(slot.assignedElements())
				.filter(e => e.tagName === "A" || e.tagName === "BUTTON")
				.forEach(link =>
				{
					let tab = document.createElement("div");
					tab.setAttribute("slot", "body");
					this.appendChild(tab);
					tab.style = "display: none; padding : 10px; overflow: hidden";
					let type = link.getAttribute("data-type") || this.type;

					link.addEventListener("click", event =>
					{
						event.preventDefault();
						event.stopPropagation();
						Array.from(this.children).filter(e => e.tagName === "DIV").forEach(e => e.style.display = "none");
						Array.from(this.children).filter(e => e.tagName !== "DIV").forEach(e => e.setAttribute("data-selected", "false"));
						tab.style.display = "block";
						link.setAttribute("data-selected", "true");

						if (!tab.childNodes.length)
						{
							switch (type)
							{
								case "frame":

									let iframe = tab.appendChild(document.createElement("iframe"));
									iframe.scrolling = "no";
									iframe.setAttribute("allowfullscreen", "true");
									iframe.style = "margin: 0; width : 100%; border: none; overflow: hiddden";

									iframe.onload = () =>
									{
										if (iframe.contentWindow
											&& iframe.contentWindow.document
											&& iframe.contentWindow.document.body
											&& iframe.contentWindow.document.body.scrollHeight)
										{
											var height = iframe.contentWindow
												.document.body.scrollHeight + "px";
											if (iframe.height !== height)
											{
												iframe.height = "0";
												iframe.height = height;
											}
										}

										iframe.style.backgroundImage = "none";
									};

									iframe.src = link.href || link.getAttribute("formaction");
									break;
								case "fetch":
									if (!link.form)
										new URL(link.getAttribute('href'))
											.get(text => tab.innerHTML = text);
									else if (link.form.checkValidity())
										new URL(link.getAttribute('href'))
											.post(new FormData(link.form),
												text => tab.innerHTML = text);
									break;
							}

						} else
							event.preventDefault();
					});

					if (link.getAttribute("data-selected") &&
						link.getAttribute("data-selected").toLowerCase() === "true")
						link.click();
				});


			Array.from(slot.assignedElements())
				.filter(e => e.tagName === "DIV")
				.forEach(tab =>
				{
					let link = this.appendChild(document.createElement("a"));
					link.innerText = tab.getAttribute("data-name");
					if (tab.hasAttribute("data-icon"))
						link.appendChild(document.createElement("i")).innerHTML = "&#X" + tab.getAttribute("data-icon") + ";";
					tab.setAttribute("slot", "body");
					tab.style = "display: none; padding : 10px; overflow: hidden";
					let type = link.getAttribute("data-type") || this.type;

					link.addEventListener("click", event =>
					{
						event.stopPropagation();
						Array.from(this.children).filter(e => e.tagName === "DIV").forEach(e => e.style.display = "none");
						Array.from(this.children).filter(e => e.tagName !== "DIV").forEach(e => e.setAttribute("data-selected", "false"));
						tab.style.display = "block";
						link.setAttribute("data-selected", "true");
					});

					if (link.getAttribute("data-selected") &&
						link.getAttribute("data-selected").toLowerCase() === "true")
						link.click();
				});

			if (slot.assignedElements().length
				&& Array.from(slot.assignedElements())
				.every(e => !e.hasAttribute("data-selected") ||
						e.getAttribute("data-selected")
						.toLowerCase() === "false"))
				slot.assignedElements()[0].click();
		}, 0);
	}
}

customElements.define('g-tab-control', GTabControl);


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
			iframe.scrolling = "no";
			iframe.setAttribute("allowfullscreen", "true");

			var resize = function ()
			{
				if (iframe.contentWindow
					&& iframe.contentWindow.document
					&& iframe.contentWindow.document.body
					&& iframe.contentWindow.document.body.scrollHeight)
				{
					var height = iframe.contentWindow.document.body.scrollHeight + "px";
					if (iframe.height !== height)
					{
						iframe.height = "0";
						iframe.height = height;
					}
				}
			};

			iframe.onload = function ()
			{
				resize();
				window.addEventListener("refresh_size", resize);
				iframe.backgroundImage = "none";
			};

			iframe.refresh = function ()
			{
				var divs = Array.from(this.parentNode.parentNode.children).filter(e => e.tagName.toLowerCase() === "div");
				for (i = 0; i < divs.length; i++)
					if (divs[i].childNodes[0] !== this)
						if (divs[i] !== this.parenNode)
							divs[i].innerHTML = '';
			};

			iframe.setAttribute("src", link.getAttribute('href'));
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('div.PageControl'))
		.forEach(element => new PageControl(element));
});

