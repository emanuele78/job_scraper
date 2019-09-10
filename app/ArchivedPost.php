<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class ArchivedPost extends Model {
		
		public $timestamps = false;
		protected $guarded = ['id'];
		protected $hidden = ['id'];
		
		public function user() {
			
			return $this->hasOne('App\User', 'id', 'user_id');
		}
		
		public function post() {
			
			return $this->hasOne('App\JobPost', 'id', 'job_post_id');
		}
	}
