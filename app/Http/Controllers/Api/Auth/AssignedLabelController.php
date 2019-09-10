<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\AssignedLabel;
	use App\Traits\SearchTrait;
	use DB;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	use Validator;
	
	class AssignedLabelController extends Controller {
		
		use SearchTrait;
		
		public function index(Request $request) {
			
			$userId = auth()->user()->id;
			$data = $request->all();
			$builder = $this->getBaseBuilder($data, $userId);
			//only current label
			$builder->whereHas(
			  'assignedLabels', function ($query) use ($userId, $data) {
				
				$query->where('user_id', $userId)->where('name', $data['showOnlyTaggedByLabel']);
			});
			$jobPosts = $builder->paginate($data['postsPerPage']);
			return response(['job_posts' => $jobPosts], 200);
		}
		
		public function store(Request $request) {
			
			$userId = auth()->user()->id;
			$validator = Validator::make(
			  $request->all(), [
			  'labelName' => 'required|string',
			  'labelColor' => 'required|string',
			  'postId' => 'required|integer|exists:job_posts,id',
			]);
			if ($validator->fails()) {
				return response('Wrong request', 400);
			}
			$data = $validator->valid();
			AssignedLabel::firstOrCreate(
			  [
				'user_id' => $userId,
				'job_post_id' => $data['postId'],
				'name' => $data['labelName'],
				'color' => $data['labelColor'],
			  ]);
			return response(['success' => true, 'message' => 'assigned label to post'], 200);
		}
		
		public function destroy($jobPostId) {
			
			$userId = auth()->user()->id;
			$assignedLabel = AssignedLabel::where('user_id', $userId)->where('job_post_id', $jobPostId)->first();
			if ($assignedLabel) {
				$assignedLabel->delete();
				return response(['success' => true, 'message' => 'removed label from post'], 200);
			}
			return response('Wrong request', 400);
		}
		
		public function update(Request $request) {
			
			$userId = auth()->user()->id;
			$validator = Validator::make(
			  $request->all(), [
			  'labelName' => 'required|string|exists:assigned_labels,name',
			]);
			if ($validator->fails()) {
				return response('Wrong request', 400);
			}
			$data = $validator->valid();
			DB::table('assigned_labels')->where('user_id', $userId)->where('name', $data['labelName'])->delete();
			return response(['success' => true, 'message' => 'label destroyed'], 200);
		}
	}
