import"./g-chart-dialog.js";import n from"./data-url.js";import s from"./request-builder.js";import h from"./response-handler.js";window.addEventListener("@chart",function(t){let i=t.composedPath(),a=i[0]||t.target,{method:r,action:l,form:m,parameters:[p],signal:c}=t.detail,e=window.top.document.createElement("g-chart-dialog");e.type=p||"pie",e.caption=a.getAttribute("title");let d=e.show();fetch(s.build(r,l,m),{signal:c}).then(h.json).then(o=>{e.value=o,d.finally(()=>t.success(i,n.ofJSON(o)))}).catch(o=>t.failure(i,o))});

//# sourceMappingURL=@chart.js.map
