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
	
	//test
	Route::get(
	  'test', function () {
		
		$userId = 1;
		$labelName = 'casa';
		return App\JobPost::whereHas(
		  'assignedLabels', function ($query) use ($userId, $labelName) {
			
			$query->where('user_id', $userId)->where('name', $labelName);
		})->count();
	});
	
	Route::get(
	  'test2', function () {
		
		$userId = 1;
		
		$customLabels = DB::table('assigned_labels')
		  ->select('name', 'color')
		  ->where('user_id', $userId)
		  ->groupBy('name', 'color')->get();
		foreach ($customLabels as $customLabel) {
			$customLabel->postsCount = App\JobPost::whereHas(
			  'assignedLabels', function ($query) use ($userId, $customLabel) {
				
				$query->where('user_id', $userId)->where('name', $customLabel->name);
			})->count();
		}
		return $customLabels;
	});
