import services from '../services';
import router from "../router";
import moment from 'moment';
import {isEmailValid} from "./password_email_validation";
import {isPasswordValid} from "./password_email_validation";

const state = {
    //registration
    signinEmailError: false,
    signinPasswordError: false,
    signinOnGoing: false,
    //login
    loginOnGoing: false,
    invalidCredentials: false,
    //user email
    userEmail: '',
    //tokens
    accessToken: null,
    refreshToken: null,
    tokenExpiresAt: null,
    //other
    timeoutRefresherId: null,
    stayConnected: true,
};

const mutations = {
    //signin
    signinEmailError(state, value) {
        state.signinEmailError = value;
    },
    signinPasswordError(state, value) {
        state.signinPasswordError = value;
    },
    signinOnGoing(state, value) {
        state.signinOnGoing = value;
    },
    //login
    loginOnGoing(state, value) {
        state.loginOnGoing = value;
    },
    invalidCredentials(state, value) {
        state.invalidCredentials = value;
    },
    //user email
    userEmail(state, email) {
        state.userEmail = email;
    },
    //reset
    resetSignin(state) {
        state.signinEmailError = false;
        state.signinPasswordError = false;
        state.signinOnGoing = false;
    },
    resetLogin(state) {
        state.loginEmailError = false;
        state.loginPasswordError = false;
        state.loginOnGoing = false;
        state.invalidCredentials = false;
    },
    resetEmail(state) {
        state.userEmail = '';
    },
    //tokens
    accessToken(state, value) {
        state.accessToken = value;
    },
    refreshToken(state, value) {
        state.refreshToken = value;
    },
    tokenExpiresAt(state, afterSeconds) {
        state.tokenExpiresAt = moment().add(afterSeconds, 'seconds');
    },
    setTimeoutRefresherId(state, id) {
        state.timeoutRefresherId = id;
    },
    stayConnected(state, value) {
        state.stayConnected = value;
    }
};

const actions = {
    async register({dispatch, commit, state}, data) {
        commit('signinEmailError', !isEmailValid(data.email));
        commit('signinPasswordError', !isPasswordValid(data.password));
        if (state.signinEmailError || state.signinPasswordError) {
            commit('signinOnGoing', false);
            return;
        }
        commit('signinOnGoing', true);
        try {

            const res = await services.doSignin(data);
            if (res.data.success) {
                commit('userEmail', res.data.email);
                router.push({name: 'login'});
            } else {
                if ('email' in res.data.errors) {
                    commit('signinEmailError', true);
                }
                if ('password' in res.data.errors) {
                    commit('signinPasswordError', true);
                }
                commit('signinOnGoing', false);
            }
        } catch (e) {
            //server error
            dispatch('showError');
        }
    },
    async login({commit, dispatch, state}, data) {
        if (!isEmailValid(data.email) || !isPasswordValid(data.password)) {
            commit('invalidCredentials', true);
            return;
        }
        commit('loginOnGoing', true);
        commit('invalidCredentials', false);
        commit('stayConnected', data.stayConnected);
        try {
            const res = await services.doLogin(data);
            dispatch('saveAuthData', {...res.data.data});
            //logged in
            dispatch('resetEmail');
            dispatch('resetStoreValues');
            commit('loginOnGoing', false);
            router.replace({name: 'dashboard'});
            await dispatch('search');
            await dispatch('retrieveAssignedLabels');
            await dispatch('retrieveSavedSearches');
        } catch (e) {
            //invalid credentials
            commit('loginOnGoing', false);
            commit('invalidCredentials', true);
        }
    },
    logout({dispatch, commit, getters}) {
        //try to logout from server
        services.doLogout(getters.accessToken).finally(() => {
            //don't care about server error
            dispatch('clearAuthAndUserData');
            dispatch('search');
            router.replace({name: 'login'});
        });
    },
    clearAuthAndUserData({commit, dispatch}) {
        commit('accessToken', null);
        commit('refreshToken', null);
        commit('tokenExpiresAt', null);
        localStorage.clear();
        clearTimeout(state.timeoutRefresherId);
        dispatch('resetStoreValues');
        dispatch('resetCounters');
        dispatch('resetCustomLabels');
        dispatch('resetActiveLabel');
        dispatch('resetSavedSearches');
    },
    saveAuthData({commit, dispatch, getters}, {expires_in, access_token, refresh_token}) {
        commit('accessToken', access_token);
        commit('refreshToken', refresh_token);
        commit('tokenExpiresAt', expires_in);
        if (getters.stayConnected) {
            localStorage.setItem('refreshToken', refresh_token);
            dispatch('setTimeoutForTokenRefresh', expires_in);
        }
    },
    async tryAutoLogin({dispatch}) {
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken == null) {
            localStorage.clear();
        } else {
            try {
                const res = await services.refreshToken(refreshToken);
                dispatch('saveAuthData', {...res.data.data});
            } catch (e) {
                localStorage.clear();
            }
        }
    },
    async doRefreshToken({state, dispatch}) {
        try {
            console.log("refreshing token");
            const res = await services.refreshToken(state.refreshToken);
            if (res.data.success) {
                dispatch('saveAuthData', {...res.data.data});
            } else {
                dispatch('logout');
            }
        } catch (e) {
            dispatch('handleAuthError');
        }
    },
    requestPasswordReset({}, email) {
        services.requestPasswordReset(email);
    },
    setTimeoutForTokenRefresh({commit, dispatch, state}, timeInSeconds) {
        commit('setTimeoutRefresherId', setTimeout(() => {
            dispatch('doRefreshToken');
        }, (timeInSeconds - 60) * 1000));
    },
    resetSignin({commit}) {
        commit('resetSignin');
    },
    resetLogin({commit}) {
        commit('resetLogin');
    },
    resetEmail({commit}) {
        commit('resetEmail');
    },
    handleAuthError({dispatch}) {
        dispatch('clearAuthAndUserData');
        router.replace({name: 'login'});
    }
};

const getters = {
    //signin
    signinOnGoing(state) {
        return state.signinOnGoing;
    },
    signinEmailError(state) {
        return state.signinEmailError;
    },
    signinPasswordError(state) {
        return state.signinPasswordError;
    },
    //login
    loginOnGoing(state) {
        return state.loginOnGoing;
    },
    invalidCredentials(state) {
        return state.invalidCredentials;
    },
    //user email
    userEmail(state) {
        return state.userEmail;
    },
    //tokens
    accessToken(state) {
        return state.accessToken;
    },
    refreshToken(state) {
        return state.refreshToken;
    },
    isAuth(state) {
        return state.accessToken != null && state.tokenExpiresAt != null;
    },
    stayConnected(state) {
        return state.stayConnected;
    }
};

export default {
    state,
    getters,
    mutations,
    actions
}
