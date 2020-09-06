<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<fieldset>
		<legend>
			<g:path/>
		</legend>
		<label>
			Nome:
			<span>
				<g:label property='form.name'/>
			</span>
		</label>
	</fieldset>

	<g-coolbar>
		<g:link module='#' screen='#' action='Update' arguments="form.id=${screen.form.id}">
			Alterar<g:icon type="update"/>
		</g:link>
		<g:link module='#' screen='#' action="Delete" arguments="form.id=${screen.form.id}"
			style='color: #660000' data-confirm="Tem certeza de que deseja remover este registro?">
			Remover<g:icon type="delete"/>
		</g:link>
		<hr/>
		<a href="#" target="_hide">
			Retornar<g:icon type="return"/>
		</a>
	</g-coolbar>

	<g-tab-control>
		<g:link module="#" screen="Auth" arguments="form.func.id=${screen.form.id}"/>
		<g:link module="#" screen="FuncScreen$User" arguments="func.id=${screen.form.id}"/>
		<g:link module="#" screen="FuncScreen$Role" arguments="func.id=${screen.form.id}"/>
	</g-tab-control>
</g:template>