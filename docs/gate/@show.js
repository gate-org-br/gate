import"./trigger.js";import h from"./dom.js";import"./data-url.js";import n from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@show",function(e){let t=e.composedPath(),a=t[0]||e.target,{method:s,action:l,form:m,parameters:[i],signal:d}=e.detail,o=h.navigate(a,i).orElseThrow(`${i} is not a valid selector`);fetch(n.build(s,l,m),{signal:d}).then(c.dataURL).then(r=>{o.show?o.show():o.removeAttribute("hidden"),e.success(t,r)}).catch(r=>e.failure(t,r))});

//# sourceMappingURL=@show.js.map
