<template>
	<div :class="{show: expandDropdown}" class="dropdown dropleft">
		<button aria-haspopup="true" class="btn btn-secondary btn-sm dropdown-toggle" data-toggle="dropdown" id="dropdownPostPerPage" type="button">
			Annunci per pagina
		</button>
		<div :class="{show: expandDropdown}" aria-labelledby="dropdownPostPerPage" class="dropdown-menu" v-on-clickaway="toggleDropdown">
			<button @click="changePostsPerPage(value)" class="dropdown-item" type="button" v-for="value in postsPerPage">
				<span :class="{current_value: currentValue == value}">{{value}}</span>
			</button>
		</div>
	</div>
</template>
<script>
    import {mixin as clickaway} from 'vue-clickaway';

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
                expandDropdown: false,
            }
        },
        methods: {
            changePostsPerPage(value) {
                this.expandDropdown = false;
                this.$emit('changePostsPerPage', value);
            },
            toggleDropdown(event) {
                if (event.srcElement.id === 'dropdownPostPerPage') {
                    this.expandDropdown = !this.expandDropdown;
                }else{
                    this.expandDropdown = false;
								}
            }
        },
        mixins: [clickaway],
    }
</script>
<style>
	.current_value {
		font-weight: 900;
		color: #3490dc;
	}
</style>
