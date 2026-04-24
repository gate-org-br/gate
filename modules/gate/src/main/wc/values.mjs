const VALUES = new WeakMap();
window.addEventListener("focusin", function (event)
{
	if (event.target.tagName === "INPUT")
		VALUES.set(event.target, event.target.value);
});

export default VALUES;