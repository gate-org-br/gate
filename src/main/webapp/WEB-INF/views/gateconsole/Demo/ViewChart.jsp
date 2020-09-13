<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div style="grid-column: 1 / span 2">
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href='#'
										   data-chart='pchart'
										   title='Programadores por Linguagem'
										   data-series='[
										   ["Linguagem", "Programadores"],
										   ["Java",10000],
										   ["Javascript",8000],
										   ["Phyton",6000],
										   ["C++",6000],
										   ["C#",5000],
										   ["Ruby",2500],
										   ["Indefinida",80]]'>
											Gráfico<g:icon type="pchart"/>
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href='#'
				   data-chart='pchart'
				   title='Programadores por Linguagem'
				   data-series='[
				   ["Linguagem", "Programadores"],
				   ["Java",10000],
				   ["Javascript",8000],
				   ["Phyton",6000],
				   ["C++",6000],
				   ["C#",5000],
				   ["Ruby",2500],
				   ["Indefinida",80]]'>
					Gráfico<G:icon type="pchart"/>
				</a>
			</g-coolbar>
		</div>
	</div>
</G:template>