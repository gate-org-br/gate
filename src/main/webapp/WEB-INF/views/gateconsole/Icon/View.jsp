<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<ul class="DeskMenu">
		<g:icons name="icon">
			<li style="width: 4%; min-width: 64px; margin: 0.5%">
				<a href="#" data-icon="&#x${icon.code}">
					${icon.code}
				</a>
			</li>
		</g:icons>
	</ul>

	<script>
		window.addEventListener("load", function ()
		{
			Array.from(document.querySelectorAll("ul.DeskMenu > li > a")).forEach(function (icon)
			{

				icon.addEventListener("mouseover", function ()
				{
					this.focus();
				});
			});
		});
	</script>
</g:template>
