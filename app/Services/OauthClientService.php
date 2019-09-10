<?php
	
	namespace App\Services;
	
	class OauthClientService {
		
		private $client_id;
		private $client_secret;
		private $base_url;
		private $ignore_ssl_certificate;
		
		public function __construct($client_id, $client_secret, $base_url, $ignore_ssl_certificate) {
			
			$this->client_id = $client_id;
			$this->client_secret = $client_secret;
			$this->base_url = $base_url;
			$this->ignore_ssl_certificate = $ignore_ssl_certificate;
		}
		
		public function login($email, $password) {
			
			if ($this->ignore_ssl_certificate) {
				$client = new \GuzzleHttp\Client(
				  [
					'curl' => array(CURLOPT_SSL_VERIFYPEER => false),
					'verify' => false
				  ]);
			} else {
				$client = new \GuzzleHttp\Client();
			}
			try {
				$response = $client->request(
				  'POST', $this->base_url . '/oauth/token', [
				  'form_params' => [
					'grant_type' => 'password',
					'client_id' => $this->client_id,
					'client_secret' => $this->client_secret,
					'username' => $email,
					'password' => $password,
					'scope' => '',
				  ],
				]);
				$data = json_decode($response->getBody(), true);
				return response(['success' => true, 'data' => $data], 200);
			} catch (\Exception $e) {
				return response(['success' => false, 'error' => $e->getMessage()], 401);
			}
		}
		
		public function refreshToken($refresh_token) {
			
			if ($this->ignore_ssl_certificate) {
				$client = new \GuzzleHttp\Client(
				  [
					'curl' => array(CURLOPT_SSL_VERIFYPEER => false),
					'verify' => false
				  ]);
			} else {
				$client = new \GuzzleHttp\Client();
			}
			try {
				$response = $client->request(
				  'POST', $this->base_url . '/oauth/token', [
				  'form_params' => [
					'grant_type' => 'refresh_token',
					'refresh_token' => $refresh_token,
					'client_id' => $this->client_id,
					'client_secret' => $this->client_secret,
					'scope' => '',
				  ],
				]);
				$data = json_decode($response->getBody(), true);
				return response(['success' => true, 'data' => $data], 200);
			} catch (\Exception $e) {
				return response(['success' => false, 'error' => $e->getMessage()], 401);
			}
		}
	}
