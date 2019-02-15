<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<div style='display: flex;
	     align-items: center;
	     justify-content: center;
	     height: calc(100vh - 40px)'>
		<div style="flex-basis: 50%">
			<progress-status id="progress"
					 process="${process}"
					 ></progress-status>

			<div class='COOLBAR'>
				<a id='action' class="Hide" href='#'
				   data-confirm='Tem certeza de que deseja fechar?'>
					Processando<g:icon type="2017"/>
				</a>
			</div>
		</div>
	</div>

	<script>
		window.addEventListener("load", function ()
		{
			var action = document.getElementById("action");
			var progress = document.getElementById("progress");
			progress.addEventListener("commited", function ()
			{
				action.style.color = "#006600";
				action.innerHTML = "Ok<i>&#X1000;</i>";
				action.removeAttribute("data-confirm");
			});
			progress.addEventListener("canceled", function ()
			{
				action.style.color = "#660000";
				action.innerHTML = "Ok<i>&#X1001;</i>";
				action.removeAttribute("data-confirm");
			});

			progress.addEventListener("redirected", event => window.location.href = event.detail);
		});
	</script>
</g:template>