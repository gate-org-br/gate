import n from"./values.js";import i from"./data-url.js";import u from"./g-search-picker.js";window.addEventListener("@search",function(a){let e=a.composedPath(),t=e[0]||a.target,{action:r,parameters:[c="label"]}=a.detail;if(t.tagName==="INPUT"&&!t.value)return a.success(e,new i("application/json","{}").toString());let o=t.tagName==="INPUT"?t.value:null,s=n.get(t)||"";u.pick(r,c,t.title,o).then(l=>l.value).then(i.ofJSON).then(l=>a.success(e,l)).catch(()=>{t.tagName==="INPUT"&&(t.value=s),a.resolve(e)}).finally(()=>t.hasAttribute("tabindex")&&t.focus())});

//# sourceMappingURL=@search.js.map
