# UMSDaily

Aplikasi android sistem informasi akademik Universitas Muhammadiyah Surakarta yang dibuat sebagai skripsi mahasiswa informatika internasional 2014 dengan NIM L200144020. Aplikasi ini dibuat sebagai pelengkap sistem informasi akademik berbasis web yang sudah ada yaitu https://star.ums.ac.id/ dengan banyak fitur tambahan.

## Naskah Publikasi Skripsi

Naskah publikasi skrispi aplikasi ini dapat dilihat di: http://eprints.ums.ac.id/66283/

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

## Halaman Login
<p align="center">
<img src="https://raw.githubusercontent.com/mrizkypk/UMSDaily/master/screenshot/Login.png" width="250px" height="450px">
</p>
