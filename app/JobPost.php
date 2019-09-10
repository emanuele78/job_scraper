<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class JobPost extends Model {
		
		protected $guarded = ['id', 'source_id', 'job_id'];
		protected $hidden = ['source_id', 'job_id'];
		
		public function scraper() {
			
			return $this->belongsTo('App\Scraper', 'source_id');
		}
		
		public function readByUsers() {
			
			return $this->hasMany('App\ReadPost');
		}
		
		public function markedFavoriteByUsers() {
			
			return $this->hasMany('App\FavoritePost');
		}
		
		public function archivedByUsers() {
			
			return $this->hasMany('App\ArchivedPost');
		}
		
		public function assignedLabels() {
			
			return $this->hasMany('App\AssignedLabel');
		}
	}
