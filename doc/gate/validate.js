export default function a(t){return t.hasAttribute("data-disabled")?!1:t.hasAttribute("data-cancel")?alert(t.getAttribute("data-cancel"),2e3)&&!1:t.hasAttribute("data-confirm")?confirm(t.getAttribute("data-confirm")):t.hasAttribute("data-alert")&&alert(t.getAttribute("data-alert"))||!0}

//# sourceMappingURL=validate.js.map
