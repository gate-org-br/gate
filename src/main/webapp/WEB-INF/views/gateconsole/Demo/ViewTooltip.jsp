<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/gateconsole/Demo/Main.jsp">
	<div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#" data-tooltip='This is a tooltip'>
											Hover here<G:icon type="2015"/>
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href="#" data-tooltip='This is a tooltip'>
					Hover here<G:icon type="2015"/>
				</a>
			</g-coolbar>
		</div>
	</div>
	<div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#" data-tooltip='["Item 1", "Item 2", "Item 3"]'>
											Hover here<G:icon type="2015"/>
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href="#" data-tooltip='["Item 1", "Item 2", "Item 3"]'>
					Hover here<G:icon type="2015"/>
				</a>
			</g-coolbar>
		</div>
	</div>
	<div>
		<div>
			<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#" data-tooltip='{"Name": "Tooltip",
										   "Type": "Attribute",
										   "Description": "This is a tooltip"}'>
											Hover here<G:icon type="2015"/>
										</a>
									</g-coolbar>
								--></code></pre>
		</div>
		<div>
			<g-coolbar>
				<a href="#" data-tooltip='{"Name": "Tooltip", "Type": "Attribute", "Description": "This is a tooltip"}'>
					Hover here<G:icon type="2015"/>
				</a>
			</g-coolbar>
		</div>
	</div>
</G:template>