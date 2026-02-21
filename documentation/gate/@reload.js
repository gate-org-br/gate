import"./trigger.js";import d from"./request-builder.js";import n from"./response-handler.js";window.addEventListener("@reload",function(t){let r=t.composedPath(),{method:i,action:s,form:l,parameters:c}=t.detail;fetch(d.build(i,s,l)).then(n.dataURL).then(o=>{t.success(r,o);let e;switch(c[0]||"_self"){case"_self":e=window;break;case"_parent":e=window.parent;break;case"_top":e=window.top;break}let a=e.location.href;a.endsWith("#")&&(a=a.slice(0,-1)),e.location=a}).catch(o=>t.failure(r,o))});

//# sourceMappingURL=@reload.js.map
