<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<fieldset>
		<legend>
			<g:name type="${action}"/>
			<g:icon type="${action}"/>
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
		<a class="Cancel Hide" href="#">
			Fechar<g:icon type="cancel"/>
		</a>
	</g-coolbar>

	<div class='PageControl'>
		<ul>
			<g:menuitem module="#" screen="Auth" arguments="form.func.id=${screen.form.id}"/>
			<g:menuitem module="#" screen="FuncScreen$User" arguments="func.id=${screen.form.id}"/>
			<g:menuitem module="#" screen="FuncScreen$Role" arguments="func.id=${screen.form.id}"/>
		</ul>
	</div>
</g:template>