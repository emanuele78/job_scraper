<template>
	<div class="">
		<div class="col-12 mt-4">
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<h3 class="text-center">Crea la nuova password</h3>
				</div>
			</div>
			<div class="row">
				<div class="offset-4 col-4 justify-content-center">
					<form>
						<div class="form-group">
							<label for="inputPassword">Nuova password</label>
							<input class="form-control" id="inputPassword" placeholder="Password" required type="password" v-model:value="password">
							<div class="text-danger text-right" v-if="!validPassword">{{passwordDescrition}}</div>
						</div>
						<div class="form-group">
							<label for="inputPasswordAgain">Ripeti password</label>
							<input class="form-control" id="inputPasswordAgain" placeholder="Password" required type="password" v-model:value="passwordRepeated">
							<div class="text-danger text-right" v-if="!passwordsMatch">Le password non coincidono</div>
						</div>
						<div class="row justify-content-center mt-2">
							<button :disabled="requestSent || !validFields" @click.prevent="submit()" class="btn btn-primary" type="submit">Reset password</button>
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
    import {isPasswordValid} from "../modules/password_email_validation";
    import {passwordRuleDescription} from "../modules/password_email_validation";
    import services
        from "../services";

    export default {
        data() {
            return {
                requestSent: false,
                password: '',
                passwordRepeated: '',
                token: null,
            }
        },
        computed: {
            validFields() {
                return isPasswordValid(this.password) && isPasswordValid(this.passwordRepeated) && this.password === this.passwordRepeated;
            },
            passwordDescrition() {
                return passwordRuleDescription();
            },
            validPassword() {
                if (this.password.length > 0) {
                    return isPasswordValid(this.password);
                }
                return true;
            },
            passwordsMatch() {
                return this.password === this.passwordRepeated;
            }
        },
        methods: {
            async submit() {
                this.$store.dispatch('showLoading', true);
                try {
                    const res = await services.saveNewPassword(this.password, this.passwordRepeated, this.token);
                    this.$store.dispatch('showLoading', false);
										this.$store.commit('userEmail', res.data.email);
                    this.$router.push({name: 'login'});
                } catch (e) {
                    this.$store.dispatch('showLoading', false);
                    this.$store.dispatch('showError');
                    this.$router.push({name: 'passwordReset'});
                }
            }
        },
        created() {
            if (!this.$route.query.token) {
                this.$router.push({name: 'passwordReset'});
            } else {
                this.token = this.$route.query.token;
            }
        }
    }
</script>
