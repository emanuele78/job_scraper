<?php
	
	namespace App;
	
	use App\Events\UserCreated;
	use Illuminate\Notifications\Notifiable;
	use Illuminate\Contracts\Auth\MustVerifyEmail;
	use Illuminate\Foundation\Auth\User as Authenticatable;
	use Laravel\Passport\HasApiTokens;
	
	class User extends Authenticatable {
		
		use HasApiTokens, Notifiable;
		
		protected $dispatchesEvents = [
		  'created' => UserCreated::class,
		];
		
		
		/**
		 * The attributes that are mass assignable.
		 *
		 * @var array
		 */
		protected $fillable = [
		  'name', 'email', 'password',
		];
		
		/**
		 * The attributes that should be hidden for arrays.
		 *
		 * @var array
		 */
		protected $hidden = [
		  'password', 'remember_token',
		];
		
		/**
		 * The attributes that should be cast to native types.
		 *
		 * @var array
		 */
		protected $casts = [
		  'email_verified_at' => 'datetime',
		];
		
		public function readPosts() {
			
			return $this->hasMany('App\ReadPost');
		}
		
		public function favoritePosts() {
			
			return $this->hasMany('App\FavoritePost');
		}
		
		public function archivedPosts() {
			
			return $this->hasMany('App\ArchivedPost');
		}
		
		public function assignedLabels() {
			
			return $this->hasMany('App\AssignedLabel');
		}
	}
	
	
