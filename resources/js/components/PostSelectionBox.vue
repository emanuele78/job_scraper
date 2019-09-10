<template>
	<div class="post_selection">
		<i :class="favoriteClass"
				@click="markFavorite"
				class="favorite"></i>
		<input :checked="isSelected"
				@change="markSelected($event)"
				class="selection"
				type="checkbox">
	</div>
</template>
<script>
    export default {
        computed: {
            favoriteClass() {
                if (this.isFavorite) {
                    return "fas fa-star";
                }
                return "far fa-star";
            }
        },
        props: {
            isFavorite: {
                default: false,
                type: Boolean,
            },
            isSelected: {
                default: false,
                type: Boolean,
            },
            id: {
                required: true,
                type: Number,
            }
        },
        methods: {
            markFavorite() {
                this.$emit('updatePostsFavoriteStatus', !this.isFavorite);
            },
            markSelected(event) {
                this.$emit('singleSelection', event.srcElement.checked);
            }
        }
    }
</script>
<style lang="scss">
	.post_selection {
		display: flex;
		flex-direction: column;
		justify-content: flex-start;
		align-items: center;
		padding: 5px;
		
		.favorite {
			font-size: 16px;
			cursor: pointer;
			
			&.far {
				color: lightgray;
			}
			
			&.fas {
				color: #EFCF88;
			}
		}
		
		.selection {
			margin-top: 10px;
			font-size: 16px;
		}
	}
</style>
