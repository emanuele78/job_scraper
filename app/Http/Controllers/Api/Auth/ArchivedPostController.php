<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\ArchivedPost;
	use App\Traits\PostsCountTrait;
	use App\Traits\SearchTrait;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	use Validator;
	
	class ArchivedPostController extends Controller {
		
		use SearchTrait;
		use PostsCountTrait;
		
		public function update(Request $request) {
			
			$userId = auth()->user()->id;
			$validator = Validator::make(
			  $request->all(), [
			  'postsIds' => 'required|array|exists:job_posts,id',
			  'postsIds.*' => 'required|distinct',
			  'moveToArchive' => 'required|boolean',
			]);
			if ($validator->fails()) {
				return config('app.debug') ? $validator->errors() : response('Wrong request', 400);
			}
			$data = $validator->valid();
			if ($data['moveToArchive']) {
				foreach ($data['postsIds'] as $postId) {
					ArchivedPost::firstOrCreate(['user_id' => $userId, 'job_post_id' => $postId]);
				}
				return response(['success' => true, 'message' => 'posts archived'], 200);
			} else {
				ArchivedPost::where('user_id', $userId)->whereIn('job_post_id', $data['postsIds'])->delete();
				return response(['success' => true, 'message' => 'posts moved to inbox'], 200);
			}
		}
		
		public function index(Request $request) {
			
			$userId = auth()->user()->id;
			$data = $request->all();
			$builder = $this->getBaseBuilder($data, $userId);
			//only archived posts
			$builder->whereHas(
			  'archivedByUsers', function ($query) use ($userId) {
				
				$query->where('user_id', $userId);
			});
			$jobPosts = $builder->paginate($data['postsPerPage']);
			$postsCount = $this->getPostsCount($userId);
			return response(['job_posts' => $jobPosts, 'posts_count' => $postsCount], 200);
		}
	}
