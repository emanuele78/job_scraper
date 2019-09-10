<template>
	<div class="">
		<div class="splash" v-if="isInitializing">
			<button :class="{'btn-primary':successfullyInitialized, 'btn-danger':!successfullyInitialized}" class="btn loading_message" disabled type="button">
				<span aria-hidden="true" class="spinner-border spinner-border-sm" role="status" v-if="successfullyInitialized"></span>
				Caricamento applicazione
			</button>
		</div>
		<div class="container app_container" v-else>
			<app-navbar/>
			<router-view class="position-relative">
				<div class="work_in_progress" v-if="isLoading"></div>
			</router-view>
			<div class="spinner_container" v-if="isLoading">
				<button class="btn btn-primary loading_spinner" disabled type="button">
					<span aria-hidden="true" class="spinner-border spinner-border-sm" role="status"></span>
					Loading...
				</button>
			</div>
			<div class="error_container" v-if="isError">
				<button class="btn btn-danger message_error" disabled type="button">
					<span aria-hidden="true" role="status"></span>
					Errore durante l'esecuzione della richiesta
				</button>
			</div>
		</div>
	</div>
</template>
<script>
    import AppNavbar
        from './components/Navbar.vue'
    import {mapGetters} from 'vuex';
    import {mapActions} from 'vuex';

    export default {
        components: {
            AppNavbar
        },
        computed: {
            ...mapGetters(['isLoading', 'isError', 'successfullyInitialized', 'isInitializing']),
        },
        methods: {
            ...mapActions(['hideSplash', 'showIrreversibleError']),
        },
        async created() {
            try {
                await this.$store.dispatch('initialize');
                this.$store.dispatch('showLoading', false);
                this.$store.dispatch('hideSplash');
            } catch (e) {
                this.$store.dispatch('showIrreversibleError');
            }
        },
    }
</script>
<style lang="scss">
	.splash {
		position: relative;
		width: 100%;
		height: 100vh;
		
		.loading_message {
			display: block;
			position: absolute;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
		}
	}
	
	.app_container {
		position: relative;
		padding-top: 30px;
		
		.work_in_progress {
			position: absolute;
			top: 0;
			left: 0;
			height: 100%;
			width: 100%;
			z-index: 2;
			background-color: rgba(10, 10, 10, 0.1);
			
		}
		
		.spinner_container, .error_container {
			position: absolute;
			top: 0;
			left: 0;
			width: 100%;
			height: 100vh;
			
		}
		
		.loading_spinner, .message_error {
			position: fixed;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
		}
		
	}
</style>
