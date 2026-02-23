import l from"./values.js";import a from"./data-url.js";import m from"./g-frame-picker.js";import s from"./g-fetch-picker.js";window.addEventListener("@pick",function(e){let i=e.composedPath(),t=i[0]||e.target,{action:r,parameters:[c]}=e.detail;if(t.tagName==="INPUT"&&!t.value)return e.success(i,new a("application/json","[]").toString());let o=l.get(t)||"";(c==="frame"?m:s).pick(r,t.title).then(a.ofJSON).then(p=>e.success(i,p)).catch(()=>{t.tagName==="INPUT"&&(t.value=o),e.resolve(i)}).finally(()=>t.hasAttribute("tabindex")&&t.focus())});

//# sourceMappingURL=@pick.js.map
