<template>
	<div class="">
		<div class="col-12 mt-4">
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<h3 class="text-center">Registrati</h3>
				</div>
			</div>
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<form>
						<div class="form-group">
							<label for="inputEmail">Email</label>
							<input aria-describedby="emailHelp" class="form-control" id="inputEmail" placeholder="Email" required type="email" v-model:value="email">
							<div class="text-danger text-right" v-if="emailError">Email non valida</div>
						</div>
						<div class="form-group">
							<label for="inputPassword">Password</label>
							<input class="form-control" id="inputPassword" placeholder="Password" required type="password" v-model:value="password">
							<div class="text-danger text-right" v-if="passwordError">La password deve avere almeno 6 caratteri</div>
						</div>
						<div class="form-check">
							<input :checked="agree_license" class="form-check-input" id="stayConnected" type="checkbox" v-model="agree_license">
							<label class="form-check-label" for="stayConnected">Dichiaro di aver letto la <router-link :to="{name: 'privacy'}">privacy policy</router-link></label>
						</div>
						<div class="row justify-content-center mt-2">
							<button :disabled="!agree_license" @click.prevent="submit()" class="btn btn-primary" type="submit" v-if="!onGoing">Registrati</button>
							<button class="btn btn-primary" disabled type="button" v-else>
								<span aria-hidden="true" class="spinner-border spinner-border-sm" role="status"></span>
								Registrazione...
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
    export default {
        data() {
            return {
                email: null,
                password: null,
                agree_license: false,
            }
        },
        computed: {
            onGoing() {
                return this.$store.getters.signinOnGoing;
            },
            emailError() {
                return this.$store.getters.signinEmailError;
            },
            passwordError() {
                return this.$store.getters.signinPasswordError;
            }
        },
        methods: {
            submit() {
                if (this.agree_license) {
                    this.$store.dispatch('register', {
                        email: this.email,
                        password: this.password
                    });
                }
            }
        },
        mounted() {
            this.$store.dispatch('resetSignin');
        }
    }
</script>
