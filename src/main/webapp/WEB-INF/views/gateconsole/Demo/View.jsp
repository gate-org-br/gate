<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/FULL.jsp">
	<style>
		div.demo { display: flex;
			   flex-wrap: wrap }

		div.demo > div { width: 50%;
				 flex-grow: 1;
				 display: flex;
				 min-width: 300px;
				 flex-direction: column;
				 border: 8px solid transparent }

		div.demo > div > div { margin: 0;
				       padding: 0;
				       flex-grow: 1;
				       display: flex;
				       min-height: 100px;
				       align-items: center;}

		div.demo > div > div:first-child { flex-basis: 0 }
		div.demo > div > div:not(:first-child){ background-color: #F5F2F0 }

		div.demo > div > div > * { margin: 0; flex-grow: 1; }
	</style>
	<g-tab-control>
		<a href="#">
			Select
		</a>
		<div>
			<div class='demo'>
			
			</div>
		</div>
		<a href="#">
			Grid
		</a>
		<div>
			<div class='demo'>
			</div>
		</div>							
		<a href="#">
			Outros
		</a>
		<div>
			<div class='demo'>

			</div>
		</div>
	</g-tab-control>
</G:template>