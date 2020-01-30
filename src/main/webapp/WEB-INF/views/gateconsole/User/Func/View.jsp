<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/PAGE.jsp">
	<form method='POST' action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Insert'>
		<fieldset>
			<label>
				<span>
					<g:hidden property="user.id"/>
					<g:text property='func.name'
						options="${screen.funcs}"
						labels="${e -> e.name}"
						values="${e -> e.id}"
						tabindex='1'
						placeholder="Incluir"/>
					<g:hidden id='func.id' property='func.id'
						  data-method="post"/>
				</span>
			</label>
		</fieldset>
	</form>

	<g:choose>
		<g:when condition="${empty screen.page}">
			<div class='TEXT'>
				<h1>
					Esta usuário não possui função
				</h1>
			</div>
		</g:when>
		<g:otherwise>
			<table class="c2">
				<caption>
					FUNÇÕES ENCONTRADAS: ${screen.page.size()}
				</caption>
				<thead>
					<tr>
						<th>
							Nome
						</th>
						<th style="width: 48px">
							<g:icon type="delete"/>
						</th>
					</tr>
				</thead>
				<tbody>
					<g:iterator source="${screen.page}" target="target">
						<tr>
							<td>
								<g:print value="${target.name}"/>
							</td>
							<td>
								<g:shortcut module="#" screen="#" action="Delete"
									    arguments="user.id=${screen.user.id}&func.id=${target.id}"/>
							</td>
						</tr>
					</g:iterator>
				</tbody>
			</table>
		</g:otherwise>
	</g:choose>
</g:template>