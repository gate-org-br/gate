export default class c{static create(t){let e=t.cloneNode(!0);return e.onclick=l=>{t.click(),l.preventDefault()},e}static copyStyle(t,e){e=e.style,t=getComputedStyle(t);for(var l=t.length;l-- >0;){var r=t[l];alert(r),e.setProperty(r,t.getPropertyValue(r))}}}

//# sourceMappingURL=proxy.js.map
