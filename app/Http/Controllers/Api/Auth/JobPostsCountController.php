<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\Traits\PostsCountTrait;
	use App\Http\Controllers\Controller;
	
	class JobPostsCountController extends Controller {
		
		use PostsCountTrait;
		
		public function index() {
			
			$userId = auth()->user()->id;
			return $this->getPostsCount($userId);
		}
	}
