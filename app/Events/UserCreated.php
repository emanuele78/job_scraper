<?php
	
	namespace App\Events;
	
	use Illuminate\Broadcasting\Channel;
	use Illuminate\Queue\SerializesModels;
	use Illuminate\Broadcasting\PrivateChannel;
	use Illuminate\Broadcasting\PresenceChannel;
	use Illuminate\Foundation\Events\Dispatchable;
	use Illuminate\Broadcasting\InteractsWithSockets;
	use Illuminate\Contracts\Broadcasting\ShouldBroadcast;
	
	class UserCreated {
		
		use Dispatchable, InteractsWithSockets, SerializesModels;
		
		private $userEmail;
		
		/**
		 * UserCreated constructor.
		 *
		 * @param $email
		 */
		public function __construct($email) {
			
			$this->userEmail = $email;
		}
		
		/**
		 * Get the channels the event should broadcast on.
		 *
		 * @return \Illuminate\Broadcasting\Channel|array
		 */
		public function broadcastOn() {
			
			return new PrivateChannel('channel-name');
		}
		
		public function getUserEmail() {
			
			return $this->userEmail;
		}
	}
