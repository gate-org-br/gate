<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div>
		<div>
			<pre class="language-markup" style="height: 172px"><code><!--
									<fieldset>
										<g:selectn property="types"/>
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
	<div>
		<div>
			<pre class="language-markup"><code><!--
			<fieldset>
				<label>
					Select:
					<span>
						<g:selectx property="types"/>
					</span>
				</label>
			</fieldset>
								--></code></pre>
		</div>
		<div style="flex-basis: 220px">
			<fieldset>
				<label>
					Select:
					<span>
						<G:selectx property="types"/>
					</span>
				</label>
			</fieldset>
		</div>
	</div>
</G:template>