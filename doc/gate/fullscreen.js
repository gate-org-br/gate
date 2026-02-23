export default class t{static status(){return document.fullscreenElement||document.mozFullScreenElement||document.webkitFullscreenElement}static switch(e){return t.status()?t.exit():t.enter(e),t.status()}static enter(e){e.requestFullscreen?e.requestFullscreen():e.mozRequestFullScreen?e.mozRequestFullScreen():e.webkitRequestFullScreen&&e.webkitRequestFullScreen()}static exit(){document.exitFullscreen?document.exitFullscreen():document.webkitExitFullscreen?document.webkitExitFullscreen():document.mozCancelFullScreen&&document.mozCancelFullScreen()}}

//# sourceMappingURL=fullscreen.js.map
