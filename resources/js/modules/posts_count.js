const state = {
    inBoxCount: 0,
    favoriteCount: 0,
    archivedCount: 0,
};

const mutations = {
    inBoxCount(state, count) {
        state.inBoxCount = count;
    },
    favoriteCount(state, count) {
        state.favoriteCount = count;
    },
    archivedCount(state, count) {
        state.archivedCount = count;
    }
};

const actions = {
    updateCounters({commit}, data) {
        commit('inBoxCount', data.inBoxCount);
        commit('favoriteCount', data.favoriteCount);
        commit('archivedCount', data.archivedCount);
    },
    resetCounters({commit}) {
        commit('inBoxCount', 0);
        commit('favoriteCount', 0);
        commit('archivedCount', 0);
    }
};

const getters = {
    inBoxCount(state) {
        return state.inBoxCount;
    },
    favoriteCount(state) {
        return state.favoriteCount;
    },
    archivedCount(state) {
        return state.archivedCount;
    }
};

export default {
    state,
    getters,
    mutations,
    actions,
}
