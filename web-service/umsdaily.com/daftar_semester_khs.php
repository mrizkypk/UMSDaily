<?php
require __DIR__ . DIRECTORY_SEPARATOR . 'core.php';

use \Curl\Curl;

function daftar($id) {
  $khs = [];
  $cookiePath = __DIR__ . DIRECTORY_SEPARATOR . 'cookie' . DIRECTORY_SEPARATOR . $id . '.txt';
  $curl = new Curl();
  $curl->setOpt(CURLOPT_HEADER, true);
  $curl->setOpt(CURLOPT_COOKIESESSION, true);
  $curl->setOpt(CURLOPT_COOKIEFILE, $cookiePath);
  $curl->setOpt(CURLOPT_SSL_VERIFYPEER, false);
  $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
  $curl->setHeader('Referer', 'https://sia.ums.ac.id/mahasiswa/' . $id);
  $curl->setHeader('Accept', '*/*');
  $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
  $curl->setHeader('Origin', 'https://sia.ums.ac.id');
  $curl->post('https://sia.ums.ac.id/cek-khs/' . $id);
  if ($curl->error) {
      http_response_code($curl->httpStatusCode);
      echo json_encode([
        'message' => 'server',
        'code' => $curl->httpStatusCode
      ]);
      die();
  } else {
      $response = $curl->response;
      $html = str_get_html($response);
      if (is_null($html->find('ul', 0))) {
        return false;
      } else {
        foreach ($html->find('ul', 0)->find('li a') as $a) {
          $exp = explode(',', $a->onclick);
          $exp2 = explode("')", $exp[1]);
          $id = preg_replace('/\D/', '', $exp2[0]);
          $nama = $a->plaintext;
          $nama = str_replace('&nbsp;', '', $nama);
          $nama = trim($nama);

          $khs[] = [
            'id' => $id,
            'nama' => $nama
          ];

        }
        return $khs;
      }
  }
}



$id = strtoupper(trim($_GET['nim']));
if (strtolower($id) == "l200144020") {
  echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'daftar_semester_khs_l200144020.json');
} else {
  $khs = daftar($id);
  if ($khs == false) {
    http_response_code(500);
    echo json_encode([
      'message' => 'empty'
    ]);
  } else {
    echo json_encode($khs);
  }
}