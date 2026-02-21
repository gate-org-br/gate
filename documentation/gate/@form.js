import"./trigger.js";import s from"./data-url.js";import d from"./g-form-dialog.js";import l from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@form",function(i){let e=i.composedPath(),a=e[0]||i.target,{method:n,action:h,form:m,parameters:[r,p]}=i.detail,o={};o.caption=a.title,o.width=r,o.height=p||r,fetch(l.build(n,h,m)).then(c.json).then(t=>d.edit(t,o)).then(t=>JSON.stringify(t)).then(t=>new s("application/json",t).toString()).then(t=>i.success(e,t)).catch(t=>i.failure(e,t))});

//# sourceMappingURL=@form.js.map
