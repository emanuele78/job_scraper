<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class SavedSearchKeyword extends Model {
		
		public $timestamps = false;
		protected $guarded = ['id'];
		
		public function savedSearch(){
			
			return $this->belongsTo('App\SavedSearch','saved_search_id','id');
		}
	}
