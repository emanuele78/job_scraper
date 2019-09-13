<?php
	
	namespace App\Mail;
	
	use Illuminate\Bus\Queueable;
	use Illuminate\Mail\Mailable;
	use Illuminate\Queue\SerializesModels;
	use Illuminate\Contracts\Queue\ShouldQueue;
	
	class SavedSearchNotification extends Mailable {
		
		use Queueable, SerializesModels;
		
		private $postsFound;
		private $checkedScrapers;
		private $keywords;
		private $userEmail;
		
		/**
		 * SavedSearchNotification constructor.
		 *
		 * @param $postsFound
		 * @param $checkedScrapers
		 * @param $keywords
		 * @param $userEmail
		 */
		public function __construct($postsFound, $checkedScrapers, $keywords, $userEmail) {
			
			$this->postsFound = $postsFound;
			$this->checkedScrapers = $checkedScrapers;
			$this->keywords = $keywords;
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
			  ->to($this->userEmail)
			  ->subject('Nuovi annunci trovati')
			  ->view('saved_search_mail')
			  ->with(['postsFound' => $this->postsFound, 'checkedScrapers' => $this->checkedScrapers, 'keywords' => $this->keywords]);
		}
	}
