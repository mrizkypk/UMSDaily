<?php
require __DIR__ . '/vendor/autoload.php';

use \Curl\Curl;

function replaceSpacesWithOne($string) {
    return preg_replace('!\s+!', ' ', $string);
}

function rapi($string) {
	return trim(preg_replace('!\s+!', ' ', $string));
}

function artiJam($jam, $mode) {
    $biasa = [
        '1' => '07.00',
        '2' => '07.50',
        '3' => '08.40',
        '4' => '09.30',
        '5' => '10.20',
        '6' => '11.10',
        '7' => '12.30',
        '8' => '13.20',
        '9' => '14.10',
        '10' => '15.20',
        '11' => '16.10',
        '12' => '17.00',
        '13' => '17.50',
        '14' => '18.40',
        '15' => '19.30',
        '16' => '20.20',
    ];

    $jumat = [
        '1' => '07.00',
        '2' => '07.50',
        '3' => '08.40',
        '4' => '09.30',
        '5' => '10.20',
        '6' => '11.10',
        '7' => '13.00',
        '8' => '13.50',
        '9' => '14.40',
        '10' => '16.00',
        '11' => '16.50',
        '12' => '17.40',
        '13' => '18.30',
        '14' => '19.20',
        '15' => '20.10',
        '16' => '21.00',
    ];

    $part = explode('-', $jam);

    if ($mode == true) {
        $mulai = $jumat[$part[0]];
    } else {
        $mulai = $biasa[$part[0]];        
    }

    $mulaiPart = explode('.', $mulai);
    $jam = $mulaiPart[0];
    $menit = $mulaiPart[1];
    $sks = $part[1] - $part[0] + 1;
    $totalMenit = ($jam * 60) + $menit + ($sks * 50);

    $hours  = round(intval($totalMenit/60) , 2);
    $minutes = $totalMenit % 60;

    $selesai = sprintf("%02d", $hours).'.'.sprintf("%02d", $minutes);
    return $mulai . ' - ' . $selesai;

}

function loginSia($id, $password) {
  if (strtolower($id) == "l200144020") {
    return "Moch Rizky Prasetya Kurniadi";
  } else {
    $cookiePath = __DIR__ . DIRECTORY_SEPARATOR . 'cookie' . DIRECTORY_SEPARATOR . $id . '.txt';

    $curl = new Curl();
    $curl->setOpt(CURLOPT_HEADER, true);
    $curl->setOpt(CURLOPT_COOKIESESSION, true);
    $curl->setOpt(CURLOPT_COOKIEJAR, $cookiePath);
    $curl->setOpt(CURLOPT_FOLLOWLOCATION, false);
    $curl->setOpt(CURLOPT_SSL_VERIFYPEER, false);
    $curl->setOpt(CURLOPT_CONNECTTIMEOUT, 15);
    $curl->setOpt(CURLOPT_TIMEOUT, 15);
    $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
    $curl->setHeader('Referer', 'https://sia.ums.ac.id/?logout=true' . strtolower($id));
    $curl->setHeader('Accept', '*/*');
    $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
    $curl->setHeader('Origin', 'https://sia.ums.ac.id');
    $curl->post('https://sia.ums.ac.id/login/submit', [
        'login_email' => $id,
        'login_sandi' => $password
    ]);
    if ($curl->error) {
        http_response_code(500);
        echo json_encode([
            'message' => 'server',
            'code' => $curl->httpStatusCode
        ]);
        die();
    } else {
        $response = urldecode($curl->response);
        preg_match('/\"logged_fullname\"\;s\:([\d]+)\:\"([\w\s.]+)/', $response, $matches);
        if (isset($matches[2])) {
        return ucwords(strtolower($matches[2]));
        } else {
            return false;
        }
    }
    }
}