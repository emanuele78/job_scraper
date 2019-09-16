<?php
	
	namespace App\Http\Controllers\Api;
	
	use App\Mail\PasswordResetRequest;
	use App\Services\OauthClientService;
	use App\User;
	use Carbon\Carbon;
	use DB;
	use Illuminate\Http\Request;
	use App\Http\Controllers\Controller;
	use Illuminate\Support\Facades\Hash;
	use Illuminate\Support\Facades\Mail;
	use Illuminate\Support\Str;
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
		
		public function login(Request $request, OauthClientService $oauthClientService) {
			
			return $oauthClientService->login($request->email, $request->password);
			
		}
		
		public function refreshToken(Request $request, OauthClientService $oauthClientService) {
			
			return $oauthClientService->refreshToken($request->refresh_token);
			
		}
		
		public function passwordResetRequest(Request $request) {
			
			$validator = Validator::make(
			  $request->all(), [
			  'email' => ['required'],
			]);
			if ($validator->fails()) {
				return response(['success' => false, 'errors' => 'Valid email must be provided'], 400);
			}
			$data = $validator->valid();
			$user = User::where('email', $data['email'])->first();
			if ($user) {
				//user found
				//delete previous token
				DB::table('password_resets')->where('email', $data['email'])->delete();
				$token = Str::uuid();
				DB::table('password_resets')->insert(['email' => $data['email'], 'token' => $token, 'created_at' => Carbon::now()]);
				$url = config('app.debug') ? config('app.debug_url') : config('app.url');
				$url = $url . '/password/nuova?token=' . $token;
				try {
					Mail::send(new PasswordResetRequest($url, $data['email']));
				} catch (\Exception $e) {
					//don't care
				}
				return response(['success' => true], 200);
			}
			return response(['success' => true], 200);
		}
		
		public function saveNewPassword(Request $request) {
			
			$validator = Validator::make(
			  $request->all(), [
			  'requestToken' => 'required|exists:password_resets,token',
			  'password' => ['required', 'string', 'min:6', 'confirmed'],
			]);
			if ($validator->fails()) {
				return config('app.debug') ? response(['success' => false, 'errors' => $validator->errors()], 400) : response(['success' => false], 400);
			}
			$data = $validator->valid();
			$passwordReset = DB::table('password_resets')->where('token', $data['requestToken'])->first();
			//check if expired
			$createdAt = Carbon::createFromFormat('Y-m-d H:i:s', $passwordReset->created_at);
			$expiredAfterMinutes = 60;
			if ($createdAt->diffInMinutes(Carbon::now()) > $expiredAfterMinutes) {
				return response(['success' => false], 400);
			}
			$user = User::where('email', $passwordReset->email)->first();
			$user->password = Hash::make($data['password']);
			$user->save();
			DB::table('password_resets')->where('email', $passwordReset->email)->delete();
			return response(['success' => true, 'email' => $passwordReset->email]);
		}
		
		public function logout() {
			
			auth()->user()->tokens->each(
			  function ($token, $key) {
				  
				  $token->delete();
			  });
			return response()->json('Logged out', 200);
		}
	}
