<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<table class="TREEVIEW">
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
		<g:iterator source="${screen.apps}" target="_app">
			<tr data-depth="0">
				<td></td>
				<td>
					<g:icon type="gate.entity.App"/>
					<g:print value="${_app.id}" empty="${_app.id}"/>
				</td>
				<td style="text-align: center; color: #888888">N/A</td>
				<td style="text-align: center; color: #888888">N/A</td>
				<td style="text-align: center; color: #888888">N/A</td>
			</tr>
			<g:iterator source="${_app.modules}" target="_module">
				<tr data-depth="1" data-ret='${_module.id},,'>
					<td></td>
					<td style="text-indent: 80px">
						<g:icon type="${not empty _module.icon ? _module.icon : 'gate.entity.App$Module'}"/>
						<g:print value="${_module.name}" empty="${_module.id}"/>
					</td>
					<td style="text-align: center; font-weight: bold">${_module.id}</td>
					<td style="text-align: center; color: #888888">*</td>
					<td style="text-align: center; color: #888888">*</td>
				</tr>
				<g:iterator source="${_module.screens}" target="_screen">
					<tr data-depth="2" data-ret='${_module.id}, ${_screen.id},'>
						<td></td>
						<td style="text-indent: 160px">
							<g:icon type="${not empty _screen.icon ? _screen.icon : 'gate.entity.App$Module$Screen'}"/>
							<g:print value="${_screen.name}" empty="${_screen.id}"/>
						</td>
						<td style="text-align: center; font-weight: bold">${_module.id}</td>
						<td style="text-align: center; font-weight: bold">${_screen.id}</td>
						<td style="text-align: center; color: #888888">*</td>
					</tr>
					<g:iterator source="${_screen.actions}" target="_action">
						<tr data-depth="3" data-ret='${_module.id}, ${_screen.id}, ${_action.id}'>
							<td></td>
							<td style="text-indent: 240px">
								<g:icon type="${not empty _action.icon ? _action.icon : 'gate.entity.App$Module$Screen$Action'}"/>
								<g:print value="${_action.name}" empty="${_action.id}"/>
							</td>
							<td style="text-align: center; font-weight: bold">${_module.id}</td>
							<td style="text-align: center; font-weight: bold">${_screen.id}</td>
							<td style="text-align: center; font-weight: bold">${_action.id}</td>
						</tr>
					</g:iterator>
				</g:iterator>
			</g:iterator>
		</g:iterator>
	</table>

	<g-coolbar>
		<a href="#" target="_hide">
			Fechar<g:icon type="cancel"/>
		</a>
	</g-coolbar>
</g:template>