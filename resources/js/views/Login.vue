<template>
	<div class="">
		<div class="col-12 mt-4">
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<h3 class="text-center">Accedi</h3>
				</div>
			</div>
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<form>
						<div class="form-group">
							<label for="inputEmail">Email</label>
							<input aria-describedby="emailHelp" class="form-control" id="inputEmail" placeholder="Email" required type="email" v-model:value="email">
						</div>
						<div class="form-group">
							<label for="inputPassword">Password</label>
							<input class="form-control" id="inputPassword" placeholder="Password" required type="password" v-model:value="password">
						</div>
						<div class="form-check">
							<input :checked="stayConnected" class="form-check-input" id="stayConnected" type="checkbox" v-model="stayConnected">
							<label class="form-check-label" for="stayConnected">Rimani connesso</label>
						</div>
						<div class="text-right mb-4">
							<router-link :to="{name: 'passwordReset'}">Password dimenticata</router-link>
						</div>
						<div class="row justify-content-center mt-2">
							<button @click.prevent="submit()" class="btn btn-primary" type="submit" v-if="!onGoing">Accedi</button>
							<button class="btn btn-primary" disabled type="button" v-else>
								<span aria-hidden="true" class="spinner-border spinner-border-sm" role="status"></span>
								Accesso in corso...
							</button>
						</div>
						<div class="text-danger text-center" v-if="invalidCredentials">Email o password non validi</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
    export default {
        computed: {
            onGoing() {
                return this.$store.getters.loginOnGoing;
            },
            userEmail() {
                return this.$store.getters.userEmail;
            },
            invalidCredentials() {
                return this.$store.getters.invalidCredentials;
            }
        },
        data() {
            return {
                email: null,
                password: null,
                stayConnected: true,
            }
        },
        methods: {
            submit() {
                this.$store.dispatch('login', {
                    email: this.email,
                    password: this.password,
                    stayConnected: this.stayConnected,
                });
            }
        },
        mounted() {
            this.$store.dispatch('resetLogin');
            this.email = this.userEmail;
        },
    }
</script>
