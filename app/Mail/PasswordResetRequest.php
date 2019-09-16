<?php
	
	namespace App\Mail;
	
	use Illuminate\Bus\Queueable;
	use Illuminate\Mail\Mailable;
	use Illuminate\Queue\SerializesModels;
	use Illuminate\Contracts\Queue\ShouldQueue;
	
	class PasswordResetRequest extends Mailable {
		
		use Queueable, SerializesModels;
		
		private $url;
		private $emailTo;
		
		/**
		 * PasswordResetRequest constructor.
		 *
		 * @param $url
		 * @param $emailTo
		 */
		public function __construct($url, $emailTo) {
			
			$this->emailTo = $emailTo;
			$this->url = $url;
		}
		
		/**
		 * Build the message.
		 *
		 * @return $this
		 */
		public function build() {
			
			return $this
			  ->from('jobscraper@emanuelemazzante.dev')
			  ->to($this->emailTo)
			  ->subject('Reset della password')
			  ->view('password_reset_mail')
			  ->with(['url' => $this->url]);
		}
	}
