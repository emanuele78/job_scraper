<?php
	
	Route::get(
	  '/', function () {
		
		return view('index');
	});
	
	Route::get(
	  '/scraper', function () {
		
		return view('index');
	});
	
	Route::get(
	  '/annunci', function () {
		
		return view('index');
	});
	
	Route::get(
	  '/accedi', function () {
		
		return view('index');
	});
	
	Route::get(
	  '/registrati', function () {
		
		return view('index');
	});
	
	Route::get(
	  '/dashboard', function () {
		
		return view('index');
	});
	
	Route::get(
	  '/ricerche', function () {
		
		return view('index');
	});
	
	//todo to be deleted
	Route::get(
	  '/test', function () {
		
		$userId = 1;
		
		$savedSearches = App\SavedSearch::with(['scrapers.scraper', 'keywords'])
		  ->where('user_id', $userId)
		  ->get()->toArray();
		$data = [];
		foreach ($savedSearches as $savedSearch) {
			$search = [
			  'id' => $savedSearch['id'],
			  'notification_enabled' => $savedSearch['notification_enabled'],
			  'created_at' => $savedSearch['created_at'],
			  'scrapers' => array_map(
				function ($item) {
					
					return $item['scraper'];
				}, $savedSearch['scrapers']),
			  'keywords' => array_map(
				function ($item) {
					
					return $item['keyword'];
				}, $savedSearch['keywords']),
			];
			array_push($data, $search);
		}
		
		return $data;
	});
	
