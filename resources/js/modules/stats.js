import services from "../services";

const state = {
    statsData: [],
    scraperStatus: null,
};

const mutations = {
    setStats(state, data) {
        state.statsData = data;
    },
    setScraperStatus(state, status) {
        state.scraperStatus = status;
    }
};

const actions = {
    async readScraperStats({dispatch, commit}) {
        try {
            const res = await services.getScrapersStats();
            commit('setStats', res.data.stats);
            commit('setScraperStatus', res.data.status);
        } catch (e) {
            //server error
            dispatch('showError');
        }
    },
    async startScraper({commit}) {
        try {
            const SCRAPER_STATUS_STARTED = 1;
            commit('setScraperStatus', SCRAPER_STATUS_STARTED);
            await services.launchScraper();
        } catch (e) {
        }
    }
};

const getters = {
    stats(state) {
        return state.statsData;
    },
    scraperStatus(state) {
        return state.scraperStatus;
    },
};

export default {
    state,
    mutations,
    actions,
    getters
}
