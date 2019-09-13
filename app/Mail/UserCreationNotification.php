<?php
	
	namespace App\Mail;
	
	use Illuminate\Bus\Queueable;
	use Illuminate\Mail\Mailable;
	use Illuminate\Queue\SerializesModels;
	use Illuminate\Contracts\Queue\ShouldQueue;
	
	class UserCreationNotification extends Mailable {
		
		use Queueable, SerializesModels;
		
		private $userEmail;
		
		/**
		 * UserCreationNotification constructor.
		 *
		 * @param $userEmail
		 */
		public function __construct($userEmail) {
			
			$this->userEmail = $userEmail;
		}
		
		/**
		 * Build the message.
		 *
		 * @return $this
		 */
		public function build() {
			
			return $this
			  ->from('jobscraper@emanuelemazzante.dev')
			  ->to(config('app.admin_mail_for_notification'))
			  ->subject('Creazione nuovo utente')
			  ->view('admin_user_created_mail')
			  ->with(['userEmail' => $this->userEmail]);
		}
	}
