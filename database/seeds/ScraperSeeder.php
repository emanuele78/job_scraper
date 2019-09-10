<?php
	
	use App\Scraper;
	use Illuminate\Database\Seeder;
	
	class ScraperSeeder extends Seeder {
		
		public function run() {
			
			$scrapers = [
			  [
				'name' => 'indeed',
				'showed_name' => 'indeed'
			  ],
			  [
				'name' => 'ci',
				'showed_name' => 'centri impiego'
			  ],
			  [
				'name' => 'monster',
				'showed_name' => 'monster'
			  ],
			  [
				'name' => 'gigroup',
				'showed_name' => 'gi group'
			  ],
			  [
				'name' => 'adecco',
				'showed_name' => 'adecco'
			  ],
			  [
				'name' => 'randstad',
				'showed_name' => 'randstad'
			  ],
			  [
				'name' => 'manpower',
				'showed_name' => 'manpower'
			  ],
			  [
				'name' => 'subito',
				'showed_name' => 'subito'
			  ],
			  [
				'name' => 'linkedin',
				'showed_name' => 'linkedin'
			  ]
			];
			foreach ($scrapers as $scraper) {
				$s = new Scraper();
				$s->fill(['name' => $scraper['name'], 'showed_name' => $scraper['showed_name']]);
				$s->save();
			}
			
		}
	}
