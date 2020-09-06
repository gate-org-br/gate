<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/FULL.jsp">
	<form method='POST' action='#'>
		<fieldset>
			<legend>
				<g:path/>
			</legend>
			<label>
				Pesquisar:
				<span>
					<g:text property='form' tabindex='1' placeholder="Pesquisar usuários (nome, email ou login) ou perfis (nome, email ou sigla)"/>
				</span>
			</label>
		</fieldset>

		<g-coolbar>
			<g:link method="post" module='#' screen="#" action="#" tabindex='2'>
				Pesquisar<g:icon type="search"/>
			</g:link>
		</g-coolbar>
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
			<table class="c1 c2">
				<caption>
					REGISTROS ENCONTRADOS: ${screen.page.size()}
				</caption>

				<thead>
					<tr>
						<th style='width: 64px'>
							Tipo
						</th>
						<th style='width: 256px'>
							Login/Sigla
						</th>
						<th>
							Nome
						</th>
					</tr>
				</thead>

				<tfoot></tfoot>

				<tbody>
					<g:iterator source="${screen.page}" index="indx" target="item">
						<g:choose>
							<g:when condition="${item.class.name eq 'gate.entity.User'}">
								<tr data-target='_stack' data-action='Gate?MODULE=${MODULE}&SCREEN=User&ACTION=Select&form.id=${item.id}' tabindex='3'>
									<td>
										<g:icon type="${item}"/>
									</td>
									<td>
										<g:print value="${item.userID}"/>
									</td>
									<td>
										<g:print value="${item.name}"/>
									</td>
								</tr>
							</g:when>
							<g:when condition="${item.class.name eq 'gate.entity.Role'}">
								<tr data-target='_stack' data-action='Gate?MODULE=${MODULE}&SCREEN=Role&ACTION=Select&form.id=${item.id}' tabindex='3'>
									<td>
										<g:icon type="${item}"/>
									</td>
									<td>
										<g:print value="${item.roleID}"/>
									</td>
									<td>
										<g:print value="${item.name}"/>
									</td>
								</tr>
							</g:when>
						</g:choose>
					</g:iterator>
				</tbody>
			</table>

			<script>
				window.addEventListener("load", function ()
				{
					let actions = Array.from(document.querySelectorAll("tr[data-target]"));
					if (actions.length === 1)
						actions[0].click();
				});
			</script>
		</g:otherwise>
	</g:choose>
</g:template>
