<?php
	
	namespace App;
	
	use Illuminate\Database\Eloquent\Model;
	
	class AssignedLabel extends Model {
		
		public $timestamps = false;
		protected $guarded = ['id'];
		protected $hidden = ['id'];
		
		public function user() {
			
			return $this->belongsTo('App\User');
		}
		
		public function jobPosts() {
			
			return $this->hasMany('App\JobPost', 'id', 'job_post_id');
		}
	}
