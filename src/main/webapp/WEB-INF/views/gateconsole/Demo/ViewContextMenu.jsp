<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div>
		<pre class="language-markup"><code><!--
									<div class="TEXT">
										<h1>
											Clique aqui com o botão direito
										</h1>

										<g-context-menu>
											<g:link module="#" screen="Icon"/>
											<button id="action1" href="#">
												Action 1<i>&#X2003;</i>
											</button>
										</g-context-menu>
									</div>
								--></code></pre>
	</div>
	<div>
		<pre class="language-javascript"><code><!--
									window.addEventListener("load", function ()
									{
										document.getElementById("action1")
											.addEventListener("click",
												e => alert(e.target.parentNode
														.context.innerText));

									});
								--></code></pre>
	</div>
	<div style="grid-column: 1 / span 2">
		<div class="TEXT" data-context-menu="g-context-menu">
			<h1>
				Clique aqui com o botão direito
			</h1>
		</div>

		<g-context-menu id="g-context-menu">
			<G:link module="#" screen="Icon"/>
			<button id="action1" href="#">
				Action 1<i>&#X2003;</i>
			</button>
		</g-context-menu>

		<script>
			window.addEventListener("load", function ()
			{
				document.getElementById("action1")
					.addEventListener("click",
						e => alert(e.target.parentNode
								.context.innerText));

			});
		</script>
	</div>
</G:template>