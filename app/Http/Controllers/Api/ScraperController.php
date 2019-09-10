<?php
	
	namespace App\Http\Controllers\Api;
	
	use App\Scraper;
	use App\Http\Controllers\Controller;
	
	class ScraperController extends Controller {
		
		public function index() {
			
			$scrapers = Scraper::all();
			return response(['scrapers' => $scrapers], 200);
		}
	}
