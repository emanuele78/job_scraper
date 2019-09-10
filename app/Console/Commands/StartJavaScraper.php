<?php
	
	namespace App\Console\Commands;
	
	use Illuminate\Console\Command;
	
	class StartJavaScraper extends Command {
		
		/**
		 * The name and signature of the console command.
		 *
		 * @var string
		 */
		protected $signature = 'scraper:start';
		
		/**
		 * The console command description.
		 *
		 * @var string
		 */
		protected $description = 'Start the java scraper program';
		
		/**
		 * Create a new command instance.
		 *
		 * @return void
		 */
		public function __construct() {
			
			parent::__construct();
		}
		
		/**
		 * Execute the console command.
		 *
		 * @return mixed
		 */
		public function handle() {
			
			$this->line('Starting the java scraper program');
			exec('java -jar ' . config('app.java_scraper_path'));
			$this->line('Scraping finished');
		}
	}
