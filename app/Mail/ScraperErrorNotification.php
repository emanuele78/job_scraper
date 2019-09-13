<?php
	
	namespace App\Mail;
	
	use Illuminate\Bus\Queueable;
	use Illuminate\Mail\Mailable;
	use Illuminate\Queue\SerializesModels;
	use Illuminate\Contracts\Queue\ShouldQueue;
	
	class ScraperErrorNotification extends Mailable {
		
		use Queueable, SerializesModels;
		
		private $scrapers;
		
		public function __construct($scrapers) {
			
			$this->scrapers = $scrapers;
		}
		
		public function build() {
			
			return $this
			  ->from('jobscraper@emanuelemazzante.dev')
			  ->to(config('app.admin_mail_for_notification'))
			  ->subject('Errore scraper')
			  ->view('admin_scraper_error_mail')
			  ->with(['scrapers' => $this->scrapers]);
		}
		
	}
