export default {
    computed: {
        openLeft() {
            const breakpoint = 576;
            return Math.max(document.documentElement.clientWidth, window.innerWidth || 0) > breakpoint;
        }
    }
}
