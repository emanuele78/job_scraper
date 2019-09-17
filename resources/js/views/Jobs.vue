<template>
	<div class="">
		<slot name="loading"/>
		<div class="col-sm-12 col-md-3 text-left mt-3">
			<app-source-filter
					:scrapers="scrapers"
					@checkAllScrapers="checkAllScrapers($event)"
					@filterScrapers="filterScrapers($event)"
					class="row mb-5"/>
			<app-keywords-box
					:keywords="keywords"
					:show-save-search-button="false"
					@addKeyword="addKeyword($event)"
					@removeKeyword="removeKeyword($event)"
					class="row"/>
		</div>
		
		<div class="col-sm-12 col-md-9">
			
			<div class="row mt-3">
				
				<div class="col-12 col-sm-6">
					<app-info-panel :jobs-count="jobsCount"/>
				</div>
				<div class="col-12 col-sm-6 ordering_counting">
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
				<div class="col-12">
					<hr>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12">
					<app-job-list-item
							:explicit-ask-post-status-change="false"
							:is-auth="isAuth"
							:key="jobPost.id"
							:post="jobPost"
							:show-archive-post-button="false"
							:show-move-to-inbox-button="false"
							v-for="jobPost in jobPosts"/>
				</div>
			</div>
			<div class="row pb-5">
				<div class="col-12">
					<app-pagination
							:current-page="currentPage"
							:last-page="lastPage"
							@moveTo="search($event)"/>
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
    import appPostsToShowPanel
        from '../components/PostsToShowPanel';
    import appKeywordsBox
        from '../components/KeywordsBox.vue';
    import {mapGetters} from 'vuex';
    import {mapActions} from 'vuex';

    export default {
        computed: {
            ...mapGetters(['scrapers', 'jobPosts', 'jobsCount', 'currentPage', 'lastPage', 'sortTypes', 'currentSortType', 'isAuth', 'postsPerPage', 'currentPostsPerPage', 'keywords']),
        },
        methods: {
            ...mapActions(['search', 'filterScrapers', 'addKeyword', 'removeKeyword', 'setOrderMode', 'setPostsPerPage', 'checkAllScrapers',]),
        },
        components: {
            appSourceFilter,
            appInfoPanel,
            appOrderingPanel,
            appJobListItem,
            appPagination,
            appPostsToShowPanel,
            appKeywordsBox,
        },
    }
</script>

<style lang="scss">
	input[type="checkbox"]:disabled + label {
		text-decoration: line-through;
	}
	
	@media screen and (min-width: 576px) {
		.ordering_counting {
			text-align: right;
		}
	}
</style>
