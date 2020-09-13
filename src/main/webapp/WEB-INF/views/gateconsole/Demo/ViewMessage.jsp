<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#"
										   style="color: #000000"
										   onclick="Message.info('Informação')">
					<G:icon type="2015"/>Informação
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href="#"
				   style="color: #000000"
				   onclick="Message.info('Informação')">
					<G:icon type="2015"/>Informação
				</a>
			</g-coolbar>
		</div>
	</div>
	<div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#"
										   style="color: #006600"
										   onclick="Message.success('Sucesso')">
					<G:icon type="1000"/>Sucesso
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href="#"
				   style="color: #006600"
				   onclick="Message.success('Sucesso')">
					<G:icon type="1000"/>Sucesso
				</a>
			</g-coolbar>
		</div>
	</div>
	<div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#"
										   style="color: #444400"
										   onclick="Message.warning('Alerta')" >
					<G:icon type="1007"/>Alerta
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href="#"
				   style="color: #444400"
				   onclick="Message.warning('Alerta')" >
					<G:icon type="1007"/>Alerta
				</a>
			</g-coolbar>
		</div>
	</div>

	<div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#"
										   style="color: #660000"
										   onclick="Message.error('Erro')">
					<G:icon type="1001"/>Erro
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href="#"
				   style="color: #660000"
				   onclick="Message.error('Erro')">
					<G:icon type="1001"/>Erro
				</a>
			</g-coolbar>
		</div>
	</div>
</G:template>