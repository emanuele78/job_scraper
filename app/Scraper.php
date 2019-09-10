<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class Scraper extends Model {
		
		public $timestamps = false;
		protected $guarded = ['id'];
		protected $hidden = ['id'];
		
		public function jobPosts() {
			
			return $this->hasMany('App\JobPost', 'source_id', 'id');
		}
		
		public function reports() {
			
			return $this->hasMany('App\Report', 'source_id', 'id');
		}
		
		public function getEnabledAttribute($value) {
			
			return $value == 1 ? true : false;
		}
	}
