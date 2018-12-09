<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<form method='POST' action='#'>
		<fieldset>
			<legend>
				Pesquisar
			</legend>
			<label style='width: 100%'>
				Nome/Login/Sigla:
				<span>
					<g:text property='form' tabindex='1'/>
				</span>
			</label>
		</fieldset>

		<div class='COOLBAR'>
			<g:link method="post" module='#'
				screen="#" action="#" tabindex='2'/>
		</div>
	</form>


	<g:choose>
		<g:when condition="${screen.GET}">
			<div class="TEXT">
				<h1>
					Entre com o nome a ser pesquisado
				</h1>
			</div>
		</g:when>
		<g:when condition="${empty screen.page}">
			<div class="TEXT">
				<h1>
					Nenhum registro encontrado
				</h1>
			</div>
		</g:when>
		<g:otherwise>
			<table>
				<col style='width: 06%'/>
				<col style='width: 24%'/>
				<col style='width: 70%'/>

				<caption>
					REGISTROS ENCONTRADOS: ${screen.page.size()}
				</caption>

				<thead>
					<tr>
						<th style='text-align: center'>
							Tipo
						</th>
						<th style='text-align: center'>
							Login/Sigla
						</th>
						<th style='text-align: left'>
							Nome
						</th>
					</tr>
				</thead>

				<tfoot></tfoot>

				<tbody>
					<g:iterator source="${screen.page}" index="indx" target="item">
						<g:choose>
							<g:when condition="${item.class.name eq 'gate.entity.User'}">
								<tr data-target='_dialog' data-action='Gate?MODULE=${MODULE}&SCREEN=User&ACTION=Select&form.id=${item.id}' tabindex='3'>
									<td  style='text-align: center'>
										<g:icon type="${item}"/>
									</td>
									<td  style='text-align: center'>
										<g:print value="${item.userID}"/>
									</td>
									<td style='text-align: left'>
										<g:print value="${item.name}"/>
									</td>
								</tr>
							</g:when>
							<g:when condition="${item.class.name eq 'gate.entity.Role'}">
								<tr data-target='_dialog' data-action='Gate?MODULE=${MODULE}&SCREEN=Role&ACTION=Select&form.id=${item.id}' tabindex='3'>
									<td  style='text-align: center'>
										<g:icon type="${item}"/>
									</td>
									<td  style='text-align: center'>
										<g:print value="${item.roleID}"/>
									</td>
									<td style='text-align: left'>
										<g:print value="${item.name}"/>
									</td>
								</tr>
							</g:when>
						</g:choose>
					</g:iterator>
				</tbody>
			</table>

			<g:if condition="${screen.page.size() == 1}">
				<script>
					<g:with name="target" value="${screen.page[0]}">
					new Dialog()
						.setTarget("Gate?MODULE=${MODULE}&SCREEN=${target.class.simpleName}&ACTION=Select&form.id=${target.id}")
						.show();
					</g:with>
				</script>
			</g:if>
		</g:otherwise>
	</g:choose>
</g:template>