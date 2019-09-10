<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateSavedSearchesKeywordsTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'saved_searches_keywords', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->string('keyword', config('app.max_keyword_length'));
				$table->unsignedBigInteger('saved_search_id');
				$table->foreign('saved_search_id')->references('id')->on('saved_searches')->onDelete('cascade');
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('saved_searches_keywords');
		}
	}
