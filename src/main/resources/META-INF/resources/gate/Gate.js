if (!document.querySelectorAll)
	window.location = '../gate/NAVI.jsp';

import "./g-tree-grid.js";
import "./g-tree-list.js";
import "./mask.js";
import "./switch.js";

import "./g-table-scroll.js";

import "./auto-click-handler.js";
import "./auto-focus-handler.js";

import "./g-block.js";
import "./g-message.js";
import "./g-tooltip.js";

import "./g-card-pane.js";
import "./g-card.js";
import "./g-context-menu.js";
import "./g-coolbar.js";
import "./g-dialog.js";
import "./g-digital-clock.js";
import "./g-navbar.js";
import "./g-paginator.js";
import "./g-path.js";
import "./g-stack-frame.js";
import "./g-tab-control.js";
import "./g-tabbar.js";

import "./g-chart-dialog.js";
import "./g-chart-toolbar.js";
import "./g-chart.js";
import "./g-report-picker.js";
import "./g-text-editor.js";
import "./g-text-viewer.js";

import "./g-date-interval-picker.js";
import "./g-date-picker.js";
import "./g-date-time-interval-picker.js";
import "./g-date-time-picker.js";
import "./g-fetch-picker.js";
import "./g-frame-picker.js";
import "./g-icon-picker.js";
import "./g-month-interval-picker.js";
import "./g-month-picker.js";
import "./g-progress-dialog.js";
import "./g-time-interval-picker.js";
import "./g-time-picker.js";

import "./g-desk-pane.js";

import "./form.js";
import "./validation.js";

import "./password.js";

import "./datalist.js";

import "./g-search-picker.js";
import "./g-select-picker.js";

import "./g-form-dialog.js";
import "./g-form-editor.js";
import "./g-form-view.js";
import "./g-form.js";
import "./g-icon.js";
import "./g-selectn.js";

import "./g-select-menu.js";

import "./g-selectn-picker.js";

import "./g-splitter.js";

import "./g-callout.js";

import "./g-accordion.js";

import './hateoas.js';

import './g-table.js';
import "./populator.js";

import "./picker.js";

import './collapse.js';

import './g-login-form.js';

import './g-drawer.js';

import './event-source.js';

import './master-select.js';

Array.from(document.querySelectorAll("input.SELECTOR, input[type='checkbox'][data-target]")).forEach(function (element)
{
	element.addEventListener("change", function ()
	{
		var selector = 'input[type="checkbox"][name="' + this.getAttribute('data-target') + '"]';
		Array.from(document.querySelectorAll(selector)).forEach(target => target.checked = element.checked);
	});
});