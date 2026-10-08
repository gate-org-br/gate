export default class e{static format(t,o=2){if(t===0)return"0 Bytes";const a=1024,r=o<0?0:o,s=["Bytes","KB","MB","GB","TB","PB","EB","ZB","YB"],B=Math.floor(Math.log(t)/Math.log(a));return parseFloat((t/Math.pow(a,B)).toFixed(r))+" "+s[B]}}

//# sourceMappingURL=data-format.js.map
