<template>
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<router-link :to="{name: 'home'}" class="custom_brand">
			<a class="navbar-brand">
				<div class="my_logo">
					<span>E|M</span>
				</div>
				<span class="ml-1">Job Scraper</span>
			</a>
		</router-link>
		<button aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation" class="navbar-toggler" data-target="#navbarSupportedContent" data-toggle="collapse" type="button">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto ml-2">
				<router-link :to="{name:'jobs'}" active-class="item_active" class="nav-item active" exact tag="li" v-if="!isAuth">
					<a class="nav-link">Annunci</a>
				</router-link>
				<router-link :to="{name:'dashboard'}" active-class="item_active" class="nav-item active" exact tag="li" v-else>
					<a class="nav-link">Dashboard</a>
				</router-link>
				<router-link :to="{name:'scraper'}" active-class="item_active" class="nav-item active" exact tag="li">
					<a class="nav-link">Scraper</a>
				</router-link>
			</ul>
			<ul class="navbar-nav align-content-end">
				<router-link :to="{name:'login'}" active-class="item_active" class="nav-item active" exact tag="li" v-if="!isAuth">
					<a class="nav-link">Accedi</a>
				</router-link>
				<router-link :to="{name:'signin'}" active-class="item_active" class="nav-item active" exact tag="li" v-if="!isAuth">
					<a class="nav-link">Registrati</a>
				</router-link>
				<li class="nav-item active" v-if="isAuth">
					<a @click.prevent="logout" class="nav-link" href="#">Esci</a>
				</li>
			</ul>
		</div>
	</nav>
</template>
<script>
    import {mapGetters} from 'vuex';
    import {mapActions} from 'vuex';

    export default {
        computed: {
            ...mapGetters(['isAuth']),
        },
				methods: {
            ...mapActions(['logout']),
				}
    }
</script>
<style lang="scss">
	.my_logo {
		font-family: sans-serif;
		display: inline-block;
		border: 1px solid #3490dc;
		border-radius: 50%;
		padding: 5px;
		font-size: 22px;
	}
	
	.item_active > a, .nav-item:hover > a {
		text-decoration: underline;
	}
	
	.custom_brand:hover {
		color: #3490dc;
	}

</style>
