<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateReportsTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'reports', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->unsignedBigInteger('source_id');
				$table->string('region', 50);
				$table->integer('jobs_added');
				$table->integer('jobs_skipped');
				$table->timestamp('last_activity')->useCurrent();
				$table->tinyInteger('error')->default(0);
				$table->foreign('source_id')->references('id')->on('scrapers');
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('reports');
		}
	}
