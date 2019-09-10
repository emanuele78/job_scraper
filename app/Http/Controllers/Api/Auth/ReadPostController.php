<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\ReadPost;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	use Validator;
	
	class ReadPostController extends Controller {
		
		public function update(Request $request) {
			
			$userId = auth()->user()->id;
			$validator = Validator::make(
			  $request->all(), [
			  'postsIds' => 'required|array|exists:job_posts,id',
			  'postsIds.*' => 'required|distinct',
			  'markAsRead' => 'required|boolean',
			]);
			if ($validator->fails()) {
				//return $validator->errors();
				return response('Wrong request', 400);
			}
			$data = $validator->valid();
			if ($data['markAsRead']) {
				//need to add posts array in read_posts table
				foreach ($data['postsIds'] as $postId) {
					ReadPost::firstOrCreate(['user_id' => $userId, 'job_post_id' => $postId]);
				}
				return response(['success' => true, 'message' => 'posts marked as read'], 200);
			} else {
				//need to remove posts array from read_posts table
				ReadPost::where('user_id', $userId)->whereIn('job_post_id', $data['postsIds'])->delete();
				return response(['success' => true, 'message' => 'posts marked as unread'], 200);
			}
		}
		
	}
