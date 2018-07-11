<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<ul class='LIST'>
		<g:icons name="icon">
			<g:if condition="${icon.name ne '??????'}">
				<li>
					${icon.name}<i>&#x${icon.code};</i>
				</li>
			</g:if>
		</g:icons>
	</ul>
	<ul class='LIST' style='margin-top: 40px'>
		<g:icons name="icon">
			<li>
				${icon.code}<i>&#x${icon.code};</i>
			</li>
		</g:icons>
	</ul>	
</g:template>
