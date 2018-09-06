<?php
require __DIR__ . DIRECTORY_SEPARATOR . 'core.php';

use \Curl\Curl;

function getHasilPencarianHTML($kategori, $query) {
	$query = urlencode(trim($query));
	$daftarUrl = [
		'jurusan' => 'https://akademik.ums.ac.id/kuliah.php?category=FProgdi&keyword=' . $query . '&cetak=layar&submitted=yes&mod=Jadwal_Kuliah',
		'dosen' => 'https://akademik.ums.ac.id/kuliah.php?category=FNama&keyword=' . $query . '&cetak=layar&submitted=yes&mod=Jadwal_Kuliah',
		'ruang' => 'https://akademik.ums.ac.id/kuliah.php?category=Ruang&keyword=' . $query . '&cetak=layar&submitted=yes&mod=Jadwal_Kuliah',
		'matakuliah' => 'https://akademik.ums.ac.id/kuliah.php?category=FMK&keyword=' . $query . '&cetak=layar&submitted=yes&mod=Jadwal_Kuliah'
	];
	$url = $daftarUrl[$kategori];

	$curl = new Curl();
	$curl->setOpt(CURLOPT_COOKIESESSION, true);
	$curl->setOpt(CURLOPT_SSL_VERIFYPEER, false);
	$curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
	$curl->setHeader('Referer', 'https://akademik.ums.ac.id/kuliah.php?mod=Jadwal_Kuliah');
	$curl->setHeader('Accept', '*/*');
	$curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
	$curl->setHeader('Origin', 'https://akademik.ums.ac.id');
	$curl->get($url);
	if ($curl->error) {
      http_response_code($curl->httpStatusCode);
      echo json_encode([
        'message' => 'server',
        'code' => $curl->httpStatusCode
      ]);
      die();
  	} else {
		$html = $curl->response;
		if (strpos($html, '</table>')) {
			return $html;
		} else {
			return false;
		}
	}
}

function getHasilPencarianArray($kategori, $query) {
	$jadwal = array();
	$html = getHasilPencarianHTML($kategori, $query);
	if ($html == false) {
		return false;
	}
	$html = str_get_html($html);
	foreach($html->find('table[id=table-1]') as $index => $row) {
		$hari = rapi(str_replace("'", '', strtolower($row->parent()->parent()->find('span[class=label-danger]', $index)->plaintext)));
		foreach ($row->find('tbody tr') as $tr) {
			if (is_null($tr->find('td',0))) {
				continue;
			}
			$ruang = $tr->find('td',0)->plaintext;
			$jam = str_replace(' ', '', $tr->find('td',1)->plaintext);
			$kode = $tr->find('td',2)->plaintext;
			$matakuliah = $tr->find('td',3)->plaintext;
			$matakuliahPart = explode('/', $matakuliah);
			if (isset($matakuliahPart[1])) {
				$matakuliah = $matakuliahPart[0];
			}
			$pengampu = $tr->find('td',4)->plaintext;
			$kelasPart = explode(' ', $tr->find('td',5)->plaintext);
			$kelas = $kelasPart[0];
			$pradi = $tr->find('td',6)->plaintext;
			$angka = filter_var($kode, FILTER_SANITIZE_NUMBER_INT);
			$semester = $angka[0];
			$sks = substr($angka, -1);
			$jadwal[] = [
				'sks' => rapi($sks),
				'semester' => rapi($semester),
				'hari' => ucwords($hari),
				'ruang' => rapi($ruang),
				'jam' => rapi(artiJam($jam, ($hari == 'jumat' ? true : false))),
				'kode' => rapi($kode),
				'matakuliah' => rapi($matakuliah),
				'pengampu' => rapi($pengampu),
				'kelas' => rapi($kelas),
				'pradi' => rapi($pradi),
			];
		}
	}

	return $jadwal;
}

if (isset($_GET['kategori']) && isset($_GET['query'])) {
	if ($_GET['kategori'] == 'dosen' && strtolower(urldecode($_GET['query'])) == 'bana handaga') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_dosen_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'dosen' && strtolower(urldecode($_GET['query'])) == 'heru supriyono') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_dosen_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'dosen' && strtolower(urldecode($_GET['query'])) == 'nurgiyatna') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_dosen_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'dosen' && strtolower(urldecode($_GET['query'])) == 'yogiek indra kurniawan') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_dosen_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'jurusan' && strtolower(urldecode($_GET['query'])) == 'informatika') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_jurusan_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'jurusan' && strtolower(urldecode($_GET['query'])) == 'akuntansi') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_jurusan_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'matakuliah' && strtolower(urldecode($_GET['query'])) == 'bahasa indonesia') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_matakuliah_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'matakuliah' && strtolower(urldecode($_GET['query'])) == 'jaringan komputer') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_matakuliah_' . strtolower(str_replace(' ', '_', urldecode($_GET['query']))) . '.json');
	} else if ($_GET['kategori'] == 'ruang' && strtolower(urldecode($_GET['query'])) == 'j.int.1') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_ruang_j.int.1.json');
	} else if ($_GET['kategori'] == 'ruang' && strtolower(urldecode($_GET['query'])) == 'labrpl') {
		echo file_get_contents('offline' . DIRECTORY_SEPARATOR . 'pencarian_jadwal_ruang_labrpl.json');
	} else {
		$array = getHasilPencarianArray($_GET['kategori'], $_GET['query']);
		if ($array == false) {
			http_response_code(500);
			echo json_encode([
				'message' => 'empty'
			]);
		} else {
			echo json_encode($array);
		}
	}
} else {
	http_response_code(500);
	echo json_encode([
		'message' => 'empty'
	]);
}
