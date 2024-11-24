
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ASUS
 */
public class AplikasiKeuanganPribadi extends javax.swing.JFrame {
    private DefaultTableModel tableModel;
    private ArrayList<String[]> transaksi;
    private int rowToEdit = -1;

    /**
     * Creates new form AplikasiKeuanganPribadi
     */
    public AplikasiKeuanganPribadi() {
        initComponents();
        tableModel = (DefaultTableModel) jTable1.getModel();
        transaksi = new ArrayList<>();
    }
    
    private void initTable() {
    DefaultTableModel model = new DefaultTableModel(new Object[]{"Tanggal", "Deskripsi", "Jumlah", "Tipe"}, 0);
    jTable1.setModel(model);
    }
    
   private void updateSaldo() {
     double pemasukan = 0, pengeluaran = 0;

    // Iterasi untuk setiap transaksi yang ada
    for (String[] row : transaksi) {
        // Parsing dan validasi jumlah transaksi
        double jumlah = parseValidDouble(row[1]);

        // Cek tipe transaksi dan tambahkan ke saldo yang sesuai
        if (row[2].equalsIgnoreCase("Pemasukan")) {
            pemasukan += jumlah;
        } else if (row[2].equalsIgnoreCase("Pengeluaran")) {
            pengeluaran += jumlah;
        }
    }

    // Format hasil untuk ditampilkan
    DecimalFormat formatter = new DecimalFormat("#,###");
    lblPemasukan.setText("Total Pemasukan: Rp " + formatter.format(pemasukan));
    lblPengeluaran.setText("Total Pengeluaran: Rp " + formatter.format(pengeluaran));
    lblSaldo.setText("Saldo: Rp " + formatter.format(pemasukan - pengeluaran));
}
    private static double parseValidDouble(String input) {
      try {
        input = input.replace(",", "").trim(); // Pastikan format bersih dari koma atau spasi
        return Double.parseDouble(input); // Coba parsing input menjadi angka
    } catch (NumberFormatException e) {
        return 0; // Fallback jika parsing gagal
    }
    }
    
