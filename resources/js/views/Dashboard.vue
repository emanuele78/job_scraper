<template>
	<div class="">
		<slot/>
		<div class="row">
			<div class="col-3 text-left mt-3">
				<app-user-post-navigation-box
						:active-label="activeLabel"
						:archive-count="archivedCount"
						:favorite-count="favoriteCount"
						:inbox-count="inBoxCount"
						@switchToLabel="switchToLabel($event)"
						class="mb-4"/>
				<app-custom-label-creation
						:activeCustomLabel="activeCustomLabel"
						:labels="assignedLabels"
						@createNewCustomLabel="createNewCustomLabel($event)"
						@deleteCustomLabel="deleteCustomLabel($event)"
						@switchToCustomLabel="switchToCustomLabel($event)"
						class="mb-4"/>
				<app-source-filter
						:scrapers="scrapers"
						@filterScrapers="filterScrapers($event)"
						class="mb-4"/>
				<app-keywords-box
						:keywords="keywords"
						@addKeyword="addKeyword($event)"
						@removeKeyword="removeKeyword($event)"
						class="mb-4"/>
				<app-show-only-box
						:show-only="showOnly"
						@changeShowOnly="changeShowOnly($event)"
				/>
			</div>
			<div class="col-9">
				<div class="d-flex justify-content-between mt-3">
					<div class="">
						<app-post-marking-tool-box
								:all-checked="allChecked"
								:show-archive-icon="activeLabel=='inbox'"
								:show-icons="showPostsMarkingToolbox"
								:show-in-box-icon="activeLabel=='archive'"
								:show-mark-as-favorite-icon="activeLabel!='favorite'"
								:show-mark-as-read-icon="showOnly!='only_read'"
								:show-mark-as-unread-icon="showOnly!='only_unread'"
								@selectAll="selectAllPosts($event)"
								@updatePostsFavoriteStatus="updatePostsFavoriteStatus({markAsFavorite: $event, postsIds: currentSelection})"
								@updatePostsReadStatus="updatePostsReadStatus({markAsRead: $event, postsIds: currentSelection})"
								@updatePostsStatus="updatePostsStatus({moveToArchive: $event, postsIds: currentSelection})"/>
						<app-info-panel :jobs-count="jobsCount"/>
					</div>
					<div class="">
						<app-posts-to-show-panel
								:current-value="currentPostsPerPage"
								:posts-per-page="postsPerPage"
								@changePostsPerPage="setPostsPerPage($event)"
								class="d-inline"/>
						<app-ordering-panel
								:current-order="currentSortType"
								:sort-types="sortTypes"
								@changeOrder="setOrderMode($event)"
								class="d-inline"/>
					</div>
				</div>
				<hr>
				<div class="row">
					<div class="col-12">
						<app-job-list-item
								:explicit-ask-post-status-change="showOnly != 'all'"
								:is-auth="isAuth"
								:key="jobPost.id"
								:post="jobPost"
								:show-archive-post-button="activeLabel=='inbox'"
								:show-move-to-inbox-button="activeLabel=='archive'"
								@updatePostsReadStatus="updatePostsReadStatus($event)"
								@updatePostsStatus="updatePostsStatus($event)"
								v-for="(jobPost, index) in jobPosts">
							<app-custom-label-assign
									:index="index"
									:current-label="jobPost.assigned_labels.length>0?jobPost.assigned_labels[0]:null"
									:labels="assignedLabels"
									@assignCustomLabelToPost="assignCustomLabelToPost({label: $event, postId: jobPost.id})"
									@removeCustomLabelFromPost="removeCustomLabelFromPost({labelName: $event, postId: jobPost.id})"
									slot="labelBox"/>
							<app-post-selection-box
									:id="jobPost.id"
									:is-favorite="jobPost.marked_favorite_by_users.length>0"
									:is-selected="checkPostAsSelected(jobPost.id)"
									@singleSelection="addSingleSelection({isSelected: $event, postId: jobPost.id})"
									@updatePostsFavoriteStatus="updatePostsFavoriteStatus({markAsFavorite: $event, postsIds: [jobPost.id]})"
									slot="selectionBox"/>
						</app-job-list-item>
					</div>
				</div>
				<div class="row pb-5">
					<div class="col">
						<app-pagination
								:current-page="currentPage"
								:last-page="lastPage"
								@moveTo="search($event)"/>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
    import appSourceFilter
        from '../components/SourceFilter.vue';
    import appInfoPanel
        from '../components/InfoPanel.vue';
    import appOrderingPanel
        from '../components/OrderingPanel.vue';
    import appJobListItem
        from '../components/JobListItem.vue';
    import appPagination
        from '../components/Pagination.vue';
    import appUserPostNavigationBox
        from '../components/UserPostNavigationBox.vue';
    import appPostMarkingToolBox
        from '../components/PostMarkingToolBox.vue';
    import appPostsToShowPanel
        from '../components/PostsToShowPanel';
    import appPostSelectionBox
        from '../components/PostSelectionBox.vue';
    import appKeywordsBox
        from '../components/KeywordsBox.vue';
    import appShowOnlyBox
        from '../components/ShowOnlyBox';
    import appCustomLabelAssign
        from '../components/CustomLabelAssign';
    import appCustomLabelCreation
        from '../components/CustomLabelCreation.vue';
    import {mapGetters} from 'vuex';
    import {mapActions} from 'vuex';

    export default {
        computed: {
            ...mapGetters(['scrapers', 'jobPosts', 'jobsCount', 'currentPage', 'lastPage', 'sortTypes', 'currentSortType', 'isAuth', 'activeLabel', 'postsPerPage', 'currentPostsPerPage', 'inBoxCount', 'favoriteCount', 'archivedCount', 'keywords', 'showOnly', 'currentSelection', 'assignedLabels', 'activeCustomLabel', 'showPostsMarkingToolbox', 'allChecked']),
        },
        methods: {
            ...mapActions(['selectAllPosts', 'addSingleSelection', 'createNewCustomLabel', 'deleteCustomLabel', 'switchToCustomLabel', 'assignCustomLabelToPost', 'removeCustomLabelFromPost', 'switchToLabel', 'filterScrapers', 'addKeyword', 'removeKeyword', 'changeShowOnly', 'setOrderMode', 'setPostsPerPage', 'updatePostsFavoriteStatus', 'updatePostsReadStatus', 'updatePostsStatus', 'search']),
            checkPostAsSelected(postId) {
                return this.currentSelection.find(item => item === postId) != null;
            },
        },
        components: {
            appSourceFilter,
            appInfoPanel,
            appOrderingPanel,
            appJobListItem,
            appPagination,
            appUserPostNavigationBox,
            appPostMarkingToolBox,
            appPostsToShowPanel,
            appPostSelectionBox,
            appKeywordsBox,
            appShowOnlyBox,
            appCustomLabelAssign,
            appCustomLabelCreation,
        },
    }
</script>

<style lang="scss">
	input[type="checkbox"]:disabled + label {
		text-decoration: line-through;
	}
</style>
