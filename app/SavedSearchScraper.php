<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class SavedSearchScraper extends Model {
		
		public $timestamps = false;
		protected $guarded = ['id'];
		
		public function savedSearch() {
			
			return $this->belongsTo('App\SavedSearch', 'saved_search_id', 'id');
		}
		
		public function scraper() {
			
			return $this->belongsTo('App\Scraper', 'scraper_id', 'id');
		}
	}
