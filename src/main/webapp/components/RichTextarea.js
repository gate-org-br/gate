/* global CKEDITOR */

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("textarea.Rich")).forEach(function (e)
	{
		CKEDITOR.config.toolbar =
			[
				{name: 'Font', items: ['Font', 'FontSize']},
				{name: 'Format', items: ['Bold', 'Italic', 'Underline', 'Strike', '-', 'Subscript', 'Superscript']},
				{name: 'Color', items: ['TextColor', 'BGColor']},
				{name: 'Align', items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', '-', 'JustifyBlock']},
				{name: 'Identation', items: ['Outdent', 'Indent']},
				{name: 'Clear', items: ['RemoveFormat']},
				{name: 'List', items: ['NumberedList', 'BulletedList']},
				{name: 'Links', items: ['Link', 'Unlink']},
				{name: 'Insert', items: ['HorizontalRule', 'Table', 'Smiley']},
				{name: 'Tools', items: ['Maximize']}
			];

		var tabindex = e.getAttribute("tabindex");
		if (tabindex)
		{
			e.removeAttribute("tabindex");
			CKEDITOR.config.tabIndex = tabindex;
		}

		if (e.getAttribute("autofocus"))
			e.removeAttribute("autofocus");

		if (document.activeElement === e)
			CKEDITOR.config.startupFocus = true;

		CKEDITOR.config.language = 'pt-br';
		CKEDITOR.config.allowedContent = true;
		CKEDITOR.config.extraPlugins = 'maximize';
		CKEDITOR.config.width = "100%";
		CKEDITOR.config.height = e.parentNode.clientHeight + "px";
		CKEDITOR.config.resize_enabled = false;
		CKEDITOR.config.contentsCss = '../gate/Gate.css';
		CKEDITOR.replace(e.getAttribute("id"));
	});
});