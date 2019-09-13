<?php
	
	namespace App\Http\Controllers\Api;
	
	use App\Events\UserCreated;
	use App\Services\OauthClientService;
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
			event(new UserCreated($user->email));
			return response(['success' => true, 'email' => $user->email], 200);
		}
		
		public function login(Request $request, OauthClientService $oauthClientService) {
			
			return $oauthClientService->login($request->email, $request->password);
			
		}
		
		public function refreshToken(Request $request, OauthClientService $oauthClientService) {
			
			return $oauthClientService->refreshToken($request->refresh_token);
			
		}
		
		public function logout() {
			
			auth()->user()->tokens->each(
			  function ($token, $key) {
				  
				  $token->delete();
			  });
			return response()->json('Logged out', 200);
		}
	}
