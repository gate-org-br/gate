<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/PAGE.jsp">
	<g:choose>
		<g:when condition="${empty screen.page}">
			<div class='TEXT'>
				<h1>
					Este registro não possui nenhum usuário associado
				</h1>
			</div>
		</g:when>
		<g:otherwise>
			<table>
				<caption>
					PERFIS
				</caption>
				<thead>
					<tr>
						<th>
							Nome
						</th>
					</tr>
				</thead>
				<tbody>
					<g:iterator source="${screen.page}" target="target">
						<tr data-target='_parent'data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${target.id}'>
							<td>
								${target.name}
							</td>
						</tr>
					</g:iterator>
				</tbody>
			</table>
		</g:otherwise>
	</g:choose>
</g:template>
