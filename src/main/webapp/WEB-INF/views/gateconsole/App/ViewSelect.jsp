<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<fieldset>
		<label>
			Nome:
			<span>
				<g:label property='form.name'/>
			</span>
		</label>
		<label>
			Descrição:
			<span style='flex-basis: 60px'>
				<g:label property='form.description'/>
			</span>
		</label>

		<fieldset overflow: auto">
			  <table class="TREEVIEW">
				<caption>
					ACESSOS
				</caption>

				<col style="width: 50px"/>
				<col/>
				<col style="width: 200px"/>
				<col style="width: 200px"/>
				<col style="width: 200px"/>

				<thead>
					<tr>
						<th style="text-align: center">
						</th>
						<th>
							Nome
						</th>
						<th style="text-align: center">
							MODULE
						</th>
						<th style="text-align: center">
							SCREEN
						</th>
						<th style="text-align: center">
							ACTION
						</th>
					</tr>
				</thead>

				<g:iterator source="${screen.form.modules}" target="m">
					<tr data-depth="0">
						<td></td>
						<td>
							<g:icon type="${not empty m.icon ? m.icon : 'gate.entity.App$Module'}"/>
							<g:print value="${m.name}" empty="${m.id}"/>
						</td>
						<td style="text-align: center; font-weight: bold">${m.id}</td>
						<td style="text-align: center; color: #888888">*</td>
						<td style="text-align: center; color: #888888">*</td>
					</tr>
					<g:iterator source="${m.screens}" target="s">
						<tr data-depth="1">
							<td></td>
							<td style="text-indent: 80px">
								<g:icon type="${not empty s.icon ? s.icon : 'gate.entity.App$Module$Screen'}"/>
								<g:print value="${s.name}" empty="${s.id}"/>
							</td>
							<td style="text-align: center; font-weight: bold">${m.id}</td>
							<td style="text-align: center; font-weight: bold">${s.id}</td>
							<td style="text-align: center; color: #888888">*</td>
						</tr>
						<g:iterator source="${s.actions}" target="a">
							<tr data-depth="2">
								<td></td>
								<td style="text-indent: 160px">
									<g:icon type="${not empty a.icon ? a.icon : 'gate.entity.App$Module$Screen$Action'}"/>
									<g:print value="${a.name}" empty="${a.id}"/>
								</td>
								<td style="text-align: center; font-weight: bold">${m.id}</td>
								<td style="text-align: center; font-weight: bold">${s.id}</td>
								<td style="text-align: center; font-weight: bold">${a.id}</td>
							</tr>
						</g:iterator>
					</g:iterator>
				</g:iterator>
			</table>
		</fieldset>
	</fieldset>
	<g-coolbar>
		<hr/>
		<g:link module="#" screen="#" target="_hide">
			Retornar<g:icon type="cancel"/>
		</g:link>
	</g-coolbar>
</g:template>