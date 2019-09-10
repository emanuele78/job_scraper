<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class Report extends Model {
		
		public $timestamps = false;
		protected $guarded = ['id'];
		protected $hidden = ['id', 'source_id'];
		
		public function scraper() {
			
			return $this->belongsTo('App\Scraper', 'source_id');
		}
		
		public function getErrorAttribute($value) {
			
			return $value == 1 ? true : false;
		}
	}
