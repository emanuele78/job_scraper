<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateSavedSearchesScrapersTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'saved_searches_scrapers', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->unsignedBigInteger('saved_search_id');
				$table->unsignedBigInteger('scraper_id');
				$table->foreign('saved_search_id')->references('id')->on('saved_searches')->onDelete('cascade');
				$table->foreign('scraper_id')->references('id')->on('scrapers');
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('saved_searches_scrapers');
		}
	}
