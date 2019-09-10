import services from "../services";

const state = {
    activeCustomLabel: '',
    assignedLabels: [],
};

const mutations = {
    activeCustomLabel(state, label) {
        state.activeCustomLabel = label;
    },
    assignedLabels(state, value) {
        state.assignedLabels = value;
    }
};

const actions = {
    resetCustomLabels({commit}) {
        commit('activeCustomLabel', '');
        commit('assignedLabels', []);
    },
    createNewCustomLabel({state, getters}, name) {
        const colors = ['primary', 'secondary', 'success', 'danger', 'warning', 'info',];
        const missingColors = colors.filter(color => {
            return getters.assignedLabels.findIndex(label => label.color === color) === -1;
        });
        state.assignedLabels.push({color: missingColors[0], name, postsCount: 0});
    },
    async assignCustomLabelToPost({state, getters, dispatch}, data) {
        //find post and assign label to it
        //posts can only have 1 label at time so no need to check if other label is present
        dispatch('showLoading', true);
        const post = getters.jobPosts.find(item => item.id === data.postId);
        post.assigned_labels.push(data.label);
        //add label to post in DB
        const labelName = data.label.name;
        const labelColor = data.label.color;
        const postId = post.id;
        try {
            //assign custom label to post on server
            await services.assignLabelToPost(getters.accessToken, {labelName, labelColor, postId});
            //find out label in labels list and increment value of count
            const customLabel = state.assignedLabels.find(label => label.name === data.label.name);
            customLabel.postsCount++;
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
    async removeCustomLabelFromPost({state, getters, dispatch}, data) {
        dispatch('showLoading', true);
        //find post
        const post = getters.jobPosts.find(item => item.id === data.postId);
        //remove label from post
        post.assigned_labels = [];
        try {
            //remove label from post in DB
            await services.removeLabelFromPost(getters.accessToken, post.id);
            //decrement value of custom labels count
            const customLabel = state.assignedLabels.find(label => label.name === data.labelName);
            customLabel.postsCount--;
            //need to check if activeCustomLabel == removed label
            //in this case relaunch search
            if (getters.activeCustomLabel === data.labelName) {
                dispatch('search');
            } else {
                dispatch('showLoading', false);
            }
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
    async deleteCustomLabel({getters, commit, state, dispatch}, data) {
        dispatch('showLoading', true);
        //remove label from local store
        state.assignedLabels.splice(data.labelIndex, 1);
        try {
            //check if label has associated posts
            if ('postsCount' in data.label) {
                //need to remove label in local storage posts
                getters.jobPosts.forEach(post => {
                    if (post.assigned_labels.length > 0 && post.assigned_labels[0].name === data.label.name) {
                        post.assigned_labels = [];
                    }
                });
                //need to remove label from DB
                await services.destroyCustomLabel(getters.accessToken, data.label.name);
            }
            //change to inbox if current label == deleted label
            if (getters.activeCustomLabel === data.label.name) {
                commit('activeLabel', 'inbox');
                commit('activeCustomLabel', '');
                dispatch('search');
            } else {
                dispatch('showLoading', false);
            }
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
    async retrieveAssignedLabels({getters, commit}) {
        const res = await services.getUserLabels(getters.accessToken);
        commit('assignedLabels', res.data);
    },
    switchToCustomLabel({commit, dispatch}, labelName) {
        commit('activeCustomLabel', labelName);
        commit('activeLabel', '');
        dispatch('search');
    },
    async customLabelSearch({getters, dispatch, commit}, page) {
        const checkedScrapers = getters.scrapers.filter(item => item.checked).map(item => item.name);
        if (checkedScrapers.length === 0) {
            //no need to perform server request
            return null;
        }
        const orderMode = getters.currentSortType;
        const keywords = getters.keywords;
        const postsPerPage = getters.currentPostsPerPage;
        const token = getters.accessToken;
        const showOnlyTaggedByLabel = getters.activeCustomLabel;
        const showOnly = getters.showOnly;
        return await services.searchPostsByLabel(checkedScrapers, orderMode, keywords, postsPerPage, page, token, showOnly, showOnlyTaggedByLabel);
    }
};

const getters = {
    assignedLabels(state) {
        return state.assignedLabels;
    },
    activeCustomLabel(state) {
        return state.activeCustomLabel;
    }
};

export default {
    state,
    mutations,
    actions,
    getters
};
