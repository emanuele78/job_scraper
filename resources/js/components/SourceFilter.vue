<template>
	<div class="sources">
		<div class="col-12">
			<h5>Fonti:</h5>
			<div class="form-check" v-for="scraper in scrapers">
				<input
						:checked="scraper.checked"
						:id="scraper.name"
						@change="sourceChanged($event, scraper.name)"
						class="form-check-input" type="checkbox">
				<label :for="scraper.name" class="form-check-label">
					{{scraper.showed_name | capitalize}}
				</label>
			</div>
			<div class="action_buttons mt-2">
				<button @click="checkAll(true)" class="btn btn-sm btn-primary" type="button">Tutti</button>
				<button @click="checkAll(false)" class="btn btn-sm btn-primary" type="button">Nessuno</button>
			</div>
		</div>
	</div>
</template>

<script>
    export default {
        props: {
            scrapers: {
                default: [],
                type: Array,
            }
        },
        methods: {
            sourceChanged(event, name) {
                const payload = {
                    name,
                    checked: event.srcElement.checked
                };
                this.$emit('filterScrapers', payload);
            },
            checkAll(checkAll) {
                this.$emit('checkAllScrapers', checkAll);
            }
        }
    }
</script>
<style>
	input[type="checkbox"]:disabled + label {
		text-decoration: line-through;
	}
</style>
