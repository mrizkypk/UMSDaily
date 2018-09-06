<?php
require __DIR__ . DIRECTORY_SEPARATOR . 'core.php';

use \Curl\Curl;

function getJadwalHtml($id) {
  $cookiePath = __DIR__ . DIRECTORY_SEPARATOR . 'cookie' . DIRECTORY_SEPARATOR . $id . '.txt';

  $curl = new Curl();
  $curl->setOpt(CURLOPT_HEADER, true);
  $curl->setOpt(CURLOPT_COOKIESESSION, true);
  $curl->setOpt(CURLOPT_COOKIEFILE, $cookiePath);
  $curl->setOpt(CURLOPT_FOLLOWLOCATION, true);
  $curl->setOpt(CURLOPT_SSL_VERIFYPEER, false);
  $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
  $curl->setHeader('Referer', 'https://sia.ums.ac.id/?logout=true' . strtolower($id));
  $curl->setHeader('Accept', '*/*');
  $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
  $curl->setHeader('Origin', 'https://sia.ums.ac.id');
  $curl->post('https://sia.ums.ac.id/cek-jdkuliah-reg', [
    'nim' => $id,
  ]);

  if ($curl->error) {
      http_response_code($curl->httpStatusCode);
      echo json_encode([
        'message' => 'server',
        'code' => $curl->httpStatusCode
      ]);
      die();
  } else {
    $response = urldecode($curl->response);

    if ($curl->error) {
      return false;
    } else {
      $html = $curl->response;
      if (strpos($html, '<table')) {
        return $html;
      } else {
        return false;
      }
    }
  }
}

function getJadwalArray($nim) {
  $krs = array();
  $html = getJadwalHtml($nim);
  if ($html == false) {
    return false;
  }
  $html = str_get_html($html);
  foreach($html->find('table[id=tblTa]') as $index => $row) {
    $hari = rapi(str_replace("'", '', strtolower($row->parent()->parent()->find('h5',$index)->plaintext)));
    foreach ($row->find('tbody tr') as $tr) {
      if (is_null($tr->find('td', 1))) {
        continue;
      }
      $jam = str_replace(' ', '', $tr->find('td',1)->plaintext);
      $ruang = $tr->find('td', 2)->plaintext;
      $kode = $tr->find('td', 3)->plaintext;
      $matakuliah = $tr->find('td', 4)->plaintext;
      $matakuliahPart = explode('/', $matakuliah);
      if (isset($matakuliahPart[1])) {
        $matakuliah = $matakuliahPart[0];
      }
      $kelas = $tr->find('td', 5)->plaintext;
      $pengampu = $tr->find('td', 6)->plaintext;
      $angka = filter_var($kode, FILTER_SANITIZE_NUMBER_INT);
      $semester = $angka[0];
      $sks = substr($angka, -1);
      $krs[] = [
        'kode' => rapi($kode) . '-' . $kelas,
        'matakuliah' => rapi($matakuliah),
        'sks' => rapi($sks),
        'kelas' => rapi($kelas),
        'semester' => rapi($semester),
        'hari' => $hari,
        'jam' => rapi(artiJam($jam, ($hari == 'jumat' ? true : false))),
        'ruang' => $ruang,
        'pengampu' => (rapi($pengampu) == '' ? '-' : rapi($pengampu))
      ];
    }
  }

  return $krs;
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
    echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'jadwal_mahasiswa_l200144020.json');
  } else {
    $array = getJadwalArray($nim);
    if ($array == false) {
      http_response_code(500);
      $error = ['message' => 'empty'];
      echo json_encode($error);
    } else {
      echo json_encode($array);
    }
  }
}