<?php
require __DIR__ . DIRECTORY_SEPARATOR . 'core.php';

use \Curl\Curl;
class Notification {

    public $host;
    public $projectId;
    public $serverKey;

    function Notification() {
        $this->host = 'https://ums-daily.firebaseio.com/';
        $this->projectId = 'ums-daily';
        $this->serverKey = 'AAAAALgGjho:APA91bE1_2PILG3axvGsh1A7yEt9yo7M0iNUeOs11wEsZDE8_bd2wzkhFVfTx6FUi_QELq54H7__a1lHiOaw3tbca8BzNlaXMaIKKA_vyRdotASlNtItCLq_f8RN5OGlQdmuJ_p-abgv';
    }

    function send($ids, $id, $type, $roomId, $senderId, $senderName, $receiverId, $receiverName, $avatarUrl, $imageUrl, $title, $content, $icon = 'default') {
        $url = 'https://fcm.googleapis.com/fcm/send';

        $curl = new Curl();
        $curl->setOpt(CURLOPT_CONNECTTIMEOUT, 15);
        $curl->setOpt(CURLOPT_TIMEOUT, 15);
        $curl->setHeader('Content-Type', 'application/json');
        $curl->setHeader('Authorization', 'key=' . $this->serverKey);
        $curl->setHeader('Referer', 'https://www.umsdaily.com/');
        $curl->setHeader('Accept', '*/*');
        $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
        $curl->setHeader('Origin', 'https://www.umsdaily.com/');

        $data = [
            'registration_ids' => $ids,
            'data' => [
                'id' => $id,
                'type' => $type,
                'room_id' => $roomId,
                'sender_id' => $senderId,
                'sender_name' => $senderName,
                'receiver_id' => $receiverId,
                'receiver_name' => $receiverName,
                'avatar_url' => $avatarUrl,
                'image_url' => $imageUrl,
                'title' => $title,
                'content' => $content,
                'icon' => $icon
            ]
        ];
        $curl->post($url, $data);

        if ($curl->error) {
            return [$curl->httpStatusCode];
        } else {
            $response = json_encode($curl->response);
            return $response;
        }
    }

    function startWith($query, $string) {
        return (substr(trim(strtolower($string)), 0, strlen($query)) === trim(strtolower($query)));
    }

    function isPassFilter($string, $filters) {
        foreach ($filters as $filter) {
            if ($this->startWith($filter, $string)) {
                return true;
            }
        }

        return false;
    }

    function sendToAll($id, $type, $filters, $roomId, $senderId, $senderName, $receiverId, $receiverName, $avatarUrl, $imageUrl, $title, $content, $icon) {
        if ($filters == '') {
            $ids = [];
            foreach ($this->getAllMembers($roomId) as $user) {
                if ($user['id'] != $senderId) {
                    $ids[] = $user['device_token'];
                }
            }
        } else {
            $ids = [];
            foreach ($this->getAllMembers($roomId) as $user) {
                if ($user['id'] != $senderId &&  $this->isPassFilter($user['id'], $filters)) {
                    $ids[] = $user['device_token'];
                }
            } 
        }

        echo $this->send($ids, $id, $type, $roomId, $senderId, $senderName, $receiverId, $receiverName, $avatarUrl, $imageUrl, $title, $content, $icon);
    }

    function sendToRoom($id, $type, $roomId, $senderId, $senderName, $receiverId, $receiverName, $avatarUrl, $imageUrl, $title, $content, $icon) {
        $ids = [];
        foreach ($this->getRoomMembers($roomId) as $user) {
            if ($user['id'] != $senderId) {
                $ids[] = $user['device_token'];
            }
        }

        echo $this->send($ids, $id, $type, $roomId, $senderId, $senderName, $receiverId, $receiverName, $avatarUrl, $imageUrl, $title, $content, $icon);

    }

