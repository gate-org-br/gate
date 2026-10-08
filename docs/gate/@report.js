import p from"./g-report-picker.js";import s from"./request-builder.js";import h from"./response-handler.js";window.addEventListener("@report",function(t){let e=t.composedPath(),o=e[0]||t.target,{method:a,action:i,form:c,signal:d}=t.detail;p.pick(o.title).then(l=>{fetch(s.build(a,`${i}${i.indexOf("?")!==-1?"&":"?"}type=${l}`,c),{signal:d}).then(h.dataURL).then(r=>t.success(e,r)).catch(r=>t.failure(e,r))}).catch(()=>t.resolve(e))});

//# sourceMappingURL=@report.js.map
