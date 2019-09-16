<template>
	<div class="">
		<div class="col-12 mt-4">
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<h3 class="text-center">Procedura per il reset della password</h3>
				</div>
			</div>
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<form>
						<div class="form-group">
							<label for="inputEmail">Inserisci la tua email</label>
							<input aria-describedby="emailHelp" class="form-control" id="inputEmail" placeholder="Email" required type="email" v-model:value="email">
						</div>
						<div class="row justify-content-center mt-2">
							<button :disabled="requestSent" @click.prevent="submit()" class="btn btn-primary" type="submit">Reset password</button>
						</div>
						<div class="text-danger text-center mt-4" v-if="invalidCredentials">
							<span>Email non valida</span>
						</div>
						<div class="text-center mt-4" v-if="requestSent">
							<span class="text-muted">E' stata inviata una email per il reset della password all'indirizzo inserito.</span>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</template>
<script>
    import {isEmailValid} from '../modules/password_email_validation';

    export default {
        data() {
            return {
                email: null,
                invalidCredentials: false,
                requestSent: false,
            }
        },
        methods: {
            submit() {
                this.invalidCredentials = false;
                if (!isEmailValid(this.email)) {
                    this.invalidCredentials = true;
                    return;
                }
                this.$store.dispatch('requestPasswordReset', this.email);
                this.requestSent = true;
            }
        },
    }
</script>