    function getUser($userId) {
        $url = $this->host . 'user/' . $userId . '.json';

        $curl = new Curl();
        $curl->setOpt(CURLOPT_CONNECTTIMEOUT, 15);
        $curl->setOpt(CURLOPT_TIMEOUT, 15);
        $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
        $curl->setHeader('Referer', 'https://www.umsdaily.com/');
        $curl->setHeader('Accept', '*/*');
        $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
        $curl->setHeader('Origin', 'https://www.umsdaily.com/');

        $curl->get($url);
        if ($curl->error) {
            return $curl->httpStatusCode;
        } else {
            $response = json_decode(json_encode($curl->response), true);   
            return $response;
        }
    }
    
    function getAllMembers($roomId) {
        $url = $this->host . '/user.json';

        $curl = new Curl();
        $curl->setOpt(CURLOPT_CONNECTTIMEOUT, 15);
        $curl->setOpt(CURLOPT_TIMEOUT, 15);
        $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
        $curl->setHeader('Referer', 'https://www.umsdaily.com/');
        $curl->setHeader('Accept', '*/*');
        $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
        $curl->setHeader('Origin', 'https://www.umsdaily.com/');

        $curl->get($url);
        if ($curl->error) {
            return false;
        } else {
            $response = json_decode(json_encode($curl->response), true);
            return $response;
        }
    }

    function getRoomMembers($roomId) {
        $users = [];

        $url = $this->host . 'room/' . $roomId . '/member.json';

        $curl = new Curl();
        $curl->setOpt(CURLOPT_CONNECTTIMEOUT, 15);
        $curl->setOpt(CURLOPT_TIMEOUT, 15);
        $curl->setHeader('Content-Type', 'application/x-www-form-urlencoded');
        $curl->setHeader('Referer', 'https://www.umsdaily.com/');
        $curl->setHeader('Accept', '*/*');
        $curl->setHeader('User-Agent', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4');
        $curl->setHeader('Origin', 'https://www.umsdaily.com/');

        $curl->get($url);
        if ($curl->error) {
            return false;
        } else {
            $response = json_decode(json_encode($curl->response), true);

            //get user ids and user detail
            foreach ($response as $userId => $userStatus) {
                if ($userStatus == 1) {
                    $users[$userId] = $this->getUser($userId);
                }
            }

            return $users;
        }
    }
}

$notif = new Notification();
$id = $_GET['id'];
$type = $_GET['type'];
$filter = isset($_GET['filter']) ? $_GET['filter'] : '';
$roomId = isset($_GET['room_id']) ? $_GET['room_id'] : '';
$senderId = $_GET['sender_id']; 
$senderName = $_GET['sender_name'];
$receiverId = isset($_GET['receiver_id']) ? $_GET['receiver_id'] : '';
$receiverName = isset($_GET['receiver_name']) ? $_GET['receiver_name'] : '';
$avatarUrl = isset($_GET['avatar_url']) ? $_GET['avatar_url'] : '';
$imageUrl = isset($_GET['image_url']) ? $_GET['image_url'] : '';
$title = $_GET['title'];
$content = $_GET['content'];
$icon = isset($_GET['icon']) ? $_GET['icon'] : 'default';

switch ($type) {
    case 'CHAT_MESSAGE_PRIVATE':
    case 'CHAT_IMAGE_PRIVATE':
    case 'CHAT_MESSAGE_PUBLIC';
    case 'CHAT_IMAGE_PUBLIC';
    case 'ASSIGNMENT';
        $notif->sendToRoom($id, $type, $roomId, $senderId, $senderName, $receiverId, $receiverName, $avatarUrl, $imageUrl, $title, $content, $icon);
        break;
    case 'ANNOUNCEMENT';
        if (strpos($filter, ',')) {
            $filters = explode(',', $filters);
        } else {
            $filters = [$filter];
        }
        $notif->sendToAll($id, $type, $filters, $roomId, $senderId, $senderName, $receiverId, $receiverName, $avatarUrl, $imageUrl, $title, $content, $icon);
        break;                    
}