window.addEventListener("connected",function(t){if(t.target.hasAttribute("autofocus"))return t.target.focus();if(!t.target.hasAttribute("tabindex"))return;let e=parseInt(t.target.getAttribute("tabindex"),10);if(e<0)return;if(!document.activeElement||!document.activeElement.hasAttribute("tabindex"))return t.target.focus();let r=parseInt(document.activeElement.getAttribute("tabindex"),10);if(r<0)return t.target.focus();e<r&&t.target.focus()});

//# sourceMappingURL=auto-focus-handler.js.map
