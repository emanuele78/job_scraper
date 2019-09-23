<?php
	
	namespace App\Http\Controllers\Api;
	
	use App\Scraper;
	use App\Http\Controllers\Controller;
	use Artisan;
	use DB;
	
	class ScraperController extends Controller {
		
		public function index() {
			
			$scrapers = Scraper::all();
			return response(['scrapers' => $scrapers], 200);
		}
		
		public function store() {
			
			$status = DB::table('scraper_status')->first()->is_running;
			if ($status == 1) {
				return response(['success' => true], 200);
			}
			Artisan::call("scraper:start");
			return response(['success' => true], 200);
		}
	}
