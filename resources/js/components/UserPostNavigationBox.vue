<template>
	<div class="">
		<div class="col-12">
			<h5>I tuoi annunci:</h5>
			<ul class="custom_list">
				<li :class="{'active_box': activeLabel == 'inbox'}" @click="switchToLabel('inbox')" class="d-flex justify-content-start align-items-center">
					<span class="pr-2 text-muted">In arrivo</span>
					<span class="badge badge-primary badge-pill" v-if="inboxCount>0">{{inboxCount}}</span>
				</li>
				<li :class="{'active_box': activeLabel == 'favorite'}" @click="switchToLabel('favorite')" class="d-flex justify-content-start align-items-center">
					<span class="pr-2 text-muted">Preferiti</span>
					<span class="badge badge-primary badge-pill" v-if="favoriteCount>0">{{favoriteCount}}</span>
				</li>
				<li :class="{'active_box': activeLabel == 'archive'}" @click="switchToLabel('archive')" class="d-flex justify-content-start align-items-center">
					<span class="pr-2 text-muted">Archiviati</span>
					<span class="badge badge-primary badge-pill" v-if="archiveCount>0">{{archiveCount}}</span>
				</li>
			</ul>
		</div>
	</div>
</template>
<script>
    export default {
        props: {
            inboxCount: {
                required: true,
                type: Number,
            },
            favoriteCount: {
                required: true,
                type: Number,
            },
            archiveCount: {
                required: true,
                type: Number,
            },
            activeLabel: {
                required: true,
                type: String,
            }
        },
        methods: {
            switchToLabel(value) {
                if (this.activeLabel !== value) {
                    this.$emit('switchToLabel', value);
                }
            }
        }
    }
</script>
<style lang="scss">
	.custom_list {
		padding: 0;
		list-style-type: none;
		margin-bottom: 0;
		
		& > li {
			margin-bottom: 5px;
			padding-left: 10px;
			font-size: 16px;
			cursor: pointer;
			
			&:hover {
				background-color: lightgray;
			}
			
			&.active_box {
				background-color: lightgreen;
				border-radius: 0 10px 10px 0;
			}
			
		}
	}
</style>
