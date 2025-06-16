-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 25 Mei 2025 pada 20.02
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_pbo_abji`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `absen`
--

CREATE TABLE `absen` (
  `id` int(11) NOT NULL,
  `id_pegawai` int(11) NOT NULL,
  `id_shift` int(11) NOT NULL,
  `tanggal` date NOT NULL,
  `jam_masuk` time DEFAULT NULL,
  `jam_keluar` time DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `absen`
--

INSERT INTO `absen` (`id`, `id_pegawai`, `id_shift`, `tanggal`, `jam_masuk`, `jam_keluar`, `created_at`, `updated_at`) VALUES
(5, 44, 18, '2025-06-05', '11:00:00', '15:00:00', '2025-06-06 15:58:01', '2025-06-06 15:58:01'),
(6, 45, 19, '2025-06-05', '15:00:00', '19:00:00', '2025-06-06 15:58:01', '2025-06-06 15:58:01'),
(7, 46, 20, '2025-06-05', '19:00:00', '23:00:00', '2025-06-06 15:58:01', '2025-06-06 15:58:01'),
(8, 47, 21, '2025-06-05', '23:00:00', '03:00:00', '2025-06-06 15:58:01', '2025-06-06 15:58:01'),
(27, 43, 17, '2025-06-03', '07:00:00', '11:00:00', '2025-06-06 16:28:26', '2025-06-06 16:28:26'),
(28, 43, 17, '2025-06-04', '07:00:00', '11:00:00', '2025-06-06 16:28:26', '2025-06-06 16:28:26');

-- --------------------------------------------------------

--
-- Struktur dari tabel `jabatan`
--

CREATE TABLE `jabatan` (
  `id` int(11) NOT NULL,
  `nama_jabatan` varchar(100) NOT NULL,
  `gaji_pokok` decimal(12,2) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `jabatan`
--

INSERT INTO `jabatan` (`id`, `nama_jabatan`, `gaji_pokok`, `created_at`, `updated_at`) VALUES
(12, 'Pengurus', 100000.00, '2025-06-06 15:46:56', '2025-06-06 15:46:56'),
(13, 'Pengajar', 70000.00, '2025-06-06 15:46:56', '2025-06-06 15:46:56'),
(14, 'Penjaga', 70000.00, '2025-06-06 15:46:56', '2025-06-06 15:46:56');

-- --------------------------------------------------------

--
-- Struktur dari tabel `jabatan_pegawai`
--

CREATE TABLE `jabatan_pegawai` (
  `id` int(11) NOT NULL,
  `id_pegawai` int(11) NOT NULL,
  `id_jabatan` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `jabatan_pegawai`
--

INSERT INTO `jabatan_pegawai` (`id`, `id_pegawai`, `id_jabatan`, `created_at`, `updated_at`) VALUES
(7, 44, 13, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(8, 45, 13, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(9, 46, 13, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(10, 48, 13, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(11, 49, 13, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(12, 50, 13, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(13, 47, 12, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(14, 51, 14, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(15, 52, 14, '2025-06-06 15:54:28', '2025-06-06 15:54:28'),
(22, 43, 12, '2025-06-06 16:38:25', '2025-06-06 16:38:25');

-- --------------------------------------------------------

--
-- Struktur dari tabel `pegawai`
--

CREATE TABLE `pegawai` (
  `id` int(11) NOT NULL,
  `nip` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `password` text NOT NULL,
  `is_admin` tinyint(1) DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pegawai`
--

