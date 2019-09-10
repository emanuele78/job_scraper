<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateAssignedLabelsTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'assigned_labels', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->string('name', 30);
				$table->string('color', 20);
				$table->unsignedBigInteger('user_id');
				$table->unsignedBigInteger('job_post_id');
				$table->foreign('user_id')->references('id')->on('users');
				$table->foreign('job_post_id')->references('id')->on('job_posts');
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('assigned_labels');
		}
	}
