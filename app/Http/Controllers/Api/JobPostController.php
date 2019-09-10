<?php
	
	namespace App\Http\Controllers\Api;
	
	use App\JobPost;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	
	class JobPostController extends Controller {
		
		public function index(Request $request) {
			
			$data = $request->all();
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
			$builder = JobPost::with('scraper')->whereHas(
			  'scraper', function ($scraper) use ($data) {
				
				$scraper->whereIn('name', $data['checkedScrapers']);
			});
			if (array_key_exists('keywords', $data) && $data['keywords'] != null) {
				
				foreach ($data['keywords'] as $key => $value) {
					
					$builder->where(
					  function ($query) use ($value) {
						  
						  $query->where('title', 'LIKE', '%' . $value . '%')->orWhere('description', 'LIKE', '%' . $value . '%');
					  });
				}
			}
			$jobPosts = $builder->orderBy($order)->paginate($data['postsPerPage']);
			return response(['job_posts' => $jobPosts], 200);
		}
	}
