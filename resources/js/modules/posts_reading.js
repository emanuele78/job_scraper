import services from "../services";

const actions = {

    async updatePostsReadStatus({getters, dispatch}, data) {
        try {
            if (getters.showOnly === 'only_read' || getters.showOnly === 'only_unread') {
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
    //mark posts as read locally
    data.postsIds.forEach(id => {
        const post = getters.jobPosts.find(item => item.id === id);
        if (data.markAsRead) {
            post.read_by_users = ['no matter'];
        } else {
            post.read_by_users = [];
        }
    });
    //mark posts as read on DB
    await services.setPostsReadStatus(data, getters.accessToken);
    //update counters
    const jobsCount = await services.getPostsCount(getters.accessToken);
    dispatch('updateCounters', {...jobsCount.data});
}

export default {
    actions,
}

