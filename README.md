# UMSDaily

Aplikasi android sistem informasi akademik Universitas Muhammadiyah Surakarta yang dibuat sebagai skripsi mahasiswa informatika internasional 2014 dengan NIM L200144020. Aplikasi ini dibuat sebagai pelengkap sistem informasi akademik berbasis web yang sudah ada yaitu https://star.ums.ac.id/ dengan banyak fitur tambahan.

## Naskah Publikasi Skripsi

Naskah publikasi skripsi aplikasi ini dapat dilihat di: http://eprints.ums.ac.id/66283/

## Fitur

* Akses informasi secara offline. Informasi yang dimaksud adalah sama seperti di web sistem informasi yaitu informasi jadwal, nilai dan tagihan.
* Real-time chat. Bisa kirim berkas ataupun foto dari galeri dan kamera. Bisa melihat status pesan sudah terkirim dan terbaca oleh siapa dan kapan. Fitur ini dibuat semirip mungkin dengan Telegram dan Whatsapp.
* Real-time group chat yang grupnya dibuat secara otomatis setelah user login. Grup akan dibuat berdasarkan jadwal kuliah mahasiswa.
* Push Notifikasi
* Pencarian jadwal
* Pencarian untuk chat dosen, staf ataupun mahasiswa lain. Pencarian bisa berdasarkan nama dan ID universitas.
* Manajemen berkas yang sudah di upload. Fitur ini diakses dengan membuka halaman chat privat ataupun group.
* Manajemen tugas untuk dosen. Di dalam fitur ini dosen dapat mencatat tugas seperti keterangan dan kapan terakhir tugas dapat dikumpulkan. Dan seluruh mahasiswa yang mengambil matakuliah dan ada tugas dimasukkan akan mendapat notifikasi.
* Manajemen pengumuman untuk staf universitas. Di dalam fitur ini staf universitas dapat membuat pengumuman yang ditujukan kepada semua anggota universitas ataupun dengan menggunakan filter ID universitas yang memungkinkan untuk memberikan pengumuman berdasarkan jurusan dan tahun jurusan.

## Pengembangan

* Aplikasi ini dibuat dengan menggunakan android studio dan bahasa pemrograman Java. 
* Aplikasi ini menggunakan web service untuk mengolah data dari web universitas agar bisa diubah menjadi bentuk JSON sehingga dapat digunakan oleh aplikasi. 
* Database yang digunakan adalah Firebase Real-time Database yang digunakan untuk fitur chat.
* Sistem push notifikasi aplikasi ini menggunakan Firebase Cloud Messaging

## Arsitektur Sistem
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/System-Design-1.PNG" width="450px" height="250px">
</p>

## UML
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/TA-UML.png" width="250px" height="450px">
</p>

## Diagram Aktivitas
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Activity%20Diagram%20Fix.png" width="250px" height="250px">
</p>

## Web Service User JSON
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Webservice-User.png" width="450px" height="250px">
</p>

## Web Service Jadwal JSON
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Webservice-Schedule.png" width="450px" height="250px">
</p>

## Halaman Login
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Login.png" width="250px" height="450px">
</p>

## Halaman Beranda Dosen
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Lecturer-Home.png" width="250px" height="450px">
</p>

## Halaman Beranda Staf
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Staff-Home.png" width="250px" height="450px">
</p>

## Halaman Beranda Mahasiswa
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student-Home.png" width="250px" height="450px">
</p>

## Halaman Informasi Mahasiswa
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BInformation.png" width="250px" height="450px">
</p>

## Halaman Jadwal Mahasiswa
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BSchedule.png" width="250px" height="450px">
</p>

## Halaman Pencarian Chat
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BCompose.png" width="250px" height="450px">
</p>

## Halaman Hasil Pencarian Chat
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BCompose%2BSearch.png" width="250px" height="450px">
</p>

## Halaman Pencarian Jadwal
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Screenshot_1532915857.png" width="250px" height="450px">
</p>

## Halaman Chat Room
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BChat%2BRoom.png" width="250px" height="450px">
</p>

## Halaman Upload Chat Room
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Screenshot_1532923775.png" width="250px" height="450px">
</p>

## Halaman Manajemen Chat Room
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BChat%2BRoom%2BDetail.png" width="250px" height="450px">
</p>

## Halaman Manajemen Gambar Chat Room
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BChat%2BRoom%2BDetail%2BImage%2BList.png" width="250px" height="450px">
</p>

## Halaman Manajemen Berkas Chat Room
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Student%2BChat%2BRoom%2BDetail%2BFile%2BList.png" width="250px" height="450px">
</p>

## Halaman Tugas
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Screenshot_1532915834.png" width="250px" height="450px">
</p>


## Halaman Pengumuman
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Screenshot_1532915843.png" width="250px" height="450px">
</p>
