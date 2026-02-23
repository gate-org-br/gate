const d=o=>o.preventDefault();export default class t{static disable(e){e.setAttribute("data-scroll-disabled","data-scroll-disabled"),Array.from(e.children).forEach(a=>t.disable(a)),window.top.document.documentElement.addEventListener("touchmove",d,!1)}static enable(e){e.removeAttribute("data-scroll-disabled"),Array.from(e.children).forEach(a=>t.enable(a)),window.top.document.documentElement.removeEventListener("touchmove",d,!1)}}

//# sourceMappingURL=scroll.js.map
