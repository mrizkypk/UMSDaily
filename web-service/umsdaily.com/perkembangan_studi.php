<?php
require __DIR__ . DIRECTORY_SEPARATOR . 'core.php';

use \Curl\Curl;

function studi($nim) {
  $khs = [];
  $cookiePath = __DIR__ . DIRECTORY_SEPARATOR . 'cookie' . DIRECTORY_SEPARATOR . $nim . '.txt';
  $curl = new Curl();
  $curl->setOpt(CURLOPT_HEADER, true);
  $curl->setOpt(CURLOPT_COOKIESESSION, true);
  $curl->setOpt(CURLOPT_FOLLOWLOCATION, true);
  $curl->setOpt(CURLOPT_COOKIEFILE, $cookiePath);
  $curl->setOpt(CURLOPT_SSL_VERIFYPEER, false);
  $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
  $curl->setHeader('Referer', 'https://sia.ums.ac.id/mahasiswa/' . $nim);
  $curl->setHeader('Accept', '*/*');
  $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
  $curl->setHeader('Origin', 'https://sia.ums.ac.id');
  $curl->post('https://sia.ums.ac.id/cek-trk', [
    'nim' => $nim,
  ]);
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
    if (strpos($response, 'Transkrip Nilai Akademik')) {
      foreach ($html->find('tbody tr') as $tr) {
        if (is_null($tr->find('td', 1)) or is_null($tr->find('td', 2))) {
          continue;
        }
        $kode = $tr->find('td', 1)->plaintext;
        $matakuliah = $tr->find('td', 2)->plaintext;
        $matakuliahPart = explode('/', $matakuliah);
        if (isset($matakuliahPart[1])) {
          $matakuliah = $matakuliahPart[0];
        }
        $sks = $tr->find('td', 3)->plaintext;
        $semester = $tr->find('td', 4)->plaintext;
        $nilai = $tr->find('td', 5)->plaintext;
        $bobot = $tr->find('td', 6)->plaintext;

        $khs['daftar'][] = [
          'kode' => rapi($kode),
          'matakuliah' => rapi($matakuliah),
          'sks' => rapi($sks),
          'semester' => rapi($semester),
          'nilai' => rapi($nilai),
          'bobot' => rapi($bobot),
        ];
        foreach ($html->find('td[colspan=4]') as $key => $td) {
          if ($key == 0) {
            $khs['rangkuman']['jumlah_sks'] = $td->plaintext;
          } else {
            $khs['rangkuman']['indeks_prestasi_kumulatif'] = $td->plaintext;
          }
        }

      }
      return $khs;
    } else {
      return false;
    }
  }
}

$nim = trim($_GET['nim']);
$password = trim($_GET['password']);

$status = loginSia($nim, $password);
if ($status == false) {
  $error = ['message' => 'login'];
  http_response_code(403);
  echo json_encode($error);
} else {
  if (strtolower($nim) == "l200144020") {
    echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'perkembangan_studi_' . strtolower($nim) . '.json');
  } else {
    $array = studi($nim);
    if ($array == false) {
      http_response_code(500);
      $error = ['message' => 'empty'];
      echo json_encode($error);
    } else {  
      echo json_encode($array);
    }
  }
}