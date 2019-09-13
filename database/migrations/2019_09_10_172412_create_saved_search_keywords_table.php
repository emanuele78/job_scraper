<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateSavedSearchKeywordsTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'saved_search_keywords', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->unsignedBigInteger('saved_search_id');
				$table->string('keyword', config('app.max_keyword_length'));
				$table->foreign('saved_search_id')->references('id')->on('saved_searches')->onDelete('cascade');
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('saved_search_keywords');
		}
	}
