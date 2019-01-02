<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/PAGE.jsp">
	<g:datalist id="func-list"
		    options="${screen.funcs}"
		    labels="${e -> e.name}"
		    values="${e -> e.id}"/>

	<form id='form' method='POST' action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Insert'>

		<fieldset>
			<label style='width: 100%'>
				<span>
					<g:hidden property="user.id"/>
					<g:hidden id="func.id" property='func.id'/>
					<g:text id='func.name'
						property='func.name'
						list="func-list"
						data-populate-field="func.id"
						tabindex='1' placeholder="Incluir"/>
					<g:shortcut method="post" module="#" screen="#" action="Insert" tabindex="1"/>
				</span>
			</label>
		</fieldset>

		<g:choose>
			<g:when condition="${empty screen.page}">
				<div class='TEXT'>
					<h1>
						Esta usuário não possui função
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
						FUNÇÕES ENCONTRADAS: ${screen.page.paginator.dataSize}
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
										    arguments="user.id=${screen.user.id}&func.id=${target.id}"/>
								</td>
							</tr>
						</g:iterator>
					</tbody>
				</table>
			</g:otherwise>
		</g:choose>
	</form>

	<script>
		document.getElementById("func.id")
			.addEventListener("field-populated", function ()
			{
				this.form.submit();
			});
	</script>
</g:template>