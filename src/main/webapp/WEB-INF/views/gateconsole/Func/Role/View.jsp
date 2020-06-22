<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/PAGE.jsp">
	<form id='form' method='POST' action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Insert'>
		<fieldset>
			<label>
				<span>
					<g:hidden property="func.id"/>
					<g:select id='role' empty="Incluir"
						  options="${screen.roles}"
						  values="${e -> e.id}"
						  labels="${e -> e.name}"
						  children="${e -> e.children}"
						  property='role.id'
						  tabindex='1'
						  data-method="post"/>
				</span>
			</label>
		</fieldset>
	</form>

	<g:choose>
		<g:when condition="${empty screen.page}">
			<div class='TEXT'>
				<h1>
					Nenhum perfil possui esta função
				</h1>
			</div>
		</g:when>
		<g:otherwise>
			<table class="c2">
				<colgroup>
					<col/>
					<col style="width: 48px"/>
				</colgroup>

				<caption>
					PERFIS ENCONTRADOS: ${screen.page.paginator.dataSize}
				</caption>
				<thead>
					<tr>
						<th>
							Nome
						</th>
						<th>
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
							<td>
								<g:shortcut module="#" screen="#" action="Delete" style="color: #660000"
									    arguments="func.id=${screen.func.id}&role.id=${target.id}"/>
							</td>
						</tr>
					</g:iterator>
				</tbody>
			</table>
		</g:otherwise>
	</g:choose>
</g:template>