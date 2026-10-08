import"./trigger.js";import s from"./dom.js";import c from"./request-builder.js";import d from"./response-handler.js";window.addEventListener("@remove",function(e){let t=e.composedPath(),{method:r,action:i,parameters:[o],form:a,signal:l}=e.detail,m=s.navigate(e,o).orElseThrow(`${o} is not a valid selector`);return fetch(c.build(r,i,a),{signal:l}).then(d.none).then(()=>m.remove()).then(()=>e.success(t)).catch(n=>e.failure(t,n))});

//# sourceMappingURL=@remove.js.map
