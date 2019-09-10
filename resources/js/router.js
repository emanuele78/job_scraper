import Vue from 'vue';
import Router from 'vue-router';
import Home from './views/Home.vue';
import ScraperStats from './views/ScraperStats.vue';
import Jobs from './views/Jobs.vue';
import Login from './views/Login';
import Signin from './views/Signin';
import Dashboard from './views/Dashboard.vue';
import store from './store';

Vue.use(Router);

const router = new Router({
    mode: 'history',
    base: process.env.NODE_ENV === 'production'? process.env.MIX_PRODUCTION_BASE_URL : process.env.MIX_DEVELOPMENT_BASE_URL,
    // base: process.env.BASE_URL,
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home,
        },
        {
            path: '/scraper',
            name: 'scraper',
            component: ScraperStats
        },
        {
            path: '/annunci',
            name: 'jobs',
            component: Jobs,
            beforeEnter(to, from, next) {
                if (!store.getters.isAuth) {
                    next();
                } else {
                    next({name: 'dashboard'});
                }
            }
        },
        {
            path: '/accedi',
            name: 'login',
            component: Login,
            beforeEnter(to, from, next) {
                if (!store.getters.isAuth) {
                    next();
                } else {
                    next({name: 'home'});
                }
            }
        },
        {
            path: '/registrati',
            name: 'signin',
            component: Signin,
            beforeEnter(to, from, next) {
                if (!store.getters.isAuth) {
                    next();
                } else {
                    next({name: 'home'});
                }
            }
        },
        {
            path: '/dashboard',
            name: 'dashboard',
            component: Dashboard,
            beforeEnter(to, from, next) {
                if (store.getters.isAuth) {
                    next();
                } else {
                    next({name: 'login'});
                }
            }
        }
    ]
});

export default router;
