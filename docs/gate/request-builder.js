export default class n{static build(a,e,s,r){(!e||e==="#")&&(e="data:text/plain,");const t=new Headers;switch(t.append("X-G-Fragment","1"),a){case"get":return new Request(e,{headers:t});case"delete":case"head":case"options":return new Request(e,{headers:t,method:a});case"post":case"put":case"patch":return r&&t.append("Content-Type",r),s instanceof HTMLFormElement?s=new FormData(s):s||(s=""),new Request(e,{method:a,headers:t,body:s})}}}

//# sourceMappingURL=request-builder.js.map
