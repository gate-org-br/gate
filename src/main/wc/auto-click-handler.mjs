Array.from(document.querySelectorAll("*[data-auto-click]")).forEach(e => e.click());
Array.from(document.querySelectorAll("*[data-auto-click-when-unique]")).filter(e => e.parentNode.children.length === 1).forEach(e => e.click());
