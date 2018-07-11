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
				<colgroup>
					<col style="width: 20%"/>
					<col style="width: 80%"/>
				</colgroup>

				<caption>
					USU&Aacute;RIOS
				</caption>
				<thead>
					<tr>
						<th style='text-align: center'>
							Login
						</th>
						<th>
							Nome
						</th>
					</tr>
				</thead>
				<tbody>
					<g:iterator source="${screen.page}" target="target">
						<tr data-target='_parent' 
							data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${target.id}'>
							<td style='text-align: center'>
								${target.userID}
							</td>
							<td style='text-align: left'>
								${target.name}
							</td>
						</tr>
					</g:iterator>
				</tbody>	
			</table>			
		</g:otherwise>
	</g:choose>
</g:template>