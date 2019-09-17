<?php
	namespace App\Traits;
	
	use App\JobPost;
	
	trait SearchTrait {
		
		public function getBaseBuilder($data, $userId) {
			
			switch ($data['orderMode']) {
				case 'scraper_date':
					$order = 'created_at';
					break;
				case 'publication_date':
					$order = 'published_at_date';
					break;
				default:
					$order = 'source_id';
			}
			//eager load related data only for current user
			$builder = JobPost::with(
			  ['readByUsers' => function ($query) use ($userId) {
				  
				  $query->where('user_id', $userId);
			  }, 'markedFavoriteByUsers' => function ($query) use ($userId) {
				  
				  $query->where('user_id', $userId);
			  }, 'assignedLabels' => function ($query) use ($userId) {
				  
				  $query->where('user_id', $userId);
			  }, 'scraper']);
			//restrict search by given scrapers
			$builder->whereHas(
			  'scraper', function ($scraper) use ($data) {
				
				$scraper->whereIn('name', $data['checkedScrapers']);
			});
			//restrict search by given keywords if exist
			if (array_key_exists('keywords', $data) && $data['keywords'] != null) {
				
				foreach ($data['keywords'] as $key => $value) {
					
					$builder->where(
					  function ($query) use ($value) {
						  
						  $query->where('title', 'LIKE', '%' . $value . '%')
							->orWhere('company_name', 'LIKE', '%' . $value . '%')
							->orWhere('place', 'LIKE', '%' . $value . '%')
							->orWhere('description', 'LIKE', '%' . $value . '%');
					  });
				}
			}
			//show only read | unread posts if exists
			if (array_key_exists('showOnly', $data) && $data['showOnly'] != 'all') {
				if ($data['showOnly'] == 'only_read') {
					$builder->whereHas(
					  'readByUsers', function ($query) use ($userId) {
						
						$query->where('user_id', $userId);
					});
				} else {
					$builder->whereDoesntHave(
					  'readByUsers', function ($query) use ($userId) {
						
						$query->where('user_id', $userId);
					});
				}
			}
			return $builder->orderBy($order);
		}
	}
