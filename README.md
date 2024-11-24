
# Aplikasi Keuangan Pribadi

Aplikasi Keuangan Pribadi adalah aplikasi berbasis Java Swing yang dirancang untuk membantu pengguna dalam mengelola keuangan pribadi dengan mencatat pemasukan, pengeluaran, serta menghitung saldo secara otomatis.

## Fitur Utama
1. **Tambah Transaksi**: Menambahkan data pemasukan atau pengeluaran.
2. **Edit Transaksi**: Mengubah data transaksi yang sudah ada.
3. **Hapus Transaksi**: Menghapus transaksi tertentu.
4. **Impor CSV**: Memuat data transaksi dari file CSV.
5. **Ekspor CSV**: Menyimpan data transaksi ke file CSV.
6. **Filter Berdasarkan Tanggal**: Menampilkan transaksi berdasarkan rentang tanggal yang dipilih.
7. **Saldo Otomatis**: Menghitung total pemasukan, pengeluaran, dan saldo terkini.

## Cara Menggunakan
1. **Menambah Transaksi**:
   - Isi deskripsi, jumlah, pilih tipe (Pemasukan/Pengeluaran), dan tanggal transaksi.
   - Klik tombol **Tambah** untuk menyimpan transaksi.

2. **Mengedit Transaksi**:
   - Pilih baris transaksi di tabel, lalu masukkan perubahan pada kolom input.
   - Klik tombol **Edit** untuk menyimpan perubahan.

3. **Menghapus Transaksi**:
   - Pilih transaksi di tabel dan klik **Hapus**. Anda dapat memilih untuk menghapus data dari tabel atau text field.

4. **Filter Data**:
   - Pilih rentang tanggal dengan komponen kalender, lalu klik **Filter**.

5. **Impor Data**:
   - Klik tombol **Impor**, pilih file CSV yang sesuai, dan data akan dimuat ke tabel.

6. **Ekspor Data**:
   - Klik tombol **Ekspor**, pilih lokasi penyimpanan, dan data akan disimpan dalam format CSV.

7. **Menghitung Saldo**:
   - Data saldo diperbarui secara otomatis setiap kali ada perubahan pada tabel.

## Prasyarat
- **Java 8 atau lebih baru**
- **JDK dengan dukungan Swing**
- Library pihak ketiga:
  - `toedter.calendar` untuk pemilihan tanggal.

## Struktur File CSV
Format file CSV untuk impor/ekspor data:
```csv
Tanggal,Deskripsi,Jumlah,Tipe
01/01/2024,Gaji,5000000,Pemasukan
02/01/2024,Belanja,2000000,Pengeluaran
```

## Cara Menjalankan
1. Pastikan Anda memiliki JDK yang kompatibel.
2. Kompilasi program menggunakan perintah:
   ```bash
   javac AplikasiKeuanganPribadi.java
   ```
3. Jalankan aplikasi:
   ```bash
   java AplikasiKeuanganPribadi
   ```

## Lisensi
Aplikasi ini berada di bawah lisensi yang sesuai dengan kebutuhan proyek Anda.

## Penulis
Dibuat oleh: **ASUS**
