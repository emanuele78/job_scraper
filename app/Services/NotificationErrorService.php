<?php
	/**
	 * Created by PhpStorm.
	 * User: emanuelemazzante
	 * Date: 2019-09-11
	 * Time: 23:51
	 */
	
	namespace App\Services;
	
	use App\Mail\ScraperErrorNotification;
	use App\Report;
	use Mail;
	
	class NotificationErrorService {
		
		public function notifyIfError() {
			
			$errors = Report::with('scraper')->where('error', 1)->get()->toArray();
			$scrapers = [];
			foreach ($errors as $error) {
				
				array_push($scrapers, $error['scraper']['showed_name']);
			}
			
			if (count($scrapers)) {
				
				Mail::send(new ScraperErrorNotification($scrapers));
			}
			
		}
		
	}
