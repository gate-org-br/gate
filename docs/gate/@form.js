import"./trigger.js";import s from"./data-url.js";import p from"./g-form-dialog.js";import f from"./request-builder.js";import c from"./response-handler.js";window.addEventListener("@form",function(t){let e=t.composedPath(),a=e[0]||t.target,{method:h,action:m,form:d,parameters:[r,l],signal:n}=t.detail,i={};i.caption=a.title,i.width=r,i.height=l||r,fetch(f.build(h,m,d),{signal:n}).then(c.json).then(o=>p.edit(o,i)).then(s.ofJSON).then(o=>t.success(e,o)).catch(o=>t.failure(e,o))});

//# sourceMappingURL=@form.js.map
