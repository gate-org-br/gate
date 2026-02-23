import"./g-chart-dialog.js";import c from"./data-url.js";import s from"./request-builder.js";import n from"./response-handler.js";window.addEventListener("@chart",function(t){let o=t.composedPath(),r=o[0]||t.target,{method:i,action:l,form:p,parameters:[d]}=t.detail,e=window.top.document.createElement("g-chart-dialog");e.type=d||"pie",e.caption=r.getAttribute("title");let m=e.show();fetch(s.build(i,l,p)).then(n.dataURL).then(a=>{e.value=JSON.parse(c.parse(a).data),m.finally(()=>t.success(o,a))}).catch(a=>t.failure(o,a))});

//# sourceMappingURL=@chart.js.map
