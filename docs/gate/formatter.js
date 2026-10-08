export default class u{static JSONtoHTML(t){function i(e){let l="";for(let r in e)e.hasOwnProperty(r)&&e[r]&&(l+=`<dt>${r}</dt><dd>${u.JSONtoHTML(e[r])}</dd>`);return`<dl>${l}</dl>`}function a(e){let l="";for(let r of e)r&&(l+=`<li>${u.JSONtoHTML(r)}</li>`);return`<ul>${l}</ul>`}switch(typeof t){case"string":return t;case"number":return String(t);case"boolean":return String(t);case"object":return Array.isArray(t)?a(t):i(t)}}}

//# sourceMappingURL=formatter.js.map
