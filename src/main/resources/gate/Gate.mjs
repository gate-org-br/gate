if (!document.querySelectorAll)
	window.location = '../gate/NAVI.jsp';

import "./g-loading.mjs";

import "./link.mjs";
import "./mask.mjs";
import "./button.mjs";
import "./treeview.mjs";
import "./switch.mjs";

import "./action-handler.mjs";
import "./change-handler.mjs";
import "./auto-focus-handler.mjs";
import "./auto-click-handler.mjs";

import "./g-tooltip.mjs";
import "./g-message.mjs";
import "./g-block.mjs";

import "./g-event-source.mjs";
import "./g-select.mjs";
import "./g-digital-clock.mjs";
import "./g-card.mjs";
import "./g-card-pane.mjs";
import "./g-paginator.mjs";
import "./g-path.mjs";
import "./g-coolbar.mjs";
import "./g-tabbar.mjs";
import "./g-scroll-tabbar.mjs";
import "./g-tab-control.mjs";
import "./g-navbar.mjs";
import "./g-dialog.mjs";
import "./g-dialog-caption.mjs";
import "./g-stack-frame.mjs";
import "./g-context-menu.mjs";
import "./g-action-context-menu.mjs";
import "./g-checkable-context-menu.mjs";

import "./g-text-editor.mjs";
import "./g-report-dialog.mjs";
import "./g-chart.mjs";
import "./g-chart-dialog.mjs";
import "./g-icon-pane.mjs";
import "./g-popup.mjs";

import "./g-icon-picker.mjs";
import "./g-date-picker.mjs";
import "./g-time-picker.mjs";
import "./g-month-picker.mjs";
import "./g-date-time-picker.mjs";
import "./g-date-interval-picker.mjs";
import "./g-time-interval-picker.mjs";
import "./g-month-interval-picker.mjs";
import "./g-date-time-interval-picker.mjs";

import "./g-progress-dialog.mjs";

import "./g-desk-pane.mjs";

import "./sort.mjs";
import "./filter.mjs";

import "./form.mjs";
import "./validation.mjs";

import "./password.mjs";

import "./datalist.mjs";

import "./g-chat-dialog.mjs";
import "./g-chat-counter.mjs";
import "./g-chat-notificator.mjs";
import "./g-chat-contact-dialog.mjs";

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

document.body.style.visibility = 'visible';