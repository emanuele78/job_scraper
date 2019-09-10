<template>
	<div class="marking_toolbox">
		<input :checked="allChecked" @change="selectAll($event)" id="selectAllCheckBox" type="checkbox">
		<div class="selection_icon d-inline" v-if="showIcons">
			<i @click="updatePostsStatus(true)" class="fas fa-archive" v-if="showArchiveIcon"></i>
			<i @click="updatePostsStatus(false)" class="fas fa-inbox" v-if="showInBoxIcon"></i>
			<i @click="updatePostsReadStatus(true)" class="fas fa-envelope-open" v-if="showMarkAsReadIcon"></i>
			<i @click="updatePostsReadStatus(false)" class="fas fa-envelope" v-if="showMarkAsUnreadIcon"></i>
			<i @click="markPostsAsFavorite(true)" class="fas fa-star" v-if="showMarkAsFavoriteIcon"></i>
			<i @click="markPostsAsFavorite(false)" class="far fa-star"></i>
		</div>
	</div>
</template>
<script>
    export default {
        props: {
            showIcons: {
                type: Boolean,
                default: false,
            },
            showArchiveIcon: {
                type: Boolean,
                required: true,
            },
            showMarkAsFavoriteIcon: {
                type: Boolean,
                default: false,
            },
            showInBoxIcon: {
                type: Boolean,
                default: false,
            },
            allChecked: {
                type: Boolean,
                required: true,
            },
            showMarkAsReadIcon: {
                type: Boolean,
                required: true,
            },
            showMarkAsUnreadIcon: {
                type: Boolean,
                required: true,
            }
        },
        methods: {
            selectAll(event) {
                this.$emit('selectAll', event.srcElement.checked)
            },
            updatePostsStatus(archivePost) {
                this.$emit('updatePostsStatus', archivePost);
            },
            updatePostsReadStatus(setAsRead) {
                this.$emit('updatePostsReadStatus', setAsRead);
            },
            markPostsAsFavorite(setAsFavorite) {
                this.$emit('updatePostsFavoriteStatus', setAsFavorite);
            },
        }
    }
</script>
<style lang="scss">
	.marking_toolbox {
		font-size: 22px;
		
		.selection_icon i {
			margin-left: 15px;
			cursor: pointer;
			color: #596268;
		}
	}
</style>
