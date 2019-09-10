<template>
	<form class="custom_label_creation">
		<h5>Etichette personali:</h5>
		<div class="" v-if="allowNewLabels">
			<input :class="{not_allowed: wordExceeds || labelExists}" class="form-control" placeholder="etichetta" type="text" v-model.trim="userLabel">
			<button @click.prevent="createNewCustomLabel()" class="btn btn-sm btn-primary mt-2" type="submit">Crea nuova</button>
		</div>
		<div :class="{current_label_bg: label.name === activeCustomLabel}" @click="switchToCustomLabel(label.name)" class="a_label mt-2" v-for="(label, index) in labels">
			<div :class="'bg-'+label.color" @click.stop="deleteCustomLabel(label,index)" class="label_tag">x</div>
			<span class="label_name text-muted pl-2 pr-2">{{label.name}}</span>
			<span class="badge badge-primary badge-pill" v-if="label.postsCount>0">{{label.postsCount}}</span>
		</div>
	</form>
</template>
<script>
    const maxLenght = 25;
    const maxLabelsCount = 6;

    export default {
        props: {
            labels: {
                required: true,
                type: Array,
            },
            activeCustomLabel: {
                type: String,
                required: true,
            }
        },
        data() {
            return {
                userLabel: '',
            }
        },
        computed: {
            wordExceeds() {
                return this.userLabel.length > maxLenght;
            },
            labelExists() {
                return this.labels.map(label => label.name).indexOf(this.userLabel.toLowerCase()) !== -1;
            },
            allowNewLabels() {
                return this.labels.length < maxLabelsCount;
            }
        },
        methods: {
            deleteCustomLabel(label, labelIndex) {
                this.$emit('deleteCustomLabel', {
                    label,
                    labelIndex
                });
            },
            switchToCustomLabel(labelName) {
                if (!this.activeCustomLabel || labelName !== this.activeCustomLabel) {
                    this.$emit('switchToCustomLabel', labelName);
                }
            },
            createNewCustomLabel() {
                if (this.labels.map(label => label.name).indexOf(this.userLabel.toLowerCase()) === -1 && this.userLabel.length <= maxLenght && this.userLabel.length > 0) {
                    this.$emit('createNewCustomLabel', this.userLabel.toLowerCase());
                    this.userLabel = '';
                }
            }
        },
    }
</script>
<style lang="scss">
	.custom_label_creation {
		
		.current_label_bg {
			background-color: lightgray;
		}
		
		.a_label {
			display: flex;
			flex-direction: row;
			align-items: center;
			justify-content: flex-start;
			cursor: pointer;
			
			&:hover {
				background-color: lightgray;
			}
			
			.label_tag {
				display: inline-block;
				height: 20px;
				width: 25px;
				border-radius: 5px 15px 15px 5px;
				color: white;
				line-height: 20px;
				font-weight: 700;
				text-align: center;
			}
		}
		
		.not_allowed {
			border: 1px solid #e3342f;
		}
	}
</style>
