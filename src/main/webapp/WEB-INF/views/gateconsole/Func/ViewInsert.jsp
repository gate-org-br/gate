<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<form method='POST' action='#'>
		<fieldset>
			<legend>
				Nova Função<g:icon type="update"/>
			</legend>

			<label>
				Nome:
				<span>
					<g:text property='form.name' tabindex='1'/>
				</span>
			</label>
		</fieldset>

		<g-coolbar>
			<g:link class="Commit" method="post" module="#" screen="#" action="#" tabindex='2'>
				Concluir<g:icon type="commit"/>
			</g:link>
			<hr/>
			<a class='Cancel Hide' tabindex='2'>
				Desistir<g:icon type='cancel'/>
			</a>
		</g-coolbar>
	</form>
</g:template>