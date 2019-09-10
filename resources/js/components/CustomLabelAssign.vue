<template>
	<div class="labels_assignment">
		<div class="label_wrapper" v-if="currentLabel">
			<span :class="'bg-'+currentLabel.color" class="custom_label"><span @click="removeCustomLabelFromPost(currentLabel.name)" class="custom_label_delete pl-1 pr-2">x</span>{{currentLabel.name}}</span>
		</div>
		<div :class="{show: expandDropdown}" class="dropdown" v-else>
			<button :id="'dropdownCustomLabel-'+index" aria-haspopup="true" class="btn btn-outline-dark btn-sm dropdown-toggle" data-toggle="dropdown" type="button">
				{{textToShow}}
			</button>
			<div :aria-labelledby="'dropdownCustomLabel-'+index" :class="{show: expandDropdown}" class="dropdown-menu" v-on-clickaway="showList">
				<button @click="assignCustomLabelToPost(label)" class="dropdown-item label_element" type="button" v-for="label in labels">
					<span :class="'bg-'+label.color" class="label_tag"></span>
					<span class="pl-2">{{label.name}}</span>
				</button>
			</div>
		</div>
	</div>
</template>
<script>
    import {mixin as clickaway} from 'vue-clickaway';

    export default {
        props: {
            labels: {
                type: Array,
                default: null
            },
            currentLabel: {
                type: Object,
                default: null
            },
            index: {
                type: Number,
                required: true,
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
            showList(event) {
                if (this.labels.length) {
                    // this.expandDropdown = !this.expandDropdown;
                    if (event.srcElement.id === 'dropdownCustomLabel-' + this.index) {
                        this.expandDropdown = !this.expandDropdown;
                    } else {
                        this.expandDropdown = false;
                    }
                }
            },
            removeCustomLabelFromPost(labelname) {
                this.$emit('removeCustomLabelFromPost', labelname);
            }
        },
        mixins: [clickaway],
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
