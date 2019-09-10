import services from "../services";

const actions = {
    async updatePostsFavoriteStatus({getters, dispatch}, data) {
        try {
            if (getters.activeLabel === 'favorite') {
                dispatch('showLoading', true);
                await update(data, getters, dispatch);
                dispatch('search');
            } else {
                await update(data, getters, dispatch);
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
    }
};

async function update(data, getters, dispatch) {
    //mark posts as favorite locally
    data.postsIds.forEach(id => {
        const post = getters.jobPosts.find(item => item.id === id);
        if (data.markAsFavorite) {
            post.marked_favorite_by_users = ['no matter'];
        } else {
            post.marked_favorite_by_users = [];
        }
    });
    //mark posts as favorite on DB
    await services.setPostsFavoriteStatus(data, getters.accessToken);
    //update counters
    const jobsCount = await services.getPostsCount(getters.accessToken);
    dispatch('updateCounters', {...jobsCount.data});
}

export default {
    actions,
}
