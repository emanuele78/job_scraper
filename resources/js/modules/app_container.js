const state = {
    isLoading: false,
    isError: false,
    isInitializing: true,
    successfullyInitialized: true
};

const mutations = {
    isLoading(state, value) {
        state.isLoading = value;
    },
    isError(state, value) {
        state.isError = value;
    },
    isInitializing(state, value) {
        state.isInitializing = value;
    },
    successfullyInitialized(state, value) {
        state.successfullyInitialized = value;
    }
};

const actions = {
    showLoading({commit}, value) {
        commit('isLoading', value);
    },
    showError({commit}) {
        commit('isError', true);
        setTimeout(() => {
            commit('isError', false);
        }, 1500);
    },
    hideSplash({commit}) {
        commit('isInitializing', false);
    },
    showIrreversibleError({commit}) {
        commit('successfullyInitialized', false);
    }
};

const getters = {
    isLoading(state) {
        return state.isLoading;
    },
    isError(state) {
        return state.isError;
    },
    isInitializing(state) {
        return state.isInitializing;
    },
    successfullyInitialized(state) {
        return state.successfullyInitialized;
    }
};

export default {
    state,
    actions,
    getters,
    mutations,
}
