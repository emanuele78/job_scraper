import services from "../services";

export const authSearch = async function ({getters, dispatch, commit}, searchData) {
    const checkedScrapers = getters.scrapers.filter(item => item.checked).map(item => item.name);
    if (checkedScrapers.length === 0) {
        //no need to perform server request
        return null;
    }
    const orderMode = getters.currentSortType;
    const keywords = getters.keywords;
    const postsPerPage = getters.currentPostsPerPage;
    const token = getters.accessToken;
    const showOnly = getters.showOnly;
    return await services[searchData.searchType](checkedScrapers, orderMode, keywords, postsPerPage, searchData.page, token, showOnly);
};
