Array.from(document.querySelectorAll("input[type=password]")).forEach(function(e){var t=e.parentNode.appendChild(document.createElement("a"));t.href="#",t.setAttribute("tabindex",e.getAttribute("tabindex")),t.appendChild(document.createElement("i")).innerHTML="&#x2055;",t.addEventListener("click",()=>e.setAttribute("type",e.getAttribute("type")==="password"?"text":"password"))});

//# sourceMappingURL=password.js.map
