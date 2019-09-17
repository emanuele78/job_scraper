<template>
	<div class="labels_assignment">
		<div :class="{show: expandDropdown}" class="dropdown labels_dropdown">
			<button :id="'dropdownCustomLabel-'+jobPostIndex" aria-haspopup="true" class="btn btn-outline-dark btn-sm dropdown-toggle" data-toggle="dropdown" type="button">Assegna etichetta</button>
			<div :aria-labelledby="'dropdownCustomLabel-'+jobPostIndex" :class="{show: expandDropdown}" class="dropdown-menu" v-on-clickaway="showList">
				<button @click="assignCustomLabelToPost(label)" class="dropdown-item label_element" type="button" v-for="label in notAssignedLabels">
					<span :class="'bg-'+label.color" class="label_tag"></span>
					<span class="pl-2">{{label.name}}</span>
				</button>
			</div>
		</div>
		<span :class="'bg-'+assignedLabel.color" class="custom_label ml-2" v-for="assignedLabel in assignedLabels"><span @click="removeCustomLabelFromPost(assignedLabel.name)" class="custom_label_delete pl-1 pr-2">x</span>{{assignedLabel.name}}</span>
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
            assignedLabels: {
                type: Array,
                required: true,
            },
            jobPostIndex: {
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
            notAssignedLabels() {
                const freeLabels = [];
                this.labels.forEach(label => {
                    //find label in assigned labels array
                    const index = this.assignedLabels.findIndex(assignedLabel => {
                        return assignedLabel.name === label.name;
                    });
                    if (index === -1) {
                        freeLabels.push(label);
                    }
                });
                return freeLabels;
            }
        },
        methods: {
            assignCustomLabelToPost(label) {
                this.expandDropdown = false;
                this.$emit('assignCustomLabelToPost', label);
            },
            showList(event) {
                if (this.labels.length) {
                    if (event.srcElement.id === 'dropdownCustomLabel-' + this.jobPostIndex) {
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
		display: flex;
		flex-direction: row;
		justify-content: flex-start;
		align-items: center;
		
		#dropdownCustomLabel {
			font-size: 10px;
		}
		
		.custom_label {
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
