import services from "../services";

const actions = {

    async updatePostsStatus({getters, dispatch}, data) {
        try {
            //need to check if active label == 'favorite'
            if (getters.activeLabel === 'inbox' || getters.activeLabel === 'archived') {
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
    //archive posts on server
    await services.updatePostsStatus(data, getters.accessToken);
    //update counters
    const jobsCount = await services.getPostsCount(getters.accessToken);
    dispatch('updateCounters', {...jobsCount.data});
}

export default {
    actions,
}
