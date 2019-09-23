<template>
	<div class="">
		<div class="col-12 text-left">
			<div class="header mt-4 mb-4">
				<h4>Statistiche dello scraper</h4>
				<div class="header_right">
					<h6 class="d-inline mr-3">Scraper:
						<span :class="{'text-primary':!scraperStatus, 'text-success':scraperStatus}">{{scraperStatus? 'running':'Stand-by'}}</span>
					</h6>
					<button :disabled="scraperStatus===1" @click="startScraper()" class="btn btn-primary btn-sm" type="button">Avvia scraper</button>
				</div>
			</div>
		</div>
		<div class="col-12">
			<table class="table table-striped">
				<thead>
				<tr>
					<th scope="col">Scraper</th>
					<th scope="col">Stato</th>
					<th scope="col">Regione</th>
					<th scope="col">Ultima attività</th>
					<th scope="col">Annunci aggiunti</th>
					<th scope="col">Annunci saltati</th>
					<th scope="col">Errori</th>
				</tr>
				</thead>
				<tbody>
				<tr v-for="stat in stats">
					<th scope="row">{{stat.scraper.showed_name | capitalize}}</th>
					<td :class="{'text-success': stat.scraper.enabled, 'text-danger': !stat.scraper.enabled,}">{{stat.scraper.enabled ? 'Abilitato' : 'Disabilitato'}}</td>
					<td>{{stat.region | capitalize}}</td>
					<td>{{stat.last_activity | localize_from_utc}}</td>
					<td>{{stat.jobs_added}}</td>
					<td>{{stat.jobs_skipped}}</td>
					<td :class="{'text-success':!stat.errors, 'text-danger':stat.errors}">{{stat.errors ? 'Si' : 'No'}}</td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
</template>

<script>
    import {mapGetters} from 'vuex';
    import {mapActions} from 'vuex';

    export default {
        computed: {
            ...mapGetters(['stats', 'scraperStatus']),
        },
        methods: {
            ...mapActions(['startScraper']),
        },
        created() {
            this.$store.dispatch('readScraperStats');
        }
    }
</script>
<style lang="scss">
	.header {
		display: flex;
		flex-direction: row;
		justify-content: space-between;
		align-items: center;
	}
</style>
