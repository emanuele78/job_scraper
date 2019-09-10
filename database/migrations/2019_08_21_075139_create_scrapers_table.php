<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateScrapersTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'scrapers', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->string('name', 50)->unique();
				$table->string('showed_name', 50);
				$table->tinyInteger('enabled')->default(1);
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('scrapers');
		}
	}
