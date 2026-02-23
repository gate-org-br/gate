import l from"./g-report-picker.js";import p from"./request-builder.js";import h from"./response-handler.js";window.addEventListener("@report",function(t){let e=t.composedPath(),i=e[0]||t.target,{method:a,action:o,form:c}=t.detail;l.pick(i.title).then(d=>{fetch(p.build(a,`${o}${o.indexOf("?")!==-1?"&":"?"}type=${d}`,c)).then(h.dataURL).then(r=>t.success(e,r)).catch(r=>t.failure(e,r))}).catch(()=>t.resolve(e))});

//# sourceMappingURL=@report.js.map
