!function(e){var t={};function r(n){if(t[n])return t[n].exports;var o=t[n]={i:n,l:!1,exports:{}};return e[n].call(o.exports,o,o.exports,r),o.l=!0,o.exports}r.m=e,r.c=t,r.d=function(e,t,n){r.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},r.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},r.t=function(e,t){if(1&t&&(e=r(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(r.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)r.d(n,o,function(t){return e[t]}.bind(null,o));return n},r.n=function(e){var t=e&&e.__esModule?function(){return e.default}:function(){return e};return r.d(t,"a",t),t},r.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},r.p="",r(r.s=0)}([function(e,t,r){"use strict";Object.defineProperty(t,"__esModule",{value:!0});class n{constructor(){this._mockChallengeDatabase=[]}getChallenges(){return Promise.resolve(this._mockChallengeDatabase)}getChallengesByProvider(e){return Promise.resolve(this._mockChallengeDatabase.filter(t=>t.provider.id===e))}getById(e){const t=this._mockChallengeDatabase.find(t=>t.id===e);return Promise.resolve(t||null)}addChallenge(e){return this._mockChallengeDatabase.push(e),Promise.resolve()}updateChallenge(e,t){const r=this._mockChallengeDatabase.findIndex(t=>t.id===e);return r<0?Promise.reject("Can not find challenge"):(t.id=e,this._mockChallengeDatabase[r]=t,Promise.resolve())}}t.default=n,window.VueChallengeProvider=new n}]);