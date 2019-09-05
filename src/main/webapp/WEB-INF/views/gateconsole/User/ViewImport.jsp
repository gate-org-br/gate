<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>
<g:template filename="/WEB-INF/views/MAIN.jsp">
	<g:choose>
		<g:when condition="${empty screen.page}">
			<div class='TEXT'>
				<h1>
					Este registro n&atilde;o possui nenhum usu&aacute;rio associado
				</h1>
			</div>
		</g:when>
		<g:otherwise>
			<div style="overflow-x:auto;">
				<table class="c2 c3 c4" style="min-width: 820px">
					<col/>
					<col style="width: 250px"/>
					<col style="width: 125px"/>
					<col style="width: 125px"/>

					<caption>
						USUÁRIOS
					</caption>
					<thead>
						<tr>
							<th data-sortable>
								Nome
							</th>
							<th data-sortable>
								Login
							</th>
							<th data-sortable>
								Cadastro
							</th>
							<th data-sortable>
								Ativo
							</th>
						</tr>
					</thead>
					<tbody>
						<g:iterator source="${screen.page}" target="target">
							<tr data-target='_parent' data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${target.id}'>
								<td><g:print value="${target.name}"/></td>
								<td><g:print value="${target.userID}"/></td>
								<td data-value="${g:number(target.registration)}"><g:print value="${target.registration}"/></td>
								<td><g:print value="${target.active}"/></td>
							</tr>
						</g:iterator>
					</tbody>	
				</table>			
			</div>
		</g:otherwise>
	</g:choose>
</g:template>