export default class d{static parse(n){var t=[];return n.replace(/(?!\s*$)\s*(?:'([^'\\]*(?:\\[\S\s][^'\\]*)*)'|"([^"\\]*(?:\\[\S\s][^"\\]*)*)"|([^,;'"\s\\]*(?:\s+[^,;'"\s\\]+)*))\s*(?:[,;]|$)/g,function(a,e,s,i){return e!==void 0?t.push(e.replace(/\\'/g,"'")):s!==void 0?t.push(s.replace(/\\"/g,'"')):i!==void 0&&t.push(i),""}),/,\s*$/.test(n)&&t.push(""),t}static print(n,t){let a="data:text/csv;charset=utf-8,"+n.map(s=>s.map(i=>'"'+i+'"')).map(s=>s.join(",")).join(`
`);a=encodeURI(a);let e=document.createElement("a");e.setAttribute("href",a),e.setAttribute("download",t||"print.csv"),document.body.appendChild(e),e.click(),document.body.removeChild(e)}}

//# sourceMappingURL=csv.js.map
