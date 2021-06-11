window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("*[data-autoclick]")).forEach(a => a.click());
});