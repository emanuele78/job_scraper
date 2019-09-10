<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateSavedSearchesTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'saved_searches', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->tinyInteger('notification_enabled')->default(1);
				$table->unsignedBigInteger('user_id');
				$table->bigInteger('last_job_post_id_notified')->default(-1);
				$table->foreign('user_id')->references('id')->on('users');
				$table->timestamps();
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('saved_searches');
		}
	}
