<template>
	<div class="">
		<slot name="loading"/>
		<div class="col-12 mt-4" v-if="savedSearches.length">
			<div class="row">
				<div class="offset-3 col-6 mb-2">
					<h4>Le tue ricerche</h4>
					<span class="text-muted">Abilita la notifica email per essere informato quando nuovi annunci di tuo interesse vengono individuati</span>
				</div>
			</div>
			<div class="row">
				<div class="offset-3 col-6 offset-3 mb-1" v-for="(savedSearch, index) in savedSearches">
					<div class="search_wrapper">
						<span class="label text-muted">Fonti selezionate</span>
						<span v-for="(scraper, index) in savedSearch.scrapers"><strong>{{scraper.showed_name | capitalize}}</strong>{{index < savedSearch.scrapers.length-1?', ':''}}</span>
						<span class="label text-muted">Parole chiave</span>
						<span v-for="(keyword, index) in savedSearch.keywords"><strong>{{keyword | capitalize}}</strong>{{index < savedSearch.keywords.length-1?', ':''}}</span>
						<span class="label text-muted">Ricerca creata il</span>
						<span><strong>{{savedSearch.created_at | localize_from_utc}}</strong></span>
						<div class="actions_wrapper">
							<div class="">
								<span class="label text-muted">Notifica email nuovi annunci</span>
								<div class="custom-control custom-switch">
									<input :checked="savedSearch.notification_enabled === 1" :id="'email_notification_switch_'+index" @change="updateEmailNotification({isEnabled: ($event).target.checked, savedSearchId: savedSearch.id})" class="custom-control-input" type="checkbox">
									<label :for="'email_notification_switch_'+index" class="custom-control-label"></label>
								</div>
							</div>
							<div class="">
								<button @click="launchSearch(index)" class="btn btn-primary" type="button">Lancia ricerca</button>
								<button @click="deleteSavedSearch({savedSearchId: savedSearch.id, index})" class="btn btn-danger" type="button">Elimina</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="offset-3 col-6 mt-4" v-else>
			<h5>Non hai ricerche salvate</h5>
			<span class="text-muted">Crea una nuova ricerca dopo aver inserito almeno una parola chiave nella sezione <router-link :to="{name: 'dashboard'}">Dashboard</router-link></span>
		</div>
	</div>
</template>
<script>
    import {mapGetters} from 'vuex';
    import {mapActions} from 'vuex';

    export default {
        computed: {
            ...mapGetters(['savedSearches']),
        },
        methods: {
            ...mapActions(['launchSearch', 'deleteSavedSearch', 'updateEmailNotification']),
        }
    }
</script>
<style lang="scss">
	.search_wrapper {
		width: 100%;
		padding: 5px;
		border: 1px solid lightgray;
		border-radius: 5px;
		margin-bottom: 7px;
		
		.label {
			display: block;
		}
		
		.actions_wrapper {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;
		}
		
	}
</style>
