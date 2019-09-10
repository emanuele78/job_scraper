<?php
	
	use Illuminate\Support\Facades\Schema;
	use Illuminate\Database\Schema\Blueprint;
	use Illuminate\Database\Migrations\Migration;
	
	class CreateJobPostsTable extends Migration {
		
		/**
		 * Run the migrations.
		 *
		 * @return void
		 */
		public function up() {
			
			Schema::create(
			  'job_posts', function (Blueprint $table) {
				
				$table->bigIncrements('id');
				$table->unsignedBigInteger('source_id');
				$table->string('job_id');
				$table->string('published_at_string')->nullable();
				$table->date('published_at_date')->nullable();
				$table->string('company_name')->nullable();
				$table->text('title');
				$table->string('place')->nullable();
				$table->longText('description');
				$table->string('region',50);
				$table->longText('post_link');
				$table->timestamp('created_at')->useCurrent();
				$table->timestamp('updated_at')->useCurrent();
				$table->foreign('source_id')->references('id')->on('scrapers');
			});
		}
		
		/**
		 * Reverse the migrations.
		 *
		 * @return void
		 */
		public function down() {
			
			Schema::dropIfExists('job_posts');
		}
	}
