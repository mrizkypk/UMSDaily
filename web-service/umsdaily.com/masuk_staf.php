<?php
require __DIR__ . DIRECTORY_SEPARATOR . 'core.php';

use Firebase\JWT\JWT;
use \Curl\Curl;

$service_account_email = "ums-daily@appspot.gserviceaccount.com";
$private_key = "-----BEGIN PRIVATE KEY-----\nMIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQDQZJVsiGHulKvf\n+G4yadYXBTKIZpvky02EFk3kr2SvkBr4M9yhRFjB/ixIL41GhX3Bph5dEMaC8onC\nYtLu3hluOXJi5jwtPABy9vt6eyVlWLSpybUNgLrkhOBJKDtqLa0NUBiEhqs/bvH8\n7btv0umAlruWnOsNA71A1P33fn10UmzUXCJQA+TUbobkgljWPlXYpkDBGc8KVV5Z\n9PgVMJHl11aNNtWfLuIhdaA4LGWn8Jzm2ddTszYSClaRxiKe/lnS7o4wL48Hfb1K\n+B7w78Qn+pW/x7m1Pu2u4Mqds2rLikjuxPCanN6JcANLI3G8zqYuBp/tK5gHgfyg\nWOzKwEixAgMBAAECgf8iUxme8YKFSaJbFxBk6Anba3+8on9gWsbp0BgEaDStYORl\nqCUvFZLeYSnqD0Bgh/GuyS3h0Qw22rwrcxugkfSeXdf5ckgh1ernr3FnOzbC+q7H\nSJy0cf+r+YxNKyieKefXHtpAT0OQWeCB81EW73vb3PgJAXpgf70scJNkW0akDKNK\nFX5Hj6LgtN3i1InlIQewZ0lvJA0hq9v82PEkMgQmZSZnV8vlWqZIrdXpk9Bkg+0h\n03AcK0lwLmQGVIjM2R/4DMTiCp60cgYQo7eUM6O8KTB7nIVksak8+03wzCISPoeW\n5C1B5APIbC3+4bMFWZsroprdg2C9b23+zvw1yeECgYEA9q8CxcQ51hpK0oLmuV0l\nmeWLqnhYNalzrurLPIe+Bcp4yyv943bhzbrl4rwqFqgVHb2Tp+NiGZGKp4C+8/e8\nRNzfB0B+o2aDEjdxJj5VzIz/Xwqg7UeFE9oH6Vk+Sy7/QRjmKT9V+ZeXxyNgvbww\n8d4XV1MA615kLjYAGGMPxzkCgYEA2ENeunc43IFrEu8VgML0wyAHggVmns7VmThH\ngO7DjQu3ylMZYAL1yK9xq7pCtF379q1O3d31qH+uQYwlLdyWhWBBr9F5QCQfuzPt\neum8DkAdEGVpjq0t4laCnO7X1osTBMw7Q+yTJ69xQLMGU3SuInr9zSccC0ABdMgD\n86jPVTkCgYBx8eUV9M3CV+K32j844A2+SIl69JvKLbuB0HKQQyNDCYNCVc+wgflL\nTklhsgi+7mTSl/7cTevCY7foJ1AM4SHVrTBX6TKVx4RDpdheEe0PzxEmlYJAGAkg\nYlXFM7PShqLhLt8hrl/IKn2xPmYxPfxdvHiLnYuPLMqZjdf9a8SeAQKBgHWnQTPk\nhJTB+8STOXmRlqNJG4yK76daEy6GRbBJNS3YwCZcSyPGuHxzpj2s2XlS4C71XkCp\n7mm6/MxAOEHZDjKFiXbjWTmh7Xlk6L/DxUFjc+xln6W1ZY8LTYefEOFxS+mCSoPL\ngstGUTDHtJtWkR3FF6qyqjkcmy4zWqMGpbVRAoGBAIIaFXqNcSmDveLoSmZqisul\nyaX4a31UAoMqwr/woq4Q+iOpgMJ93t0fUXMFF8V+GOkiyYZcdAnBD1xA2SfcZgdV\nbx/tclLWEPGX855Q+F6PCmgRUyWwOs0TItFQ2ZOS3l+Jq1F+M3OJUwZiUyc1HKkm\nPSA1xWj6biLkBdibzfpg\n-----END PRIVATE KEY-----\n";

function create_custom_token($uid, $is_premium_account) {
  global $service_account_email, $private_key;

  $now_seconds = time();
  $payload = array(
    "iss" => $service_account_email,
    "sub" => $service_account_email,
    "aud" => "https://identitytoolkit.googleapis.com/google.identity.identitytoolkit.v1.IdentityToolkit",
    "iat" => $now_seconds,
    "exp" => $now_seconds+(60*60),  // Maximum expiration time is one hour
    "uid" => $uid,
    "claims" => array(
      "premium_account" => $is_premium_account
    )
  );
  return JWT::encode($payload, $private_key, "RS256");
}

$users = [
  'staff1' => [
    'password' => '123',
    'nama' => "Alex Setya"
  ],
  'staff2' => [
    'password' => '123',
    'nama' => "Surya Nurwahid"
  ],
];


$uid = strtolower($_GET['id']);
$password = $_GET['password'];

if (isset($users[$uid])) {
  if ($users[$uid]['password'] == $password) {
    $nama = $users[$uid]['nama'];
  } else {
    $nama = false;
  }
} else {
  $nama = false;
}


if ($nama != false) {
  echo json_encode([
    'id' => $uid,
    'nama' => $nama,
    'token' => create_custom_token($uid, false)
  ]);
} else {
  http_response_code(403);
  echo json_encode([
      'message' => 'login',
      'code' => '403'
  ]);
}
