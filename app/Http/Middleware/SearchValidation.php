<?php
	
	namespace App\Http\Middleware;
	
	use Closure;
	use Illuminate\Validation\Rule;
	use Validator;
	
	class SearchValidation {
		
		/**
		 * Handle an incoming request.
		 *
		 * @param  \Illuminate\Http\Request $request
		 * @param  \Closure $next
		 * @return mixed
		 */
		public function handle($request, Closure $next) {
			
			$validator = Validator::make(
			  $request->all(), [
			  'checkedScrapers' => 'required|array|exists:scrapers,name',
			  'checkedScrapers.*' => 'required|distinct',
			  'keywords' => 'array|nullable',
			  'showOnly' => [Rule::in(['all', 'only_read', 'only_unread'])],
			  'showOnlyTaggedByLabel' => 'string',
			  'orderMode' => ['required', Rule::in(['source', 'scraper_date', 'publication_date'])],
			  'postsPerPage' => ['required', Rule::in([20, 30, 50, 100])],
			]);
			if ($validator->fails()) {
				return config('app.debug') ? response($validator->errors()) : response('Wrong request', 400);
			}
			return $next($request);
		}
	}
