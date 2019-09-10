<template>
	<div :class="{show: expandDropdown}" class="dropdown dropleft">
		<button aria-haspopup="true" class="btn btn-secondary btn-sm dropdown-toggle" data-toggle="dropdown" id="dropdownOrderingMode" type="button">
			Ordina per
		</button>
		<div :class="{show: expandDropdown}" aria-labelledby="dropdownOrderingMode" class="dropdown-menu" v-on-clickaway="toggleDropdown">
			<button @click="changeOrder(sortType.type)" class="dropdown-item" type="button" v-for="sortType in sortTypes">
				<span :class="{current_order: currentOrder == sortType.type}">{{sortType.name}}</span>
			</button>
		</div>
	</div>
</template>
<script>
    import {mixin as clickaway} from 'vue-clickaway';

    export default {
        props: {
            sortTypes: {
                type: Array,
                required: true
            },
            currentOrder: {
                type: String,
                required: true
            }
        },
        data() {
            return {
                expandDropdown: false
            }
        },
        methods: {
            changeOrder(order) {
                this.expandDropdown = false;
                this.$emit('changeOrder', order);
            },
            toggleDropdown(event) {
                if (event.srcElement.id === 'dropdownOrderingMode') {
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
	.current_order {
		font-weight: 900;
		color: #3490dc;
	}
</style>
