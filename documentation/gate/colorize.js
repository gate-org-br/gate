export default function i(t){t=Array.from(t),t.forEach(e=>{e.classList.remove("odd"),e.classList.remove("even")});let l=t.filter(e=>window.getComputedStyle(e).display==="table-row");if(l.length!==t.length){let e="odd";l.forEach(d=>{d.classList.add(e),e=e==="even"?"odd":"even"})}}

//# sourceMappingURL=colorize.js.map
