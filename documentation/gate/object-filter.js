export default class f{static filter(r,s){return Array.isArray(r[0])?r.filter((i,l)=>l===0||t(i,s)):r.filter(i=>t(i,s))}}function t(e,r){return e?typeof e=="string"?e.toLowerCase().includes(r.toLowerCase()):typeof e=="number"?e.toString().includes(r):Array.isArray(e)?e.some(s=>t(s,r)):typeof e=="object"?Object.values(e).some(s=>t(s,r)):!1:!1}

//# sourceMappingURL=object-filter.js.map
