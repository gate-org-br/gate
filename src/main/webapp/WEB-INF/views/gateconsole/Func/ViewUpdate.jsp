<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<form method='POST' action='#' enctype='multipart/form-data'>
		<fieldset>
			<legend>
				<g:name type="${action}"/>
				<g:icon type="${action}"/>
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
			<g:link class="Cancel" module="#" screen="#" action="Select" arguments="form.id=${screen.form.id}" tabindex='2'>
				Desistir<g:icon type='cancel'/>
			</g:link>
		</g-coolbar>

		<g:hidden property='form.id'/>
	</form>
</g:template>