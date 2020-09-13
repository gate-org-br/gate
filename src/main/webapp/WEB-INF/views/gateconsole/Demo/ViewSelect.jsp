<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div style="grid-column: 1 / span 2">
		<div>
			<pre class="language-markup"><code><!--
									<fieldset>
										<g:selectn property="types"
											   options="${screen.getOptions()}"/>
									</fieldset>
								--></code></pre>
		</div>
		<div style="flex-basis: 220px">
			<fieldset>
				<G:selectn property="types"
					   options="${screen.getOptions()}"/>
			</fieldset>
		</div>
	</div>
</G:template>