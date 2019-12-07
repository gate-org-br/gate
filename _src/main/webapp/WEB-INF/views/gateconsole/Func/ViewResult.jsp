<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<g:if condition="${not empty screen.messages}">
		<div class="TEXT">
			<g:iterator source="${screen.messages}" target="message">
				<h1>
					${message}
				</h1>
			</g:iterator>
		</div>
	</g:if>
	<div class='COOLBAR'>
		<a  class='Commit Hide' href='#'>
			Fechar<g:icon type="cancel"/>
		</a>
	</div>
</g:template>
