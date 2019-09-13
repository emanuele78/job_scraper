<?php
	
	namespace App\Providers;
	
	use App\Services\NotificationErrorService;
	use App\Services\NotificationMailService;
	use App\Services\OauthClientService;
	use Illuminate\Support\ServiceProvider;
	
	class AppServiceProvider extends ServiceProvider {
		
		/**
		 * Register any application services.
		 *
		 * @return void
		 */
		public function register() {
			//
		}
		
		/**
		 * Bootstrap any application services.
		 *
		 * @return void
		 */
		public function boot() {
			
			$this->app->singleton(
			  OauthClientService::class, function () {
				
				return new OauthClientService(
				  config('app.oauthClientId'),
				  config('app.oauthClientSecret'),
				  config('app.debug') ? config('app.development_base_url') : config('app.production_base_url'),
				  config('app.debug'));
			});
			
			$this->app->bind(
			  NotificationMailService::class, function () {
				
				return new NotificationMailService();
			});
			
			$this->app->bind(
			  NotificationErrorService::class, function () {
				
				return new NotificationErrorService();
			});
		}
	}
