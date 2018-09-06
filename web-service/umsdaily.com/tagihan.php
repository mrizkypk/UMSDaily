<?php
require __DIR__ . DIRECTORY_SEPARATOR . 'core.php';

use \Curl\Curl;

function tagihan($nim) {
  $daftarTagihan = [];

  $cookiePath = __DIR__ . DIRECTORY_SEPARATOR . 'cookie' . DIRECTORY_SEPARATOR . $nim . '.txt';
  $curl = new Curl();
  $curl->setOpt(CURLOPT_HEADER, true);
  $curl->setOpt(CURLOPT_COOKIESESSION, true);
  $curl->setOpt(CURLOPT_FOLLOWLOCATION, true);
  $curl->setOpt(CURLOPT_COOKIEFILE, $cookiePath);
  $curl->setOpt(CURLOPT_SSL_VERIFYPEER, false);
  $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
  $curl->setHeader('Referer', 'https://star.ums.ac.id/mahasiswa/' . $nim);
  $curl->setHeader('Accept', '*/*');
  $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
  $curl->setHeader('Origin', 'https://star.ums.ac.id');
  $curl->post('https://sia.ums.ac.id/cek-tagihan-mhs', [
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
    if (strpos($response, 'Daftar dan Status Tagihan Pembayaran')) {
      $html = str_get_html($response);
      foreach ($html->find('table[id=mkadums]') as $index => $row) {
        foreach ($row->find('tbody tr') as $tr) {
          if (is_null($tr->find('td',1))) {
            continue;
          }
          $jenisPembayaran = $tr->find('td', 2)->plaintext;
          $jumlahBayar = $tr->find('td', 4)->plaintext;
          $tanggalBayar = $tr->find('td', 5)->plaintext;
          $statusTagihan = $tr->find('td', 6)->plaintext;
          $daftarTagihan[] = [
            'jenis_pembayaran' => trim($jenisPembayaran),
            'jumlah_bayar' => trim($jumlahBayar),
            'tanggal_bayar' => trim($tanggalBayar),
            'status_tagihan' => trim($statusTagihan)
          ];
        }
      }
      return $daftarTagihan;
    } else{
      return false;
    }
  }
}

$nim = trim($_GET['nim']);
$password = trim($_GET['password']);

$status = loginSia($nim, $password);
if ($status == false) {
  http_response_code(403);
  $error = ['message' => 'login'];
  echo json_encode($error);
} else {
  if (strtolower($nim) == "l200144020") {
    echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'tagihan_' . strtolower($nim) . '.json');
  } else {
    $array = tagihan($nim);
    if ($array == false) {
      http_response_code(500);
      $error = ['message' => 'empty'];
      echo json_encode($error);
    } else {  
      echo json_encode($array);
    }
  }
}