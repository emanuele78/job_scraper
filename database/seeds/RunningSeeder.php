<?php
	
	use Illuminate\Database\Seeder;
	
	class RunningSeeder extends Seeder {
		
		/**
		 * Run the database seeds.
		 *
		 * @return void
		 */
		public function run() {
			
			DB::table('scraper_status')->insert(['is_running' => 0]);
		}
	}
