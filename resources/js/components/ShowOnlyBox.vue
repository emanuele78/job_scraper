<template>
	<div class="">
		<h5>Mostra solo:</h5>
		<div class="form-check">
			<input @change="changeShowOnly('all')" :checked="showOnly==='all'" class="form-check-input" id="allPostsRadio" type="radio" value="all" name="postsStatusRadio">
			<label class="form-check-label" for="allPostsRadio">
				Tutti gli annunci
			</label>
		</div>
		<div class="form-check">
			<input @change="changeShowOnly('only_unread')" :checked="showOnly==='only_unread'" class="form-check-input" id="onlyUnreadPostsRadio" type="radio" value="only_unread" name="postsStatusRadio">
			<label class="form-check-label" for="onlyUnreadPostsRadio">
				Annunci non letti
			</label>
		</div>
		<div class="form-check">
			<input @change="changeShowOnly('only_read')" :checked="showOnly==='only_read'" class="form-check-input" id="onlyReadPostsRadio" type="radio" value="only_read" name="postsStatusRadio">
			<label class="form-check-label" for="onlyReadPostsRadio">
				Annunci letti
			</label>
		</div>
	</div>
</template>
<script>
    export default {
        props: {
            showOnly: {
                type: String,
                required: true,
            }
        },
        methods: {
            changeShowOnly(value) {
                this.$emit('changeShowOnly', value);
            }
        }
    }
</script>
