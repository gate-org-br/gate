import"./trigger.js";import n from"./dom.js";import s from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@remove",function(e){let t=e.composedPath(),{method:r,action:i,parameters:[o],form:a}=e.detail,m=n.navigate(e,o).orElseThrow(`${o} is not a valid selector`);return fetch(s.build(r,i,a)).then(c.none).then(()=>m.remove()).then(()=>e.success(t)).catch(l=>e.failure(t,l))});

//# sourceMappingURL=@remove.js.map
