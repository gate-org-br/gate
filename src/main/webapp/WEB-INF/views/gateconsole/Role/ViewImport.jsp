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
			<table>
				<col style="width: 100%"/>

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