INSERT INTO `pegawai` (`id`, `nip`, `nama`, `tanggal_lahir`, `alamat`, `password`, `is_admin`, `created_at`, `updated_at`) VALUES
(19, 'admin', 'Owner Pesantren', '1975-06-25', 'Surabaya', 'admin123', 1, '2025-06-04 06:41:10', '2025-06-06 15:33:43'),
(43, '123456789', 'Abdul Kholiq', '1975-06-25', 'Malang', 'password', 0, '2025-06-06 15:32:04', '2025-06-09 07:36:12'),
(44, '196509151995031001', 'M. Kardi', '1980-05-19', 'Langitan Tuban', 'password1', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(45, '196807031996032001', 'Dewi', '1968-07-03', 'Jombang', 'password2', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(46, '196901011996032001', 'Lilik', '1969-01-01', 'Surabaya', 'password3', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(47, '197304102005011004', 'Abdullah Syarqa', '1985-07-06', 'Banyuanyar', 'password4', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(48, '197007061997031002', 'Nur Salam', '1970-07-06', 'Sumenep', 'password5', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(49, '197010012000031002', 'Rofiul Ilmi', '1970-10-01', 'Gontor', 'password6', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(50, '197204102005011004', 'Mubarok Zaidan', '1973-04-10', 'Pasuruan', 'password7', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(51, '197412312005012004', 'Muhammad Nadhif', '1989-12-31', 'Jombang', 'password8', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04'),
(52, '198203022005011002', 'Miftahul Jannah', '1975-06-25', 'Blitar', 'password9', 0, '2025-06-06 15:32:04', '2025-06-06 15:32:04');

-- --------------------------------------------------------

--
-- Struktur dari tabel `shift`
--

CREATE TABLE `shift` (
  `id` int(11) NOT NULL,
  `nama_shift` varchar(50) NOT NULL,
  `jam_masuk` time NOT NULL,
  `jam_keluar` time NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `shift`
--

INSERT INTO `shift` (`id`, `nama_shift`, `jam_masuk`, `jam_keluar`, `created_at`, `updated_at`) VALUES
(17, 'Shift Pagi A (07:00 - 11:00)', '07:00:00', '11:00:00', '2025-06-06 15:48:22', '2025-06-10 17:53:23'),
(18, 'Shift Pagi B (11:00 - 15:00)', '11:00:00', '15:00:00', '2025-06-06 15:48:22', '2025-06-10 17:53:44'),
(19, 'Shift Sore A (15:00 - 19:00)', '15:00:00', '19:00:00', '2025-06-06 15:48:22', '2025-06-10 17:54:09'),
(20, 'Shift Sore B (19:00 - 23:00)', '19:00:00', '23:00:00', '2025-06-06 15:48:22', '2025-06-10 17:54:32'),
(21, 'Shift Malam A (23:00 - 03:00)', '23:00:00', '03:00:00', '2025-06-06 15:48:22', '2025-06-10 17:54:54'),
(22, 'Shift Malam B (03:00 - 07:00)', '03:00:00', '07:00:00', '2025-06-06 15:48:22', '2025-06-10 17:55:30');

-- --------------------------------------------------------

--
-- Struktur dari tabel `shift_pegawai`
--

CREATE TABLE `shift_pegawai` (
  `id` int(11) NOT NULL,
  `id_pegawai` int(11) NOT NULL,
  `id_shift` int(11) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `shift_pegawai`
--

INSERT INTO `shift_pegawai` (`id`, `id_pegawai`, `id_shift`, `created_at`, `updated_at`) VALUES
(7, 44, 17, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(8, 45, 18, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(9, 46, 18, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(10, 47, 19, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(11, 48, 19, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(12, 49, 20, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(13, 50, 20, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(14, 51, 21, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(15, 52, 22, '2025-06-06 16:05:11', '2025-06-06 16:05:11'),
(16, 43, 17, '2025-06-09 09:46:36', '2025-06-09 09:46:36');

-- --------------------------------------------------------

--
-- Struktur dari tabel `slip_gaji`
--

CREATE TABLE `slip_gaji` (
  `id` int(11) NOT NULL,
  `id_pegawai` int(11) NOT NULL,
  `jumlah_masuk` int(11) DEFAULT 0,
  `id_jabatan` int(11) DEFAULT NULL,
  `gaji_pokok` decimal(12,2) NOT NULL,
  `total_gaji` decimal(12,2) NOT NULL,
  `tanggal_cetak` timestamp NULL DEFAULT current_timestamp(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `absen`
--
ALTER TABLE `absen`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_pegawai` (`id_pegawai`),
  ADD KEY `id_shift` (`id_shift`);

--
-- Indeks untuk tabel `jabatan`
--
ALTER TABLE `jabatan`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `jabatan_pegawai`
--
ALTER TABLE `jabatan_pegawai`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_pegawai` (`id_pegawai`),
  ADD KEY `id_jabatan` (`id_jabatan`);

--
-- Indeks untuk tabel `pegawai`
--
ALTER TABLE `pegawai`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nip` (`nip`);

--
-- Indeks untuk tabel `shift`
--
ALTER TABLE `shift`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `shift_pegawai`
--
ALTER TABLE `shift_pegawai`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_pegawai` (`id_pegawai`),
  ADD KEY `id_shift` (`id_shift`);

--
-- Indeks untuk tabel `slip_gaji`
--
ALTER TABLE `slip_gaji`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_pegawai` (`id_pegawai`),
  ADD KEY `id_jabatan` (`id_jabatan`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `absen`
--
ALTER TABLE `absen`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT untuk tabel `jabatan`
--
ALTER TABLE `jabatan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT untuk tabel `jabatan_pegawai`
--
ALTER TABLE `jabatan_pegawai`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT untuk tabel `pegawai`
--
ALTER TABLE `pegawai`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT untuk tabel `shift`
--
ALTER TABLE `shift`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT untuk tabel `shift_pegawai`
--
ALTER TABLE `shift_pegawai`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT untuk tabel `slip_gaji`
--
ALTER TABLE `slip_gaji`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `absen`
--
ALTER TABLE `absen`
  ADD CONSTRAINT `absen_ibfk_1` FOREIGN KEY (`id_pegawai`) REFERENCES `pegawai` (`id`),
  ADD CONSTRAINT `absen_ibfk_2` FOREIGN KEY (`id_shift`) REFERENCES `shift` (`id`);

--
-- Ketidakleluasaan untuk tabel `jabatan_pegawai`
--
ALTER TABLE `jabatan_pegawai`
  ADD CONSTRAINT `jabatan_pegawai_ibfk_1` FOREIGN KEY (`id_pegawai`) REFERENCES `pegawai` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `jabatan_pegawai_ibfk_2` FOREIGN KEY (`id_jabatan`) REFERENCES `jabatan` (`id`) ON DELETE SET NULL;

--
-- Ketidakleluasaan untuk tabel `shift_pegawai`
--
ALTER TABLE `shift_pegawai`
  ADD CONSTRAINT `shift_pegawai_ibfk_1` FOREIGN KEY (`id_pegawai`) REFERENCES `pegawai` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `shift_pegawai_ibfk_2` FOREIGN KEY (`id_shift`) REFERENCES `shift` (`id`);

--
-- Ketidakleluasaan untuk tabel `slip_gaji`
--
ALTER TABLE `slip_gaji`
  ADD CONSTRAINT `slip_gaji_ibfk_1` FOREIGN KEY (`id_pegawai`) REFERENCES `pegawai` (`id`),
  ADD CONSTRAINT `slip_gaji_ibfk_2` FOREIGN KEY (`id_jabatan`) REFERENCES `jabatan` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
