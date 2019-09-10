const state = {
    activeLabel: 'inbox',
};

const mutations = {
    activeLabel(state, activeLabel) {
        state.activeLabel = activeLabel;
    }
};

const actions = {
    switchToLabel({commit, dispatch}, label) {
        commit('activeLabel', label);
        commit('activeCustomLabel', '');
        dispatch('search');
    },
    resetActiveLabel({commit}) {
        commit('activeLabel', 'inbox');
    }
};

const getters = {
    activeLabel(state) {
        return state.activeLabel;
    }
};

export default {
    state,
    mutations,
    actions,
    getters
}
