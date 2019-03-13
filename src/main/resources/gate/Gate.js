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
/**
@license @nocompile
Copyright (c) 2018 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
(function(){/*

 Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
 This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
 The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
 The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
 Code distributed by Google as part of the polymer project is also
 subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
'use strict';var r,t="undefined"!=typeof window&&window===this?this:"undefined"!=typeof global&&null!=global?global:this,ba="function"==typeof Object.defineProperties?Object.defineProperty:function(a,b,c){a!=Array.prototype&&a!=Object.prototype&&(a[b]=c.value)};function ca(){ca=function(){};t.Symbol||(t.Symbol=da)}var da=function(){var a=0;return function(b){return"jscomp_symbol_"+(b||"")+a++}}();
function ea(){ca();var a=t.Symbol.iterator;a||(a=t.Symbol.iterator=t.Symbol("iterator"));"function"!=typeof Array.prototype[a]&&ba(Array.prototype,a,{configurable:!0,writable:!0,value:function(){return fa(this)}});ea=function(){}}function fa(a){var b=0;return ha(function(){return b<a.length?{done:!1,value:a[b++]}:{done:!0}})}function ha(a){ea();a={next:a};a[t.Symbol.iterator]=function(){return this};return a}function ia(a){ea();var b=a[Symbol.iterator];return b?b.call(a):fa(a)}
function ja(a){for(var b,c=[];!(b=a.next()).done;)c.push(b.value);return c}var ka;if("function"==typeof Object.setPrototypeOf)ka=Object.setPrototypeOf;else{var la;a:{var ma={Ga:!0},na={};try{na.__proto__=ma;la=na.Ga;break a}catch(a){}la=!1}ka=la?function(a,b){a.__proto__=b;if(a.__proto__!==b)throw new TypeError(a+" is not extensible");return a}:null}var oa=ka;function pa(){this.f=!1;this.b=null;this.ca=void 0;this.a=1;this.F=0;this.c=null}
function qa(a){if(a.f)throw new TypeError("Generator is already running");a.f=!0}pa.prototype.u=function(a){this.ca=a};function ra(a,b){a.c={Ja:b,Na:!0};a.a=a.F}pa.prototype.return=function(a){this.c={return:a};this.a=this.F};function sa(a,b){a.a=3;return{value:b}}function ta(a){this.a=new pa;this.b=a}function ua(a,b){qa(a.a);var c=a.a.b;if(c)return va(a,"return"in c?c["return"]:function(a){return{value:a,done:!0}},b,a.a.return);a.a.return(b);return wa(a)}
function va(a,b,c,d){try{var e=b.call(a.a.b,c);if(!(e instanceof Object))throw new TypeError("Iterator result "+e+" is not an object");if(!e.done)return a.a.f=!1,e;var f=e.value}catch(g){return a.a.b=null,ra(a.a,g),wa(a)}a.a.b=null;d.call(a.a,f);return wa(a)}function wa(a){for(;a.a.a;)try{var b=a.b(a.a);if(b)return a.a.f=!1,{value:b.value,done:!1}}catch(c){a.a.ca=void 0,ra(a.a,c)}a.a.f=!1;if(a.a.c){b=a.a.c;a.a.c=null;if(b.Na)throw b.Ja;return{value:b.return,done:!0}}return{value:void 0,done:!0}}
function xa(a){this.next=function(b){qa(a.a);a.a.b?b=va(a,a.a.b.next,b,a.a.u):(a.a.u(b),b=wa(a));return b};this.throw=function(b){qa(a.a);a.a.b?b=va(a,a.a.b["throw"],b,a.a.u):(ra(a.a,b),b=wa(a));return b};this.return=function(b){return ua(a,b)};ea();this[Symbol.iterator]=function(){return this}}function Aa(a,b){b=new xa(new ta(b));oa&&oa(b,a.prototype);return b}
(function(){if(!function(){var a=document.createEvent("Event");a.initEvent("foo",!0,!0);a.preventDefault();return a.defaultPrevented}()){var a=Event.prototype.preventDefault;Event.prototype.preventDefault=function(){this.cancelable&&(a.call(this),Object.defineProperty(this,"defaultPrevented",{get:function(){return!0},configurable:!0}))}}var b=/Trident/.test(navigator.userAgent);if(!window.CustomEvent||b&&"function"!==typeof window.CustomEvent)window.CustomEvent=function(a,b){b=b||{};var c=document.createEvent("CustomEvent");
c.initCustomEvent(a,!!b.bubbles,!!b.cancelable,b.detail);return c},window.CustomEvent.prototype=window.Event.prototype;if(!window.Event||b&&"function"!==typeof window.Event){var c=window.Event;window.Event=function(a,b){b=b||{};var c=document.createEvent("Event");c.initEvent(a,!!b.bubbles,!!b.cancelable);return c};if(c)for(var d in c)window.Event[d]=c[d];window.Event.prototype=c.prototype}if(!window.MouseEvent||b&&"function"!==typeof window.MouseEvent){b=window.MouseEvent;window.MouseEvent=function(a,
b){b=b||{};var c=document.createEvent("MouseEvent");c.initMouseEvent(a,!!b.bubbles,!!b.cancelable,b.view||window,b.detail,b.screenX,b.screenY,b.clientX,b.clientY,b.ctrlKey,b.altKey,b.shiftKey,b.metaKey,b.button,b.relatedTarget);return c};if(b)for(d in b)window.MouseEvent[d]=b[d];window.MouseEvent.prototype=b.prototype}Array.from||(Array.from=function(a){return[].slice.call(a)});Object.assign||(Object.assign=function(a,b){for(var c=[].slice.call(arguments,1),d=0,e;d<c.length;d++)if(e=c[d])for(var f=
a,n=e,p=Object.getOwnPropertyNames(n),G=0;G<p.length;G++)e=p[G],f[e]=n[e];return a})})(window.WebComponents);(function(){function a(){}function b(a,b){if(!a.childNodes.length)return[];switch(a.nodeType){case Node.DOCUMENT_NODE:return Q.call(a,b);case Node.DOCUMENT_FRAGMENT_NODE:return Ab.call(a,b);default:return x.call(a,b)}}var c="undefined"===typeof HTMLTemplateElement,d=!(document.createDocumentFragment().cloneNode()instanceof DocumentFragment),e=!1;/Trident/.test(navigator.userAgent)&&function(){function a(a,b){if(a instanceof DocumentFragment)for(var d;d=a.firstChild;)c.call(this,d,b);else c.call(this,
a,b);return a}e=!0;var b=Node.prototype.cloneNode;Node.prototype.cloneNode=function(a){a=b.call(this,a);this instanceof DocumentFragment&&(a.__proto__=DocumentFragment.prototype);return a};DocumentFragment.prototype.querySelectorAll=HTMLElement.prototype.querySelectorAll;DocumentFragment.prototype.querySelector=HTMLElement.prototype.querySelector;Object.defineProperties(DocumentFragment.prototype,{nodeType:{get:function(){return Node.DOCUMENT_FRAGMENT_NODE},configurable:!0},localName:{get:function(){},
configurable:!0},nodeName:{get:function(){return"#document-fragment"},configurable:!0}});var c=Node.prototype.insertBefore;Node.prototype.insertBefore=a;var d=Node.prototype.appendChild;Node.prototype.appendChild=function(b){b instanceof DocumentFragment?a.call(this,b,null):d.call(this,b);return b};var f=Node.prototype.removeChild,g=Node.prototype.replaceChild;Node.prototype.replaceChild=function(b,c){b instanceof DocumentFragment?(a.call(this,b,c),f.call(this,c)):g.call(this,b,c);return c};Document.prototype.createDocumentFragment=
function(){var a=this.createElement("df");a.__proto__=DocumentFragment.prototype;return a};var h=Document.prototype.importNode;Document.prototype.importNode=function(a,b){b=h.call(this,a,b||!1);a instanceof DocumentFragment&&(b.__proto__=DocumentFragment.prototype);return b}}();var f=Node.prototype.cloneNode,g=Document.prototype.createElement,h=Document.prototype.importNode,k=Node.prototype.removeChild,m=Node.prototype.appendChild,n=Node.prototype.replaceChild,p=DOMParser.prototype.parseFromString,
G=Object.getOwnPropertyDescriptor(window.HTMLElement.prototype,"innerHTML")||{get:function(){return this.innerHTML},set:function(a){this.innerHTML=a}},u=Object.getOwnPropertyDescriptor(window.Node.prototype,"childNodes")||{get:function(){return this.childNodes}},x=Element.prototype.querySelectorAll,Q=Document.prototype.querySelectorAll,Ab=DocumentFragment.prototype.querySelectorAll,Bb=function(){if(!c){var a=document.createElement("template"),b=document.createElement("template");b.content.appendChild(document.createElement("div"));
a.content.appendChild(b);a=a.cloneNode(!0);return 0===a.content.childNodes.length||0===a.content.firstChild.content.childNodes.length||d}}();if(c){var T=document.implementation.createHTMLDocument("template"),Ka=!0,q=document.createElement("style");q.textContent="template{display:none;}";var ya=document.head;ya.insertBefore(q,ya.firstElementChild);a.prototype=Object.create(HTMLElement.prototype);var Z=!document.createElement("div").hasOwnProperty("innerHTML");a.P=function(b){if(!b.content&&b.namespaceURI===
document.documentElement.namespaceURI){b.content=T.createDocumentFragment();for(var c;c=b.firstChild;)m.call(b.content,c);if(Z)b.__proto__=a.prototype;else if(b.cloneNode=function(b){return a.b(this,b)},Ka)try{l(b),y(b)}catch(jh){Ka=!1}a.a(b.content)}};var X={option:["select"],thead:["table"],col:["colgroup","table"],tr:["tbody","table"],th:["tr","tbody","table"],td:["tr","tbody","table"]},l=function(b){Object.defineProperty(b,"innerHTML",{get:function(){return aa(this)},set:function(b){var c=X[(/<([a-z][^/\0>\x20\t\r\n\f]+)/i.exec(b)||
["",""])[1].toLowerCase()];if(c)for(var d=0;d<c.length;d++)b="<"+c[d]+">"+b+"</"+c[d]+">";T.body.innerHTML=b;for(a.a(T);this.content.firstChild;)k.call(this.content,this.content.firstChild);b=T.body;if(c)for(d=0;d<c.length;d++)b=b.lastChild;for(;b.firstChild;)m.call(this.content,b.firstChild)},configurable:!0})},y=function(a){Object.defineProperty(a,"outerHTML",{get:function(){return"<template>"+this.innerHTML+"</template>"},set:function(a){if(this.parentNode){T.body.innerHTML=a;for(a=this.ownerDocument.createDocumentFragment();T.body.firstChild;)m.call(a,
T.body.firstChild);n.call(this.parentNode,a,this)}else throw Error("Failed to set the 'outerHTML' property on 'Element': This element has no parent node.");},configurable:!0})};l(a.prototype);y(a.prototype);a.a=function(c){c=b(c,"template");for(var d=0,e=c.length,f;d<e&&(f=c[d]);d++)a.P(f)};document.addEventListener("DOMContentLoaded",function(){a.a(document)});Document.prototype.createElement=function(){var b=g.apply(this,arguments);"template"===b.localName&&a.P(b);return b};DOMParser.prototype.parseFromString=
function(){var b=p.apply(this,arguments);a.a(b);return b};Object.defineProperty(HTMLElement.prototype,"innerHTML",{get:function(){return aa(this)},set:function(b){G.set.call(this,b);a.a(this)},configurable:!0,enumerable:!0});var Y=/[&\u00A0"]/g,Cb=/[&\u00A0<>]/g,La=function(a){switch(a){case "&":return"&amp;";case "<":return"&lt;";case ">":return"&gt;";case '"':return"&quot;";case "\u00a0":return"&nbsp;"}};q=function(a){for(var b={},c=0;c<a.length;c++)b[a[c]]=!0;return b};var za=q("area base br col command embed hr img input keygen link meta param source track wbr".split(" ")),
Ma=q("style script xmp iframe noembed noframes plaintext noscript".split(" ")),aa=function(a,b){"template"===a.localName&&(a=a.content);for(var c="",d=b?b(a):u.get.call(a),e=0,f=d.length,g;e<f&&(g=d[e]);e++){a:{var h=g;var k=a;var m=b;switch(h.nodeType){case Node.ELEMENT_NODE:for(var l=h.localName,n="<"+l,p=h.attributes,x=0;k=p[x];x++)n+=" "+k.name+'="'+k.value.replace(Y,La)+'"';n+=">";h=za[l]?n:n+aa(h,m)+"</"+l+">";break a;case Node.TEXT_NODE:h=h.data;h=k&&Ma[k.localName]?h:h.replace(Cb,La);break a;
case Node.COMMENT_NODE:h="\x3c!--"+h.data+"--\x3e";break a;default:throw window.console.error(h),Error("not implemented");}}c+=h}return c}}if(c||Bb){a.b=function(a,b){var c=f.call(a,!1);this.P&&this.P(c);b&&(m.call(c.content,f.call(a.content,!0)),Na(c.content,a.content));return c};var Na=function(c,d){if(d.querySelectorAll&&(d=b(d,"template"),0!==d.length)){c=b(c,"template");for(var e=0,f=c.length,g,h;e<f;e++)h=d[e],g=c[e],a&&a.P&&a.P(h),n.call(g.parentNode,mf.call(h,!0),g)}},mf=Node.prototype.cloneNode=
function(b){if(!e&&d&&this instanceof DocumentFragment)if(b)var c=nf.call(this.ownerDocument,this,!0);else return this.ownerDocument.createDocumentFragment();else this.nodeType===Node.ELEMENT_NODE&&"template"===this.localName&&this.namespaceURI==document.documentElement.namespaceURI?c=a.b(this,b):c=f.call(this,b);b&&Na(c,this);return c},nf=Document.prototype.importNode=function(c,d){d=d||!1;if("template"===c.localName)return a.b(c,d);var e=h.call(this,c,d);if(d){Na(e,c);c=b(e,'script:not([type]),script[type="application/javascript"],script[type="text/javascript"]');
for(var f,k=0;k<c.length;k++){f=c[k];d=g.call(document,"script");d.textContent=f.textContent;for(var m=f.attributes,l=0,p;l<m.length;l++)p=m[l],d.setAttribute(p.name,p.value);n.call(f.parentNode,d,f)}}return e}}c&&(window.HTMLTemplateElement=a)})();var Ba=setTimeout;function Ca(){}function Da(a,b){return function(){a.apply(b,arguments)}}function v(a){if(!(this instanceof v))throw new TypeError("Promises must be constructed via new");if("function"!==typeof a)throw new TypeError("not a function");this.I=0;this.sa=!1;this.w=void 0;this.S=[];Ea(a,this)}
function Fa(a,b){for(;3===a.I;)a=a.w;0===a.I?a.S.push(b):(a.sa=!0,Ga(function(){var c=1===a.I?b.Pa:b.Qa;if(null===c)(1===a.I?Ha:Ia)(b.oa,a.w);else{try{var d=c(a.w)}catch(e){Ia(b.oa,e);return}Ha(b.oa,d)}}))}function Ha(a,b){try{if(b===a)throw new TypeError("A promise cannot be resolved with itself.");if(b&&("object"===typeof b||"function"===typeof b)){var c=b.then;if(b instanceof v){a.I=3;a.w=b;Ja(a);return}if("function"===typeof c){Ea(Da(c,b),a);return}}a.I=1;a.w=b;Ja(a)}catch(d){Ia(a,d)}}
function Ia(a,b){a.I=2;a.w=b;Ja(a)}function Ja(a){2===a.I&&0===a.S.length&&Ga(function(){a.sa||"undefined"!==typeof console&&console&&console.warn("Possible Unhandled Promise Rejection:",a.w)});for(var b=0,c=a.S.length;b<c;b++)Fa(a,a.S[b]);a.S=null}function Oa(a,b,c){this.Pa="function"===typeof a?a:null;this.Qa="function"===typeof b?b:null;this.oa=c}function Ea(a,b){var c=!1;try{a(function(a){c||(c=!0,Ha(b,a))},function(a){c||(c=!0,Ia(b,a))})}catch(d){c||(c=!0,Ia(b,d))}}
v.prototype["catch"]=function(a){return this.then(null,a)};v.prototype.then=function(a,b){var c=new this.constructor(Ca);Fa(this,new Oa(a,b,c));return c};v.prototype["finally"]=function(a){var b=this.constructor;return this.then(function(c){return b.resolve(a()).then(function(){return c})},function(c){return b.resolve(a()).then(function(){return b.reject(c)})})};
function Pa(a){return new v(function(b,c){function d(a,g){try{if(g&&("object"===typeof g||"function"===typeof g)){var h=g.then;if("function"===typeof h){h.call(g,function(b){d(a,b)},c);return}}e[a]=g;0===--f&&b(e)}catch(n){c(n)}}if(!a||"undefined"===typeof a.length)throw new TypeError("Promise.all accepts an array");var e=Array.prototype.slice.call(a);if(0===e.length)return b([]);for(var f=e.length,g=0;g<e.length;g++)d(g,e[g])})}
function Qa(a){return a&&"object"===typeof a&&a.constructor===v?a:new v(function(b){b(a)})}function Ra(a){return new v(function(b,c){c(a)})}function Sa(a){return new v(function(b,c){for(var d=0,e=a.length;d<e;d++)a[d].then(b,c)})}var Ga="function"===typeof setImmediate&&function(a){setImmediate(a)}||function(a){Ba(a,0)};/*

Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
if(!window.Promise){window.Promise=v;v.prototype.then=v.prototype.then;v.all=Pa;v.race=Sa;v.resolve=Qa;v.reject=Ra;var Ta=document.createTextNode(""),Ua=[];(new MutationObserver(function(){for(var a=Ua.length,b=0;b<a;b++)Ua[b]();Ua.splice(0,a)})).observe(Ta,{characterData:!0});Ga=function(a){Ua.push(a);Ta.textContent=0<Ta.textContent.length?"":"a"}};/*
 Copyright (C) 2015 by WebReflection

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/
(function(a,b){if(!(b in a)){var c=typeof global===typeof c?window:global,d=0,e=""+Math.random(),f="__\u0001symbol@@"+e,g=a.getOwnPropertyNames,h=a.getOwnPropertyDescriptor,k=a.create,m=a.keys,n=a.freeze||a,p=a.defineProperty,G=a.defineProperties,u=h(a,"getOwnPropertyNames"),x=a.prototype,Q=x.hasOwnProperty,Ab=x.propertyIsEnumerable,Bb=x.toString,T=function(a,b,c){Q.call(a,f)||p(a,f,{enumerable:!1,configurable:!1,writable:!1,value:{}});a[f]["@@"+b]=c},Ka=function(a,b){var c=k(a);g(b).forEach(function(a){X.call(b,
a)&&za(c,a,b[a])});return c},q=function(){},ya=function(a){return a!=f&&!Q.call(Y,a)},Z=function(a){return a!=f&&Q.call(Y,a)},X=function(a){var b=""+a;return Z(b)?Q.call(this,b)&&this[f]["@@"+b]:Ab.call(this,a)},l=function(b){p(x,b,{enumerable:!1,configurable:!0,get:q,set:function(a){aa(this,b,{enumerable:!1,configurable:!0,writable:!0,value:a});T(this,b,!0)}});return n(Y[b]=p(a(b),"constructor",Cb))},y=function(a){if(this&&this!==c)throw new TypeError("Symbol is not a constructor");return l("__\u0001symbol:".concat(a||
"",e,++d))},Y=k(null),Cb={value:y},La=function(a){return Y[a]},za=function(a,b,c){var d=""+b;if(Z(d)){b=aa;if(c.enumerable){var e=k(c);e.enumerable=!1}else e=c;b(a,d,e);T(a,d,!!c.enumerable)}else p(a,b,c);return a},Ma=function(a){return g(a).filter(Z).map(La)};u.value=za;p(a,"defineProperty",u);u.value=Ma;p(a,b,u);u.value=function(a){return g(a).filter(ya)};p(a,"getOwnPropertyNames",u);u.value=function(a,b){var c=Ma(b);c.length?m(b).concat(c).forEach(function(c){X.call(b,c)&&za(a,c,b[c])}):G(a,b);
return a};p(a,"defineProperties",u);u.value=X;p(x,"propertyIsEnumerable",u);u.value=y;p(c,"Symbol",u);u.value=function(a){a="__\u0001symbol:".concat("__\u0001symbol:",a,e);return a in x?Y[a]:l(a)};p(y,"for",u);u.value=function(a){if(ya(a))throw new TypeError(a+" is not a symbol");return Q.call(Y,a)?a.slice(20,-e.length):void 0};p(y,"keyFor",u);u.value=function(a,b){var c=h(a,b);c&&Z(b)&&(c.enumerable=X.call(a,b));return c};p(a,"getOwnPropertyDescriptor",u);u.value=function(a,b){return 1===arguments.length?
k(a):Ka(a,b)};p(a,"create",u);u.value=function(){var a=Bb.call(this);return"[object String]"===a&&Z(this)?"[object Symbol]":a};p(x,"toString",u);try{var aa=k(p({},"__\u0001symbol:",{get:function(){return p(this,"__\u0001symbol:",{value:!1})["__\u0001symbol:"]}}))["__\u0001symbol:"]||p}catch(Na){aa=function(a,b,c){var d=h(x,b);delete x[b];p(a,b,c);p(x,b,d)}}}})(Object,"getOwnPropertySymbols");
(function(a){var b=a.defineProperty,c=a.prototype,d=c.toString,e;"iterator match replace search split hasInstance isConcatSpreadable unscopables species toPrimitive toStringTag".split(" ").forEach(function(f){if(!(f in Symbol))switch(b(Symbol,f,{value:Symbol(f)}),f){case "toStringTag":e=a.getOwnPropertyDescriptor(c,"toString"),e.value=function(){var a=d.call(this),b=this[Symbol.toStringTag];return"undefined"===typeof b?a:"[object "+b+"]"},b(c,"toString",e)}})})(Object,Symbol);
(function(a,b,c){function d(){return this}b[a]||(b[a]=function(){var b=0,c=this,g={next:function(){var a=c.length<=b;return a?{done:a}:{done:a,value:c[b++]}}};g[a]=d;return g});c[a]||(c[a]=function(){var b=String.fromCodePoint,c=this,g=0,h=c.length,k={next:function(){var a=h<=g,d=a?"":b(c.codePointAt(g));g+=d.length;return a?{done:a}:{done:a,value:d}}};k[a]=d;return k})})(Symbol.iterator,Array.prototype,String.prototype);/*

Copyright (c) 2018 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
var Va=Object.prototype.toString;Object.prototype.toString=function(){return void 0===this?"[object Undefined]":null===this?"[object Null]":Va.call(this)};Object.keys=function(a){return Object.getOwnPropertyNames(a).filter(function(b){return(b=Object.getOwnPropertyDescriptor(a,b))&&b.enumerable})};var Wa=window.Symbol.iterator;
String.prototype[Wa]&&String.prototype.codePointAt||(String.prototype[Wa]=function Xa(){var b,c=this;return Aa(Xa,function(d){1==d.a&&(b=0);if(3!=d.a)return b<c.length?d=sa(d,c[b]):(d.a=0,d=void 0),d;b++;d.a=2})});Set.prototype[Wa]||(Set.prototype[Wa]=function Ya(){var b,c=this,d;return Aa(Ya,function(e){1==e.a&&(b=[],c.forEach(function(c){b.push(c)}),d=0);if(3!=e.a)return d<b.length?e=sa(e,b[d]):(e.a=0,e=void 0),e;d++;e.a=2})});
Map.prototype[Wa]||(Map.prototype[Wa]=function Za(){var b,c=this,d;return Aa(Za,function(e){1==e.a&&(b=[],c.forEach(function(c,d){b.push([d,c])}),d=0);if(3!=e.a)return d<b.length?e=sa(e,b[d]):(e.a=0,e=void 0),e;d++;e.a=2})});/*

 Copyright (c) 2014 The Polymer Project Authors. All rights reserved.
 This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
 The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
 The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
 Code distributed by Google as part of the polymer project is also
 subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
window.WebComponents=window.WebComponents||{flags:{}};var $a=document.querySelector('script[src*="webcomponents-bundle"]'),ab=/wc-(.+)/,w={};if(!w.noOpts){location.search.slice(1).split("&").forEach(function(a){a=a.split("=");var b;a[0]&&(b=a[0].match(ab))&&(w[b[1]]=a[1]||!0)});if($a)for(var bb=0,cb=void 0;cb=$a.attributes[bb];bb++)"src"!==cb.name&&(w[cb.name]=cb.value||!0);if(w.log&&w.log.split){var db=w.log.split(",");w.log={};db.forEach(function(a){w.log[a]=!0})}else w.log={}}
window.WebComponents.flags=w;var eb=w.shadydom;eb&&(window.ShadyDOM=window.ShadyDOM||{},window.ShadyDOM.force=eb);var fb=w.register||w.ce;fb&&window.customElements&&(window.customElements.forcePolyfill=fb);/*

Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
function gb(){this.va=this.root=null;this.ba=!1;this.N=this.Z=this.ka=this.assignedSlot=this.assignedNodes=this.R=null;this.childNodes=this.nextSibling=this.previousSibling=this.lastChild=this.firstChild=this.parentNode=this.U=void 0;this.qa=this.ra=!1;this.Y={}}gb.prototype.toJSON=function(){return{}};function z(a){a.__shady||(a.__shady=new gb);return a.__shady}function A(a){return a&&a.__shady};var B=window.ShadyDOM||{};B.La=!(!Element.prototype.attachShadow||!Node.prototype.getRootNode);var hb=Object.getOwnPropertyDescriptor(Node.prototype,"firstChild");B.D=!!(hb&&hb.configurable&&hb.get);B.ma=B.force||!B.La;B.T=B.noPatch||!1;B.ua=B.preferPerformance;function ib(a){return(a=A(a))&&void 0!==a.firstChild}function C(a){return"ShadyRoot"===a.Da}function jb(a){return(a=(a=A(a))&&a.root)&&kb(a)}
var lb=Element.prototype,mb=lb.matches||lb.matchesSelector||lb.mozMatchesSelector||lb.msMatchesSelector||lb.oMatchesSelector||lb.webkitMatchesSelector,nb=document.createTextNode(""),ob=0,pb=[];(new MutationObserver(function(){for(;pb.length;)try{pb.shift()()}catch(a){throw nb.textContent=ob++,a;}})).observe(nb,{characterData:!0});function qb(a){pb.push(a);nb.textContent=ob++}var rb=!!document.contains;function sb(a,b){for(;b;){if(b==a)return!0;b=b.__shady_parentNode}return!1}
function tb(a){for(var b=a.length-1;0<=b;b--){var c=a[b],d=c.getAttribute("id")||c.getAttribute("name");d&&"length"!==d&&isNaN(d)&&(a[d]=c)}a.item=function(b){return a[b]};a.namedItem=function(b){if("length"!==b&&isNaN(b)&&a[b])return a[b];for(var c=ia(a),d=c.next();!d.done;d=c.next())if(d=d.value,(d.getAttribute("id")||d.getAttribute("name"))==b)return d;return null};return a}
function D(a,b,c,d){c=void 0===c?"":c;for(var e in b){var f=b[e];if(!(d&&0<=d.indexOf(e))){f.configurable=!0;var g=c+e;if(f.value)a[g]=f.value;else try{Object.defineProperty(a,g,f)}catch(h){}}}}function E(a){var b={};Object.getOwnPropertyNames(a).forEach(function(c){b[c]=Object.getOwnPropertyDescriptor(a,c)});return b};var ub=[],vb;function wb(a){vb||(vb=!0,qb(xb));ub.push(a)}function xb(){vb=!1;for(var a=!!ub.length;ub.length;)ub.shift()();return a}xb.list=ub;function yb(){this.a=!1;this.addedNodes=[];this.removedNodes=[];this.aa=new Set}function zb(a){a.a||(a.a=!0,qb(function(){a.flush()}))}yb.prototype.flush=function(){if(this.a){this.a=!1;var a=this.takeRecords();a.length&&this.aa.forEach(function(b){b(a)})}};yb.prototype.takeRecords=function(){if(this.addedNodes.length||this.removedNodes.length){var a=[{addedNodes:this.addedNodes,removedNodes:this.removedNodes}];this.addedNodes=[];this.removedNodes=[];return a}return[]};
function Db(a,b){var c=z(a);c.R||(c.R=new yb);c.R.aa.add(b);var d=c.R;return{Ca:b,O:d,Ea:a,takeRecords:function(){return d.takeRecords()}}}function Eb(a){var b=a&&a.O;b&&(b.aa.delete(a.Ca),b.aa.size||(z(a.Ea).R=null))}
function Fb(a,b){var c=b.getRootNode();return a.map(function(a){var b=c===a.target.getRootNode();if(b&&a.addedNodes){if(b=Array.from(a.addedNodes).filter(function(a){return c===a.getRootNode()}),b.length)return a=Object.create(a),Object.defineProperty(a,"addedNodes",{value:b,configurable:!0}),a}else if(b)return a}).filter(function(a){return a})};var Gb=/[&\u00A0"]/g,Hb=/[&\u00A0<>]/g;function Ib(a){switch(a){case "&":return"&amp;";case "<":return"&lt;";case ">":return"&gt;";case '"':return"&quot;";case "\u00a0":return"&nbsp;"}}function Jb(a){for(var b={},c=0;c<a.length;c++)b[a[c]]=!0;return b}var Kb=Jb("area base br col command embed hr img input keygen link meta param source track wbr".split(" ")),Lb=Jb("style script xmp iframe noembed noframes plaintext noscript".split(" "));
function Mb(a,b){"template"===a.localName&&(a=a.content);for(var c="",d=b?b(a):a.childNodes,e=0,f=d.length,g=void 0;e<f&&(g=d[e]);e++){a:{var h=g;var k=a,m=b;switch(h.nodeType){case Node.ELEMENT_NODE:k=h.localName;for(var n="<"+k,p=h.attributes,G=0,u;u=p[G];G++)n+=" "+u.name+'="'+u.value.replace(Gb,Ib)+'"';n+=">";h=Kb[k]?n:n+Mb(h,m)+"</"+k+">";break a;case Node.TEXT_NODE:h=h.data;h=k&&Lb[k.localName]?h:h.replace(Hb,Ib);break a;case Node.COMMENT_NODE:h="\x3c!--"+h.data+"--\x3e";break a;default:throw window.console.error(h),
Error("not implemented");}}c+=h}return c};var Nb=B.D,Ob={querySelector:function(a){return this.__shady_native_querySelector(a)},querySelectorAll:function(a){return this.__shady_native_querySelectorAll(a)}},Pb={};function Qb(a){Pb[a]=function(b){return b["__shady_native_"+a]}}function Rb(a,b){D(a,b,"__shady_native_");for(var c in b)Qb(c)}function F(a,b){b=void 0===b?[]:b;for(var c=0;c<b.length;c++){var d=b[c],e=Object.getOwnPropertyDescriptor(a,d);e&&(Object.defineProperty(a,"__shady_native_"+d,e),e.value?Ob[d]||(Ob[d]=e.value):Qb(d))}}
var H=document.createTreeWalker(document,NodeFilter.SHOW_ALL,null,!1),I=document.createTreeWalker(document,NodeFilter.SHOW_ELEMENT,null,!1),Sb=document.implementation.createHTMLDocument("inert");function Tb(a){for(var b;b=a.__shady_native_firstChild;)a.__shady_native_removeChild(b)}var Ub=["firstElementChild","lastElementChild","children","childElementCount"],Vb=["querySelector","querySelectorAll"];
function Wb(){var a=["dispatchEvent","addEventListener","removeEventListener"];window.EventTarget?F(window.EventTarget.prototype,a):(F(Node.prototype,a),F(Window.prototype,a));Nb?F(Node.prototype,"parentNode firstChild lastChild previousSibling nextSibling childNodes parentElement textContent".split(" ")):Rb(Node.prototype,{parentNode:{get:function(){H.currentNode=this;return H.parentNode()}},firstChild:{get:function(){H.currentNode=this;return H.firstChild()}},lastChild:{get:function(){H.currentNode=
this;return H.lastChild()}},previousSibling:{get:function(){H.currentNode=this;return H.previousSibling()}},nextSibling:{get:function(){H.currentNode=this;return H.nextSibling()}},childNodes:{get:function(){var a=[];H.currentNode=this;for(var c=H.firstChild();c;)a.push(c),c=H.nextSibling();return a}},parentElement:{get:function(){I.currentNode=this;return I.parentNode()}},textContent:{get:function(){switch(this.nodeType){case Node.ELEMENT_NODE:case Node.DOCUMENT_FRAGMENT_NODE:for(var a=document.createTreeWalker(this,
NodeFilter.SHOW_TEXT,null,!1),c="",d;d=a.nextNode();)c+=d.nodeValue;return c;default:return this.nodeValue}},set:function(a){if("undefined"===typeof a||null===a)a="";switch(this.nodeType){case Node.ELEMENT_NODE:case Node.DOCUMENT_FRAGMENT_NODE:Tb(this);(0<a.length||this.nodeType===Node.ELEMENT_NODE)&&this.__shady_native_insertBefore(document.createTextNode(a),void 0);break;default:this.nodeValue=a}}}});F(Node.prototype,"appendChild insertBefore removeChild replaceChild cloneNode contains".split(" "));
a={firstElementChild:{get:function(){I.currentNode=this;return I.firstChild()}},lastElementChild:{get:function(){I.currentNode=this;return I.lastChild()}},children:{get:function(){var a=[];I.currentNode=this;for(var c=I.firstChild();c;)a.push(c),c=I.nextSibling();return tb(a)}},childElementCount:{get:function(){return this.children?this.children.length:0}}};Nb?(F(Element.prototype,Ub),F(Element.prototype,["previousElementSibling","nextElementSibling","innerHTML"]),Object.getOwnPropertyDescriptor(HTMLElement.prototype,
"children")&&F(HTMLElement.prototype,["children"]),Object.getOwnPropertyDescriptor(HTMLElement.prototype,"innerHTML")&&F(HTMLElement.prototype,["innerHTML"])):(Rb(Element.prototype,a),Rb(Element.prototype,{previousElementSibling:{get:function(){I.currentNode=this;return I.previousSibling()}},nextElementSibling:{get:function(){I.currentNode=this;return I.nextSibling()}},innerHTML:{get:function(){return Mb(this,function(a){return a.__shady_native_childNodes})},set:function(a){var b="template"===this.localName?
this.content:this;Tb(b);var d=this.localName||"div";d=this.namespaceURI&&this.namespaceURI!==Sb.namespaceURI?Sb.createElementNS(this.namespaceURI,d):Sb.createElement(d);d.innerHTML=a;for(a="template"===this.localName?d.content:d;d=a.__shady_native_firstChild;)b.__shady_native_insertBefore(d,void 0)}}}));F(Element.prototype,"setAttribute getAttribute hasAttribute removeAttribute focus blur".split(" "));F(Element.prototype,Vb);F(HTMLElement.prototype,["focus","blur","contains"]);Nb&&F(HTMLElement.prototype,
["parentElement","children","innerHTML"]);window.HTMLTemplateElement&&F(window.HTMLTemplateElement.prototype,["innerHTML"]);Nb?F(DocumentFragment.prototype,Ub):Rb(DocumentFragment.prototype,a);F(DocumentFragment.prototype,Vb);Nb?(F(Document.prototype,Ub),F(Document.prototype,["activeElement"])):Rb(Document.prototype,a);F(Document.prototype,["importNode","getElementById"]);F(Document.prototype,Vb)};var Xb=E({get childNodes(){return this.__shady_childNodes},get firstChild(){return this.__shady_firstChild},get lastChild(){return this.__shady_lastChild},get textContent(){return this.__shady_textContent},set textContent(a){this.__shady_textContent=a},get childElementCount(){return this.__shady_childElementCount},get children(){return this.__shady_children},get firstElementChild(){return this.__shady_firstElementChild},get lastElementChild(){return this.__shady_lastElementChild},get innerHTML(){return this.__shady_innerHTML},
set innerHTML(a){return this.__shady_innerHTML=a},get shadowRoot(){return this.__shady_shadowRoot}}),Yb=E({get parentElement(){return this.__shady_parentElement},get parentNode(){return this.__shady_parentNode},get nextSibling(){return this.__shady_nextSibling},get previousSibling(){return this.__shady_previousSibling},get nextElementSibling(){return this.__shady_nextElementSibling},get previousElementSibling(){return this.__shady_previousElementSibling},get className(){return this.__shady_className},
set className(a){return this.__shady_className=a}}),Zb;for(Zb in Xb)Xb[Zb].enumerable=!1;for(var $b in Yb)Yb[$b].enumerable=!1;var ac=B.D||B.T,bc=ac?function(){}:function(a){var b=z(a);b.ra||(b.ra=!0,D(a,Yb))},cc=ac?function(){}:function(a){var b=z(a);b.qa||(b.qa=!0,D(a,Xb))};var dc="__eventWrappers"+Date.now(),ec=function(){var a=Object.getOwnPropertyDescriptor(Event.prototype,"composed");return a?function(b){return a.get.call(b)}:null}(),fc={blur:!0,focus:!0,focusin:!0,focusout:!0,click:!0,dblclick:!0,mousedown:!0,mouseenter:!0,mouseleave:!0,mousemove:!0,mouseout:!0,mouseover:!0,mouseup:!0,wheel:!0,beforeinput:!0,input:!0,keydown:!0,keyup:!0,compositionstart:!0,compositionupdate:!0,compositionend:!0,touchstart:!0,touchend:!0,touchmove:!0,touchcancel:!0,pointerover:!0,
pointerenter:!0,pointerdown:!0,pointermove:!0,pointerup:!0,pointercancel:!0,pointerout:!0,pointerleave:!0,gotpointercapture:!0,lostpointercapture:!0,dragstart:!0,drag:!0,dragenter:!0,dragleave:!0,dragover:!0,drop:!0,dragend:!0,DOMActivate:!0,DOMFocusIn:!0,DOMFocusOut:!0,keypress:!0},gc={DOMAttrModified:!0,DOMAttributeNameChanged:!0,DOMCharacterDataModified:!0,DOMElementNameChanged:!0,DOMNodeInserted:!0,DOMNodeInsertedIntoDocument:!0,DOMNodeRemoved:!0,DOMNodeRemovedFromDocument:!0,DOMSubtreeModified:!0};
function hc(a){return a instanceof Node?a.__shady_getRootNode():a}function ic(a,b){var c=[],d=a;for(a=hc(a);d;)c.push(d),d.__shady_assignedSlot?d=d.__shady_assignedSlot:d.nodeType===Node.DOCUMENT_FRAGMENT_NODE&&d.host&&(b||d!==a)?d=d.host:d=d.__shady_parentNode;c[c.length-1]===document&&c.push(window);return c}function jc(a){a.__composedPath||(a.__composedPath=ic(a.target,!0));return a.__composedPath}
function kc(a,b){if(!C)return a;a=ic(a,!0);for(var c=0,d,e=void 0,f,g=void 0;c<b.length;c++)if(d=b[c],f=hc(d),f!==e&&(g=a.indexOf(f),e=f),!C(f)||-1<g)return d}function lc(a){function b(b,d){b=new a(b,d);b.__composed=d&&!!d.composed;return b}b.__proto__=a;b.prototype=a.prototype;return b}var mc={focus:!0,blur:!0};function nc(a){return a.__target!==a.target||a.__relatedTarget!==a.relatedTarget}
function oc(a,b,c){if(c=b.__handlers&&b.__handlers[a.type]&&b.__handlers[a.type][c])for(var d=0,e;(e=c[d])&&(!nc(a)||a.target!==a.relatedTarget)&&(e.call(b,a),!a.__immediatePropagationStopped);d++);}
function pc(a){var b=a.composedPath();Object.defineProperty(a,"currentTarget",{get:function(){return d},configurable:!0});for(var c=b.length-1;0<=c;c--){var d=b[c];oc(a,d,"capture");if(a.ha)return}Object.defineProperty(a,"eventPhase",{get:function(){return Event.AT_TARGET}});var e;for(c=0;c<b.length;c++){d=b[c];var f=A(d);f=f&&f.root;if(0===c||f&&f===e)if(oc(a,d,"bubble"),d!==window&&(e=d.__shady_getRootNode()),a.ha)break}}
function qc(a,b,c,d,e,f){for(var g=0;g<a.length;g++){var h=a[g],k=h.type,m=h.capture,n=h.once,p=h.passive;if(b===h.node&&c===k&&d===m&&e===n&&f===p)return g}return-1}
function rc(a,b,c){if(b){var d=typeof b;if("function"===d||"object"===d)if("object"!==d||b.handleEvent&&"function"===typeof b.handleEvent){if(gc[a])return this.__shady_native_addEventListener(a,b,c);if(c&&"object"===typeof c){var e=!!c.capture;var f=!!c.once;var g=!!c.passive}else e=!!c,g=f=!1;var h=c&&c.ia||this,k=b[dc];if(k){if(-1<qc(k,h,a,e,f,g))return}else b[dc]=[];k=function(e){f&&this.__shady_removeEventListener(a,b,c);e.__target||sc(e);if(h!==this){var g=Object.getOwnPropertyDescriptor(e,"currentTarget");
Object.defineProperty(e,"currentTarget",{get:function(){return h},configurable:!0})}e.__previousCurrentTarget=e.currentTarget;if(!C(h)||-1!=e.composedPath().indexOf(h))if(e.composed||-1<e.composedPath().indexOf(h))if(nc(e)&&e.target===e.relatedTarget)e.eventPhase===Event.BUBBLING_PHASE&&e.stopImmediatePropagation();else if(e.eventPhase===Event.CAPTURING_PHASE||e.bubbles||e.target===h||h instanceof Window){var k="function"===d?b.call(h,e):b.handleEvent&&b.handleEvent(e);h!==this&&(g?(Object.defineProperty(e,
"currentTarget",g),g=null):delete e.currentTarget);return k}};b[dc].push({node:h,type:a,capture:e,once:f,passive:g,$a:k});mc[a]?(this.__handlers=this.__handlers||{},this.__handlers[a]=this.__handlers[a]||{capture:[],bubble:[]},this.__handlers[a][e?"capture":"bubble"].push(k)):this.__shady_native_addEventListener(a,k,c)}}}
function tc(a,b,c){if(b){if(gc[a])return this.__shady_native_removeEventListener(a,b,c);if(c&&"object"===typeof c){var d=!!c.capture;var e=!!c.once;var f=!!c.passive}else d=!!c,f=e=!1;var g=c&&c.ia||this,h=void 0;var k=null;try{k=b[dc]}catch(m){}k&&(e=qc(k,g,a,d,e,f),-1<e&&(h=k.splice(e,1)[0].$a,k.length||(b[dc]=void 0)));this.__shady_native_removeEventListener(a,h||b,c);h&&mc[a]&&this.__handlers&&this.__handlers[a]&&(a=this.__handlers[a][d?"capture":"bubble"],h=a.indexOf(h),-1<h&&a.splice(h,1))}}
function uc(){for(var a in mc)window.__shady_native_addEventListener(a,function(a){a.__target||(sc(a),pc(a))},!0)}
var vc=E({get composed(){void 0===this.__composed&&(ec?this.__composed="focusin"===this.type||"focusout"===this.type||ec(this):!1!==this.isTrusted&&(this.__composed=fc[this.type]));return this.__composed||!1},composedPath:function(){this.__composedPath||(this.__composedPath=ic(this.__target,this.composed));return this.__composedPath},get target(){return kc(this.currentTarget||this.__previousCurrentTarget,this.composedPath())},get relatedTarget(){if(!this.__relatedTarget)return null;this.__relatedTargetComposedPath||
(this.__relatedTargetComposedPath=ic(this.__relatedTarget,!0));return kc(this.currentTarget||this.__previousCurrentTarget,this.__relatedTargetComposedPath)},stopPropagation:function(){Event.prototype.stopPropagation.call(this);this.ha=!0},stopImmediatePropagation:function(){Event.prototype.stopImmediatePropagation.call(this);this.ha=this.__immediatePropagationStopped=!0}});
function sc(a){a.__target=a.target;a.__relatedTarget=a.relatedTarget;if(B.D){var b=Object.getPrototypeOf(a);if(!Object.hasOwnProperty(b,"__shady_patchedProto")){var c=Object.create(b);c.__shady_sourceProto=b;D(c,vc);b.__shady_patchedProto=c}a.__proto__=b.__shady_patchedProto}else D(a,vc)}var wc=lc(Event),xc=lc(CustomEvent),yc=lc(MouseEvent);
function zc(){if(!ec&&Object.getOwnPropertyDescriptor(Event.prototype,"isTrusted")){var a=function(){var a=new MouseEvent("click",{bubbles:!0,cancelable:!0,composed:!0});this.__shady_dispatchEvent(a)};Element.prototype.click?Element.prototype.click=a:HTMLElement.prototype.click&&(HTMLElement.prototype.click=a)}}var Ac=Object.getOwnPropertyNames(Document.prototype).filter(function(a){return"on"===a.substring(0,2)});function Bc(a,b){return{index:a,V:[],$:b}}
function Cc(a,b,c,d){var e=0,f=0,g=0,h=0,k=Math.min(b-e,d-f);if(0==e&&0==f)a:{for(g=0;g<k;g++)if(a[g]!==c[g])break a;g=k}if(b==a.length&&d==c.length){h=a.length;for(var m=c.length,n=0;n<k-g&&Dc(a[--h],c[--m]);)n++;h=n}e+=g;f+=g;b-=h;d-=h;if(0==b-e&&0==d-f)return[];if(e==b){for(b=Bc(e,0);f<d;)b.V.push(c[f++]);return[b]}if(f==d)return[Bc(e,b-e)];k=e;g=f;d=d-g+1;h=b-k+1;b=Array(d);for(m=0;m<d;m++)b[m]=Array(h),b[m][0]=m;for(m=0;m<h;m++)b[0][m]=m;for(m=1;m<d;m++)for(n=1;n<h;n++)if(a[k+n-1]===c[g+m-1])b[m][n]=
b[m-1][n-1];else{var p=b[m-1][n]+1,G=b[m][n-1]+1;b[m][n]=p<G?p:G}k=b.length-1;g=b[0].length-1;d=b[k][g];for(a=[];0<k||0<g;)0==k?(a.push(2),g--):0==g?(a.push(3),k--):(h=b[k-1][g-1],m=b[k-1][g],n=b[k][g-1],p=m<n?m<h?m:h:n<h?n:h,p==h?(h==d?a.push(0):(a.push(1),d=h),k--,g--):p==m?(a.push(3),k--,d=m):(a.push(2),g--,d=n));a.reverse();b=void 0;k=[];for(g=0;g<a.length;g++)switch(a[g]){case 0:b&&(k.push(b),b=void 0);e++;f++;break;case 1:b||(b=Bc(e,0));b.$++;e++;b.V.push(c[f]);f++;break;case 2:b||(b=Bc(e,0));
b.$++;e++;break;case 3:b||(b=Bc(e,0)),b.V.push(c[f]),f++}b&&k.push(b);return k}function Dc(a,b){return a===b};function Ec(a,b,c){bc(a);c=c||null;var d=z(a),e=z(b),f=c?z(c):null;d.previousSibling=c?f.previousSibling:b.__shady_lastChild;if(f=A(d.previousSibling))f.nextSibling=a;if(f=A(d.nextSibling=c))f.previousSibling=a;d.parentNode=b;c?c===e.firstChild&&(e.firstChild=a):(e.lastChild=a,e.firstChild||(e.firstChild=a));e.childNodes=null}
function Fc(a,b,c){cc(b);var d=z(b);void 0!==d.firstChild&&(d.childNodes=null);if(a.nodeType===Node.DOCUMENT_FRAGMENT_NODE){d=a.__shady_childNodes;for(var e=0;e<d.length;e++)Ec(d[e],b,c);a=z(a);b=void 0!==a.firstChild?null:void 0;a.firstChild=a.lastChild=b;a.childNodes=b}else Ec(a,b,c)}
function Gc(a,b){var c=z(a);b=z(b);a===b.firstChild&&(b.firstChild=c.nextSibling);a===b.lastChild&&(b.lastChild=c.previousSibling);a=c.previousSibling;var d=c.nextSibling;a&&(z(a).nextSibling=d);d&&(z(d).previousSibling=a);c.parentNode=c.previousSibling=c.nextSibling=void 0;void 0!==b.childNodes&&(b.childNodes=null)}
function Hc(a){var b=z(a);if(void 0===b.firstChild){b.childNodes=null;var c=b.firstChild=a.__shady_native_firstChild||null;b.lastChild=a.__shady_native_lastChild||null;cc(a);b=c;for(c=void 0;b;b=b.__shady_native_nextSibling){var d=z(b);d.parentNode=a;d.nextSibling=b.__shady_native_nextSibling||null;d.previousSibling=c||null;c=b;bc(b)}}};var Ic=null;function Jc(){Ic||(Ic=window.ShadyCSS&&window.ShadyCSS.ScopingShim);return Ic||null}function Kc(a,b){var c=Jc();c&&c.unscopeNode(a,b)}function Lc(a,b){var c=Jc();if(!c)return!0;if(a.nodeType===Node.DOCUMENT_FRAGMENT_NODE){c=!0;a=a.__shady_childNodes;for(var d=0;c&&d<a.length;d++)c=c&&Lc(a[d],b);return c}return a.nodeType!==Node.ELEMENT_NODE?!0:c.currentScopeForNode(a)===b}function Mc(a){if(a.nodeType!==Node.ELEMENT_NODE)return"";var b=Jc();return b?b.currentScopeForNode(a):""}
function Nc(a,b){if(a){a.nodeType===Node.ELEMENT_NODE&&b(a);a=a.__shady_childNodes;for(var c=0,d;c<a.length;c++)d=a[c],d.nodeType===Node.ELEMENT_NODE&&Nc(d,b)}};var Oc=window.document,Pc=B.ua,Qc=Object.getOwnPropertyDescriptor(Node.prototype,"isConnected"),Rc=Qc&&Qc.get;function Sc(a){for(var b;b=a.__shady_firstChild;)a.__shady_removeChild(b)}function Tc(a){var b=A(a);if(b&&void 0!==b.U){b=a.__shady_childNodes;for(var c=0,d=b.length,e=void 0;c<d&&(e=b[c]);c++)Tc(e)}if(a=A(a))a.U=void 0}function Uc(a){var b=a;a&&"slot"===a.localName&&(b=(b=(b=A(a))&&b.N)&&b.length?b[0]:Uc(a.__shady_nextSibling));return b}
function Vc(a,b,c){if(a=(a=A(a))&&a.R)b&&a.addedNodes.push(b),c&&a.removedNodes.push(c),zb(a)}
var Zc=E({get parentNode(){var a=A(this);a=a&&a.parentNode;return void 0!==a?a:this.__shady_native_parentNode},get firstChild(){var a=A(this);a=a&&a.firstChild;return void 0!==a?a:this.__shady_native_firstChild},get lastChild(){var a=A(this);a=a&&a.lastChild;return void 0!==a?a:this.__shady_native_lastChild},get nextSibling(){var a=A(this);a=a&&a.nextSibling;return void 0!==a?a:this.__shady_native_nextSibling},get previousSibling(){var a=A(this);a=a&&a.previousSibling;return void 0!==a?a:this.__shady_native_previousSibling},
get childNodes(){if(ib(this)){var a=A(this);if(!a.childNodes){a.childNodes=[];for(var b=this.__shady_firstChild;b;b=b.__shady_nextSibling)a.childNodes.push(b)}var c=a.childNodes}else c=this.__shady_native_childNodes;c.item=function(a){return c[a]};return c},get parentElement(){var a=A(this);(a=a&&a.parentNode)&&a.nodeType!==Node.ELEMENT_NODE&&(a=null);return void 0!==a?a:this.__shady_native_parentElement},get isConnected(){if(Rc&&Rc.call(this))return!0;if(this.nodeType==Node.DOCUMENT_FRAGMENT_NODE)return!1;
var a=this.ownerDocument;if(rb){if(a.__shady_native_contains(this))return!0}else if(a.documentElement&&a.documentElement.__shady_native_contains(this))return!0;for(a=this;a&&!(a instanceof Document);)a=a.__shady_parentNode||(C(a)?a.host:void 0);return!!(a&&a instanceof Document)},get textContent(){if(ib(this)){for(var a=[],b=0,c=this.__shady_childNodes,d;d=c[b];b++)d.nodeType!==Node.COMMENT_NODE&&a.push(d.__shady_textContent);return a.join("")}return this.__shady_native_textContent},set textContent(a){if("undefined"===
typeof a||null===a)a="";switch(this.nodeType){case Node.ELEMENT_NODE:case Node.DOCUMENT_FRAGMENT_NODE:if(!ib(this)&&B.D){var b=this.__shady_firstChild;(b!=this.__shady_lastChild||b&&b.nodeType!=Node.TEXT_NODE)&&Sc(this);this.__shady_native_textContent=a}else Sc(this),(0<a.length||this.nodeType===Node.ELEMENT_NODE)&&this.__shady_insertBefore(document.createTextNode(a));break;default:this.nodeValue=a}},insertBefore:function(a,b){if(this.ownerDocument!==Oc&&a.ownerDocument!==Oc)return this.__shady_native_insertBefore(a,
b),a;if(a===this)throw Error("Failed to execute 'appendChild' on 'Node': The new child element contains the parent.");if(b){var c=A(b);c=c&&c.parentNode;if(void 0!==c&&c!==this||void 0===c&&b.__shady_native_parentNode!==this)throw Error("Failed to execute 'insertBefore' on 'Node': The node before which the new node is to be inserted is not a child of this node.");}if(b===a)return a;var d=[],e=(c=Wc(this))?c.host.localName:Mc(this),f=a.__shady_parentNode;if(f){var g=Mc(a);f.__shady_removeChild(a,!!c||
!Wc(a))}f=!0;var h=(!Pc||void 0===a.__noInsertionPoint)&&!Lc(a,e),k=c&&!a.__noInsertionPoint&&(!Pc||a.nodeType===Node.DOCUMENT_FRAGMENT_NODE);if(k||h)h&&(g=g||Mc(a)),Nc(a,function(a){k&&"slot"===a.localName&&d.push(a);if(h){var b=g;Jc()&&(b&&Kc(a,b),(b=Jc())&&b.scopeNode(a,e))}});if("slot"===this.localName||d.length)d.length&&(c.c=c.c||[],c.a=c.a||[],c.b=c.b||{},c.c.push.apply(c.c,d instanceof Array?d:ja(ia(d)))),c&&Xc(c);ib(this)&&(Fc(a,this,b),c=A(this),jb(this)?(Xc(c.root),f=!1):c.root&&(f=!1));
f?(c=C(this)?this.host:this,b?(b=Uc(b),c.__shady_native_insertBefore(a,b)):c.__shady_native_appendChild(a)):a.ownerDocument!==this.ownerDocument&&this.ownerDocument.adoptNode(a);Vc(this,a);return a},appendChild:function(a){return this.__shady_insertBefore(a)},removeChild:function(a,b){b=void 0===b?!1:b;if(this.ownerDocument!==Oc)return this.__shady_native_removeChild(a);if(a.__shady_parentNode!==this)throw Error("The node to be removed is not a child of this node: "+a);var c=Wc(a),d=c&&Yc(c,a),e=
A(this);if(ib(this)&&(Gc(a,this),jb(this))){Xc(e.root);var f=!0}if(Jc()&&!b&&c){var g=Mc(a);Nc(a,function(a){Kc(a,g)})}Tc(a);c&&((b=this&&"slot"===this.localName)&&(f=!0),(d||b)&&Xc(c));f||(f=C(this)?this.host:this,(!e.root&&"slot"!==a.localName||f===a.__shady_native_parentNode)&&f.__shady_native_removeChild(a));Vc(this,null,a);return a},replaceChild:function(a,b){this.__shady_insertBefore(a,b);this.__shady_removeChild(b);return a},cloneNode:function(a){if("template"==this.localName)return this.__shady_native_cloneNode(a);
var b=this.__shady_native_cloneNode(!1);if(a&&b.nodeType!==Node.ATTRIBUTE_NODE){a=this.__shady_childNodes;for(var c=0,d;c<a.length;c++)d=a[c].__shady_cloneNode(!0),b.__shady_appendChild(d)}return b},getRootNode:function(a){if(this&&this.nodeType){var b=z(this),c=b.U;void 0===c&&(C(this)?(c=this,b.U=c):(c=(c=this.__shady_parentNode)?c.__shady_getRootNode(a):this,document.documentElement.__shady_native_contains(this)&&(b.U=c)));return c}},contains:function(a){return sb(this,a)}});function $c(a,b,c){var d=[];ad(a.__shady_childNodes,b,c,d);return d}function ad(a,b,c,d){for(var e=0,f=a.length,g=void 0;e<f&&(g=a[e]);e++){var h;if(h=g.nodeType===Node.ELEMENT_NODE){h=g;var k=b,m=c,n=d,p=k(h);p&&n.push(h);m&&m(p)?h=p:(ad(h.__shady_childNodes,k,m,n),h=void 0)}if(h)break}}
var bd=E({get firstElementChild(){var a=A(this);if(a&&void 0!==a.firstChild){for(a=this.__shady_firstChild;a&&a.nodeType!==Node.ELEMENT_NODE;)a=a.__shady_nextSibling;return a}return this.__shady_native_firstElementChild},get lastElementChild(){var a=A(this);if(a&&void 0!==a.lastChild){for(a=this.__shady_lastChild;a&&a.nodeType!==Node.ELEMENT_NODE;)a=a.__shady_previousSibling;return a}return this.__shady_native_lastElementChild},get children(){return ib(this)?tb(Array.prototype.filter.call(this.__shady_childNodes,
function(a){return a.nodeType===Node.ELEMENT_NODE})):this.__shady_native_children},get childElementCount(){var a=this.__shady_children;return a?a.length:0}}),cd=E({querySelector:function(a){return $c(this,function(b){return mb.call(b,a)},function(a){return!!a})[0]||null},querySelectorAll:function(a,b){if(b){b=Array.prototype.slice.call(this.__shady_native_querySelectorAll(a));var c=this.__shady_getRootNode();return b.filter(function(a){return a.__shady_getRootNode()==c})}return $c(this,function(b){return mb.call(b,
a)})}}),dd=B.ua?Object.assign({},bd):bd;Object.assign(bd,cd);var ed=E({getElementById:function(a){return""===a?null:$c(this,function(b){return b.id==a},function(a){return!!a})[0]||null}});var fd=E({get activeElement(){var a=B.D?document.__shady_native_activeElement:document.activeElement;if(!a||!a.nodeType)return null;var b=!!C(this);if(!(this===document||b&&this.host!==a&&this.host.__shady_native_contains(a)))return null;for(b=Wc(a);b&&b!==this;)a=b.host,b=Wc(a);return this===document?b?null:a:b===this?a:null}});var gd=document.implementation.createHTMLDocument("inert"),hd=E({get innerHTML(){return ib(this)?Mb("template"===this.localName?this.content:this,function(a){return a.__shady_childNodes}):this.__shady_native_innerHTML},set innerHTML(a){if("template"===this.localName)this.__shady_native_innerHTML=a;else{Sc(this);var b=this.localName||"div";b=this.namespaceURI&&this.namespaceURI!==gd.namespaceURI?gd.createElementNS(this.namespaceURI,b):gd.createElement(b);for(B.D?b.__shady_native_innerHTML=a:b.innerHTML=
a;a=b.__shady_firstChild;)this.__shady_insertBefore(a)}}});var id=E({addEventListener:function(a,b,c){"object"!==typeof c&&(c={capture:!!c});c.ia=this;this.host.__shady_addEventListener(a,b,c)},removeEventListener:function(a,b,c){"object"!==typeof c&&(c={capture:!!c});c.ia=this;this.host.__shady_removeEventListener(a,b,c)}});function jd(a,b){D(a,id,b);D(a,fd,b);D(a,hd,b);D(a,bd,b);B.T&&!b?(D(a,Zc,b),D(a,ed,b)):B.D||(D(a,Yb),D(a,Xb))};var kd={},ld=B.deferConnectionCallbacks&&"loading"===document.readyState,md;function nd(a){var b=[];do b.unshift(a);while(a=a.__shady_parentNode);return b}
function od(a,b,c){if(a!==kd)throw new TypeError("Illegal constructor");this.Da="ShadyRoot";this.host=b;this.mode=c&&c.mode;Hc(b);a=z(b);a.root=this;a.va="closed"!==this.mode?this:null;a=z(this);a.firstChild=a.lastChild=a.parentNode=a.nextSibling=a.previousSibling=null;a.childNodes=[];this.ja=this.M=!1;this.c=this.b=this.a=null;if(B.preferPerformance)for(;a=b.__shady_native_firstChild;)b.__shady_native_removeChild(a);else Xc(this)}function Xc(a){a.M||(a.M=!0,wb(function(){return pd(a)}))}
function pd(a){var b;if(b=a.M){for(var c;a;)a:{a.M&&(c=a),b=a;a=b.host.__shady_getRootNode();if(C(a)&&(b=A(b.host))&&0<b.X)break a;a=void 0}b=c}(c=b)&&c._renderSelf()}
od.prototype._renderSelf=function(){var a=ld;ld=!0;this.M=!1;if(this.a){qd(this);for(var b=0,c;b<this.a.length;b++){c=this.a[b];var d=A(c),e=d.assignedNodes;d.assignedNodes=[];d.N=[];if(d.ka=e)for(d=0;d<e.length;d++){var f=A(e[d]);f.Z=f.assignedSlot;f.assignedSlot===c&&(f.assignedSlot=null)}}for(b=this.host.__shady_firstChild;b;b=b.__shady_nextSibling)rd(this,b);for(b=0;b<this.a.length;b++){c=this.a[b];e=A(c);if(!e.assignedNodes.length)for(d=c.__shady_firstChild;d;d=d.__shady_nextSibling)rd(this,
d,c);(d=(d=A(c.__shady_parentNode))&&d.root)&&(kb(d)||d.M)&&d._renderSelf();sd(this,e.N,e.assignedNodes);if(d=e.ka){for(f=0;f<d.length;f++)A(d[f]).Z=null;e.ka=null;d.length>e.assignedNodes.length&&(e.ba=!0)}e.ba&&(e.ba=!1,td(this,c))}c=this.a;b=[];for(e=0;e<c.length;e++)d=c[e].__shady_parentNode,(f=A(d))&&f.root||!(0>b.indexOf(d))||b.push(d);for(c=0;c<b.length;c++){f=b[c];e=f===this?this.host:f;d=[];f=f.__shady_childNodes;for(var g=0;g<f.length;g++){var h=f[g];if("slot"==h.localName){h=A(h).N;for(var k=
0;k<h.length;k++)d.push(h[k])}else d.push(h)}f=Array.prototype.slice.call(e.__shady_native_childNodes);g=Cc(d,d.length,f,f.length);k=h=0;for(var m=void 0;h<g.length&&(m=g[h]);h++){for(var n=0,p=void 0;n<m.V.length&&(p=m.V[n]);n++)p.__shady_native_parentNode===e&&e.__shady_native_removeChild(p),f.splice(m.index+k,1);k-=m.$}k=0;for(m=void 0;k<g.length&&(m=g[k]);k++)for(h=f[m.index],n=m.index;n<m.index+m.$;n++)p=d[n],e.__shady_native_insertBefore(p,h),f.splice(n,0,p)}}if(!B.preferPerformance&&!this.ja)for(b=
this.host.__shady_childNodes,c=0,e=b.length;c<e;c++)d=b[c],f=A(d),d.__shady_native_parentNode!==this.host||"slot"!==d.localName&&f.assignedSlot||this.host.__shady_native_removeChild(d);this.ja=!0;ld=a;md&&md()};function rd(a,b,c){var d=z(b),e=d.Z;d.Z=null;c||(c=(a=a.b[b.__shady_slot||"__catchall"])&&a[0]);c?(z(c).assignedNodes.push(b),d.assignedSlot=c):d.assignedSlot=void 0;e!==d.assignedSlot&&d.assignedSlot&&(z(d.assignedSlot).ba=!0)}
function sd(a,b,c){for(var d=0,e=void 0;d<c.length&&(e=c[d]);d++)if("slot"==e.localName){var f=A(e).assignedNodes;f&&f.length&&sd(a,b,f)}else b.push(c[d])}function td(a,b){b.__shady_native_dispatchEvent(new Event("slotchange"));b=A(b);b.assignedSlot&&td(a,b.assignedSlot)}
function qd(a){if(a.c&&a.c.length){for(var b=a.c,c,d=0;d<b.length;d++){var e=b[d];Hc(e);var f=e.__shady_parentNode;Hc(f);f=A(f);f.X=(f.X||0)+1;f=ud(e);a.b[f]?(c=c||{},c[f]=!0,a.b[f].push(e)):a.b[f]=[e];a.a.push(e)}if(c)for(var g in c)a.b[g]=vd(a.b[g]);a.c=[]}}function ud(a){var b=a.name||a.getAttribute("name")||"__catchall";return a.Ba=b}
function vd(a){return a.sort(function(a,c){a=nd(a);for(var b=nd(c),e=0;e<a.length;e++){c=a[e];var f=b[e];if(c!==f)return a=Array.from(c.__shady_parentNode.__shady_childNodes),a.indexOf(c)-a.indexOf(f)}})}
function Yc(a,b){if(a.a){qd(a);var c=a.b,d;for(d in c)for(var e=c[d],f=0;f<e.length;f++){var g=e[f];if(sb(b,g)){e.splice(f,1);var h=a.a.indexOf(g);0<=h&&(a.a.splice(h,1),(h=A(g.__shady_parentNode))&&h.X&&h.X--);f--;g=A(g);if(h=g.N)for(var k=0;k<h.length;k++){var m=h[k],n=m.__shady_native_parentNode;n&&n.__shady_native_removeChild(m)}g.N=[];g.assignedNodes=[];h=!0}}return h}}function kb(a){qd(a);return!(!a.a||!a.a.length)}
(function(a){a.__proto__=DocumentFragment.prototype;jd(a,"__shady_");jd(a);Object.defineProperties(a,{nodeType:{value:Node.DOCUMENT_FRAGMENT_NODE,configurable:!0},nodeName:{value:"#document-fragment",configurable:!0},nodeValue:{value:null,configurable:!0}});["localName","namespaceURI","prefix"].forEach(function(b){Object.defineProperty(a,b,{value:void 0,configurable:!0})});["ownerDocument","baseURI","isConnected"].forEach(function(b){Object.defineProperty(a,b,{get:function(){return this.host[b]},
configurable:!0})})})(od.prototype);
if(window.customElements&&B.ma&&!B.preferPerformance){var wd=new Map;md=function(){var a=[];wd.forEach(function(b,c){a.push([c,b])});wd.clear();for(var b=0;b<a.length;b++){var c=a[b][0];a[b][1]?c.za():c.Aa()}};ld&&document.addEventListener("readystatechange",function(){ld=!1;md()},{once:!0});var xd=function(a,b,c){var d=0,e="__isConnected"+d++;if(b||c)a.prototype.connectedCallback=a.prototype.za=function(){ld?wd.set(this,!0):this[e]||(this[e]=!0,b&&b.call(this))},a.prototype.disconnectedCallback=
a.prototype.Aa=function(){ld?this.isConnected||wd.set(this,!1):this[e]&&(this[e]=!1,c&&c.call(this))};return a},define=window.customElements.define;Object.defineProperty(window.CustomElementRegistry.prototype,"define",{value:function(a,b){var c=b.prototype.connectedCallback,d=b.prototype.disconnectedCallback;define.call(window.customElements,a,xd(b,c,d));b.prototype.connectedCallback=c;b.prototype.disconnectedCallback=d}})}function Wc(a){a=a.__shady_getRootNode();if(C(a))return a};function yd(a){this.node=a}r=yd.prototype;r.addEventListener=function(a,b,c){return this.node.__shady_addEventListener(a,b,c)};r.removeEventListener=function(a,b,c){return this.node.__shady_removeEventListener(a,b,c)};r.appendChild=function(a){return this.node.__shady_appendChild(a)};r.insertBefore=function(a,b){return this.node.__shady_insertBefore(a,b)};r.removeChild=function(a){return this.node.__shady_removeChild(a)};r.replaceChild=function(a,b){return this.node.__shady_replaceChild(a,b)};
r.cloneNode=function(a){return this.node.__shady_cloneNode(a)};r.getRootNode=function(a){return this.node.__shady_getRootNode(a)};r.contains=function(a){return this.node.__shady_contains(a)};r.dispatchEvent=function(a){return this.node.__shady_dispatchEvent(a)};r.setAttribute=function(a,b){this.node.__shady_setAttribute(a,b)};r.getAttribute=function(a){return this.node.__shady_native_getAttribute(a)};r.hasAttribute=function(a){return this.node.__shady_native_hasAttribute(a)};r.removeAttribute=function(a){this.node.__shady_removeAttribute(a)};
r.attachShadow=function(a){return this.node.__shady_attachShadow(a)};r.focus=function(){this.node.__shady_native_focus()};r.blur=function(){this.node.__shady_blur()};r.importNode=function(a,b){if(this.node.nodeType===Node.DOCUMENT_NODE)return this.node.__shady_importNode(a,b)};r.getElementById=function(a){if(this.node.nodeType===Node.DOCUMENT_NODE)return this.node.__shady_getElementById(a)};r.querySelector=function(a){return this.node.__shady_querySelector(a)};
r.querySelectorAll=function(a,b){return this.node.__shady_querySelectorAll(a,b)};r.assignedNodes=function(a){if("slot"===this.node.localName)return this.node.__shady_assignedNodes(a)};
t.Object.defineProperties(yd.prototype,{activeElement:{configurable:!0,enumerable:!0,get:function(){if(C(this.node)||this.node.nodeType===Node.DOCUMENT_NODE)return this.node.__shady_activeElement}},_activeElement:{configurable:!0,enumerable:!0,get:function(){return this.activeElement}},host:{configurable:!0,enumerable:!0,get:function(){if(C(this.node))return this.node.host}},parentNode:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_parentNode}},firstChild:{configurable:!0,
enumerable:!0,get:function(){return this.node.__shady_firstChild}},lastChild:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_lastChild}},nextSibling:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_nextSibling}},previousSibling:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_previousSibling}},childNodes:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_childNodes}},parentElement:{configurable:!0,enumerable:!0,
get:function(){return this.node.__shady_parentElement}},firstElementChild:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_firstElementChild}},lastElementChild:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_lastElementChild}},nextElementSibling:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_nextElementSibling}},previousElementSibling:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_previousElementSibling}},
children:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_children}},childElementCount:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_childElementCount}},shadowRoot:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_shadowRoot}},assignedSlot:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_assignedSlot}},isConnected:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_isConnected}},innerHTML:{configurable:!0,
enumerable:!0,get:function(){return this.node.__shady_innerHTML},set:function(a){this.node.__shady_innerHTML=a}},textContent:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_textContent},set:function(a){this.node.__shady_textContent=a}},slot:{configurable:!0,enumerable:!0,get:function(){return this.node.__shady_slot},set:function(a){this.node.__shady_slot=a}}});
Ac.forEach(function(a){Object.defineProperty(yd.prototype,a,{get:function(){return this.node["__shady_"+a]},set:function(b){this.node["__shady_"+a]=b},configurable:!0})});var zd=new WeakMap;function Ad(a){if(C(a)||a instanceof yd)return a;var b=zd.get(a);b||(b=new yd(a),zd.set(a,b));return b};var Bd=E({dispatchEvent:function(a){xb();return this.__shady_native_dispatchEvent(a)},addEventListener:rc,removeEventListener:tc});var Cd=E({get assignedSlot(){var a=this.__shady_parentNode;(a=a&&a.__shady_shadowRoot)&&pd(a);return(a=A(this))&&a.assignedSlot||null}});var Dd=window.document;function Ed(a,b){if("slot"===b)a=a.__shady_parentNode,jb(a)&&Xc(A(a).root);else if("slot"===a.localName&&"name"===b&&(b=Wc(a))){if(b.a){qd(b);var c=a.Ba,d=ud(a);if(d!==c){c=b.b[c];var e=c.indexOf(a);0<=e&&c.splice(e,1);c=b.b[d]||(b.b[d]=[]);c.push(a);1<c.length&&(b.b[d]=vd(c))}}Xc(b)}}
var Fd=E({get previousElementSibling(){var a=A(this);if(a&&void 0!==a.previousSibling){for(a=this.__shady_previousSibling;a&&a.nodeType!==Node.ELEMENT_NODE;)a=a.__shady_previousSibling;return a}return this.__shady_native_previousElementSibling},get nextElementSibling(){var a=A(this);if(a&&void 0!==a.nextSibling){for(a=this.__shady_nextSibling;a&&a.nodeType!==Node.ELEMENT_NODE;)a=a.__shady_nextSibling;return a}return this.__shady_native_nextElementSibling},get slot(){return this.getAttribute("slot")},
set slot(a){this.__shady_setAttribute("slot",a)},get shadowRoot(){var a=A(this);return a&&a.va||null},get className(){return this.getAttribute("class")||""},set className(a){this.__shady_setAttribute("class",a)},setAttribute:function(a,b){if(this.ownerDocument!==Dd)this.__shady_native_setAttribute(a,b);else{var c;(c=Jc())&&"class"===a?(c.setElementClass(this,b),c=!0):c=!1;c||(this.__shady_native_setAttribute(a,b),Ed(this,a))}},removeAttribute:function(a){this.__shady_native_removeAttribute(a);Ed(this,
a)},attachShadow:function(a){if(!this)throw Error("Must provide a host.");if(!a)throw Error("Not enough arguments.");return new od(kd,this,a)}});var Gd=E({blur:function(){var a=A(this);(a=(a=a&&a.root)&&a.activeElement)?a.__shady_blur():this.__shady_native_blur()}});Ac.forEach(function(a){Gd[a]={set:function(b){var c=z(this),d=a.substring(2);c.Y[a]&&this.removeEventListener(d,c.Y[a]);this.__shady_addEventListener(d,b);c.Y[a]=b},get:function(){var b=A(this);return b&&b.Y[a]},configurable:!0}});var Hd=E({assignedNodes:function(a){if("slot"===this.localName){var b=this.__shady_getRootNode();b&&C(b)&&pd(b);return(b=A(this))?(a&&a.flatten?b.N:b.assignedNodes)||[]:[]}}});var Id=window.document,Jd=E({importNode:function(a,b){if(a.ownerDocument!==Id||"template"===a.localName)return this.__shady_native_importNode(a,b);var c=this.__shady_native_importNode(a,!1);if(b){a=a.__shady_childNodes;b=0;for(var d;b<a.length;b++)d=this.__shady_importNode(a[b],!0),c.__shady_appendChild(d)}return c}});var Kd=E({addEventListener:rc.bind(window),removeEventListener:tc.bind(window)});var Ld={};Object.getOwnPropertyDescriptor(HTMLElement.prototype,"parentElement")&&(Ld.parentElement=Zc.parentElement);Object.getOwnPropertyDescriptor(HTMLElement.prototype,"contains")&&(Ld.contains=Zc.contains);Object.getOwnPropertyDescriptor(HTMLElement.prototype,"children")&&(Ld.children=bd.children);Object.getOwnPropertyDescriptor(HTMLElement.prototype,"innerHTML")&&(Ld.innerHTML=hd.innerHTML);Object.getOwnPropertyDescriptor(HTMLElement.prototype,"className")&&(Ld.className=Fd.className);
var Md={EventTarget:[Bd],Node:[Zc,window.EventTarget?null:Bd],Text:[Cd],Element:[Fd,bd,Cd,!B.D||"innerHTML"in Element.prototype?hd:null,window.HTMLSlotElement?null:Hd],HTMLElement:[Gd,Ld],HTMLSlotElement:[Hd],DocumentFragment:[dd,ed],Document:[Jd,dd,ed,fd],Window:[Kd]},Nd=B.D?null:["innerHTML","textContent"];function Od(a){var b=a?null:Nd,c={},d;for(d in Md)c.fa=window[d]&&window[d].prototype,Md[d].forEach(function(c){return function(d){return c.fa&&d&&D(c.fa,d,a,b)}}(c)),c={fa:c.fa}};if(B.ma){var ShadyDOM={inUse:B.ma,patch:function(a){cc(a);bc(a);return a},isShadyRoot:C,enqueue:wb,flush:xb,flushInitial:function(a){!a.ja&&a.M&&pd(a)},settings:B,filterMutations:Fb,observeChildren:Db,unobserveChildren:Eb,deferConnectionCallbacks:B.deferConnectionCallbacks,preferPerformance:B.preferPerformance,handlesDynamicScoping:!0,wrap:B.T?Ad:function(a){return a},Wrapper:yd,composedPath:jc,noPatch:B.T,nativeMethods:Ob,nativeTree:Pb};window.ShadyDOM=ShadyDOM;Wb();Od("__shady_");Object.defineProperty(document,
"_activeElement",fd.activeElement);D(Window.prototype,Kd,"__shady_");B.T||(Od(),zc());uc();window.Event=wc;window.CustomEvent=xc;window.MouseEvent=yc;window.ShadowRoot=od};var Pd=new Set("annotation-xml color-profile font-face font-face-src font-face-uri font-face-format font-face-name missing-glyph".split(" "));function Qd(a){var b=Pd.has(a);a=/^[a-z][.0-9_a-z]*-[\-.0-9_a-z]*$/.test(a);return!b&&a}function J(a){var b=a.isConnected;if(void 0!==b)return b;for(;a&&!(a.__CE_isImportDocument||a instanceof Document);)a=a.parentNode||(window.ShadowRoot&&a instanceof ShadowRoot?a.host:void 0);return!(!a||!(a.__CE_isImportDocument||a instanceof Document))}
function Rd(a,b){for(;b&&b!==a&&!b.nextSibling;)b=b.parentNode;return b&&b!==a?b.nextSibling:null}
function Sd(a,b,c){c=void 0===c?new Set:c;for(var d=a;d;){if(d.nodeType===Node.ELEMENT_NODE){var e=d;b(e);var f=e.localName;if("link"===f&&"import"===e.getAttribute("rel")){d=e.import;if(d instanceof Node&&!c.has(d))for(c.add(d),d=d.firstChild;d;d=d.nextSibling)Sd(d,b,c);d=Rd(a,e);continue}else if("template"===f){d=Rd(a,e);continue}if(e=e.__CE_shadowRoot)for(e=e.firstChild;e;e=e.nextSibling)Sd(e,b,c)}d=d.firstChild?d.firstChild:Rd(a,d)}}function K(a,b,c){a[b]=c};function Td(){this.a=new Map;this.u=new Map;this.f=[];this.c=!1}function Ud(a,b,c){a.a.set(b,c);a.u.set(c.constructorFunction,c)}function Vd(a,b){a.c=!0;a.f.push(b)}function Wd(a,b){a.c&&Sd(b,function(b){return a.b(b)})}Td.prototype.b=function(a){if(this.c&&!a.__CE_patched){a.__CE_patched=!0;for(var b=0;b<this.f.length;b++)this.f[b](a)}};function L(a,b){var c=[];Sd(b,function(a){return c.push(a)});for(b=0;b<c.length;b++){var d=c[b];1===d.__CE_state?a.connectedCallback(d):Xd(a,d)}}
function M(a,b){var c=[];Sd(b,function(a){return c.push(a)});for(b=0;b<c.length;b++){var d=c[b];1===d.__CE_state&&a.disconnectedCallback(d)}}
function N(a,b,c){c=void 0===c?{}:c;var d=c.Za||new Set,e=c.ga||function(b){return Xd(a,b)},f=[];Sd(b,function(b){if("link"===b.localName&&"import"===b.getAttribute("rel")){var c=b.import;c instanceof Node&&(c.__CE_isImportDocument=!0,c.__CE_hasRegistry=!0);c&&"complete"===c.readyState?c.__CE_documentLoadHandled=!0:b.addEventListener("load",function(){var c=b.import;if(!c.__CE_documentLoadHandled){c.__CE_documentLoadHandled=!0;var f=new Set(d);f.delete(c);N(a,c,{Za:f,ga:e})}})}else f.push(b)},d);
if(a.c)for(b=0;b<f.length;b++)a.b(f[b]);for(b=0;b<f.length;b++)e(f[b])}
function Xd(a,b){if(void 0===b.__CE_state){var c=b.ownerDocument;if(c.defaultView||c.__CE_isImportDocument&&c.__CE_hasRegistry)if(c=a.a.get(b.localName)){c.constructionStack.push(b);var d=c.constructorFunction;try{try{if(new d!==b)throw Error("The custom element constructor did not produce the element being upgraded.");}finally{c.constructionStack.pop()}}catch(g){throw b.__CE_state=2,g;}b.__CE_state=1;b.__CE_definition=c;if(c.attributeChangedCallback)for(c=c.observedAttributes,d=0;d<c.length;d++){var e=
c[d],f=b.getAttribute(e);null!==f&&a.attributeChangedCallback(b,e,null,f,null)}J(b)&&a.connectedCallback(b)}}}Td.prototype.connectedCallback=function(a){var b=a.__CE_definition;b.connectedCallback&&b.connectedCallback.call(a)};Td.prototype.disconnectedCallback=function(a){var b=a.__CE_definition;b.disconnectedCallback&&b.disconnectedCallback.call(a)};
Td.prototype.attributeChangedCallback=function(a,b,c,d,e){var f=a.__CE_definition;f.attributeChangedCallback&&-1<f.observedAttributes.indexOf(b)&&f.attributeChangedCallback.call(a,b,c,d,e)};function Yd(a){var b=document;this.b=a;this.a=b;this.O=void 0;N(this.b,this.a);"loading"===this.a.readyState&&(this.O=new MutationObserver(this.c.bind(this)),this.O.observe(this.a,{childList:!0,subtree:!0}))}function Zd(a){a.O&&a.O.disconnect()}Yd.prototype.c=function(a){var b=this.a.readyState;"interactive"!==b&&"complete"!==b||Zd(this);for(b=0;b<a.length;b++)for(var c=a[b].addedNodes,d=0;d<c.length;d++)N(this.b,c[d])};function $d(){var a=this;this.a=this.w=void 0;this.b=new Promise(function(b){a.a=b;a.w&&b(a.w)})}$d.prototype.resolve=function(a){if(this.w)throw Error("Already resolved.");this.w=a;this.a&&this.a(a)};function O(a){this.c=!1;this.a=a;this.F=new Map;this.f=function(a){return a()};this.b=!1;this.u=[];this.ca=new Yd(a)}r=O.prototype;
r.xa=function(a,b){var c=this;if(!(b instanceof Function))throw new TypeError("Custom element constructors must be functions.");if(!Qd(a))throw new SyntaxError("The element name '"+a+"' is not valid.");if(this.a.a.get(a))throw Error("A custom element with name '"+a+"' has already been defined.");if(this.c)throw Error("A custom element is already being defined.");this.c=!0;try{var d=function(a){var b=e[a];if(void 0!==b&&!(b instanceof Function))throw Error("The '"+a+"' callback must be a function.");
return b},e=b.prototype;if(!(e instanceof Object))throw new TypeError("The custom element constructor's prototype is not an object.");var f=d("connectedCallback");var g=d("disconnectedCallback");var h=d("adoptedCallback");var k=d("attributeChangedCallback");var m=b.observedAttributes||[]}catch(n){return}finally{this.c=!1}b={localName:a,constructorFunction:b,connectedCallback:f,disconnectedCallback:g,adoptedCallback:h,attributeChangedCallback:k,observedAttributes:m,constructionStack:[]};Ud(this.a,
a,b);this.u.push(b);this.b||(this.b=!0,this.f(function(){return ae(c)}))};r.ga=function(a){N(this.a,a)};
function ae(a){if(!1!==a.b){a.b=!1;for(var b=a.u,c=[],d=new Map,e=0;e<b.length;e++)d.set(b[e].localName,[]);N(a.a,document,{ga:function(b){if(void 0===b.__CE_state){var e=b.localName,f=d.get(e);f?f.push(b):a.a.a.get(e)&&c.push(b)}}});for(e=0;e<c.length;e++)Xd(a.a,c[e]);for(;0<b.length;){var f=b.shift();e=f.localName;f=d.get(f.localName);for(var g=0;g<f.length;g++)Xd(a.a,f[g]);(e=a.F.get(e))&&e.resolve(void 0)}}}r.get=function(a){if(a=this.a.a.get(a))return a.constructorFunction};
r.ya=function(a){if(!Qd(a))return Promise.reject(new SyntaxError("'"+a+"' is not a valid custom element name."));var b=this.F.get(a);if(b)return b.b;b=new $d;this.F.set(a,b);this.a.a.get(a)&&!this.u.some(function(b){return b.localName===a})&&b.resolve(void 0);return b.b};r.Ra=function(a){Zd(this.ca);var b=this.f;this.f=function(c){return a(function(){return b(c)})}};window.CustomElementRegistry=O;O.prototype.define=O.prototype.xa;O.prototype.upgrade=O.prototype.ga;O.prototype.get=O.prototype.get;
O.prototype.whenDefined=O.prototype.ya;O.prototype.polyfillWrapFlushCallback=O.prototype.Ra;var be=window.Document.prototype.createElement,ce=window.Document.prototype.createElementNS,de=window.Document.prototype.importNode,ee=window.Document.prototype.prepend,fe=window.Document.prototype.append,ge=window.DocumentFragment.prototype.prepend,he=window.DocumentFragment.prototype.append,ie=window.Node.prototype.cloneNode,je=window.Node.prototype.appendChild,ke=window.Node.prototype.insertBefore,le=window.Node.prototype.removeChild,me=window.Node.prototype.replaceChild,ne=Object.getOwnPropertyDescriptor(window.Node.prototype,
"textContent"),oe=window.Element.prototype.attachShadow,pe=Object.getOwnPropertyDescriptor(window.Element.prototype,"innerHTML"),qe=window.Element.prototype.getAttribute,re=window.Element.prototype.setAttribute,se=window.Element.prototype.removeAttribute,te=window.Element.prototype.getAttributeNS,ue=window.Element.prototype.setAttributeNS,ve=window.Element.prototype.removeAttributeNS,we=window.Element.prototype.insertAdjacentElement,xe=window.Element.prototype.insertAdjacentHTML,ye=window.Element.prototype.prepend,
ze=window.Element.prototype.append,Ae=window.Element.prototype.before,Be=window.Element.prototype.after,Ce=window.Element.prototype.replaceWith,De=window.Element.prototype.remove,Ee=window.HTMLElement,Fe=Object.getOwnPropertyDescriptor(window.HTMLElement.prototype,"innerHTML"),Ge=window.HTMLElement.prototype.insertAdjacentElement,He=window.HTMLElement.prototype.insertAdjacentHTML;var Ie=new function(){};function Je(){var a=Ke;window.HTMLElement=function(){function b(){var b=this.constructor,d=a.u.get(b);if(!d)throw Error("The custom element being constructed was not registered with `customElements`.");var e=d.constructionStack;if(0===e.length)return e=be.call(document,d.localName),Object.setPrototypeOf(e,b.prototype),e.__CE_state=1,e.__CE_definition=d,a.b(e),e;d=e.length-1;var f=e[d];if(f===Ie)throw Error("The HTMLElement constructor was either called reentrantly for this constructor or called multiple times.");
e[d]=Ie;Object.setPrototypeOf(f,b.prototype);a.b(f);return f}b.prototype=Ee.prototype;Object.defineProperty(b.prototype,"constructor",{writable:!0,configurable:!0,enumerable:!1,value:b});return b}()};function Le(a,b,c){function d(b){return function(c){for(var d=[],e=0;e<arguments.length;++e)d[e]=arguments[e];e=[];for(var f=[],m=0;m<d.length;m++){var n=d[m];n instanceof Element&&J(n)&&f.push(n);if(n instanceof DocumentFragment)for(n=n.firstChild;n;n=n.nextSibling)e.push(n);else e.push(n)}b.apply(this,d);for(d=0;d<f.length;d++)M(a,f[d]);if(J(this))for(d=0;d<e.length;d++)f=e[d],f instanceof Element&&L(a,f)}}void 0!==c.ea&&(b.prepend=d(c.ea));void 0!==c.append&&(b.append=d(c.append))};function Me(){var a=Ke;K(Document.prototype,"createElement",function(b){if(this.__CE_hasRegistry){var c=a.a.get(b);if(c)return new c.constructorFunction}b=be.call(this,b);a.b(b);return b});K(Document.prototype,"importNode",function(b,c){b=de.call(this,b,!!c);this.__CE_hasRegistry?N(a,b):Wd(a,b);return b});K(Document.prototype,"createElementNS",function(b,c){if(this.__CE_hasRegistry&&(null===b||"http://www.w3.org/1999/xhtml"===b)){var d=a.a.get(c);if(d)return new d.constructorFunction}b=ce.call(this,
b,c);a.b(b);return b});Le(a,Document.prototype,{ea:ee,append:fe})};function Ne(){function a(a,d){Object.defineProperty(a,"textContent",{enumerable:d.enumerable,configurable:!0,get:d.get,set:function(a){if(this.nodeType===Node.TEXT_NODE)d.set.call(this,a);else{var c=void 0;if(this.firstChild){var e=this.childNodes,h=e.length;if(0<h&&J(this)){c=Array(h);for(var k=0;k<h;k++)c[k]=e[k]}}d.set.call(this,a);if(c)for(a=0;a<c.length;a++)M(b,c[a])}}})}var b=Ke;K(Node.prototype,"insertBefore",function(a,d){if(a instanceof DocumentFragment){var c=Array.prototype.slice.apply(a.childNodes);
a=ke.call(this,a,d);if(J(this))for(d=0;d<c.length;d++)L(b,c[d]);return a}c=J(a);d=ke.call(this,a,d);c&&M(b,a);J(this)&&L(b,a);return d});K(Node.prototype,"appendChild",function(a){if(a instanceof DocumentFragment){var c=Array.prototype.slice.apply(a.childNodes);a=je.call(this,a);if(J(this))for(var e=0;e<c.length;e++)L(b,c[e]);return a}c=J(a);e=je.call(this,a);c&&M(b,a);J(this)&&L(b,a);return e});K(Node.prototype,"cloneNode",function(a){a=ie.call(this,!!a);this.ownerDocument.__CE_hasRegistry?N(b,a):
Wd(b,a);return a});K(Node.prototype,"removeChild",function(a){var c=J(a),e=le.call(this,a);c&&M(b,a);return e});K(Node.prototype,"replaceChild",function(a,d){if(a instanceof DocumentFragment){var c=Array.prototype.slice.apply(a.childNodes);a=me.call(this,a,d);if(J(this))for(M(b,d),d=0;d<c.length;d++)L(b,c[d]);return a}c=J(a);var f=me.call(this,a,d),g=J(this);g&&M(b,d);c&&M(b,a);g&&L(b,a);return f});ne&&ne.get?a(Node.prototype,ne):Vd(b,function(b){a(b,{enumerable:!0,configurable:!0,get:function(){for(var a=
[],b=0;b<this.childNodes.length;b++)a.push(this.childNodes[b].textContent);return a.join("")},set:function(a){for(;this.firstChild;)le.call(this,this.firstChild);je.call(this,document.createTextNode(a))}})})};function Oe(a){function b(b){return function(c){for(var d=[],e=0;e<arguments.length;++e)d[e]=arguments[e];e=[];for(var h=[],k=0;k<d.length;k++){var m=d[k];m instanceof Element&&J(m)&&h.push(m);if(m instanceof DocumentFragment)for(m=m.firstChild;m;m=m.nextSibling)e.push(m);else e.push(m)}b.apply(this,d);for(d=0;d<h.length;d++)M(a,h[d]);if(J(this))for(d=0;d<e.length;d++)h=e[d],h instanceof Element&&L(a,h)}}var c=Element.prototype;void 0!==Ae&&(c.before=b(Ae));void 0!==Ae&&(c.after=b(Be));void 0!==Ce&&
K(c,"replaceWith",function(b){for(var c=[],d=0;d<arguments.length;++d)c[d]=arguments[d];d=[];for(var g=[],h=0;h<c.length;h++){var k=c[h];k instanceof Element&&J(k)&&g.push(k);if(k instanceof DocumentFragment)for(k=k.firstChild;k;k=k.nextSibling)d.push(k);else d.push(k)}h=J(this);Ce.apply(this,c);for(c=0;c<g.length;c++)M(a,g[c]);if(h)for(M(a,this),c=0;c<d.length;c++)g=d[c],g instanceof Element&&L(a,g)});void 0!==De&&K(c,"remove",function(){var b=J(this);De.call(this);b&&M(a,this)})};function Pe(){function a(a,b){Object.defineProperty(a,"innerHTML",{enumerable:b.enumerable,configurable:!0,get:b.get,set:function(a){var c=this,e=void 0;J(this)&&(e=[],Sd(this,function(a){a!==c&&e.push(a)}));b.set.call(this,a);if(e)for(var f=0;f<e.length;f++){var g=e[f];1===g.__CE_state&&d.disconnectedCallback(g)}this.ownerDocument.__CE_hasRegistry?N(d,this):Wd(d,this);return a}})}function b(a,b){K(a,"insertAdjacentElement",function(a,c){var e=J(c);a=b.call(this,a,c);e&&M(d,c);J(a)&&L(d,c);return a})}
function c(a,b){function c(a,b){for(var c=[];a!==b;a=a.nextSibling)c.push(a);for(b=0;b<c.length;b++)N(d,c[b])}K(a,"insertAdjacentHTML",function(a,d){a=a.toLowerCase();if("beforebegin"===a){var e=this.previousSibling;b.call(this,a,d);c(e||this.parentNode.firstChild,this)}else if("afterbegin"===a)e=this.firstChild,b.call(this,a,d),c(this.firstChild,e);else if("beforeend"===a)e=this.lastChild,b.call(this,a,d),c(e||this.firstChild,null);else if("afterend"===a)e=this.nextSibling,b.call(this,a,d),c(this.nextSibling,
e);else throw new SyntaxError("The value provided ("+String(a)+") is not one of 'beforebegin', 'afterbegin', 'beforeend', or 'afterend'.");})}var d=Ke;oe&&K(Element.prototype,"attachShadow",function(a){return this.__CE_shadowRoot=a=oe.call(this,a)});pe&&pe.get?a(Element.prototype,pe):Fe&&Fe.get?a(HTMLElement.prototype,Fe):Vd(d,function(b){a(b,{enumerable:!0,configurable:!0,get:function(){return ie.call(this,!0).innerHTML},set:function(a){var b="template"===this.localName,c=b?this.content:this,d=ce.call(document,
this.namespaceURI,this.localName);for(d.innerHTML=a;0<c.childNodes.length;)le.call(c,c.childNodes[0]);for(a=b?d.content:d;0<a.childNodes.length;)je.call(c,a.childNodes[0])}})});K(Element.prototype,"setAttribute",function(a,b){if(1!==this.__CE_state)return re.call(this,a,b);var c=qe.call(this,a);re.call(this,a,b);b=qe.call(this,a);d.attributeChangedCallback(this,a,c,b,null)});K(Element.prototype,"setAttributeNS",function(a,b,c){if(1!==this.__CE_state)return ue.call(this,a,b,c);var e=te.call(this,a,
b);ue.call(this,a,b,c);c=te.call(this,a,b);d.attributeChangedCallback(this,b,e,c,a)});K(Element.prototype,"removeAttribute",function(a){if(1!==this.__CE_state)return se.call(this,a);var b=qe.call(this,a);se.call(this,a);null!==b&&d.attributeChangedCallback(this,a,b,null,null)});K(Element.prototype,"removeAttributeNS",function(a,b){if(1!==this.__CE_state)return ve.call(this,a,b);var c=te.call(this,a,b);ve.call(this,a,b);var e=te.call(this,a,b);c!==e&&d.attributeChangedCallback(this,b,c,e,a)});Ge?b(HTMLElement.prototype,
Ge):we?b(Element.prototype,we):console.warn("Custom Elements: `Element#insertAdjacentElement` was not patched.");He?c(HTMLElement.prototype,He):xe?c(Element.prototype,xe):console.warn("Custom Elements: `Element#insertAdjacentHTML` was not patched.");Le(d,Element.prototype,{ea:ye,append:ze});Oe(d)};var Qe=window.customElements;if(!Qe||Qe.forcePolyfill||"function"!=typeof Qe.define||"function"!=typeof Qe.get){var Ke=new Td;Je();Me();Le(Ke,DocumentFragment.prototype,{ea:ge,append:he});Ne();Pe();document.__CE_hasRegistry=!0;var customElements=new O(Ke);Object.defineProperty(window,"customElements",{configurable:!0,enumerable:!0,value:customElements})};function Re(){this.end=this.start=0;this.rules=this.parent=this.previous=null;this.cssText=this.parsedCssText="";this.atRule=!1;this.type=0;this.parsedSelector=this.selector=this.keyframesName=""}
function Se(a){a=a.replace(Te,"").replace(Ue,"");var b=Ve,c=a,d=new Re;d.start=0;d.end=c.length;for(var e=d,f=0,g=c.length;f<g;f++)if("{"===c[f]){e.rules||(e.rules=[]);var h=e,k=h.rules[h.rules.length-1]||null;e=new Re;e.start=f+1;e.parent=h;e.previous=k;h.rules.push(e)}else"}"===c[f]&&(e.end=f+1,e=e.parent||d);return b(d,a)}
function Ve(a,b){var c=b.substring(a.start,a.end-1);a.parsedCssText=a.cssText=c.trim();a.parent&&(c=b.substring(a.previous?a.previous.end:a.parent.start,a.start-1),c=We(c),c=c.replace(Xe," "),c=c.substring(c.lastIndexOf(";")+1),c=a.parsedSelector=a.selector=c.trim(),a.atRule=0===c.indexOf("@"),a.atRule?0===c.indexOf("@media")?a.type=Ye:c.match(Ze)&&(a.type=$e,a.keyframesName=a.selector.split(Xe).pop()):a.type=0===c.indexOf("--")?af:bf);if(c=a.rules)for(var d=0,e=c.length,f=void 0;d<e&&(f=c[d]);d++)Ve(f,
b);return a}function We(a){return a.replace(/\\([0-9a-f]{1,6})\s/gi,function(a,c){a=c;for(c=6-a.length;c--;)a="0"+a;return"\\"+a})}
function cf(a,b,c){c=void 0===c?"":c;var d="";if(a.cssText||a.rules){var e=a.rules,f;if(f=e)f=e[0],f=!(f&&f.selector&&0===f.selector.indexOf("--"));if(f){f=0;for(var g=e.length,h=void 0;f<g&&(h=e[f]);f++)d=cf(h,b,d)}else b?b=a.cssText:(b=a.cssText,b=b.replace(df,"").replace(ef,""),b=b.replace(ff,"").replace(gf,"")),(d=b.trim())&&(d="  "+d+"\n")}d&&(a.selector&&(c+=a.selector+" {\n"),c+=d,a.selector&&(c+="}\n\n"));return c}
var bf=1,$e=7,Ye=4,af=1E3,Te=/\/\*[^*]*\*+([^/*][^*]*\*+)*\//gim,Ue=/@import[^;]*;/gim,df=/(?:^[^;\-\s}]+)?--[^;{}]*?:[^{};]*?(?:[;\n]|$)/gim,ef=/(?:^[^;\-\s}]+)?--[^;{}]*?:[^{};]*?{[^}]*?}(?:[;\n]|$)?/gim,ff=/@apply\s*\(?[^);]*\)?\s*(?:[;\n]|$)?/gim,gf=/[^;:]*?:[^;]*?var\([^;]*\)(?:[;\n]|$)?/gim,Ze=/^@[^\s]*keyframes/,Xe=/\s+/g;var P=!(window.ShadyDOM&&window.ShadyDOM.inUse),hf;function jf(a){hf=a&&a.shimcssproperties?!1:P||!(navigator.userAgent.match(/AppleWebKit\/601|Edge\/15/)||!window.CSS||!CSS.supports||!CSS.supports("box-shadow","0 0 0 var(--foo)"))}var kf;window.ShadyCSS&&void 0!==window.ShadyCSS.cssBuild&&(kf=window.ShadyCSS.cssBuild);var lf=!(!window.ShadyCSS||!window.ShadyCSS.disableRuntime);
window.ShadyCSS&&void 0!==window.ShadyCSS.nativeCss?hf=window.ShadyCSS.nativeCss:window.ShadyCSS?(jf(window.ShadyCSS),window.ShadyCSS=void 0):jf(window.WebComponents&&window.WebComponents.flags);var R=hf,of=kf;var pf=/(?:^|[;\s{]\s*)(--[\w-]*?)\s*:\s*(?:((?:'(?:\\'|.)*?'|"(?:\\"|.)*?"|\([^)]*?\)|[^};{])+)|\{([^}]*)\}(?:(?=[;\s}])|$))/gi,qf=/(?:^|\W+)@apply\s*\(?([^);\n]*)\)?/gi,rf=/(--[\w-]+)\s*([:,;)]|$)/gi,sf=/(animation\s*:)|(animation-name\s*:)/,tf=/@media\s(.*)/,uf=/\{[^}]*\}/g;var vf=new Set;function wf(a,b){if(!a)return"";"string"===typeof a&&(a=Se(a));b&&xf(a,b);return cf(a,R)}function yf(a){!a.__cssRules&&a.textContent&&(a.__cssRules=Se(a.textContent));return a.__cssRules||null}function zf(a){return!!a.parent&&a.parent.type===$e}function xf(a,b,c,d){if(a){var e=!1,f=a.type;if(d&&f===Ye){var g=a.selector.match(tf);g&&(window.matchMedia(g[1]).matches||(e=!0))}f===bf?b(a):c&&f===$e?c(a):f===af&&(e=!0);if((a=a.rules)&&!e)for(e=0,f=a.length,g=void 0;e<f&&(g=a[e]);e++)xf(g,b,c,d)}}
function Af(a,b,c,d){var e=document.createElement("style");b&&e.setAttribute("scope",b);e.textContent=a;Bf(e,c,d);return e}var Cf=null;function Df(a){a=document.createComment(" Shady DOM styles for "+a+" ");var b=document.head;b.insertBefore(a,(Cf?Cf.nextSibling:null)||b.firstChild);return Cf=a}function Bf(a,b,c){b=b||document.head;b.insertBefore(a,c&&c.nextSibling||b.firstChild);Cf?a.compareDocumentPosition(Cf)===Node.DOCUMENT_POSITION_PRECEDING&&(Cf=a):Cf=a}
function Ef(a,b){for(var c=0,d=a.length;b<d;b++)if("("===a[b])c++;else if(")"===a[b]&&0===--c)return b;return-1}function Ff(a,b){var c=a.indexOf("var(");if(-1===c)return b(a,"","","");var d=Ef(a,c+3),e=a.substring(c+4,d);c=a.substring(0,c);a=Ff(a.substring(d+1),b);d=e.indexOf(",");return-1===d?b(c,e.trim(),"",a):b(c,e.substring(0,d).trim(),e.substring(d+1).trim(),a)}function Gf(a,b){P?a.setAttribute("class",b):window.ShadyDOM.nativeMethods.setAttribute.call(a,"class",b)}
var Hf=window.ShadyDOM&&window.ShadyDOM.wrap||function(a){return a};function If(a){var b=a.localName,c="";b?-1<b.indexOf("-")||(c=b,b=a.getAttribute&&a.getAttribute("is")||""):(b=a.is,c=a.extends);return{is:b,W:c}}function Jf(a){for(var b=[],c="",d=0;0<=d&&d<a.length;d++)if("("===a[d]){var e=Ef(a,d);c+=a.slice(d,e+1);d=e}else","===a[d]?(b.push(c),c=""):c+=a[d];c&&b.push(c);return b}
function Kf(a){if(void 0!==of)return of;if(void 0===a.__cssBuild){var b=a.getAttribute("css-build");if(b)a.__cssBuild=b;else{a:{b="template"===a.localName?a.content.firstChild:a.firstChild;if(b instanceof Comment&&(b=b.textContent.trim().split(":"),"css-build"===b[0])){b=b[1];break a}b=""}if(""!==b){var c="template"===a.localName?a.content.firstChild:a.firstChild;c.parentNode.removeChild(c)}a.__cssBuild=b}}return a.__cssBuild||""}
function Lf(a){a=void 0===a?"":a;return""!==a&&R?P?"shadow"===a:"shady"===a:!1};function Mf(){}function Nf(a,b){Of(S,a,function(a){Pf(a,b||"")})}function Of(a,b,c){b.nodeType===Node.ELEMENT_NODE&&c(b);var d;"template"===b.localName?d=(b.content||b._content||b).childNodes:d=b.children||b.childNodes;if(d)for(b=0;b<d.length;b++)Of(a,d[b],c)}
function Pf(a,b,c){if(b)if(a.classList)c?(a.classList.remove("style-scope"),a.classList.remove(b)):(a.classList.add("style-scope"),a.classList.add(b));else if(a.getAttribute){var d=a.getAttribute("class");c?d&&(b=d.replace("style-scope","").replace(b,""),Gf(a,b)):Gf(a,(d?d+" ":"")+"style-scope "+b)}}function Qf(a,b,c){Of(S,a,function(a){Pf(a,b,!0);Pf(a,c)})}function Rf(a,b){Of(S,a,function(a){Pf(a,b||"",!0)})}
function Sf(a,b,c,d,e){var f=S;e=void 0===e?"":e;""===e&&(P||"shady"===(void 0===d?"":d)?e=wf(b,c):(a=If(a),e=Tf(f,b,a.is,a.W,c)+"\n\n"));return e.trim()}function Tf(a,b,c,d,e){var f=Uf(c,d);c=c?"."+c:"";return wf(b,function(b){b.c||(b.selector=b.C=Vf(a,b,a.b,c,f),b.c=!0);e&&e(b,c,f)})}function Uf(a,b){return b?"[is="+a+"]":a}
function Vf(a,b,c,d,e){var f=Jf(b.selector);if(!zf(b)){b=0;for(var g=f.length,h=void 0;b<g&&(h=f[b]);b++)f[b]=c.call(a,h,d,e)}return f.filter(function(a){return!!a}).join(",")}function Wf(a){return a.replace(Xf,function(a,c,d){-1<d.indexOf("+")?d=d.replace(/\+/g,"___"):-1<d.indexOf("___")&&(d=d.replace(/___/g,"+"));return":"+c+"("+d+")"})}
function Yf(a){for(var b=[],c;c=a.match(Zf);){var d=c.index,e=Ef(a,d);if(-1===e)throw Error(c.input+" selector missing ')'");c=a.slice(d,e+1);a=a.replace(c,"\ue000");b.push(c)}return{pa:a,matches:b}}function $f(a,b){var c=a.split("\ue000");return b.reduce(function(a,b,f){return a+b+c[f+1]},c[0])}
Mf.prototype.b=function(a,b,c){var d=!1;a=a.trim();var e=Xf.test(a);e&&(a=a.replace(Xf,function(a,b,c){return":"+b+"("+c.replace(/\s/g,"")+")"}),a=Wf(a));var f=Zf.test(a);if(f){var g=Yf(a);a=g.pa;g=g.matches}a=a.replace(ag,":host $1");a=a.replace(bg,function(a,e,f){d||(a=cg(f,e,b,c),d=d||a.stop,e=a.Ha,f=a.value);return e+f});f&&(a=$f(a,g));e&&(a=Wf(a));return a};
function cg(a,b,c,d){var e=a.indexOf("::slotted");0<=a.indexOf(":host")?a=dg(a,d):0!==e&&(a=c?eg(a,c):a);c=!1;0<=e&&(b="",c=!0);if(c){var f=!0;c&&(a=a.replace(fg,function(a,b){return" > "+b}))}a=a.replace(gg,function(a,b,c){return'[dir="'+c+'"] '+b+", "+b+'[dir="'+c+'"]'});return{value:a,Ha:b,stop:f}}
function eg(a,b){a=a.split(/(\[.+?\])/);for(var c=[],d=0;d<a.length;d++)if(1===d%2)c.push(a[d]);else{var e=a[d];if(""!==e||d!==a.length-1)e=e.split(":"),e[0]+=b,c.push(e.join(":"))}return c.join("")}function dg(a,b){var c=a.match(hg);return(c=c&&c[2].trim()||"")?c[0].match(ig)?a.replace(hg,function(a,c,f){return b+f}):c.split(ig)[0]===b?c:"should_not_match":a.replace(":host",b)}function jg(a){":root"===a.selector&&(a.selector="html")}
Mf.prototype.c=function(a){return a.match(":host")?"":a.match("::slotted")?this.b(a,":not(.style-scope)"):eg(a.trim(),":not(.style-scope)")};t.Object.defineProperties(Mf.prototype,{a:{configurable:!0,enumerable:!0,get:function(){return"style-scope"}}});
var Xf=/:(nth[-\w]+)\(([^)]+)\)/,bg=/(^|[\s>+~]+)((?:\[.+?\]|[^\s>+~=[])+)/g,ig=/[[.:#*]/,ag=/^(::slotted)/,hg=/(:host)(?:\(((?:\([^)(]*\)|[^)(]*)+?)\))/,fg=/(?:::slotted)(?:\(((?:\([^)(]*\)|[^)(]*)+?)\))/,gg=/(.*):dir\((?:(ltr|rtl))\)/,Zf=/:(?:matches|any|-(?:webkit|moz)-any)/,S=new Mf;function kg(a,b,c,d,e){this.L=a||null;this.b=b||null;this.na=c||[];this.G=null;this.cssBuild=e||"";this.W=d||"";this.a=this.H=this.K=null}function U(a){return a?a.__styleInfo:null}function lg(a,b){return a.__styleInfo=b}kg.prototype.c=function(){return this.L};kg.prototype._getStyleRules=kg.prototype.c;function mg(a){var b=this.matches||this.matchesSelector||this.mozMatchesSelector||this.msMatchesSelector||this.oMatchesSelector||this.webkitMatchesSelector;return b&&b.call(this,a)}var ng=navigator.userAgent.match("Trident");function og(){}function pg(a){var b={},c=[],d=0;xf(a,function(a){qg(a);a.index=d++;a=a.A.cssText;for(var c;c=rf.exec(a);){var e=c[1];":"!==c[2]&&(b[e]=!0)}},function(a){c.push(a)});a.b=c;a=[];for(var e in b)a.push(e);return a}
function qg(a){if(!a.A){var b={},c={};rg(a,c)&&(b.J=c,a.rules=null);b.cssText=a.parsedCssText.replace(uf,"").replace(pf,"");a.A=b}}function rg(a,b){var c=a.A;if(c){if(c.J)return Object.assign(b,c.J),!0}else{c=a.parsedCssText;for(var d;a=pf.exec(c);){d=(a[2]||a[3]).trim();if("inherit"!==d||"unset"!==d)b[a[1].trim()]=d;d=!0}return d}}
function sg(a,b,c){b&&(b=0<=b.indexOf(";")?tg(a,b,c):Ff(b,function(b,e,f,g){if(!e)return b+g;(e=sg(a,c[e],c))&&"initial"!==e?"apply-shim-inherit"===e&&(e="inherit"):e=sg(a,c[f]||f,c)||f;return b+(e||"")+g}));return b&&b.trim()||""}
function tg(a,b,c){b=b.split(";");for(var d=0,e,f;d<b.length;d++)if(e=b[d]){qf.lastIndex=0;if(f=qf.exec(e))e=sg(a,c[f[1]],c);else if(f=e.indexOf(":"),-1!==f){var g=e.substring(f);g=g.trim();g=sg(a,g,c)||g;e=e.substring(0,f)+g}b[d]=e&&e.lastIndexOf(";")===e.length-1?e.slice(0,-1):e||""}return b.join(";")}
function ug(a,b){var c={},d=[];xf(a,function(a){a.A||qg(a);var e=a.C||a.parsedSelector;b&&a.A.J&&e&&mg.call(b,e)&&(rg(a,c),a=a.index,e=parseInt(a/32,10),d[e]=(d[e]||0)|1<<a%32)},null,!0);return{J:c,key:d}}
function vg(a,b,c,d){b.A||qg(b);if(b.A.J){var e=If(a);a=e.is;e=e.W;e=a?Uf(a,e):"html";var f=b.parsedSelector,g=":host > *"===f||"html"===f,h=0===f.indexOf(":host")&&!g;"shady"===c&&(g=f===e+" > *."+e||-1!==f.indexOf("html"),h=!g&&0===f.indexOf(e));if(g||h)c=e,h&&(b.C||(b.C=Vf(S,b,S.b,a?"."+a:"",e)),c=b.C||e),d({pa:c,Oa:h,ab:g})}}function wg(a,b,c){var d={},e={};xf(b,function(b){vg(a,b,c,function(c){mg.call(a._element||a,c.pa)&&(c.Oa?rg(b,d):rg(b,e))})},null,!0);return{Ta:e,Ma:d}}
function xg(a,b,c,d){var e=If(b),f=Uf(e.is,e.W),g=new RegExp("(?:^|[^.#[:])"+(b.extends?"\\"+f.slice(0,-1)+"\\]":f)+"($|[.:[\\s>+~])"),h=U(b);e=h.L;h=h.cssBuild;var k=yg(e,d);return Sf(b,e,function(b){var e="";b.A||qg(b);b.A.cssText&&(e=tg(a,b.A.cssText,c));b.cssText=e;if(!P&&!zf(b)&&b.cssText){var h=e=b.cssText;null==b.ta&&(b.ta=sf.test(e));if(b.ta)if(null==b.da){b.da=[];for(var m in k)h=k[m],h=h(e),e!==h&&(e=h,b.da.push(m))}else{for(m=0;m<b.da.length;++m)h=k[b.da[m]],e=h(e);h=e}b.cssText=h;b.C=
b.C||b.selector;e="."+d;m=Jf(b.C);h=0;for(var u=m.length,x=void 0;h<u&&(x=m[h]);h++)m[h]=x.match(g)?x.replace(f,e):e+" "+x;b.selector=m.join(",")}},h)}function yg(a,b){a=a.b;var c={};if(!P&&a)for(var d=0,e=a[d];d<a.length;e=a[++d]){var f=e,g=b;f.f=new RegExp("\\b"+f.keyframesName+"(?!\\B|-)","g");f.a=f.keyframesName+"-"+g;f.C=f.C||f.selector;f.selector=f.C.replace(f.keyframesName,f.a);c[e.keyframesName]=zg(e)}return c}function zg(a){return function(b){return b.replace(a.f,a.a)}}
function Ag(a,b){var c=Bg,d=yf(a);a.textContent=wf(d,function(a){var d=a.cssText=a.parsedCssText;a.A&&a.A.cssText&&(d=d.replace(df,"").replace(ef,""),a.cssText=tg(c,d,b))})}t.Object.defineProperties(og.prototype,{a:{configurable:!0,enumerable:!0,get:function(){return"x-scope"}}});var Bg=new og;var Cg={},Dg=window.customElements;if(Dg&&!P&&!lf){var Eg=Dg.define;Dg.define=function(a,b,c){Cg[a]||(Cg[a]=Df(a));Eg.call(Dg,a,b,c)}};function Fg(){this.cache={}}Fg.prototype.store=function(a,b,c,d){var e=this.cache[a]||[];e.push({J:b,styleElement:c,H:d});100<e.length&&e.shift();this.cache[a]=e};function Gg(){}var Hg=new RegExp(S.a+"\\s*([^\\s]*)");function Ig(a){return(a=(a.classList&&a.classList.value?a.classList.value:a.getAttribute("class")||"").match(Hg))?a[1]:""}function Jg(a){var b=Hf(a).getRootNode();return b===a||b===a.ownerDocument?"":(a=b.host)?If(a).is:""}
function Kg(a){for(var b=0;b<a.length;b++){var c=a[b];if(c.target!==document.documentElement&&c.target!==document.head)for(var d=0;d<c.addedNodes.length;d++){var e=c.addedNodes[d];if(e.nodeType===Node.ELEMENT_NODE){var f=e.getRootNode(),g=Ig(e);if(g&&f===e.ownerDocument&&("style"!==e.localName&&"template"!==e.localName||""===Kf(e)))Rf(e,g);else if(f instanceof ShadowRoot)for(f=Jg(e),f!==g&&Qf(e,g,f),e=window.ShadyDOM.nativeMethods.querySelectorAll.call(e,":not(."+S.a+")"),g=0;g<e.length;g++){f=e[g];
var h=Jg(f);h&&Pf(f,h)}}}}}
if(!(P||window.ShadyDOM&&window.ShadyDOM.handlesDynamicScoping)){var Lg=new MutationObserver(Kg),Mg=function(a){Lg.observe(a,{childList:!0,subtree:!0})};if(window.customElements&&!window.customElements.polyfillWrapFlushCallback)Mg(document);else{var Ng=function(){Mg(document.body)};window.HTMLImports?window.HTMLImports.whenReady(Ng):requestAnimationFrame(function(){if("loading"===document.readyState){var a=function(){Ng();document.removeEventListener("readystatechange",a)};document.addEventListener("readystatechange",
a)}else Ng()})}Gg=function(){Kg(Lg.takeRecords())}}var Og=Gg;var Pg={};var Qg=Promise.resolve();function Rg(a){if(a=Pg[a])a._applyShimCurrentVersion=a._applyShimCurrentVersion||0,a._applyShimValidatingVersion=a._applyShimValidatingVersion||0,a._applyShimNextVersion=(a._applyShimNextVersion||0)+1}function Sg(a){return a._applyShimCurrentVersion===a._applyShimNextVersion}function Tg(a){a._applyShimValidatingVersion=a._applyShimNextVersion;a._validating||(a._validating=!0,Qg.then(function(){a._applyShimCurrentVersion=a._applyShimNextVersion;a._validating=!1}))};var Ug={},Vg=new Fg;function V(){this.F={};this.c=document.documentElement;var a=new Re;a.rules=[];this.f=lg(this.c,new kg(a));this.u=!1;this.b=this.a=null}r=V.prototype;r.flush=function(){Og()};r.Ka=function(a){return yf(a)};r.Xa=function(a){return wf(a)};r.prepareTemplate=function(a,b,c){this.prepareTemplateDom(a,b);this.prepareTemplateStyles(a,b,c)};
r.prepareTemplateStyles=function(a,b,c){if(!a._prepared&&!lf){P||Cg[b]||(Cg[b]=Df(b));a._prepared=!0;a.name=b;a.extends=c;Pg[b]=a;var d=Kf(a),e=Lf(d);c={is:b,extends:c};for(var f=[],g=a.content.querySelectorAll("style"),h=0;h<g.length;h++){var k=g[h];if(k.hasAttribute("shady-unscoped")){if(!P){var m=k.textContent;vf.has(m)||(vf.add(m),m=k.cloneNode(!0),document.head.appendChild(m));k.parentNode.removeChild(k)}}else f.push(k.textContent),k.parentNode.removeChild(k)}f=f.join("").trim()+(Ug[b]||"");
Wg(this);if(!e){if(g=!d)g=qf.test(f)||pf.test(f),qf.lastIndex=0,pf.lastIndex=0;h=Se(f);g&&R&&this.a&&this.a.transformRules(h,b);a._styleAst=h}g=[];R||(g=pg(a._styleAst));if(!g.length||R)h=P?a.content:null,b=Cg[b]||null,d=Sf(c,a._styleAst,null,d,e?f:""),d=d.length?Af(d,c.is,h,b):null,a._style=d;a.a=g}};r.Sa=function(a,b){Ug[b]=a.join(" ")};r.prepareTemplateDom=function(a,b){if(!lf){var c=Kf(a);P||"shady"===c||a._domPrepared||(a._domPrepared=!0,Nf(a.content,b))}};
function Xg(a){var b=If(a),c=b.is;b=b.W;var d=Cg[c]||null,e=Pg[c];if(e){c=e._styleAst;var f=e.a;e=Kf(e);b=new kg(c,d,f,b,e);lg(a,b);return b}}function Yg(a){!a.b&&window.ShadyCSS&&window.ShadyCSS.CustomStyleInterface&&(a.b=window.ShadyCSS.CustomStyleInterface,a.b.transformCallback=function(b){a.wa(b)},a.b.validateCallback=function(){requestAnimationFrame(function(){(a.b.enqueued||a.u)&&a.flushCustomStyles()})})}
function Wg(a){!a.a&&window.ShadyCSS&&window.ShadyCSS.ApplyShim&&(a.a=window.ShadyCSS.ApplyShim,a.a.invalidCallback=Rg);Yg(a)}
r.flushCustomStyles=function(){if(!lf&&(Wg(this),this.b)){var a=this.b.processStyles();if(this.b.enqueued&&!Lf(this.f.cssBuild)){if(R){if(!this.f.cssBuild)for(var b=0;b<a.length;b++){var c=this.b.getStyleForCustomStyle(a[b]);if(c&&R&&this.a){var d=yf(c);Wg(this);this.a.transformRules(d);c.textContent=wf(d)}}}else{Zg(this,this.c,this.f);for(b=0;b<a.length;b++)(c=this.b.getStyleForCustomStyle(a[b]))&&Ag(c,this.f.K);this.u&&this.styleDocument()}this.b.enqueued=!1}}};
r.styleElement=function(a,b){if(lf){if(b){U(a)||lg(a,new kg(null));var c=U(a);c.G=c.G||{};Object.assign(c.G,b);$g(this,a,c)}}else if(c=U(a)||Xg(a))if(a!==this.c&&(this.u=!0),b&&(c.G=c.G||{},Object.assign(c.G,b)),R)$g(this,a,c);else if(this.flush(),Zg(this,a,c),c.na&&c.na.length){b=If(a).is;var d;a:{if(d=Vg.cache[b])for(var e=d.length-1;0<=e;e--){var f=d[e];b:{var g=c.na;for(var h=0;h<g.length;h++){var k=g[h];if(f.J[k]!==c.K[k]){g=!1;break b}}g=!0}if(g){d=f;break a}}d=void 0}g=d?d.styleElement:null;
e=c.H;(f=d&&d.H)||(f=this.F[b]=(this.F[b]||0)+1,f=b+"-"+f);c.H=f;f=c.H;h=Bg;h=g?g.textContent||"":xg(h,a,c.K,f);k=U(a);var m=k.a;m&&!P&&m!==g&&(m._useCount--,0>=m._useCount&&m.parentNode&&m.parentNode.removeChild(m));P?k.a?(k.a.textContent=h,g=k.a):h&&(g=Af(h,f,a.shadowRoot,k.b)):g?g.parentNode||(ng&&-1<h.indexOf("@media")&&(g.textContent=h),Bf(g,null,k.b)):h&&(g=Af(h,f,null,k.b));g&&(g._useCount=g._useCount||0,k.a!=g&&g._useCount++,k.a=g);f=g;P||(g=c.H,k=h=a.getAttribute("class")||"",e&&(k=h.replace(new RegExp("\\s*x-scope\\s*"+
e+"\\s*","g")," ")),k+=(k?" ":"")+"x-scope "+g,h!==k&&Gf(a,k));d||Vg.store(b,c.K,f,c.H)}};
function $g(a,b,c){var d=If(b).is;if(c.G){var e=c.G,f;for(f in e)null===f?b.style.removeProperty(f):b.style.setProperty(f,e[f])}e=Pg[d];if(!(!e&&b!==a.c||e&&""!==Kf(e))&&e&&e._style&&!Sg(e)){if(Sg(e)||e._applyShimValidatingVersion!==e._applyShimNextVersion)Wg(a),a.a&&a.a.transformRules(e._styleAst,d),e._style.textContent=Sf(b,c.L),Tg(e);P&&(a=b.shadowRoot)&&(a=a.querySelector("style"))&&(a.textContent=Sf(b,c.L));c.L=e._styleAst}}
function ah(a,b){return(b=Hf(b).getRootNode().host)?U(b)||Xg(b)?b:ah(a,b):a.c}function Zg(a,b,c){var d=ah(a,b),e=U(d),f=e.K;d===a.c||f||(Zg(a,d,e),f=e.K);a=Object.create(f||null);d=wg(b,c.L,c.cssBuild);b=ug(e.L,b).J;Object.assign(a,d.Ma,b,d.Ta);b=c.G;for(var g in b)if((e=b[g])||0===e)a[g]=e;g=Bg;b=Object.getOwnPropertyNames(a);for(e=0;e<b.length;e++)d=b[e],a[d]=sg(g,a[d],a);c.K=a}r.styleDocument=function(a){this.styleSubtree(this.c,a)};
r.styleSubtree=function(a,b){var c=Hf(a),d=c.shadowRoot;(d||a===this.c)&&this.styleElement(a,b);if(a=d&&(d.children||d.childNodes))for(c=0;c<a.length;c++)this.styleSubtree(a[c]);else if(c=c.children||c.childNodes)for(a=0;a<c.length;a++)this.styleSubtree(c[a])};
r.wa=function(a){var b=this,c=Kf(a);c!==this.f.cssBuild&&(this.f.cssBuild=c);if(!Lf(c)){var d=yf(a);xf(d,function(a){if(P)jg(a);else{var d=S;a.selector=a.parsedSelector;jg(a);a.selector=a.C=Vf(d,a,d.c,void 0,void 0)}R&&""===c&&(Wg(b),b.a&&b.a.transformRule(a))});R?a.textContent=wf(d):this.f.L.rules.push(d)}};r.getComputedStyleValue=function(a,b){var c;R||(c=(U(a)||U(ah(this,a))).K[b]);return(c=c||window.getComputedStyle(a).getPropertyValue(b))?c.trim():""};
r.Wa=function(a,b){var c=Hf(a).getRootNode();b=b?b.split(/\s/):[];c=c.host&&c.host.localName;if(!c){var d=a.getAttribute("class");if(d){d=d.split(/\s/);for(var e=0;e<d.length;e++)if(d[e]===S.a){c=d[e+1];break}}}c&&b.push(S.a,c);R||(c=U(a))&&c.H&&b.push(Bg.a,c.H);Gf(a,b.join(" "))};r.Fa=function(a){return U(a)};r.Va=function(a,b){Pf(a,b)};r.Ya=function(a,b){Pf(a,b,!0)};r.Ua=function(a){return Jg(a)};r.Ia=function(a){return Ig(a)};V.prototype.flush=V.prototype.flush;V.prototype.prepareTemplate=V.prototype.prepareTemplate;
V.prototype.styleElement=V.prototype.styleElement;V.prototype.styleDocument=V.prototype.styleDocument;V.prototype.styleSubtree=V.prototype.styleSubtree;V.prototype.getComputedStyleValue=V.prototype.getComputedStyleValue;V.prototype.setElementClass=V.prototype.Wa;V.prototype._styleInfoForNode=V.prototype.Fa;V.prototype.transformCustomStyleForDocument=V.prototype.wa;V.prototype.getStyleAst=V.prototype.Ka;V.prototype.styleAstToString=V.prototype.Xa;V.prototype.flushCustomStyles=V.prototype.flushCustomStyles;
V.prototype.scopeNode=V.prototype.Va;V.prototype.unscopeNode=V.prototype.Ya;V.prototype.scopeForNode=V.prototype.Ua;V.prototype.currentScopeForNode=V.prototype.Ia;V.prototype.prepareAdoptedCssText=V.prototype.Sa;Object.defineProperties(V.prototype,{nativeShadow:{get:function(){return P}},nativeCss:{get:function(){return R}}});var W=new V,bh,ch;window.ShadyCSS&&(bh=window.ShadyCSS.ApplyShim,ch=window.ShadyCSS.CustomStyleInterface);
window.ShadyCSS={ScopingShim:W,prepareTemplate:function(a,b,c){W.flushCustomStyles();W.prepareTemplate(a,b,c)},prepareTemplateDom:function(a,b){W.prepareTemplateDom(a,b)},prepareTemplateStyles:function(a,b,c){W.flushCustomStyles();W.prepareTemplateStyles(a,b,c)},styleSubtree:function(a,b){W.flushCustomStyles();W.styleSubtree(a,b)},styleElement:function(a){W.flushCustomStyles();W.styleElement(a)},styleDocument:function(a){W.flushCustomStyles();W.styleDocument(a)},flushCustomStyles:function(){W.flushCustomStyles()},
getComputedStyleValue:function(a,b){return W.getComputedStyleValue(a,b)},nativeCss:R,nativeShadow:P,cssBuild:of,disableRuntime:lf};bh&&(window.ShadyCSS.ApplyShim=bh);ch&&(window.ShadyCSS.CustomStyleInterface=ch);(function(a){function b(a){""==a&&(f.call(this),this.i=!0);return a.toLowerCase()}function c(a){var b=a.charCodeAt(0);return 32<b&&127>b&&-1==[34,35,60,62,63,96].indexOf(b)?a:encodeURIComponent(a)}function d(a){var b=a.charCodeAt(0);return 32<b&&127>b&&-1==[34,35,60,62,96].indexOf(b)?a:encodeURIComponent(a)}function e(a,e,g){function h(a){X.push(a)}var k=e||"scheme start",x=0,q="",u=!1,Q=!1,X=[];a:for(;(void 0!=a[x-1]||0==x)&&!this.i;){var l=a[x];switch(k){case "scheme start":if(l&&p.test(l))q+=l.toLowerCase(),
k="scheme";else if(e){h("Invalid scheme.");break a}else{q="";k="no scheme";continue}break;case "scheme":if(l&&G.test(l))q+=l.toLowerCase();else if(":"==l){this.h=q;q="";if(e)break a;void 0!==m[this.h]&&(this.B=!0);k="file"==this.h?"relative":this.B&&g&&g.h==this.h?"relative or authority":this.B?"authority first slash":"scheme data"}else if(e){void 0!=l&&h("Code point not allowed in scheme: "+l);break a}else{q="";x=0;k="no scheme";continue}break;case "scheme data":"?"==l?(this.o="?",k="query"):"#"==
l?(this.v="#",k="fragment"):void 0!=l&&"\t"!=l&&"\n"!=l&&"\r"!=l&&(this.la+=c(l));break;case "no scheme":if(g&&void 0!==m[g.h]){k="relative";continue}else h("Missing scheme."),f.call(this),this.i=!0;break;case "relative or authority":if("/"==l&&"/"==a[x+1])k="authority ignore slashes";else{h("Expected /, got: "+l);k="relative";continue}break;case "relative":this.B=!0;"file"!=this.h&&(this.h=g.h);if(void 0==l){this.j=g.j;this.m=g.m;this.l=g.l.slice();this.o=g.o;this.s=g.s;this.g=g.g;break a}else if("/"==
l||"\\"==l)"\\"==l&&h("\\ is an invalid code point."),k="relative slash";else if("?"==l)this.j=g.j,this.m=g.m,this.l=g.l.slice(),this.o="?",this.s=g.s,this.g=g.g,k="query";else if("#"==l)this.j=g.j,this.m=g.m,this.l=g.l.slice(),this.o=g.o,this.v="#",this.s=g.s,this.g=g.g,k="fragment";else{k=a[x+1];var y=a[x+2];if("file"!=this.h||!p.test(l)||":"!=k&&"|"!=k||void 0!=y&&"/"!=y&&"\\"!=y&&"?"!=y&&"#"!=y)this.j=g.j,this.m=g.m,this.s=g.s,this.g=g.g,this.l=g.l.slice(),this.l.pop();k="relative path";continue}break;
case "relative slash":if("/"==l||"\\"==l)"\\"==l&&h("\\ is an invalid code point."),k="file"==this.h?"file host":"authority ignore slashes";else{"file"!=this.h&&(this.j=g.j,this.m=g.m,this.s=g.s,this.g=g.g);k="relative path";continue}break;case "authority first slash":if("/"==l)k="authority second slash";else{h("Expected '/', got: "+l);k="authority ignore slashes";continue}break;case "authority second slash":k="authority ignore slashes";if("/"!=l){h("Expected '/', got: "+l);continue}break;case "authority ignore slashes":if("/"!=
l&&"\\"!=l){k="authority";continue}else h("Expected authority, got: "+l);break;case "authority":if("@"==l){u&&(h("@ already seen."),q+="%40");u=!0;for(l=0;l<q.length;l++)y=q[l],"\t"==y||"\n"==y||"\r"==y?h("Invalid whitespace in authority."):":"==y&&null===this.g?this.g="":(y=c(y),null!==this.g?this.g+=y:this.s+=y);q=""}else if(void 0==l||"/"==l||"\\"==l||"?"==l||"#"==l){x-=q.length;q="";k="host";continue}else q+=l;break;case "file host":if(void 0==l||"/"==l||"\\"==l||"?"==l||"#"==l){2!=q.length||
!p.test(q[0])||":"!=q[1]&&"|"!=q[1]?(0!=q.length&&(this.j=b.call(this,q),q=""),k="relative path start"):k="relative path";continue}else"\t"==l||"\n"==l||"\r"==l?h("Invalid whitespace in file host."):q+=l;break;case "host":case "hostname":if(":"!=l||Q)if(void 0==l||"/"==l||"\\"==l||"?"==l||"#"==l){this.j=b.call(this,q);q="";k="relative path start";if(e)break a;continue}else"\t"!=l&&"\n"!=l&&"\r"!=l?("["==l?Q=!0:"]"==l&&(Q=!1),q+=l):h("Invalid code point in host/hostname: "+l);else if(this.j=b.call(this,
q),q="",k="port","hostname"==e)break a;break;case "port":if(/[0-9]/.test(l))q+=l;else if(void 0==l||"/"==l||"\\"==l||"?"==l||"#"==l||e){""!=q&&(q=parseInt(q,10),q!=m[this.h]&&(this.m=q+""),q="");if(e)break a;k="relative path start";continue}else"\t"==l||"\n"==l||"\r"==l?h("Invalid code point in port: "+l):(f.call(this),this.i=!0);break;case "relative path start":"\\"==l&&h("'\\' not allowed in path.");k="relative path";if("/"!=l&&"\\"!=l)continue;break;case "relative path":if(void 0!=l&&"/"!=l&&"\\"!=
l&&(e||"?"!=l&&"#"!=l))"\t"!=l&&"\n"!=l&&"\r"!=l&&(q+=c(l));else{"\\"==l&&h("\\ not allowed in relative path.");if(y=n[q.toLowerCase()])q=y;".."==q?(this.l.pop(),"/"!=l&&"\\"!=l&&this.l.push("")):"."==q&&"/"!=l&&"\\"!=l?this.l.push(""):"."!=q&&("file"==this.h&&0==this.l.length&&2==q.length&&p.test(q[0])&&"|"==q[1]&&(q=q[0]+":"),this.l.push(q));q="";"?"==l?(this.o="?",k="query"):"#"==l&&(this.v="#",k="fragment")}break;case "query":e||"#"!=l?void 0!=l&&"\t"!=l&&"\n"!=l&&"\r"!=l&&(this.o+=d(l)):(this.v=
"#",k="fragment");break;case "fragment":void 0!=l&&"\t"!=l&&"\n"!=l&&"\r"!=l&&(this.v+=l)}x++}}function f(){this.s=this.la=this.h="";this.g=null;this.m=this.j="";this.l=[];this.v=this.o="";this.B=this.i=!1}function g(a,b){void 0===b||b instanceof g||(b=new g(String(b)));this.a=a;f.call(this);a=this.a.replace(/^[ \t\r\n\f]+|[ \t\r\n\f]+$/g,"");e.call(this,a,null,b)}var h=!1;try{var k=new URL("b","http://a");k.pathname="c%20d";h="http://a/c%20d"===k.href}catch(x){}if(!h){var m=Object.create(null);m.ftp=
21;m.file=0;m.gopher=70;m.http=80;m.https=443;m.ws=80;m.wss=443;var n=Object.create(null);n["%2e"]=".";n[".%2e"]="..";n["%2e."]="..";n["%2e%2e"]="..";var p=/[a-zA-Z]/,G=/[a-zA-Z0-9\+\-\.]/;g.prototype={toString:function(){return this.href},get href(){if(this.i)return this.a;var a="";if(""!=this.s||null!=this.g)a=this.s+(null!=this.g?":"+this.g:"")+"@";return this.protocol+(this.B?"//"+a+this.host:"")+this.pathname+this.o+this.v},set href(a){f.call(this);e.call(this,a)},get protocol(){return this.h+
":"},set protocol(a){this.i||e.call(this,a+":","scheme start")},get host(){return this.i?"":this.m?this.j+":"+this.m:this.j},set host(a){!this.i&&this.B&&e.call(this,a,"host")},get hostname(){return this.j},set hostname(a){!this.i&&this.B&&e.call(this,a,"hostname")},get port(){return this.m},set port(a){!this.i&&this.B&&e.call(this,a,"port")},get pathname(){return this.i?"":this.B?"/"+this.l.join("/"):this.la},set pathname(a){!this.i&&this.B&&(this.l=[],e.call(this,a,"relative path start"))},get search(){return this.i||
!this.o||"?"==this.o?"":this.o},set search(a){!this.i&&this.B&&(this.o="?","?"==a[0]&&(a=a.slice(1)),e.call(this,a,"query"))},get hash(){return this.i||!this.v||"#"==this.v?"":this.v},set hash(a){this.i||(a?(this.v="#","#"==a[0]&&(a=a.slice(1)),e.call(this,a,"fragment")):this.v="")},get origin(){var a;if(this.i||!this.h)return"";switch(this.h){case "data":case "file":case "javascript":case "mailto":return"null"}return(a=this.host)?this.h+"://"+a:""}};var u=a.URL;u&&(g.createObjectURL=function(a){return u.createObjectURL.apply(u,
arguments)},g.revokeObjectURL=function(a){u.revokeObjectURL(a)});a.URL=g}})(window);Object.getOwnPropertyDescriptor(Node.prototype,"baseURI")||Object.defineProperty(Node.prototype,"baseURI",{get:function(){var a=(this.ownerDocument||this).querySelector("base[href]");return a&&a.href||window.location.href},configurable:!0,enumerable:!0});var dh=document.createElement("style");dh.textContent="body {transition: opacity ease-in 0.2s; } \nbody[unresolved] {opacity: 0; display: block; overflow: hidden; position: relative; } \n";var eh=document.querySelector("head");eh.insertBefore(dh,eh.firstChild);var fh=window.customElements,gh=!1,hh=null;fh.polyfillWrapFlushCallback&&fh.polyfillWrapFlushCallback(function(a){hh=a;gh&&a()});function ih(){window.HTMLTemplateElement.bootstrap&&window.HTMLTemplateElement.bootstrap(window.document);hh&&hh();gh=!0;window.WebComponents.ready=!0;document.dispatchEvent(new CustomEvent("WebComponentsReady",{bubbles:!0}))}
"complete"!==document.readyState?(window.addEventListener("load",ih),window.addEventListener("DOMContentLoaded",function(){window.removeEventListener("load",ih);ih()})):ih();}).call(this);

//# sourceMappingURL=webcomponents-bundle.js.map

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
	if (Block.instance)
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
class ProgressStatus extends HTMLElement
{
	constructor()
	{
		super();

		var shadow = this.attachShadow({mode: 'open'});

		var title = shadow.appendChild(document.createElement("label"));
		title.id = "title";
		title.style.fontSize = "20px";
		title.style.flexBasis = "40px";
		title.innerHTML = "Conectando ao servidor";

		var progress = shadow.appendChild(document.createElement("progress"));
		progress.id = "progress";
		progress.style.width = "100%";
		progress.style.flexBasis = "40px";

		var div = shadow.appendChild(document.createElement("div"));
		div.style.flexBasis = "40px";
		div.style.display = "flex";
		div.style.alignItems = "center";

		var clock = div.appendChild(document.createElement("label"));
		clock.id = "clock";
		clock.style.flexGrow = "1";
		clock.style.fontSize = "12px";
		clock.style.display = "flex";
		clock.style.alignItems = "center";
		clock.style.justifyContent = "flex-start";
		clock.innerHTML = "00:00:00";

		var counter = div.appendChild(document.createElement("label"));
		counter.id = "counter";
		counter.style.flexGrow = "1";
		counter.style.fontSize = "12px";
		counter.style.display = "flex";
		counter.style.alignItems = "center";
		counter.style.justifyContent = "flex-end";
		counter.innerHTML = "...";

		var div = shadow.appendChild(document.createElement("div"));
		div.style.padding = "4px";
		div.style.display = "flex";
		div.style.overflow = "auto";
		div.style.flexBasis = "120px";
		div.style.borderRadius = "5px";
		div.style.alignItems = "stretch";
		div.style.justifyContent = "center";
		div.style.backgroundColor = "white";

		var logger = div.appendChild(document.createElement("ul"));
		logger.id = "logger";
		logger.style.flexGrow = "1";
		logger.style.margin = "0";
		logger.style.padding = "0";
		logger.style.listStyleType = "none";
	}

	connectedCallback()
	{
		var colors = new Object();
		colors["PENDING"] = '#000000';
		colors["COMMITED"] = '#006600';
		colors["CANCELED"] = '#660000';

		var title = this.shadowRoot.getElementById("title");
		var progress = this.shadowRoot.getElementById("progress");
		var clock = this.shadowRoot.getElementById("clock");
		var counter = this.shadowRoot.getElementById("counter");
		var logger = this.shadowRoot.getElementById("logger");

		var time = 0;
		this.onClockTick = () => clock.innerHTML = new Duration(++time).toString();

		var ws = new WebSocket("ws://" + window.location.host + "/" +
			window.location.pathname.split("/")[1] + "/Progress/" + this.getAttribute('process'));
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
						title.innerHTML = event.text;

						var log = logger.firstElementChild ?
							logger.insertBefore(document.createElement("li"),
								logger.firstElementChild)
							: logger.appendChild(document.createElement("li"));
						log.innerHTML = event.text;
						log.style.height = "16px";
						log.style.display = "flex";
						log.style.alignItems = "center";
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

							this.onClockTick = null;
							break;
						case "CANCELED":
							if (!progress.max)
								progress.max = 100;
							if (!progress.value)
								progress.value = 0;

							this.dispatchEvent(new CustomEvent('canceled'));

							this.onClockTick = null;
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

window.addEventListener("load", () =>
	customElements.define('progress-status', ProgressStatus));



class ProgressDialog extends Modal
{
	constructor(process, options)
	{
		super();

		var status = "Pending";

		var main = this.element().appendChild(document.createElement("div"));
		main.className = "ProgressDialog";
		this.main = () => main;

		var head = main.appendChild(document.createElement("div"));
		if (options && options.title)
			head.innerHTML = options.title;

		var body = main.appendChild(document.createElement("div"));

		var progress = body.appendChild(document.createElement("progress-status"));
		progress.setAttribute("process", process);

		var foot = main.appendChild(document.createElement("div"));

		var action = foot.appendChild(document.createElement("a"));
		action.appendChild(document.createTextNode("Processando"));
		action.href = "#";

		this.creator().addEventListener("hide", function (event)
		{
			if (status === "Pending"
				&& !confirm("Tem certeza de que deseja fechar o progresso?"))
				event.preventDefault();
		});

		action.onclick = () => this.hide();

		progress.addEventListener("commited", () =>
		{
			status = "Canceled";
			action.innerHTML = "OK";
			action.style.color = "#006600";
		});

		progress.addEventListener("canceled", () =>
		{
			status = "Commited";
			action.innerHTML = "OK";
			action.style.color = "#660000";
		});

		progress.addEventListener("redirected", url =>
		{
			this.creator().addEventListener("hide", event =>
			{
				this.hide();
				event.preventDefault();
				window.location.href = url.detail;
			});
		});
	}
}
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

			if (hidden)
				hidden.value = "";

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
						hidden.dispatchEvent(new CustomEvent('populated',
							{detail: {source: this}}));
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

				case "_progress":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					new URL(this.href).get(function (process)
					{
						process = JSON.parse(process);
						new ProgressDialog(process,
							{title: link.getAttribute("title")}).show();
					});

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

					if (this.form.reportValidity())
					{
						this.disabled = true;
						new URL(this.getAttribute("formaction"))
							.post(new FormData(this.form), function (status)
							{
								alert(status);
								button.disabled = false;
							});
					}
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

				case "_progress":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					if (this.form.reportValidity())
					{
						this.disabled = true;
						new URL(this.getAttribute("formaction"))
							.post(new FormData(this.form), function (process)
							{
								process = JSON.parse(process);
								new ProgressDialog(process,
									{title: button.getAttribute("title")}).show();
								button.disabled = false;
							});
					}

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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new DatePicker(time =>
				{
					input.value = time;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new TimePicker(time =>
				{
					input.value = time;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new MonthPicker(time =>
				{
					input.value = time;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new DateTimePicker(dateTime =>
				{
					input.value = dateTime;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new DateIntervalPicker(selection =>
				{
					input.value = selection;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new TimeIntervalPicker(time =>
				{
					input.value = time;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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
		link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new MonthIntervalPicker(time =>
				{
					input.value = time;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new DateTimeIntervalPicker(dateTime =>
				{
					input.value = dateTime;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			if (input.value) {
				input.value = '';

				input.dispatchEvent(new CustomEvent('cleared',
					{detail: {source: this}}));
			} else
				new IconPicker(code =>
				{
					input.value = code;
					link.focus();

					input.dispatchEvent(new CustomEvent('picked',
						{detail: {source: this}}));
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

			Array.from(this.parentNode.parentNode.parentNode.children)
				.filter(e => e.tagName.toUpperCase() === "TBODY")
				.forEach(e => e.sort(Array.prototype.indexOf
						.call(this.parentNode.children, this),
						this.getAttribute("data-sortable")));
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