     private void updateSaldoFromTable() {
     double totalPemasukan = 0;
    double totalPengeluaran = 0;   

    // Iterasi untuk setiap baris di tabel
    for (int i = 0; i < jTable1.getRowCount(); i++) {
        String tipe = (String) jTable1.getValueAt(i, 3); // Kolom tipe (Pemasukan/Pengeluaran)
        String jumlahString = jTable1.getValueAt(i, 2).toString().replace(",", "").trim(); // Kolom jumlah
        double jumlah = 0;

        try {
            jumlah = Double.parseDouble(jumlahString); // Konversi jumlah ke angka
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Data jumlah pada baris " + (i+1) + " tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Hentikan proses jika terjadi error pada jumlah
        }

        // Menambahkan jumlah ke pemasukan atau pengeluaran
        if (tipe.equalsIgnoreCase("Pemasukan")) {
            totalPemasukan += jumlah;
        } else if (tipe.equalsIgnoreCase("Pengeluaran")) {
            totalPengeluaran += jumlah;
        }
    }

    // Hitung saldo
    double saldo = totalPemasukan - totalPengeluaran;

    // Tampilkan saldo dan total lainnya
    lblSaldo.setText("Saldo: Rp " + String.format("%,.0f", saldo));
    lblPemasukan.setText("Total Pemasukan: Rp " + String.format("%,.0f", totalPemasukan));
    lblPengeluaran.setText("Total Pengeluaran: Rp " + String.format("%,.0f", totalPengeluaran));
}
    
    
private void updateSaldoFiltered() {
    double pemasukan = 0, pengeluaran = 0;

    // Iterasi setiap baris di tabel untuk menghitung pemasukan dan pengeluaran
    for (int i = 0; i < jTable1.getRowCount(); i++) {
        try {
            // Parsing jumlah dari tabel dan konversi ke desimal
            String jumlahString = jTable1.getValueAt(i, 2).toString().replace(",", "");
            double jumlah = Double.parseDouble(jumlahString.trim());
            String tipe = jTable1.getValueAt(i, 3).toString().trim();

            // Akumulasi berdasarkan tipe
            if (tipe.equalsIgnoreCase("Pemasukan")) {
                pemasukan += jumlah;
            } else if (tipe.equalsIgnoreCase("Pengeluaran")) {
                pengeluaran += jumlah;
            }
        } catch (NumberFormatException e) {
            System.err.println("Kesalahan parsing jumlah di baris " + (i + 1));
        }
    }
     // Format hasil untuk ditampilkan
    DecimalFormat formatter = new DecimalFormat("#,###");
    lblPemasukan.setText("Total Pemasukan: Rp " + formatter.format(pemasukan));
    lblPengeluaran.setText("Total Pengeluaran: Rp " + formatter.format(pengeluaran));
    lblSaldo.setText("Saldo: Rp " + formatter.format(pemasukan - pengeluaran));
}

  
private void filterByDateRange() {
     Date tanggalAwal = dcAwal.getDate();
    Date tanggalAkhir = dcAkhir.getDate();

    // Validasi input tanggal
    if (tanggalAwal == null || tanggalAkhir == null) {
        JOptionPane.showMessageDialog(this, "Harap pilih rentang tanggal!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (tanggalAwal.after(tanggalAkhir)) {
        JOptionPane.showMessageDialog(this, "Tanggal awal tidak boleh lebih besar dari tanggal akhir!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Format tanggal
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
    tableModel.setRowCount(0); // Kosongkan tabel sebelum menampilkan data baru

    DecimalFormat formatter = new DecimalFormat("#,###");

    // Iterasi data transaksi
    for (String[] row : transaksi) {
        try {
            if (row.length < 4 || row[0] == null || row[1] == null || row[2] == null || row[3] == null) {
                System.err.println("Baris tidak valid atau data kosong: " + Arrays.toString(row));
                continue;
            }

            Date tanggalTransaksi = dateFormat.parse(row[0]);

            // Cek apakah transaksi berada dalam rentang tanggal
            if (!tanggalTransaksi.before(tanggalAwal) && !tanggalTransaksi.after(tanggalAkhir)) {
                // Parsing jumlah ke double
                double jumlah = Double.parseDouble(row[2].replace(",", ""));
                String jumlahFormatted = formatter.format(jumlah); // Format sebagai desimal

                // Tambahkan data ke tabel
                tableModel.addRow(new Object[]{
                    row[0],            // Tanggal
                    row[1],            // Deskripsi
                    jumlahFormatted,   // Jumlah (diformat sebagai desimal)
                    row[3]             // Tipe
                });
            }

        } catch (ParseException e) {
            System.err.println("Kesalahan parsing tanggal: " + row[0]);
        } catch (NumberFormatException e) {
            System.err.println("Kesalahan parsing jumlah: " + row[2]);
        }
    }

    // Periksa apakah ada data yang ditampilkan
    if (tableModel.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada data yang sesuai dengan rentang tanggal!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Update saldo setelah filter
    updateSaldoFiltered();
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDeskripsi = new javax.swing.JTextField();
        txtJumlah = new javax.swing.JTextField();
        cmbBox = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnImpor = new javax.swing.JButton();
        btnEkspor = new javax.swing.JButton();
        dcAwal = new com.toedter.calendar.JDateChooser();
        dcAkhir = new com.toedter.calendar.JDateChooser();
        btnFilter = new javax.swing.JButton();
        dcTanggal = new com.toedter.calendar.JDateChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        btnKeluar = new javax.swing.JButton();
        lblSaldo = new javax.swing.JLabel();
        lblPemasukan = new javax.swing.JLabel();
        lblPengeluaran = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Deskripsi");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Jumlah");

        cmbBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pengeluaran", "Pemasukan" }));

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        btnImpor.setText("Impor");
        btnImpor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImporActionPerformed(evt);
            }
        });

        btnEkspor.setText("Ekspor");
        btnEkspor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEksporActionPerformed(evt);
            }
        });

        btnFilter.setText("Filter");
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "Deskripsi", "Jumlah", "Tipe"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Tanggal");

        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        lblSaldo.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblSaldo.setForeground(new java.awt.Color(255, 255, 255));

        lblPemasukan.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblPemasukan.setForeground(new java.awt.Color(255, 255, 255));

        lblPengeluaran.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblPengeluaran.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(217, 217, 217)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addGap(89, 89, 89)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dcTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDeskripsi, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1))
                            .addGap(256, 256, 256)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(dcAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dcAwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btnTambah)
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnHapus, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEkspor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnImpor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnKeluar, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPemasukan)
                            .addComponent(lblSaldo)
                            .addComponent(lblPengeluaran))))
                .addContainerGap(273, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEdit, btnEkspor, btnHapus, btnImpor, btnTambah});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcAwal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnTambah)
                        .addComponent(btnFilter)
                        .addComponent(jLabel1)
                        .addComponent(txtDeskripsi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnEdit)
                            .addComponent(btnImpor)
                            .addComponent(btnKeluar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnHapus)
                            .addComponent(btnEkspor)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(txtJumlah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dcAkhir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(dcTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(113, 113, 113)
                        .addComponent(lblSaldo)
                        .addGap(30, 30, 30)
                        .addComponent(lblPemasukan)
                        .addGap(30, 30, 30)
                        .addComponent(lblPengeluaran)))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setForeground(new java.awt.Color(51, 51, 51));

        jLabel4.setFont(new java.awt.Font("Wide Latin", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("APLIKASI KEUANGAN PRIBADI");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(258, 258, 258)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
    // Tampilkan dialog pilihan
    String[] options = {"Hapus dari TextField", "Hapus dari Tabel", "Batal"};
    int choice = JOptionPane.showOptionDialog(null, 
            "Pilih tindakan yang ingin dilakukan:", 
            "Konfirmasi Hapus", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[2]);

    switch (choice) {
        case 0: // Hapus dari TextField
            txtDeskripsi.setText("");
            txtJumlah.setText("");
            cmbBox.setSelectedIndex(0);
            dcTanggal.setDate(null);
            JOptionPane.showMessageDialog(null, "Data di TextField telah dihapus!");
            break;

        case 1: // Hapus dari Tabel
            int selectedRow = jTable1.getSelectedRow(); // Ambil baris yang dipilih
            if (selectedRow != -1) {
                // Tampilkan dialog konfirmasi untuk menghapus
                int confirm = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus transaksi ini dari tabel?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Hapus data dari model tabel
                    ((DefaultTableModel) jTable1.getModel()).removeRow(selectedRow);

                    // Update saldo setelah penghapusan
                    updateSaldo();
                    updateSaldoFromTable();
                    JOptionPane.showMessageDialog(null, "Data berhasil dihapus dari tabel!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Silakan pilih transaksi yang ingin dihapus dari tabel.");
            }
            break;

        case 2: // Batal
            JOptionPane.showMessageDialog(null, "Tindakan dibatalkan.");
            break;

        default:
            break;
    }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
     // Mendapatkan baris yang dipilih di jTable1
        int selectedRow = jTable1.getSelectedRow();

        // Pastikan ada baris yang dipilih
        if (selectedRow != -1) {
            // Ambil nilai dari text field dan komponen lainnya
            String deskripsi = txtDeskripsi.getText();
            String jumlahStr = txtJumlah.getText();
            String tipe = cmbBox.getSelectedItem().toString();

            // Ambil nilai tanggal dari JDateChooser
            java.util.Date selectedDate = dcTanggal.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(null, "Pilih tanggal terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Format tanggal ke string
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String tanggal = dateFormat.format(selectedDate);

            try {
                // Validasi jumlah (harus berupa angka)
                double jumlah = Double.parseDouble(jumlahStr);

                // Perbarui data di model tabel
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                model.setValueAt(tanggal, selectedRow, 0); // Kolom Tanggal
                model.setValueAt(deskripsi, selectedRow, 1); // Kolom Deskripsi
                model.setValueAt(jumlah, selectedRow, 2); // Kolom Jumlah
                model.setValueAt(tipe, selectedRow, 3); // Kolom Tipe
                
                updateSaldo();
                updateSaldoFromTable(); 

                JOptionPane.showMessageDialog(null, "Data berhasil diperbarui!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Jumlah harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih baris yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
     String deskripsi = txtDeskripsi.getText().trim();
    String jumlah = txtJumlah.getText().trim();
    String tipe = cmbBox.getSelectedItem().toString();
    Date tanggal = dcTanggal.getDate();

    // Validasi input kosong
    if (deskripsi.isEmpty() || jumlah.isEmpty() || tanggal == null) {
        JOptionPane.showMessageDialog(this, "Harap isi semua field termasuk tanggal!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Hapus simbol pemisah seperti koma atau titik
        jumlah = jumlah.replaceAll("[,\\.]", "").trim();

        // Validasi jumlah hanya angka
        if (!jumlah.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka tanpa simbol!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parsing jumlah ke tipe double
        double jumlahValue = Double.parseDouble(jumlah);

        // Format tanggal menjadi string (format konsisten dengan filter)
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String tanggalStr = dateFormat.format(tanggal);

        // Format jumlah dengan pemisah ribuan
        DecimalFormat formatter = new DecimalFormat("#,###");
        String jumlahFormatted = formatter.format(jumlahValue);

        // Tambahkan data ke tabel
        tableModel.addRow(new Object[]{tanggalStr, deskripsi, jumlahFormatted, tipe});

        // Tambahkan data ke array transaksi
        transaksi.add(new String[]{tanggalStr, deskripsi, String.valueOf(jumlahValue), tipe});

        // Update saldo
        updateSaldo();
        updateSaldoFromTable();

        // Berikan notifikasi berhasil
        JOptionPane.showMessageDialog(this, "Data berhasil ditambahkan!", "Info", JOptionPane.INFORMATION_MESSAGE);

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnImporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImporActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Pilih file CSV");
    int userSelection = fileChooser.showOpenDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        java.io.File fileToOpen = fileChooser.getSelectedFile();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fileToOpen))) {
            String line;
            tableModel.setRowCount(0); // Kosongkan tabel sebelum impor
            br.readLine(); // Lewati header CSV (jika ada)

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

            while ((line = br.readLine()) != null) {
                // Coba berbagai pemisah
                String[] data = line.split("[,;\\t]");

                // Validasi jumlah kolom
                if (data.length != 4) {
                    System.err.println("Baris tidak valid: " + line);
                    JOptionPane.showMessageDialog(this, "Format file tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // Parsing data
                    String tanggalStr = data[0].trim();
                    String deskripsi = data[1].trim();
                    String jumlahStr = data[2].trim().replace(",", ""); // Hapus koma sebelum parsing
                    String tipe = data[3].trim();

                    // Validasi dan parsing tanggal
                    Date tanggal = dateFormat.parse(tanggalStr);

                    // Validasi dan parsing jumlah
                    double jumlah = Double.parseDouble(jumlahStr);

                    // Format ulang jumlah untuk ditampilkan di tabel
                    String jumlahFormatted = decimalFormat.format(jumlah);

                    // Tambahkan data ke tabel dan transaksi
                    tableModel.addRow(new Object[]{
                        dateFormat.format(tanggal), // Format tanggal ke string
                        deskripsi,
                        jumlahFormatted,
                        tipe
                    });

                    // Simpan data asli ke list transaksi
                    transaksi.add(new String[]{
                        dateFormat.format(tanggal),
                        String.valueOf(jumlah),
                        tipe
                    });

                } catch (ParseException | NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Kesalahan format data di file CSV!", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }

            // Update saldo setelah data diimpor
            updateSaldo();
            JOptionPane.showMessageDialog(this, "Data berhasil diimpor!", "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat membaca file!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    }//GEN-LAST:event_btnImporActionPerformed

    private void btnEksporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEksporActionPerformed
   JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan data sebagai CSV");
    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
    java.io.File fileToSave = fileChooser.getSelectedFile();

    // Pastikan file memiliki ekstensi ".csv"
    if (!fileToSave.getName().endsWith(".csv")) {
        fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".csv");
    }

    try (java.io.FileWriter fileWriter = new java.io.FileWriter(fileToSave)) {
        // Tulis header sesuai dengan kolom tabel
        fileWriter.write("Tanggal,Deskripsi,Jumlah,Tipe\n");

        // Tulis data dari JTable ke file CSV
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            String tanggal = jTable1.getValueAt(i, 0).toString().trim();
            String deskripsi = jTable1.getValueAt(i, 1).toString().trim();
            String jumlah = jTable1.getValueAt(i, 2).toString().trim().replace(",", ""); // Hilangkan pemisah ribuan
            String tipe = jTable1.getValueAt(i, 3).toString().trim();

            // Format menjadi CSV
            fileWriter.write(tanggal + "," + deskripsi + "," + jumlah + "," + tipe + "\n");
        }

        JOptionPane.showMessageDialog(this, "Data berhasil diekspor!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
    } catch (java.io.IOException ex) {
        JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menulis file!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    }//GEN-LAST:event_btnEksporActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
         filterByDateRange();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnKeluarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AplikasiKeuanganPribadi.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AplikasiKeuanganPribadi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEkspor;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImpor;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbBox;
    private com.toedter.calendar.JDateChooser dcAkhir;
    private com.toedter.calendar.JDateChooser dcAwal;
    private com.toedter.calendar.JDateChooser dcTanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblPemasukan;
    private javax.swing.JLabel lblPengeluaran;
    private javax.swing.JLabel lblSaldo;
    private javax.swing.JTextField txtDeskripsi;
    private javax.swing.JTextField txtJumlah;
    // End of variables declaration//GEN-END:variables
}
