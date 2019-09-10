<template>
	<div :class="{show: expandDropdown}" class="dropdown dropleft">
		<button @click="expandDropdown=!expandDropdown" aria-haspopup="true" class="btn btn-secondary btn-sm dropdown-toggle" data-toggle="dropdown" id="dropdownPostPerPage" type="button">
			Annunci per pagina
		</button>
		<div :class="{show: expandDropdown}" aria-labelledby="dropdownPostPerPage" class="dropdown-menu">
			<button @click="changePostsPerPage(value)" class="dropdown-item" type="button" v-for="value in postsPerPage">
				<span :class="{current_value: currentValue == value}">{{value}}</span>
			</button>
		</div>
	</div>
</template>
<script>
    export default {
        props: {
            postsPerPage: {
                type: Array,
                required: true
            },
            currentValue: {
                type: Number,
                required: true
            }
        },
        data() {
            return {
                expandDropdown: false
            }
        },
        methods: {
            changePostsPerPage(value) {
                this.expandDropdown = false;
                this.$emit('changePostsPerPage', value);
            }
        }
    }
</script>
<style>
	.current_value {
		font-weight: 900;
		color: #3490dc;
	}
</style>
