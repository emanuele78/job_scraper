<template>
	<div>
		<div class="row m-3 justify-content-center">
			<div class="col-4">
				<h3 class="text-center">Accedi</h3>
			</div>
		</div>
		<div class="row justify-content-center">
			<div class="col-4">
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
						<input :checked="stayConnected" v-model="stayConnected" type="checkbox" class="form-check-input" id="stayConnected">
						<label class="form-check-label" for="stayConnected">Rimani connesso</label>
					</div>
					<div class="row justify-content-center mt-2">
						<button @click.prevent="submit()" class="btn btn-primary" type="submit" v-if="!onGoing">Accedi</button>
						<button class="btn btn-primary" disabled type="button" v-else>
							<span aria-hidden="true" class="spinner-border spinner-border-sm" role="status"></span>
							Accesso in corso...
						</button>
					</div>
					<div v-if="invalidCredentials" class="text-danger text-center">Email o password non validi</div>
				</form>
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
            invalidCredentials(){
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
