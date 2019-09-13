<?php
	
	namespace App\Http\Controllers\Api\Auth;
	
	use App\JobPost;
	use App\SavedSearch;
	use DB;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	use Validator;
	
	class SavedSearchController extends Controller {
		
		public function store(Request $request) {
			
			$validator = Validator::make(
			  $request->all(), [
			  'checkedScrapers' => 'required|array|exists:scrapers,id',
			  'checkedScrapers.*' => 'required|distinct',
			  'keywords' => 'required|array',
			  'keywords.*' => 'required|distinct',
			]);
			if ($validator->fails()) {
				return config('app.debug') ? response($validator->errors()) : response('Wrong request', 400);
			}
			$data = $validator->valid();
			$userId = auth()->user()->id;
			$searchExists = DB::table('saved_searches')
				->where('user_id', $userId)
				->whereIn(
				  'id', function ($query) use ($data) {
					
					$query->select('saved_search_id')->from('saved_search_keywords')->whereIn('keyword', $data['keywords'])->groupBy('saved_search_id')->havingRaw("COUNT(KEYWORD) >= " . count($data['keywords']));
				})
				->whereIn(
				  'id', function ($query) use ($data) {
					
					$query->select('saved_search_id')->from('saved_search_scrapers')->whereIn('scraper_id', $data['checkedScrapers'])->groupBy('saved_search_id')->havingRaw("COUNT(scraper_id) >= " . count($data['checkedScrapers']));
				})->count() > 0;
			if ($searchExists) {
				return response('Search already exists', 400);
			}
			//search can be saved
			//retrieve last job post id
			$lastJobPostId = JobPost::orderBy('id', 'desc')->take(1)->get()->first()->id;
			//save search
			$savedSearch = SavedSearch::create(['last_job_post_id_notified' => $lastJobPostId, 'user_id' => $userId]);
			//save keywords
			foreach ($data['keywords'] as $keyword) {
				$savedSearch->keywords()->create(['keyword' => $keyword]);
			}
			//save scrapers
			foreach ($data['checkedScrapers'] as $scraper) {
				$savedSearch->scrapers()->create(['scraper_id' => $scraper]);
			}
			return response(['success' => true], 200);
		}
		
		public function destroy(SavedSearch $savedSearch) {
			
			$savedSearch->delete();
			return response(['success' => true], 200);
		}
		
		public function update(SavedSearch $savedSearch, Request $request) {
			
			$validator = Validator::make(
			  $request->all(), [
			  'emailNotification' => 'required|boolean',
			]);
			if ($validator->fails()) {
				return config('app.debug') ? response($validator->errors()) : response('Wrong request', 400);
			}
			$emailNotification = $validator->valid()['emailNotification'];
			$savedSearch->notification_enabled = $emailNotification;
			$savedSearch->save();
			return response(['success' => true], 200);
		}
		
		public function index() {
			
			$userId = auth()->user()->id;
			
			$savedSearches = SavedSearch::with(['scrapers.scraper', 'keywords'])
			  ->where('user_id', $userId)
			  ->get()->toArray();
			$data = [];
			foreach ($savedSearches as $savedSearch) {
				$search = [
				  'id' => $savedSearch['id'],
				  'notification_enabled' => $savedSearch['notification_enabled'],
				  'created_at' => $savedSearch['created_at'],
				  'scrapers' => array_map(
					function ($item) {
						
						return $item['scraper'];
					}, $savedSearch['scrapers']),
				  'keywords' => array_map(
					function ($item) {
						
						return $item['keyword'];
					}, $savedSearch['keywords']),
				];
				array_push($data, $search);
			}
			return response(['savedSearches' => $data], 200);
		}
	}
