<template>
	<div class="">
		<div class="col-12">
			<h5>Mostra solo:</h5>
			<div class="form-check">
				<input :checked="showOnly==='all'" @change="changeShowOnly('all')" class="form-check-input" id="allPostsRadio" name="postsStatusRadio" type="radio" value="all">
				<label class="form-check-label" for="allPostsRadio">
					Tutti gli annunci
				</label>
			</div>
			<div class="form-check">
				<input :checked="showOnly==='only_unread'" @change="changeShowOnly('only_unread')" class="form-check-input" id="onlyUnreadPostsRadio" name="postsStatusRadio" type="radio" value="only_unread">
				<label class="form-check-label" for="onlyUnreadPostsRadio">
					Annunci non letti
				</label>
			</div>
			<div class="form-check">
				<input :checked="showOnly==='only_read'" @change="changeShowOnly('only_read')" class="form-check-input" id="onlyReadPostsRadio" name="postsStatusRadio" type="radio" value="only_read">
				<label class="form-check-label" for="onlyReadPostsRadio">
					Annunci letti
				</label>
			</div>
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
