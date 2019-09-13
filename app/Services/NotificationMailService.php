<?php
	
	namespace App\Services;
	
	use App\JobPost;
	use App\Mail\SavedSearchNotification;
	use App\SavedSearch;
	use Log;
	use Mail;
	
	class NotificationMailService {
		
		public function startNotify() {
			
			//retrieve last job post id
			$lastJobPostId = JobPost::orderBy('id', 'desc')->take(1)->get()->first()->id;
			//get all enabled saved searches
			$savedSearches = SavedSearch::with(['scrapers.scraper', 'keywords', 'user'])
			  ->where('notification_enabled', 1)
			  ->where('last_job_post_id_notified', '<', $lastJobPostId)
			  ->get();
			
			foreach ($savedSearches as $savedSearchObject) {
				
				$savedSearch = $savedSearchObject->toArray();
				
				$lastNotifiedPost = $savedSearch['last_job_post_id_notified'];
				
				$checkedScrapersIds = array_map(
				  function ($item) {
					  
					  return $item['id'];
				  }, $savedSearch['scrapers']);
				
				$checkedScrapersNames = array_map(
				  function ($item) {
					  
					  return $item['scraper']['showed_name'];
				  }, $savedSearch['scrapers']);
				
				$keywords = array_map(
				  function ($item) {
					  
					  return $item['keyword'];
				  }, $savedSearch['keywords']);
				
				//looking for new job posts since last notified
				$builder = JobPost::where('id', '>', $lastNotifiedPost)->whereHas(
				  'scraper', function ($scraper) use ($checkedScrapersIds) {
					
					$scraper->whereIn('id', $checkedScrapersIds);
				});
				foreach ($keywords as $keyword) {
					
					$builder->where(
					  function ($query) use ($keyword) {
						  
						  $query->where('title', 'LIKE', '%' . $keyword . '%')->orWhere('description', 'LIKE', '%' . $keyword . '%');
					  });
				}
				$postsFounds = $builder->count();
				
				
				if ($postsFounds) {
					$userMail = $savedSearch['user']['email'];
					
					Mail::send(new SavedSearchNotification($postsFounds, $checkedScrapersNames, $keywords, $userMail));
					//update search last post id notified
					$savedSearchObject->last_job_post_id_notified = $lastJobPostId;
					$savedSearchObject->save();
				}
			}
		}
		
	}
