# DONE-ATE: Food Donation Platform 🍲

**DONE-ATE** adalah platform donasi makanan yang dirancang untuk menghubungkan donatur dengan penerima manfaat guna mengurangi limbah makanan dan membantu mereka yang membutuhkan. Proyek ini dikembangkan sebagai bagian dari tugas besar mata kuliah **IF-2050 Dasar Rekayasa Perangkat Lunak**.

---

## 🚀 Fitur Utama

### 👤 Peran Pengguna
Platform ini mendukung dua peran utama:
- **Donatur**: Pengguna yang ingin menyumbangkan makanan berlebih.
- **Penerima (Recipient)**: Pengguna atau organisasi yang membutuhkan bantuan pangan.

### 🛠️ Fungsionalitas
- **Autentikasi Aman**: Login dan registrasi menggunakan JWT (JSON Web Token) untuk keamanan data.
- **Input Donasi & QC**: Donatur dapat mengunggah detail makanan lengkap dengan formulir *Quality Control* digital.
- **Katalog Donasi**: Menampilkan daftar makanan yang tersedia untuk diklaim secara real-time.
- **Sistem Klaim**: Penerima dapat memilih dan mengklaim makanan dari katalog.
- **Verifikasi Transaksi**: Proses verifikasi serah terima makanan untuk memastikan donasi sampai ke tangan yang tepat.
- **Histori & Laporan**: Melacak riwayat donasi/klaim dan mengunduh laporan dalam format PDF.
- **Kotak Masuk (Inbox)**: Notifikasi sistem terkait status donasi dan klaim.

---

## 🛠️ Tech Stack

- **Backend**: [Spring Boot 4.x](https://spring.io/projects/spring-boot) (Java 21)
- **Frontend**: [JavaFX 21](https://openjfx.io/)
- **Database**: [PostgreSQL](https://www.postgresql.org/) (Production) & [H2](https://www.h2database.com/) (Testing)
- **Security**: JJWT (Java JWT)
- **Reporting**: OpenPDF
- **Build Tool**: Maven
- **Containerization**: Docker (via `docker-compose.yml`)

---

## 🏁 Memulai

### Prasyarat
- Java Development Kit (JDK) 21 atau lebih tinggi.
- Maven 3.9+.
- Docker & Docker Compose (untuk menjalankan database PostgreSQL).

### Instalasi & Setup

1. **Klon Repositori**
   ```bash
   git clone https://github.com/danarrigo/IF-2050.git
   cd IF-2050
   ```

2. **Jalankan Database**
   Gunakan Docker Compose untuk menjalankan instance PostgreSQL:
   ```bash
   docker-compose up -d
   ```

3. **Konfigurasi Environment**
   Pastikan file `.env` sudah terkonfigurasi dengan benar (Database URL, username, password).

### Menjalankan Aplikasi

1. **Backend (Spring Boot)**
   Jalankan server backend:
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Frontend (JavaFX UI)**
   Jalankan antarmuka grafis:
   ```bash
   ./mvnw javafx:run
   ```

### Pengujian (Testing)
Jalankan unit test dan integration test:
```bash
./mvnw test
```

---

## 📂 Struktur Proyek

```text
src/main/java/io/github/danarrigo/if20502026k01g1doneate/
├── boundaries/     # Antarmuka JavaFX (UI)
├── controllers/    # Spring REST Controllers
├── services/       # Logika Bisnis (Business Logic)
├── entities/       # Model JPA (Database Entities)
├── repositories/   # Akses Data (Spring Data JPA)
├── security/       # Konfigurasi Keamanan & JWT
├── dtos/           # Data Transfer Objects
└── config/         # Konfigurasi Aplikasi
```

---

## 📝 Lisensi
Proyek ini dikembangkan untuk tujuan akademik dalam lingkup mata kuliah IF-2050.

---
*Dibuat dengan ❤️ oleh Kelompok G10 - DONE-ATE*