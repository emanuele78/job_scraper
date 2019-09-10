<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\FavoritePost;
	use App\Traits\PostsCountTrait;
	use App\Traits\SearchTrait;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	use Validator;
	
	class FavoritePostController extends Controller {
		
		use SearchTrait;
		use PostsCountTrait;
		
		public function update(Request $request) {
			
			$userId = auth()->user()->id;
			$validator = Validator::make(
			  $request->all(), [
			  'postsIds' => 'required|array|exists:job_posts,id',
			  'postsIds.*' => 'required|distinct',
			  'markAsFavorite' => 'required|boolean',
			]);
			if ($validator->fails()) {
				return config('app.debug') ? $validator->errors() : response('Wrong request', 400);
			}
			$data = $validator->valid();
			if ($data['markAsFavorite']) {
				//need to add posts array in favorite_posts table
				foreach ($data['postsIds'] as $postId) {
					FavoritePost::firstOrCreate(['user_id' => $userId, 'job_post_id' => $postId]);
				}
				return response(['success' => true, 'message' => 'posts marked as favorite'], 200);
			} else {
				//need to remove posts array from readed_posts table
				FavoritePost::where('user_id', $userId)->whereIn('job_post_id', $data['postsIds'])->delete();
				return response(['success' => true, 'message' => 'posts marked as not favorite'], 200);
			}
		}
		
		public function index(Request $request) {
			
			$userId = auth()->user()->id;
			$data = $request->all();
			$builder = $this->getBaseBuilder($data, $userId);
			//only favorite posts
			$builder->whereHas(
			  'markedFavoriteByUsers', function ($query) use ($userId) {
				
				$query->where('user_id', $userId);
			});
			$jobPosts = $builder->paginate($data['postsPerPage']);
			$postsCount = $this->getPostsCount($userId);
			return response(['job_posts' => $jobPosts, 'posts_count' => $postsCount], 200);
		}
	}
