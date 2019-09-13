<?php
	
	namespace App\Console\Commands;
	
	use App\Services\NotificationErrorService;
	use App\Services\NotificationMailService;
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
		 * @param NotificationErrorService $notificationErrorService
		 * @param NotificationMailService $notificationMailService
		 */
		public function handle(NotificationErrorService $notificationErrorService, NotificationMailService $notificationMailService) {
			
			$this->line('Starting the java scraper program');
			exec('java -jar ' . config('app.java_scraper_path'));
			$this->line('Scraping finished');
			$this->line('Check for errors');
			$notificationErrorService->notifyIfError();
			$this->line('Notify user with saved searches');
			$notificationMailService->startNotify();
			$this->line('Finished');
		}
	}
