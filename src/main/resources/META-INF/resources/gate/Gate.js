if (!document.querySelectorAll)
	window.location = '../gate/NAVI.jsp';

import "./g-loading.js";

import "./link.js";
import "./mask.js";
import "./button.js";
import "./treeview.js";
import "./switch.js";

import "./clipboard.js";

import "./g-table-scroll.js";

import "./action-handler.js";
import "./change-handler.js";
import "./auto-focus-handler.js";
import "./auto-click-handler.js";

import "./g-tooltip.js";
import "./g-message.js";
import "./g-block.js";

import "./g-event-source.js";
import "./g-digital-clock.js";
import "./g-card.js";
import "./g-card-pane.js";
import "./g-paginator.js";
import "./g-path.js";
import "./g-coolbar.js";
import "./g-tabbar.js";
import "./g-scroll-tabbar.js";
import "./g-tab-control.js";
import "./g-navbar.js";
import "./g-dialog.js";
import "./g-dialog-caption.js";
import "./g-stack-frame.js";
import "./g-context-menu.js";

import "./g-text-editor.js";
import "./g-report-dialog.js";
import "./g-chart.js";
import "./g-chart-dialog.js";
import "./g-icon-pane.js";
import "./g-popup.js";

import "./g-icon-picker.js";
import "./g-date-picker.js";
import "./g-time-picker.js";
import "./g-month-picker.js";
import "./g-date-time-picker.js";
import "./g-date-interval-picker.js";
import "./g-time-interval-picker.js";
import "./g-month-interval-picker.js";
import "./g-date-time-interval-picker.js";

import "./g-progress-dialog.js";

import "./g-desk-pane.js";

import "./sort.js";
import "./filter.js";

import "./form.js";
import "./validation.js";

import "./password.js";

import "./datalist.js";

import "./g-select-picker.js";
import "./g-search-picker.js";

import "./g-icon.js";
import "./g-form.js";
import "./g-selectn.js";
import "./g-form-view.js";
import "./g-form-editor.js";
import "./g-form-dialog.js";

import "./g-select-menu.js";

import "./g-selectn-picker.js";

import "./g-splitter.js";

import "./g-callout.js";

import "./g-accordion.js";
import "./g-accordion-section.js";

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