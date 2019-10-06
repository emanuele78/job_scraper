<template>
	<div class="">
		<div class="offset-1 col-10 text-left mt-5">
			<h3>Benvenuto in Job Scraper!</h3>
			<p class="mt-4">
				<strong>Job Scraper</strong> è un aggregatore di annunci di lavoro presenti in altri portali. Gli annunci vengono prelevati tramite uno
				<a href="https://it.wikipedia.org/wiki/Web_scraping">scraper</a> programmato in Java. E' possibile impostare un cron job nel server in modo da prelevare gli annunci in determinati orari durante il giorno. Ai fini della presente demo non sono stati impostati cron job ma lo scraper può essere avviato manualmente nell'apposita <router-link :to="{name: 'scraper'}">pagina</router-link>.
			</p>
			<p>E' possibile effettuare delle ricerche, escludere determinate fonti e ordinare i risultati in base ad alcuni criteri. L'utente registrato può gestire gli annunci come se fosse una casella di posta elettronica, archiviarli, filtrarli per letto/non letto, impostare dei preferiti e assegnare delle etichette personalizzate.</p>
			<p>Inoltre gli utenti registrati possono salvare delle ricerche impostando termini specifici. Quando lo scraper rileva annunci contenenti tali termini, gli utenti registrati ricevono un avviso tramite e-mail.</p>
			<p>Al momento lo scraper è attivo solo per la regione
				<strong>Marche</strong>.
			</p>
			<p>La presente app è realizzata a solo scopo dimostrativo come demo di
				<a href="https://emanuelemazzante.dev">portfolio</a> e non fornisce alcuna garanzia del servizio reso. Tutti i marchi riportati appartengono ai legittimi proprietari.
			</p>
			<p>Il codice sorgente è disponibile su
				<a href="https://github.com/emanuele78/job_scraper">GitHub</a>.
			</p>
			<br>
			<router-link :to="{name: 'privacy'}">Privacy policy</router-link>
		</div>
	</div>
</template>
