<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<g:choose>
		<g:when condition="${not empty subscriptions}">
			<table>
				<col style='width: 12.5%'/>
				<col style='width: 37.5%'/>
				<col style='width: 12.5%'/>
				<col style='width: 12.5%'/>
				<col style='width: 25%'/>

				<caption>
					PEDIDOS DE ACESSO: ${subscriptions.size()}
				</caption>

				<thead>
					<tr>
						<th style='text-align: center'>
							Login
						</th>
						<th style='text-align: left'>
							Nome
						</th>
						<th style='text-align: center'>
							Telefone
						</th>
						<th style='text-align: center'>
							Celular
						</th>
						<th style='text-align: left'>
							E-Mail
						</th>
					</tr>
				</thead>

				<tfoot></tfoot>

				<tbody>
					<g:iterator source="${subscriptions}" index="indx" target="item">
						<tr data-target="_dialog" data-action='Gate?MODULE=gateconsole.screen&SCREEN=Access&ACTION=Select&form.id=${item.id}'
						    title='Requisi&ccedil;&atilde;o de Cadastro'>
							<td style='text-align: center'>${item.userID}</td>
							<td style='text-align: left'>${item.name}</td>
							<td style='text-align: center'>${item.phone}</td>
							<td style='text-align: center'>${item.cellPhone}</td>
							<td style='text-align: left'>${item.email}</td>
						</tr>
					</g:iterator>
				</tbody>
			</table>
		</g:when>
		<g:otherwise>
			<div class='TEXT'>
				<h1>
					Nenhum usu&aacute;rio pendente de cadastro encontrado
				</h1>
			</div>
		</g:otherwise>
	</g:choose>
</g:template>