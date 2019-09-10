<?php
	
	namespace App\Http\Controllers\Api;
	
	use App\User;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	use Illuminate\Support\Facades\Hash;
	use Validator;
	
	class AuthController extends Controller {
		
		public function register(Request $request) {
			
			$validator = Validator::make(
			  $request->all(), [
			  'email' => ['required', 'string', 'email', 'max:255', 'unique:users'],
			  'password' => ['required', 'string', 'min:6'],
			]);
			if ($validator->fails()) {
				return response(['success' => false, 'errors' => $validator->errors()], 200);
			}
			$data = $validator->valid();
			$user = User::create(
			  [
				'name' => $data['email'],
				'email' => $data['email'],
				'password' => Hash::make($data['password']),
			  ]);
			return response(['success' => true, 'email' => $user->email], 200);
		}
		
		public function login(Request $request) {
			
			if (config('app.debug')) {
				$client = new \GuzzleHttp\Client(
				  [
					'curl' => array(CURLOPT_SSL_VERIFYPEER => false),
					'verify' => false
				  ]);
				$base_url = config('app.development_base_url');
			} else {
				$client = new \GuzzleHttp\Client();
				$base_url = config('app.production_base_url');
			}
			try {
				$response = $client->request(
				  'POST', $base_url . '/oauth/token', [
				  'form_params' => [
					'grant_type' => 'password',
					'client_id' => config('app.oauthClientId'),
					'client_secret' => config('app.oauthClientSecret'),
					'username' => $request->email,
					'password' => $request->password,
					'scope' => '',
				  ],
				]);
				$data = json_decode($response->getBody(), true);
				return response(['success' => true, 'data' => $data], 200);
			} catch (\Exception $e) {
				return response(['success' => false, 'error' => $e->getMessage()], 401);
			}
		}
		
		public function refreshToken(Request $request) {
			
			if (config('app.debug')) {
				$client = new \GuzzleHttp\Client(
				  [
					'curl' => array(CURLOPT_SSL_VERIFYPEER => false),
					'verify' => false
				  ]);
				$base_url = config('app.development_base_url');
			} else {
				$client = new \GuzzleHttp\Client();
				$base_url = config('app.production_base_url');
			}
			try {
				$response = $client->request(
				  'POST', $base_url . '/oauth/token', [
				  'form_params' => [
					'grant_type' => 'refresh_token',
					'refresh_token' => $request->refresh_token,
					'client_id' => config('app.oauthClientId'),
					'client_secret' => config('app.oauthClientSecret'),
					'scope' => '',
				  ],
				]);
				$data = json_decode($response->getBody(), true);
				return response(['success' => true, 'data' => $data], 200);
			} catch (\Exception $e) {
				return response(['success' => false, 'error' => $e->getMessage()], 401);
			}
		}
		
		public function logout() {
			
			auth()->user()->tokens->each(
			  function ($token, $key) {
				  
				  $token->delete();
			  });
			return response()->json('Logged out', 200);
		}
	}
