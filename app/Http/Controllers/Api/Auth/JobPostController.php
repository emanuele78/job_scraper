<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\Traits\PostsCountTrait;
	use App\Traits\SearchTrait;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	
	class JobPostController extends Controller {
		
		use SearchTrait;
		use PostsCountTrait;
		
		public function index(Request $request) {
			
			$userId = auth()->user()->id;
			$data = $request->all();
			$builder = $this->getBaseBuilder($data, $userId);
			//only inbox posts
			$builder->whereDoesntHave(
			  'archivedByUsers', function ($query) use ($userId) {
				
				$query->where('user_id', $userId);
			});
			$jobPosts = $builder->paginate($data['postsPerPage']);
			$postsCount = $this->getPostsCount($userId);
			return response(['job_posts' => $jobPosts, 'posts_count' => $postsCount], 200);
			
		}
	}
