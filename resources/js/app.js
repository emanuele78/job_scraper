/**
 * First we will load all of this project's JavaScript dependencies which
 * includes Vue and other libraries. It is a great starting point when
 * building robust, powerful web applications using Vue and Laravel.
 */

require('./bootstrap');

// window.Vue = require('vue');
import Vue from 'vue';
import router from './router';
import store from './store';
import App from './App.vue';
import moment from 'moment';
/**
 * The following block of code may be used to automatically register your
 * Vue components. It will recursively scan this directory for the Vue
 * components and automatically register them with their "basename".
 *
 * Eg. ./components/ExampleComponent.vue -> <example-component></example-component>
 */

// const files = require.context('./', true, /\.vue$/i);
// files.keys().map(key => Vue.component(key.split('/').pop().split('.')[0], files(key).default));

// Vue.component('example-component', require('./components/ExampleComponent.vue').default);

/**
 * Next, we will create a fresh Vue application instance and attach it to
 * the page. Then, you may begin adding components to this application
 * or customize the JavaScript scaffolding to fit your unique needs.
 */

moment.locale('it');

Vue.filter('capitalize', value => {
    if (!value) return '';
    value = value.toString();
    return value.replace(/\w\S*/g, function (txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
});

Vue.filter('uppercase', value => {
    if (!value) return '(ND)';
    return value.toUpperCase();
});

Vue.filter('localize', value => {
    if (!value) return '(ND)';
    let date = moment(value, "YYYY-MM-DD HH:mm:ss");
    return date.format("DD/MM/YYYY HH:mm");
});

Vue.filter('localize_from_utc', value => {
    if (!value) return '(ND)';
    let date = moment.utc(value, "YYYY-MM-DD HH:mm:ss");
    return moment(date).local().format('DD/MM/YYYY HH:mm');
});

const app = new Vue({
    router,
    store,
    render: h => h(App),
}).$mount('#app');
