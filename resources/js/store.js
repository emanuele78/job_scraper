import Vue from 'vue'
import Vuex from 'vuex'
import services from './services';
import auth from './modules/auth';
import labelsNavigation from './modules/labels_navigation';
import stats from './modules/stats';
import postsArchiving from "./modules/posts_archiving";
import postsReading from "./modules/posts_reading";
import postsFavorite from "./modules/posts_favorite";
import postsCount from "./modules/posts_count";
import customLabels from "./modules/custom_labels";
import {authSearch} from "./mixin/auth_search_action";
import appContainer from "./modules/app_container";
import router from "./router";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        scrapers: null,
        jobsCount: 0,
        isLoading: false,
        keywords: [],
        jobPosts: [],
        currentPage: 1,
        lastPage: 1,
        sortTypes: [
            {type: 'source', name: 'Fonte'},
            {type: 'scraper_date', name: 'Data acquisizione'},
            {type: 'publication_date', name: 'Data pubblicazione (ove presente)'},
        ],
        currentSortType: 'source',
        postsPerPage: [20, 30, 50, 100],
        currentPostsPerPage: 20,
        showOnly: 'all',
        currentSelection: [],
        allChecked: false,
    },
    mutations: {
        setScrapers(state, scrapers) {
            state.scrapers = scrapers;
        },
        setOrderMode(state, order) {
            state.currentSortType = order;
        },
        setPostsPerPage(state, postsPerPage) {
            state.currentPostsPerPage = postsPerPage;
        },
        filterScrapers(state, filteredScraper) {
            const scraper = state.scrapers.find(item => item.name == filteredScraper.name);
            scraper.checked = filteredScraper.checked;
        },
        setJobCount(state, count) {
            state.jobsCount = count;
        },
        isLoading(state, isShowed) {
            state.isLoading = isShowed;
        },
        setJobPosts(state, jobPosts) {
            state.jobPosts = jobPosts;
        },
        setLastPage(state, lastPage) {
            state.lastPage = lastPage;
        },
        setCurrentPage(state, currentPage) {
            state.currentPage = currentPage;
        },
        addKeyword(state, keyword) {
            state.keywords.push(keyword);
        },
        setKeywords(state, keywords) {
            state.keywords = keywords;
        },
        removeKeyword(state, index) {
            state.keywords.splice(index, 1);
        },
        showOnly(state, value) {
            state.showOnly = value;
        },
        currentSelection(state, value) {
            state.currentSelection = value;
        },
        allChecked(state, value) {
            state.allChecked = value;
        }
    },
    actions: {
        authSearch,
        async initialize({dispatch, getters, commit}) {
            //read available scrapers from server
            const res = await services.getScrapers();
            //adding checked property
            res.data.scrapers.forEach(item => item.checked = true);
            //saving scrapers list in store
            commit('setScrapers', res.data.scrapers);
            //try login
            await dispatch('tryAutoLogin');
            //retrieve posts
            await dispatch('search');
            if (getters.isAuth) {
                //logged in - get user custom labels
                await dispatch('retrieveAssignedLabels');
                router.replace({name: 'dashboard'});
            }
        },
        setOrderMode({dispatch, commit}, order) {
            commit('setOrderMode', order);
            dispatch('search');
        },
        setPostsPerPage({dispatch, commit}, postsPerPage) {
            commit('setPostsPerPage', postsPerPage);
            dispatch('search');
        },
        filterScrapers({commit, dispatch}, filteredScraper) {
            commit('filterScrapers', filteredScraper);
            dispatch('search');
        },
        changeShowOnly({commit, dispatch}, value) {
            commit('showOnly', value);
            dispatch('search');
        },
        saveSearchResponseData({commit, dispatch}, data) {
            if (data != null) {
                commit('setJobCount', data.job_posts.total);
                commit('setJobPosts', data.job_posts.data);
                commit('setLastPage', data.job_posts.last_page);
                commit('setCurrentPage', data.job_posts.current_page);
                if ('posts_count' in data) {
                    dispatch('updateCounters', {...data.posts_count});
                }
            } else {
                commit('setJobCount', 0);
                commit('setJobPosts', []);
                commit('setLastPage', 1);
                commit('setCurrentPage', 1);
            }
        },
        selectAllPosts({commit, getters}, selectAll) {
            commit('allChecked', selectAll);
            if (selectAll) {
                commit('currentSelection', getters.jobPosts.map(post => post.id));
            } else {
                commit('currentSelection', []);
            }
        },
        addSingleSelection({state}, data) {
            if (data.isSelected) {
                //add in array
                state.currentSelection.push(data.postId);
            } else {
                //remove from array
                let itemIndex = state.currentSelection.indexOf(data.postId);
                state.currentSelection.splice(itemIndex, 1);
            }
        },
        async search({dispatch, getters, commit}, page = 1) {
            commit('currentSelection', []);
            commit('allChecked', false);
            dispatch('showLoading', true);
            try {
                let res;
                if (!getters.isAuth) {
                    res = await dispatch('guestSearch', page);
                    dispatch('saveSearchResponseData', res.data);
                } else {
                    switch (getters.activeLabel) {
                        case 'inbox':
                            res = await dispatch('authSearch', {page, searchType: 'searchInboxPosts'});
                            break;
                        case 'favorite':
                            res = await dispatch('authSearch', {page, searchType: 'searchFavoritePosts'});
                            break;
                        case 'archive':
                            res = await dispatch('authSearch', {page, searchType: 'searchArchivedPosts'});
                            break;
                        default:
                            //custom label selected
                            res = await dispatch('customLabelSearch', page);
                            break;
                    }
                }
                dispatch('saveSearchResponseData', res.data);
                dispatch('showLoading', false);
            } catch (e) {
                dispatch('showLoading', false);
                if (e.response.status === 401) {
                    dispatch('handleAuthError');
                } else {
                    //server error
                    dispatch('showError');
                }
            }
        },
        async guestSearch({dispatch, commit, getters}, page) {
            const checkedScrapers = getters.scrapers.filter(item => item.checked).map(item => item.name);
            if (checkedScrapers.length === 0) {
                //no need to perform server request
                return null;
            }
            const orderMode = getters.currentSortType;
            const keywords = getters.keywords;
            const postsPerPage = getters.currentPostsPerPage;
            return await services.searchJobPostsForGuest(checkedScrapers, orderMode, keywords, postsPerPage, page);
        },
        addKeyword({dispatch, commit, getters}, keyword) {
            const index = getters.keywords.indexOf(keyword);
            //check if already exists
            if (index === -1) {
                commit("addKeyword", keyword);
                dispatch('search');
            }
        },
        removeKeyword({dispatch, commit}, index) {
            commit("removeKeyword", index);
            dispatch('search');
        },
        resetStoreValues({commit, getters, dispatch, state}) {
            commit('setKeywords', []);
            dispatch('saveSearchResponseData', null);
            commit('currentSelection', []);
            commit('setOrderMode', getters.sortTypes[0].type);
            commit('setPostsPerPage', getters.postsPerPage[0]);
            state.scrapers.forEach(scraper => {
                scraper.checked = true;
            });
        }
    },
    getters: {
        scrapers(state) {
            return state.scrapers;
        },
        jobsCount(state) {
            return state.jobsCount;
        },
        currentSortType(state) {
            return state.currentSortType;
        },
        jobPosts(state) {
            return state.jobPosts;
        },
        currentPage(state) {
            return state.currentPage;
        },
        lastPage(state) {
            return state.lastPage;
        },
        sortTypes(state) {
            return state.sortTypes;
        },
        postsPerPage(state) {
            return state.postsPerPage;
        },
        currentPostsPerPage(state) {
            return state.currentPostsPerPage;
        },
        keywords(state) {
            return state.keywords;
        },
        showOnly(state) {
            return state.showOnly;
        },
        currentSelection(state) {
            return state.currentSelection;
        },
        showPostsMarkingToolbox(state) {
            return state.currentSelection.length > 0;
        },
        allChecked(state) {
            return state.allChecked;
        }
    },
    modules: {
        auth,
        labelsNavigation,
        stats,
        postsArchiving,
        postsReading,
        postsFavorite,
        postsCount,
        customLabels,
        appContainer,
    }
});


