<template>
	<div class="labels_assignment">
		<div class="label_wrapper" v-if="currentLabel">
			<span :class="'bg-'+currentLabel.color" class="custom_label"><span @click="removeCustomLabelFromPost(currentLabel.name)" class="custom_label_delete pl-1 pr-2">x</span>{{currentLabel.name}}</span>
		</div>
		<div :class="{show: expandDropdown}" class="dropdown" v-else>
			<button @click="showList()" aria-haspopup="true" class="btn btn-outline-dark btn-sm dropdown-toggle" data-toggle="dropdown" id="dropdownCustomLabel" type="button">
				{{textToShow}}
			</button>
			<div :class="{show: expandDropdown}" aria-labelledby="dropdownCustomLabel" class="dropdown-menu">
				<button @click="assignCustomLabelToPost(label)" class="dropdown-item label_element" type="button" v-for="label in labels">
					<span :class="'bg-'+label.color" class="label_tag"></span>
					<span class="pl-2">{{label.name}}</span>
				</button>
			</div>
		</div>
	</div>
</template>
<script>
    export default {
        props: {
            labels: {
                type: Array,
                default: null
            },
            currentLabel: {
                type: Object,
                default: null
            }
        },
        data() {
            return {
                expandDropdown: false
            }
        },
        computed: {
            textToShow() {
                return this.currentLabel ? this.currentLabel.name : 'Assegna etichetta'
            },
        },
        methods: {
            assignCustomLabelToPost(label) {
                if (!this.currentLabel || (this.currentLabel.name != label.name)) {
                    this.expandDropdown = false;
                    this.$emit('assignCustomLabelToPost', label);
                }
            },
            showList() {
                if (this.labels.length) {
                    this.expandDropdown = !this.expandDropdown;
                }
            },
            removeCustomLabelFromPost(labelname) {
                this.$emit('removeCustomLabelFromPost', labelname);
            }
        }
    }
</script>
<style lang="scss">
	.labels_assignment {
		#dropdownCustomLabel {
			font-size: 10px;
		}
		
		.label_wrapper {
			.custom_label {
				display: inline-block;
				padding: 2px 6px 2px 4px;
				font-size: 12px;
				border-radius: 5px 15px 15px 5px;
				color: white;
			}
			
			.custom_label_delete {
				font-weight: 700;
				color: black;
				cursor: pointer;
			}
		}
		
		.label_element {
			display: flex;
			flex-direction: row;
			justify-content: flex-start;
			align-items: center;
			font-size: 12px;
			
			.label_tag {
				display: inline-block;
				height: 20px;
				width: 25px;
				border-radius: 5px 15px 15px 5px;
				line-height: 20px;
				font-weight: 700;
				text-align: center;
				cursor: pointer;
			}
		}
	}
</style>
