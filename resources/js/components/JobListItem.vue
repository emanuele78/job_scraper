<template>
	<div class="post_wrapper">
		<slot name="selectionBox"/>
		<div class="post_content">
			<div class="post_info">
				<h5 :class="{unread: isUnread}">{{post.title | capitalize}}</h5>
				<h6>ditta {{post.company_name | uppercase}}</h6>
			</div>
			<div class="mb-2">
				<slot name="labelBox"/>
			</div>
			<div class="post_info">
				<span class="text-muted">da <strong>{{post.scraper.showed_name | uppercase}}</strong></span>
				<span class="text-muted">acquisito il {{post.created_at | localize}}</span>
			</div>
			<div class="post_info">
				<span class="text-muted">regione <strong>{{post.region | uppercase}}</strong></span>
				<span class="text-muted">pubblicato il {{post.published_at_date | localize}}</span>
			</div>
			<div class="post_info my-1">
				<span class="text-muted">luogo {{post.place | uppercase}}</span>
				<div class="">
					<button @click="updatePostsStatus(true)" class="btn btn-primary btn-sm mr-2" type="button" v-if="showArchivePostButton">Archivia</button>
					<button @click="updatePostsStatus(false)" class="btn btn-primary btn-sm mr-2" type="button" v-if="showMoveToInboxButton">Sposta in arrivo</button>
					<button @click="changePostStatusAsRead(isUnread?true:false)" class="btn btn-primary btn-sm mr-2" type="button" v-if="explicitAskPostStatusChange">{{isUnread? 'Segna come letto':'Segna come da leggere'}}</button>
					<button @click="readContent()" class="btn btn-primary btn-sm" type="button">{{showContent? 'Nascondi':'Espandi'}}</button>
				</div>
			</div>
			<div class="description" v-if="showContent">
				<div class="post_info">
					<span><strong>Contenuto:</strong></span>
					<a :href="post.post_link" rel="noopener noreferrer" target="_blank" v-if="post.post_link">Annuncio originale</a>
				</div>
				<p>{{post.description}}</p>
			</div>
		</div>
	</div>
</template>
<script>
    export default {
        props: {
            post: {
                required: true,
                type: Object
            },
            isAuth: {
                required: true,
                type: Boolean,
            },
            showArchivePostButton: {
                required: true,
                type: Boolean
            },
            showMoveToInboxButton: {
                required: true,
                type: Boolean
            },
            explicitAskPostStatusChange: {
                required: true,
                type: Boolean
            }
        },
        computed: {
            isUnread() {
                return this.isAuth ? this.post.read_by_users.length === 0 : false;
            },
        },
        data() {
            return {
                showContent: false,
            }
        },
        methods: {
            readContent() {
                this.showContent = !this.showContent;
                if (this.isAuth && !this.explicitAskPostStatusChange) {
                    if (this.post.read_by_users.length === 0) {
                        this.$emit('updatePostsReadStatus', {
                            markAsRead: true,
                            postsIds: [this.post.id]
                        });
                    }
                }
            },
            updatePostsStatus(archivePost) {
                this.$emit('updatePostsStatus', {
                    moveToArchive: archivePost,
                    postsIds: [this.post.id]
                });
            },
            changePostStatusAsRead(markAsRead) {
                this.$emit('updatePostsReadStatus', {
                    markAsRead: markAsRead,
                    postsIds: [this.post.id]
                });
            }
        }
    }
</script>
<style lang="scss">
	.post_wrapper {
		display: flex;
		flex-direction: row;
		justify-content: flex-start;
		align-items: flex-start;
		width: 100%;
		padding: 5px;
		border: 1px solid lightgray;
		border-radius: 5px;
		margin-bottom: 7px;
		
		.post_content {
			flex-grow: 1;
			
			.unread {
				font-weight: 900;
			}
		}
		
		.post_info {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
		}
		
		.description {
			margin-top: 10px;
		}
	}
</style>
