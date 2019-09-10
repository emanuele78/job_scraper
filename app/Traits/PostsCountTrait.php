<?php
	namespace App\Traits;
	
	use App\JobPost;
	
	trait PostsCountTrait {
		
		public function getPostsCount($userId) {
			
			$inBoxCount = JobPost::whereDoesntHave(
			  'archivedByUsers', function ($query) use ($userId) {
				
				$query->where('user_id', $userId);
			})->count();
			
			$archivedCount = JobPost::whereHas(
			  'archivedByUsers', function ($query) use ($userId) {
				
				$query->where('user_id', $userId);
			})->count();
			
			$favoriteCount = JobPost::whereHas(
			  'markedFavoriteByUsers', function ($query) use ($userId) {
				
				$query->where('user_id', $userId);
			})->count();
			
			return [
			  'inBoxCount' => $inBoxCount,
			  'favoriteCount' => $favoriteCount,
			  'archivedCount' => $archivedCount,
			];
		}
	}
