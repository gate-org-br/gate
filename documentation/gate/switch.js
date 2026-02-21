window.setInterval(()=>{Array.from(document.querySelectorAll("*[data-switch]")).forEach(t=>{let e=t.innerHTML,r=t.getAttribute("data-switch");t.innerHTML=r,t.setAttribute("data-switch",e)})},500);

//# sourceMappingURL=switch.js.map
