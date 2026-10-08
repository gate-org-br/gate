export default function c(o){const t=document.createElement("template");return t.innerHTML=o,t.content.querySelectorAll("script, style").forEach(e=>e.remove()),t.content.querySelectorAll("*").forEach(e=>{[...e.attributes].forEach(r=>{const n=r.name.toLowerCase(),a=r.value;n.startsWith("on")&&e.removeAttribute(r.name),(n==="href"||n==="src")&&/^javascript:/i.test(a)&&e.removeAttribute(r.name)})}),t.innerHTML}

//# sourceMappingURL=sanitize.js.map
