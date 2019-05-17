<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/PAGE.jsp">
	<form method='POST' action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Insert'>
		<fieldset>
			<legend>
				Inserir Acesso<g:icon type="insert"/>
			</legend>
			<g:hidden property="form.user.id" required=''/>
			<g:hidden property="form.role.id" required=''/>
			<g:hidden property="form.func.id" required=''/>
			<label x2>
				Modo:
				<span>
					<g:select property="form.mode" tabindex='1'/>
				</span>
			</label>
			<label x2>
				Tipo:
				<span>
					<g:choose>
						<g:when condition="${not empty screen.form.role.id}">
							<g:select property="form.type" tabindex='1'/>
						</g:when>
						<g:when condition="${not empty screen.form.func.id}">
							<g:select property="form.type" tabindex='1'/>
						</g:when>
						<g:otherwise>
							<g:hidden property="form.type" value='1'/>
							<g:icon type="gate.entity.Auth$Type:PRIVATE"/>Privado
						</g:otherwise>
					</g:choose>
				</span>
			</label>
			<label x4>
				Module:
				<span>
					<g:text id='module' property="form.module" tabindex='1'/>
					<g:shortcut module="#" screen="#" action="Search" data-get='module, screen, action' title='Pesquisar acessos'/>
				</span>
			</label>
			<label x4>
				Screen:
				<span>
					<g:text id='screen' property="form.screen" tabindex='1'/>
					<g:shortcut module="#" screen="#" action="Search" data-get='module, screen, action' title='Pesquisar acessos'/>
				</span>
			</label>
			<label x4>
				Action:
				<span>
					<g:text id='action' property="form.action" tabindex='1'/>
					<g:shortcut module="#" screen="#" action="Search" data-get='module, screen, action' title='Pesquisar acessos'/>
				</span>
			</label>
		</fieldset>
		<div class='Coolbar'>
			<g:link method="post" module="#" screen="#" action="Insert" tabindex="2">
				Inserir<g:icon type="insert"/>
			</g:link>
		</div>

		<g:choose>
			<g:when condition="${empty screen.page}">
				<div class='TEXT'>
					<h1>
						N&atilde;o h&aacute; nenhum acesso cadastrado para este registro
					</h1>
				</div>
			</g:when>
			<g:otherwise>
				<table breakpoint1 c1 c2 c3 c4 c5 c6>
					<caption>
						ACESSOS
					</caption>

					<thead>
						<tr>
							<th>
								Modo
							</th>
							<th>
								Tipo
							</th>
							<th>
								Module
							</th>
							<th>
								Screen
							</th>
							<th>
								Action
							</th>
							<th>
								<g:icon type="delete"/>
							</th>
						</tr>
					</thead>

					<tfoot></tfoot>

					<tbody>
						<g:iterator source="${screen.page}" target="item" index="indx">
							<tr>
								<td title="Modo">
									<g:icon type="${item.mode}"/>&nbsp;<g:print value="${item.mode}"/>
								</td>
								<td title="Tipo">
									<g:icon type="${item.type}"/>&nbsp;<g:print value="${item.type}"/>
								</td>
								<td title="Module">
									<g:print value="${item.module}" empty="*"/>
								</td>
								<td title="Screen">
									<g:print value="${item.screen}" empty="*"/>
								</td>
								<td title="Action">
									<g:print value="${item.action}" empty="*"/>
								</td>
								<td title="Remover">
									<g:link module="#" screen="#" action="Delete" arguments="form.id=${item.id}&form.role.id=${screen.form.role.id}&form.user.id=${screen.form.user.id}">
										<g:icon type="delete"/>
									</g:link>
								</td>
							</tr>
						</g:iterator>
					</tbody>
				</table>
			</g:otherwise>
		</g:choose>
	</form>
</g:template>

