let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	margin: 0;
	margin-top: 10px;
	margin-bottom: 10px;

	width: 100%;
	height: 48px;
	flex-grow: 1;
	padding: 2px;
	display: flex;
	overflow: hidden;
	align-items: stretch;
	flex-direction: row-reverse;

	color: var(--g-coolbar-color, black);
	border: var(--g-coolbar-border, none);
	border-radius: var(--g-coolbar-border-radius, 5px);
	background-color: var(--g-coolbar-background-color, #e4e2d9);
	background-image: var(--g-coolbar-background-image, linear-gradient(to bottom, #e4e2d9 0%, #a4a294 100%));
}


:host(:first-child) {
	margin-top: 0px;
}

:host(:last-child) {
	margin-bottom: 0px;
}

:host(:only-child)
{
	margin-bottom: 0px;
	margin-bottom: 0px
}

:host([reverse]) #container
{
	flex-direction: row;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	margin: 2px;
	height: 40px;
	display: flex;
	flex: 0 1 auto;
	font-size: 12px;
	cursor: pointer;
	min-width: 120px;
	font-weight: bold;
	font-style: normal;
	align-items: center;
	text-decoration: none;
	justify-content: space-between;

	color: var(--g-coolbar-button-color, black);
	padding: var(--g-coolbar-button-padding, 4px);
	border: var(--g-coolbar-button-border, 1px solid #CCCCCC);
	border-radius: var(--g-coolbar-button-border-radius, 5px);
	background-color: var(--g-coolbar-button-background-color, #d7d5c7);
	background-image: var(--g-coolbar-button-background-image, linear-gradient(to bottom, #d7d5c7 0%, #858378 100%));
}

::slotted(progress)
{
	margin: 4px;
	flex-grow: 100000;
}

::slotted(hr)
{
	flex-grow: 100000;
	border: none;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	color: var(--g-coolbar-hovered-button-color, black);
	border: var(--g-coolbar-hovered-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-hovered-button-background-color, #d9d5e1);
	background-image: var(--g-coolbar-hovered-button-background-image, linear-gradient(to bottom,  #d9d5e1 0%, #9a94a4 100%));
}

::slotted(a.Commit),
::slotted(button.Commit),
::slotted(.g-command.Commit)
{
	color: var(--g-coolbar-commit-button-color, #006600);
	border: var(--g-coolbar-commit-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-commit-button-background-color, #d7d5c7);
	background-image: var(--g-coolbar-commit-button-background-image, linear-gradient(to bottom, #d7d5c7 0%, #858378 100%));
}

::slotted(a.Commit:hover),
::slotted(button.Commit:hover),
::slotted(.g-command.Commit:hover)
{
	color: var(--g-coolbar-hovered-commit-button-color, #006600);
	border: var(--g-coolbar-hovered-commit-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-hovered-commit-button-background-color, #d9d5e1);
	background-image: var(--g-coolbar-hovered-commit-button-background-image, linear-gradient(to bottom,  #d9d5e1 0%, #9a94a4 100%));
}

::slotted(a.Action),
::slotted(button.Action),
::slotted(.g-command.Action)
{
	color: var(--g-coolbar-action-button-color, #000066);
	border: var(--g-coolbar-action-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-action-button-background-color, #d7d5c7);
	background-image: var(--g-coolbar-action-button-background-image, linear-gradient(to bottom, #d7d5c7 0%, #858378 100%));
}

::slotted(a.Action:hover),
::slotted(button.Action:hover),
::slotted(.g-command.Action:hover)
{
	color: var(--g-coolbar-hovered-action-button-color, #000066);
	border: var(--g-coolbar-hovered-action-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-hovered-action-button-background-color, #d9d5e1);
	background-image: var(--g-coolbar-hovered-action-button-background-image, linear-gradient(to bottom,  #d9d5e1 0%, #9a94a4 100%));
}

::slotted(a.Delete),
::slotted(button.Delete),
::slotted(.g-command.Delete)
{
	color: var(--g-coolbar-delete-button-color, #660000);
	border: var(--g-coolbar-delete-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-delete-button-background-color, #d7d5c7);
	background-image: var(--g-coolbar-delete-button-background-image, linear-gradient(to bottom, #d7d5c7 0%, #858378 100%));
}

::slotted(a.Delete:hover),
::slotted(button.Delete:hover),
::slotted(.g-command.Delete:hover)
{
	color: var(--g-coolbar-hovered-delete-button-color, #660000);
	border: var(--g-coolbar-hovered-delete-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-hovered-delete-button-background-color, #d9d5e1);
	background-image: var(--g-coolbar-hovered-delete-button-background-image, linear-gradient(to bottom,  #d9d5e1 0%, #9a94a4 100%));
}

::slotted(a.Cancel),
::slotted(button.Cancel),
::slotted(.g-command.Cancel)
{
	color: var(--g-coolbar-cancel-button-color, #660000);
	border: var(--g-coolbar-cancel-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-cancel-button-background-color, #d7d5c7);
	background-image: var(--g-coolbar-cancel-button-background-image, linear-gradient(to bottom, #d7d5c7 0%, #858378 100%));
}

::slotted(a.Cancel:hover),
::slotted(button.Cancel:hover),
::slotted(.g-command.Cancel:hover)
{
	color: var(--g-coolbar-hovered-cancel-button-color, #660000);
	border: var(--g-coolbar-hovered-cancel-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-hovered-cancel-button-background-color, #d9d5e1);
	background-image: var(--g-coolbar-hovered-cancel-button-background-image, linear-gradient(to bottom,  #d9d5e1 0%, #9a94a4 100%));
}

::slotted(a.Return),
::slotted(button.Return),
::slotted(.g-command.Return)
{
	color: var(--g-coolbar-return-button-color, #660000);
	border: var(--g-coolbar-return-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-return-button-background-color, #d7d5c7);
	background-image: var(--g-coolbar-return-button-background-image, linear-gradient(to bottom, #d7d5c7 0%, #858378 100%));
}

::slotted(a.Return:hover),
::slotted(button.Return:hover),
::slotted(.g-command.Return:hover)
{
	color: var(--g-coolbar-hovered-return-button-color, #660000);
	border: var(--g-coolbar-hovered-return-button-border, 1px solid #CCCCCC);
	background-color: var(--g-coolbar-hovered-return-button-background-color, #d9d5e1);
	background-image: var(--g-coolbar-hovered-return-button-background-image, linear-gradient(to bottom,  #d9d5e1 0%, #9a94a4 100%));
}

::slotted(a:focus),
::slotted(button:focus),
::slotted(.g-command:focus)
{
	outline: none;
	border: 2px solid var(--hovered);
}

::slotted(*[hidden="true"])
{
	display: none;
}</style>`;

/* global customElements, template */

import GOverflow from "./g-overflow.mjs";

export default class GCoolbar extends GOverflow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
}

customElements.define('g-coolbar', GCoolbar);