<template>
	<form class="userKeywords">
		<h5>Ricerca con parole chiave:</h5>
		<input :class="{tooLong: wordExceeds}" class="form-control" placeholder="termine di ricerca" type="text" v-model.trim="userKeyword">
		<button @click.prevent="addKeyword()" class="btn btn-sm btn-primary mt-2" type="submit">Aggiungi</button>
		<div class="keywords mt-2">
			<span class="keyword" v-for="(keyword, index) in keywords"><span @click="removeKeyword(index)" class="keyword_delete pl-1 pr-2">x</span>{{keyword}}</span>
		</div>
	</form>
</template>

<script>
    const maxLenght = 25;
    export default {
        props: {
            keywords: {
                required: true,
                type: Array,
            }
        },
        data() {
            return {
                userKeyword: '',
            }
        },
        computed: {
            wordExceeds() {
                return this.userKeyword.length > maxLenght;
            }
        },
        methods: {
            removeKeyword(index) {
                this.$emit('removeKeyword', index);
            },
            addKeyword() {
                if (this.userKeyword.length > 0 && this.userKeyword.length < maxLenght) {
                    this.$emit('addKeyword', this.userKeyword.toLowerCase());
                    this.userKeyword = '';
                }
            }
        }
    }
</script>
<style lang="scss">
	.userKeywords {
		
		.keywords {
			
			.keyword {
				display: inline-block;
				padding: 2px 4px;
				margin: 5px 5px 0 0;
				border-radius: 5px;
				color: white;
				background-color: #3490dc;
			}
			
			.keyword_delete {
				font-weight: 700;
				color: black;
				cursor: pointer;
			}
		}
		
		.tooLong {
			border: 1px solid #e3342f;
		}
	}
</style>
