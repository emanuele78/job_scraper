<template>
	<div class="row">
		<div class="offset-1 col-10">
			<a class="iubenda-white no-brand iubenda-embed iub-body-embed" href="https://www.iubenda.com/privacy-policy/12084778" title="Privacy Policy">Privacy Policy</a>
		</div>
	</div>
</template>
<script>
    export default {
        created() {
            let s = document.createElement("script");
            let tag = document.getElementsByTagName("script")[0];
            s.src = "https://cdn.iubenda.com/iubenda.js";
            tag.parentNode.insertBefore(s, tag);
        }
    }
</script>
