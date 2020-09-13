<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<g-tab-control type="dummy">
		<g:link module="#" screen="#" action="TabBar" data-selected="${empty ACTION or ACTION eq 'TabBar'}"/>
		<g:link module="#" screen="#" action="Pickers"  data-selected="${ACTION eq 'Pickers'}"/>
		<g:link module="#" screen="#" action="Tooltip"  data-selected="${ACTION eq 'Tooltip'}"/>
		<g:link module="#" screen="#" action="Message"  data-selected="${ACTION eq 'Message'}"/>
		<g:link module="#" screen="#" action="Chart"  data-selected="${ACTION eq 'Chart'}"/>
		<g:link module="#" screen="#" action="ContextMenu"  data-selected="${ACTION eq 'ContextMenu'}"/>
		<g:link module="#" screen="#" action="Select"  data-selected="${ACTION eq 'Select'}"/>
		<g:link module="#" screen="#" action="Other"  data-selected="${ACTION eq 'Other'}"/>
		<g:link module="#" screen="#" action="Grid1"  data-selected="${ACTION eq 'Grid1'}"/>
		<g:link module="#" screen="#" action="Grid2"  data-selected="${ACTION eq 'Grid2'}"/>
		<div>
			<div style="display: grid; grid-template-columns: 1fr 1fr; grid-gap: 10px;">
				<g:insert/>
			</div>
		</div>
	</g-tab-control>
</g:template>