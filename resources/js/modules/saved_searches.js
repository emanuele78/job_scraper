import services from "../services";
import router from "../router";

const state = {
    savedSearches: [],
};

const mutations = {
    savedSearches(state, savedSearches) {
        state.savedSearches = savedSearches;
    }
};

const actions = {
    async retrieveSavedSearches({getters, commit}) {
        const res = await services.savedSearchesList(getters.accessToken);
        commit('savedSearches', res.data.savedSearches);
    },
    resetSavedSearches({commit}) {
        commit('savedSearches', []);
    },
    launchSearch({dispatch, commit, getters, state}, searchIndex) {
        const selectedSearch = state.savedSearches[searchIndex];
        getters.scrapers.forEach(scraper => {
            scraper.checked = selectedSearch.scrapers.find(item => {
                return item.id === scraper.id;
            });
        });
        commit('setKeywords', selectedSearch.keywords);
        commit('showOnly', 'only_unread');
        commit('activeLabel', 'inbox');
        commit('activeCustomLabel', '');
        dispatch('search');
        router.push({name: 'dashboard'});
    },
    async deleteSavedSearch({dispatch, getters}, {index, savedSearchId}) {
        try {
            dispatch('showLoading', true);
            await services.deleteSavedSearch(getters.accessToken, savedSearchId);
            //delete from local store
            getters.savedSearches.splice(index, 1);
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
    async updateEmailNotification({dispatch, getters}, data) {
        dispatch('showLoading', true);
        try {
            await services.updateSavedSearchStatus(getters.accessToken, data.savedSearchId, data.isEnabled);
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
    async saveSearch({getters, dispatch}) {
        dispatch('showLoading', true);
        const checkedScrapers = [];
        getters.scrapers.forEach(item => {
            if (item.checked) {
                checkedScrapers.push(item.id);
            }
        });
        try {
            await services.saveSearch(getters.accessToken, {keywords: getters.keywords, checkedScrapers});
            await dispatch('retrieveSavedSearches');
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
    }
};

const getters = {
    savedSearches(state) {
        return state.savedSearches;
    },
    searchExists(state, getters) {
        let goOn = true;
        let savedSearchIndex = 0;
        let searchExists = false;
        while (goOn && savedSearchIndex < state.savedSearches.length) {
            const keywordsInCurrentSearch = state.savedSearches[savedSearchIndex].keywords.slice();
            const userKeywords = getters.keywords.slice();
            const scrapersInCurrentSearch = state.savedSearches[savedSearchIndex].scrapers.map(item => item.id);
            const scrapersSelected = [];
            getters.scrapers.forEach(item => {
                if (item.checked) {
                    scrapersSelected.push(item.id);
                }
            });
            if (keywordsInCurrentSearch.sort().join('').toLowerCase() === userKeywords.sort().join('').toLowerCase() && scrapersInCurrentSearch.sort().join('') === scrapersSelected.sort().join('')) {
                goOn = false;
                searchExists = true;
            }
            savedSearchIndex++;
        }
        return searchExists;
    }
};

export default {
    state,
    mutations,
    actions,
    getters
}
