# Draf Kelas Tambahan untuk PL (Bagian 3.3)

Berikut adalah daftar kelas yang ada di repository namun **belum terdaftar** dalam list CD-01 hingga CD-40 Anda. Gunakan draf ini untuk melengkapi Bab 3.3.

---

## 1. Perancangan Kelas Terkait Entitas (Enums)

### CD-41 DonatorType (Enum)
Nama Kelas: DonatorType

| Nama Atribut | Visibility | Tipe | Keterangan |
| :--- | :--- | :--- | :--- |
| INDIVIDUAL | public static | Enum | Donatur perorangan. |
| RESTAURANT | public static | Enum | Donatur dari bisnis restoran. |
| CATERING | public static | Enum | Donatur dari bisnis katering. |

### CD-42 RecipientType (Enum)
Nama Kelas: RecipientType

| Nama Atribut | Visibility | Tipe | Keterangan |
| :--- | :--- | :--- | :--- |
| NGO | public static | Enum | Penerima lembaga non-pemerintah. |
| ORPHANAGE | public static | Enum | Penerima panti asuhan. |
| INDIVIDUAL | public static | Enum | Penerima perorangan. |

### CD-43 NotificationType (Enum)
Nama Kelas: NotificationType

| Nama Atribut | Visibility | Tipe | Keterangan |
| :--- | :--- | :--- | :--- |
| CLAIM | public static | Enum | Notifikasi terkait klaim donasi. |
| SYSTEM | public static | Enum | Notifikasi sistem umum. |
| TRANSACTION | public static | Enum | Notifikasi terkait status transaksi. |

---

## 2. Perancangan Kelas Persistence (Repositories)

### CD-44 UserRepository (Interface)
Nama Kelas: UserRepository

| Nama Operasi | Visibility | Keterangan |
| :--- | :--- | :--- |
| findByUsername(username) | public | Mencari user berdasarkan username unik. |
| findByEmail(email) | public | Mencari user berdasarkan alamat email. |

### CD-45 DonationRepository (Interface)
Nama Kelas: DonationRepository

| Nama Operasi | Visibility | Keterangan |
| :--- | :--- | :--- |
| findByOngoing(status) | public | Mengambil daftar donasi yang sedang aktif/tayang. |
| findByDonator_Username(un) | public | Mengambil histori donasi milik donatur tertentu. |
| findByDonator_UsernameAndOngoing(un, stat) | public | Mengambil donasi aktif milik donatur tertentu. |

### CD-46 TransactionRepository (Interface)
Nama Kelas: TransactionRepository

| Nama Operasi | Visibility | Keterangan |
| :--- | :--- | :--- |
| findByTransactionCode(code) | public | Mencari transaksi berdasarkan kode 6-digit. |
| findByRecipient_Username(un) | public | Mengambil histori transaksi milik penerima tertentu. |

---

## 3. Perancangan Kelas Boundary (UI)

### CD-47 CancelDonationUI (UC4)
Nama Kelas: CancelDonationUI

| Nama Operasi | Visibility | Keterangan |
| :--- | :--- | :--- |
| showUI() | public | Menampilkan halaman pembatalan klaim dalam mode standalone. |
| getSceneContent(stage) | public | Membangun layout UI pembatalan (Info Donasi & Form Kode). |
| handleCancel(stage, codeField, btn) | private | Memvalidasi input dan mengirim request DELETE ke /api/claims/cancel. |
| buildCancelCard(stage) | private | Membuat komponen visual kartu input kode transaksi. |
| **Nama Atribut** | **Visibility** | **Tipe** |
| donation | private | Donation |
| codeField | private | TextField |


### CD-48 Navigator (Utility)
Nama Kelas: Navigator

| Nama Operasi | Visibility | Keterangan |
| :--- | :--- | :--- |
| navigate(stage, targetUI) | public static | Berpindah halaman UI dengan animasi fade. |
| createBottomNav(stage, user, active) | public static | Membuat komponen navigasi bawah (Home, Catalog, dll). |
| **Nama Atribut** | **Visibility** | **Tipe** |
| DARK_GREEN | private static | String |
| LIGHT_GREEN | private static | String |

---

## 4. Catatan Penting untuk Sinkronisasi
1.  **CD-22 (HomeUI)**: Di dalam kode aktual repository, kelas ini diimplementasikan sebagai `DonatorHomeUI`. Anda bisa mengganti namanya di PL agar lebih presisi.
2.  **CD-09 (TransactionVerificationUI)**: Di repository, kelas ini bernama `VerificationUI`. Sebaiknya gunakan nama `VerificationUI` agar sesuai dengan file `.java`.
3.  **CD-29 (RegisterUI)**: Di list Anda tertulis nomor CD-29 (duplikat dengan ClaimDonationUI). Seharusnya ini menjadi **CD-36**.
