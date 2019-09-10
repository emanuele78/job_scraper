<?php
	
	namespace App\Http\Controllers\Api;
	
	use App\Report;
	use DB;
	use App\Http\Controllers\Controller;
	
	class ReportsController extends Controller {
		
		public function index() {
			
			$coll = Report::with('scraper')->orderBy('source_id')->orderBy('region')->get();
			$status = DB::table('scraper_status')->first()->is_running;
			return response(['stats' => $coll, 'status' => $status], 200);
			
		}
	}
