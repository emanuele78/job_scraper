<?php
	
	Route::middleware(['only_ajax'])->group(
	  function () {
		  
		  //list of all scrapers with status
		  Route::get('scrapers', 'Api\ScraperController@index');
		  
		  //job posts for guest
		  Route::get('jobposts', 'Api\JobPostController@index')->middleware('search_validation');
		  
		  //reports
		  Route::get('reports', 'Api\ReportsController@index');
		  
		  //register
		  Route::post('register', 'Api\AuthController@register');
		  
		  //login
		  Route::post('login', 'Api\AuthController@login');
		  
		  //token refresh
		  Route::post('refresh_token', 'Api\AuthController@refreshToken');
		  
		  //request password reset
		  Route::get('password_reset', 'Api\AuthController@passwordResetRequest');
		  
		  //store new password
		  Route::post('password_reset', 'Api\AuthController@saveNewPassword');
	  });
	
	//auth routes
	Route::middleware(['auth:api', 'only_ajax'])->group(
	  function () {
		  
		  //logout
		  Route::post('auth/logout', 'Api\AuthController@logout');
		  
		  //all the posts
		  Route::get('auth/jobposts', 'Api\Auth\JobPostController@index')->middleware('search_validation');
		  
		  //favorite posts
		  Route::get('auth/favoriteposts', 'Api\Auth\FavoritePostController@index')->middleware('search_validation');
		  
		  //archived posts
		  Route::get('auth/archivedposts', 'Api\Auth\ArchivedPostController@index')->middleware('search_validation');
		  
		  //posts count
		  Route::get('auth/postscount', 'Api\Auth\JobPostsCountController@index');
		  
		  //mark | unmark post as read
		  Route::patch('auth/readposts', 'Api\Auth\ReadPostController@update');
		  
		  //mark | unmark post as favorite
		  Route::patch('auth/favoriteposts', 'Api\Auth\FavoritePostController@update');
		  
		  //archive | unarchive post
		  Route::patch('auth/archivedposts', 'Api\Auth\ArchivedPostController@update');
		  
		  //assigned labels (grouped + count)
		  Route::get('auth/assignedlabelscount', 'Api\Auth\AssignedLabelCountController@index');
		  
		  //assign remove custom label to post
		  Route::post('auth/assignedlabels', 'Api\Auth\AssignedLabelController@store');
		  
		  //remove custom label to post
		  Route::delete('auth/assignedlabels/{id}', 'Api\Auth\AssignedLabelController@destroy');
		  
		  //destroy custom label
		  Route::patch('auth/assignedlabels', 'Api\Auth\AssignedLabelController@update');
		  
		  //assigned label search
		  Route::get('auth/assignedlabels', 'Api\Auth\AssignedLabelController@index')->middleware('search_validation');
		
		  //saved searches list
		  Route::get('auth/savedsearches', 'Api\Auth\SavedSearchController@index');
		  
		  //saved search creation
		  Route::post('auth/savedsearches', 'Api\Auth\SavedSearchController@store');
		  
		  //delete saved search
		  Route::delete('auth/savedsearches/{savedSearch}', 'Api\Auth\SavedSearchController@destroy');
		  
		  //change saved search email notification status
		  Route::patch('auth/savedsearches/{savedSearch}', 'Api\Auth\SavedSearchController@update');
	  });
	
	


