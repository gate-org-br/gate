<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div style="grid-column: 1 / span 2">
		<div>
			<pre class="language-markup"><code><!--
									<fieldset>
										<g-grid class="c1 c2 c3" 
											action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Data&form.id=@0'
											cols='[null, "Nome", "E-Mail", {"head": "Ativo", "style": "width: 120px"}]'
											data='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Data&size=@size'
											more='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Data&size=@size'>
										</g-grid>
									</fieldset>
								--></code></pre>
		</div>
		<div>
			<fieldset>
				<g-grid class="c1 c2 c3" 
					action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Data&form.id=@0'
					cols='[null, "Nome", "E-Mail", {"head": "Ativo", "style": "width: 120px"}]'
					data='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Data&size=@size'>
				</g-grid>
			</fieldset>
		</div>
	</div>
</G:template>