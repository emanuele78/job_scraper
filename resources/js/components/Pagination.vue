<template>
	<nav aria-label="pagination" v-if="usePagination">
		<ul class="pagination justify-content-center">
			<li :class="{disabled: currentPage==1}" class="page-item">
				<a @click.prevent="moveTo(1)" class="page-link" href="#">
					<i class="fas fa-fast-backward" v-if="showIcon()"></i>
					<span v-else>Prima</span>
				</a>
			</li>
			<li :class="{disabled: currentPage==1}" class="page-item">
				<a @click.prevent="moveTo(currentPage-1)" class="page-link" href="#">
					<i class="fas fa-step-backward" v-if="showIcon()"></i>
					<span v-else>Precedente</span>
				</a>
			</li>
			<li :class="{active: page==currentPage}" class="page-item" v-for="page in pagination">
				<a @click.prevent="moveTo(page)" class="page-link" href="#">{{page}}</a>
			</li>
			<li :class="{disabled: currentPage==lastPage}" class="page-item">
				<a @click.prevent="moveTo(currentPage+1)" class="page-link" href="#">
					<i class="fas fa-step-forward" v-if="showIcon()"></i>
					<span v-else>Successiva</span>
				</a>
			</li>
			<li :class="{disabled: currentPage==lastPage}" class="page-item">
				<a @click.prevent="moveTo(lastPage)" class="page-link" href="#">
					<i class="fas fa-fast-forward" v-if="showIcon()"></i>
					<span v-else>Ultima</span>
				</a>
			</li>
		</ul>
	</nav>
</template>
<script>
    export default {
        props: {
            lastPage: {
                type: Number,
                required: true
            },
            currentPage: {
                type: Number,
                required: true
            }
        },
        computed: {
            usePagination() {
                return this.lastPage > 1;
            },
            pagination() {
                //must be odd
                const PAGE_LINKS_TO_SHOW = 5;
                const PAGES_FOR_EACH_DIRECTION = Math.floor(PAGE_LINKS_TO_SHOW / 2);
                const data = [];
                if (this.currentPage == 1) {
                    for (let i = 1; i <= this.lastPage && i <= PAGE_LINKS_TO_SHOW; i++) {
                        data.push(i);
                    }
                } else {
                    let startBlocked = false;
                    let startPage = this.currentPage - PAGES_FOR_EACH_DIRECTION;
                    if (startPage < 1) {
                        startBlocked = true;
                        startPage = 1;
                    }
                    let endBlocked = false;
                    let endPage = this.currentPage + PAGES_FOR_EACH_DIRECTION;
                    if (endPage > this.lastPage) {
                        endBlocked = true;
                        endPage = this.lastPage;
                    }
                    if (endPage - startPage < PAGE_LINKS_TO_SHOW - 1 && (!startBlocked || !endBlocked)) {
                        if (startBlocked) {
                            let pageToAdd = PAGES_FOR_EACH_DIRECTION - (this.currentPage - startPage);
                            endPage = pageToAdd + PAGES_FOR_EACH_DIRECTION > this.lastPage ? this.lastPage : this.currentPage + pageToAdd + PAGES_FOR_EACH_DIRECTION;
                        } else if (endBlocked) {
                            let pageToAdd = PAGES_FOR_EACH_DIRECTION - (endPage - this.currentPage);
                            startPage = this.currentPage - (pageToAdd + PAGES_FOR_EACH_DIRECTION) < 1 ? 1 : this.currentPage - (pageToAdd + PAGES_FOR_EACH_DIRECTION);
                        }
                    }
                    for (let i = startPage; i <= endPage; i++) {
                        data.push(i);
                    }
                }
                return data;
            }
        },
        methods: {
            moveTo(page) {
                if (page !== this.currentPage) {
                    this.$emit('moveTo', page);
                }
            },
            showIcon() {
                const breakpoint = 576;
                return Math.max(document.documentElement.clientWidth, window.innerWidth || 0) < breakpoint;
            }
        }
    }
</script>
<style></style>
