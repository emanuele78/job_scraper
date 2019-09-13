<?php
	
	namespace App\Listeners;
	
	use App\Events\UserCreated;
	use App\Mail\UserCreationNotification;
	use Illuminate\Queue\InteractsWithQueue;
	use Illuminate\Contracts\Queue\ShouldQueue;
	use Mail;
	
	class NotifyUserCreation {
		
		/**
		 * Create the event listener.
		 *
		 * @return void
		 */
		public function __construct() {
			//
		}
		
		/**
		 * Handle the event.
		 *
		 * @param  UserCreated $event
		 * @return void
		 */
		public function handle(UserCreated $event) {
			
			if (config('app.notify_user_creation')) {
				Mail::send(new UserCreationNotification($event->getUserEmail()));
			}
		}
	}
