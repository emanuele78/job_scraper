<template>
	<div class="">
		<div class="offset-1 col-10 text-left mt-5">
			<h3>Benvenuto in JobSraper!</h3>
			<p>JobScraper è un aggregatore di annunci di lavoro presenti in altri portali. Gli annunci vengono prelevati più volte al giorno tramite uno
				<a href="https://it.wikipedia.org/wiki/Web_scraping">scraper</a> programmato in Java.</p>
			<p>E' possibile effettuare delle ricerche, escludere determinate fonti e ordinare i risultati in base ad alcuni criteri. L'utente registrato può gestire gli annunci al pari di una casella di posta elettronica, archiviarli, filtrarli per letto/non letto, impostare dei preferiti e assegnare delle etichette personalizzate.</p>
			<p>Inoltre gli utenti registrati possono impostare delle ricerche attivando degli avvisi in modo da ricevere delle notifiche quando lo scraper rileva nuovi annunci corrispondenti.</p>
			<p>Al momento lo scraper è attivo solo per la regione <strong>Marche</strong>.</p>
			<p>La presente app è realizzata a solo scopo dimostrativo come demo di
				<a href="https://emanuelemazzante.dev">portfolio</a> e non fornisce alcuna garanzia del servizio reso. Tutti i marchi riportati appartengono ai legittimi proprietari.</p>
			<p>Il codice sorgente è disponibile su
				<a href="https://github.com/emanuele78/job_scraper">GitHub</a>.</p>
			<br>
			<router-link :to="{name: 'privacy'}">Privacy policy</router-link>
		</div>
	</div>
</template>
