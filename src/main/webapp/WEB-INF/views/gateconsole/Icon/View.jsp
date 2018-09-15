<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<ul class='LIST'>
		<g:icons name="icon">
			<g:if condition="${icon.name ne '??????'}">
				<li tabindex='1'>
					${icon.name}<i>&#x${icon.code};</i>
				</li>
			</g:if>
		</g:icons>
	</ul>
	<ul class='LIST' style='margin-top: 40px'>
		<g:icons name="icon">
			<li tabindex='1'>
				${icon.code}<i>&#x${icon.code};</i>
			</li>
		</g:icons>
	</ul>
	<script>
		window.addEventListener("load", function ()
		{
			Array.from(document.querySelectorAll("ul.LIST > li")).forEach(function (icon)
			{

				icon.addEventListener("mouseover", function ()
				{
					this.focus();
				});
			});
		});
	</script>
</g:template>
