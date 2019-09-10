<template>
	<span>Annunci trovati: {{count}}</span>
</template>
<script>
    export default {
        props: {
            jobsCount: {
                required: true,
                type: Number
            }
        },
        computed: {
            count() {
                return this.jobsCount;
            }
        }
    }
</script>
