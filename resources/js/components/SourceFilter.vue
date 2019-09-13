<template>
	<div class="">
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
            }
        }
    }
</script>
<style>
	input[type="checkbox"]:disabled + label {
		text-decoration: line-through;
	}
</style>
