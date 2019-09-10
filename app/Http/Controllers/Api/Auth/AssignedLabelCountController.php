<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\Http\Controllers\Controller;
	use App\JobPost;
	use DB;
	
	class AssignedLabelCountController extends Controller {
		
		public function index() {
			
			$userId = auth()->user()->id;
			
			$customLabels = DB::table('assigned_labels')
			  ->select('name', 'color')
			  ->where('user_id', $userId)
			  ->groupBy('name', 'color')->get();
			foreach ($customLabels as $customLabel) {
				$customLabel->postsCount = JobPost::whereHas(
				  'assignedLabels', function ($query) use ($userId, $customLabel) {
					
					$query->where('user_id', $userId)->where('name', $customLabel->name);
				})->count();
			}
			return $customLabels;
		}
	}
