<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/PAGE.jsp">
	<form id='form' method='POST' action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Insert'>
		<fieldset>
			<label>
				<span>
					<g:text property='user.name'
						options="${screen.users}"
						labels="${e -> e.name}"
						values="${e -> e.id}"
						tabindex='1'
						placeholder="Incluir"/>
					<g:hidden id="user.id" property='user.id' data-method="post"/>
				</span>
			</label>
		</fieldset>

		<g:hidden property="func.id"/>
	</form>


	<g:choose>
		<g:when condition="${empty screen.page}">
			<div class='TEXT'>
				<h1>
					Nenhum usuário possui esta função
				</h1>
			</div>
		</g:when>
		<g:otherwise>
			<table style='table-layout: fixed'>
				<colgroup>
					<col/>
					<col style="width: 48px"/>
				</colgroup>

				<caption>
					USUÁRIOS ENCONTRADOS: ${screen.page.paginator.dataSize}
				</caption>
				<thead>
					<tr>
						<th>
							Nome
						</th>
						<th style="text-align: center">
							<g:icon type="delete"/>
						</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<td colspan='2' style='text-align: right'>
							<g:paginator/>
						</td>
					</tr>
				</tfoot>
				<tbody>
					<g:iterator source="${screen.page}" target="target">
						<tr>
							<td>
								<g:print value="${target.name}"/>
							</td>
							<td style="text-align: center">
								<g:shortcut module="#" screen="#" action="Delete" style="color: #660000"
									    arguments="func.id=${screen.func.id}&user.id=${target.id}"/>
							</td>
						</tr>
					</g:iterator>
				</tbody>
			</table>
		</g:otherwise>
	</g:choose>
</g:template>