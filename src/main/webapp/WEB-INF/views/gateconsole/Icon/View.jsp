<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">

	<div style="display: flex; flex-wrap: wrap; justify-content: flex-start">
		<g:icons name="icon">
			<g:if condition="${icon.name ne '??????'}">
				<label data-icon="&#x${icon.code}" style="width: 9%; margin: 0.5%; height: 46px;
				       background-color: #FFFFFF; font-size: 20px; color: #006600">${icon.code}<br/>${icon.name}</label>
				</g:if>
			</g:icons>
	</div>

	<div style="display: flex; flex-wrap: wrap; justify-content: flex-start">
		<g:icons name="icon">
			<g:if condition="${icon.name eq '??????'}">
				<label data-icon="&#x${icon.code}" style="width: 9%; margin: 0.5%;
				       background-color: #FFFFFF; font-size: 20px; color: #000066">${icon.code}</label>
			</g:if>
		</g:icons>
	</div>

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
