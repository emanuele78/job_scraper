<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class SavedSearch extends Model {
		
		protected $guarded = ['id'];
		
		public function keywords() {
			
			return $this->hasMany('App\SavedSearchKeyword');
		}
		
		public function scrapers() {
			
			return $this->hasMany('App\SavedSearchScraper');
		}
		
		public function user(){
			
			return $this->belongsTo('App\User');
		}
	}
