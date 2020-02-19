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
	<g-coolbar>
		<a class="Commit" href='#' target="_hide">
			OK<g:icon type="commit"/>
		</a>
	</g-coolbar>
</g:template>
