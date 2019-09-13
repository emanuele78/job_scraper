<template>
	<div class="">
		<div class="col-12">
			<form class="userKeywords">
				<h5>Ricerca con parole chiave:</h5>
				<input :class="{tooLong: wordExceeds}" class="form-control" placeholder="termine di ricerca" type="text" v-model.trim="userKeyword">
				<div class="action_buttons mt-2">
					<button @click.prevent="addKeyword()" class="btn btn-sm btn-primary" type="submit">Aggiungi</button>
					<button v-if="showSaveSearchButton" @click.prevent="saveSearch()" class="btn btn-sm btn-success">Salva ricerca</button>
				</div>
				<div class="keywords mt-2">
					<span class="keyword" v-for="(keyword, index) in keywords"><span @click="removeKeyword(index)" class="keyword_delete pl-1 pr-2">x</span>{{keyword}}</span>
				</div>
			</form>
		</div>
	</div>
</template>
<script>
    const maxLenght = process.env.MIX_MAX_KEYWORD_LENGTH;
    export default {
        props: {
            keywords: {
                required: true,
                type: Array,
            },
            showSaveSearchButton: {
                required: true,
                type: Boolean
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
            },
            saveSearch() {
                this.$emit('saveSearch');
            }
        }
    }
</script>
<style lang="scss">
	.userKeywords {
		
		.action_buttons {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: space-between;
		}
		
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
