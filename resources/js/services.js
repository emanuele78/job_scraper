import axios from 'axios';

if (process.env.NODE_ENV === 'production') {
    // axios.defaults.baseURL = "https://emanuelemazzante.dev/demo/jobscraper/api";
    axios.defaults.baseURL = process.env.MIX_PRODUCTION_API_ENDPOINT;
} else {
    // axios.defaults.baseURL = "https://jobscraper.dev/api";
    axios.defaults.baseURL = process.env.MIX_DEVELOPMENT_API_ENDPOINT;
}

function getScrapers() {
    return axios.get('/scrapers');
}

function searchJobPostsForGuest(checkedScrapers, orderMode, keywords, postsPerPage, page) {
    const request = {
        params: {
            checkedScrapers,
            keywords,
            orderMode,
            postsPerPage,
            page
        }
    };
    return axios.get('/jobposts', request);
}

function getScrapersStats() {
    return axios.get('/reports');
}

function doSignin(data) {
    return axios.post('/register', data);
}

function doLogin(data) {
    return axios.post('/login', data);
}

function refreshToken(refreshToken) {
    const data = {
        refresh_token: refreshToken
    };
    return axios.post('/refresh_token', data);
}

//AUTH SERVICES
function doLogout(token) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.post('/auth/logout', null, auth);
}

function searchInboxPosts(checkedScrapers, orderMode, keywords, postsPerPage, page, token, showOnly) {
    const request = {
        headers: {'Authorization': "Bearer " + token},
        params: {
            checkedScrapers,
            keywords,
            orderMode,
            postsPerPage,
            page,
            showOnly
        }
    };
    return axios.get('/auth/jobposts', request);
}

function searchArchivedPosts(checkedScrapers, orderMode, keywords, postsPerPage, page, token, showOnly) {
    const request = {
        headers: {'Authorization': "Bearer " + token},
        params: {
            checkedScrapers,
            keywords,
            orderMode,
            postsPerPage,
            page,
            showOnly
        }
    };
    return axios.get('/auth/archivedposts', request);
}

function searchFavoritePosts(checkedScrapers, orderMode, keywords, postsPerPage, page, token, showOnly) {
    const request = {
        headers: {'Authorization': "Bearer " + token},
        params: {
            checkedScrapers,
            keywords,
            orderMode,
            postsPerPage,
            page,
            showOnly
        }
    };
    return axios.get('/auth/favoriteposts', request);
}

function setPostsReadStatus(data, token) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.patch('/auth/readposts', data, auth);
}

function setPostsFavoriteStatus(data, token) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.patch('/auth/favoriteposts', data, auth);
}

function updatePostsStatus(data, token) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.patch('/auth/archivedposts', data, auth);
}

function getPostsCount(token) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.get('/auth/postscount', auth);
}

function getUserLabels(token) {
    const request = {
        headers: {'Authorization': "Bearer " + token},
    };
    return axios.get('/auth/assignedlabelscount', request);
}

function assignLabelToPost(token, data) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.post('/auth/assignedlabels', data, auth);
}

function removeLabelFromPost(token, postId) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.delete(`/auth/assignedlabels/${postId}`, auth);
}

function destroyCustomLabel(token, labelName) {
    const auth = {
        headers: {'Authorization': "Bearer " + token}
    };
    return axios.patch('/auth/assignedlabels', {labelName}, auth);
}

function searchPostsByLabel(checkedScrapers, orderMode, keywords, postsPerPage, page, token, showOnly, showOnlyTaggedByLabel) {
    const request = {
        headers: {'Authorization': "Bearer " + token},
        params: {
            checkedScrapers,
            keywords,
            orderMode,
            postsPerPage,
            page,
            showOnly,
            showOnlyTaggedByLabel
        }
    };
    return axios.get('/auth/assignedlabels', request);
}

export default {
    getScrapers,
    searchJobPostsForGuest,
    searchInboxPosts,
    getScrapersStats,
    doSignin,
    doLogin,
    refreshToken,
    doLogout,
    setPostsReadStatus,
    setPostsFavoriteStatus,
    updatePostsStatus,
    getPostsCount,
    searchArchivedPosts,
    searchFavoritePosts,
    getUserLabels,
    assignLabelToPost,
    removeLabelFromPost,
    destroyCustomLabel,
    searchPostsByLabel,
}
