<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div>
		<div>
			<g-coolbar>
				<G:link target="_report"
					module="#"
					screen="#"
					action="Report"/>
			</g-coolbar>
		</div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<g:link target="_report"
											module="#"
											screen="#"
											action="Report"/>
									</g-coolbar>
								--></code></pre>
		</div>
	</div>
	<div>
		<div>
			<g-coolbar>
				<G:link target="_dialog"
					module="#"
					screen="#"
					action="Progress"/>
			</g-coolbar>
		</div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<g:link target="_dialog"
											module="#"
											screen="#"
											action="Progress"/>
									</g-coolbar>
								--></code></pre>
		</div>
	</div>
	<div>
		<div>
			<g-coolbar>
				<G:link module="#"
					screen="#"
					action="Progress"/>
			</g-coolbar>
		</div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<g:link module="#"
											screen="#"
											action="Progress"/>
									</g-coolbar>
								--></code></pre>
		</div>
	</div>
	<div>
		<div>
			<g-coolbar>
				<G:link module="#"
					screen="#"
					action="Block"
					data-block="Aguarde"/>
			</g-coolbar>
		</div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<g:link module="#"
											screen="#"
											action="Block"
											data-block="Aguarde"/>
									</g-coolbar>
								--></code></pre>
		</div>
	</div>
</G:template>