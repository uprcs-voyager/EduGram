package app.edugram.controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TagFilterController {

    @FXML private static FlowPane tagContainer;
    @FXML private Button clearSelectionButton;
    @FXML private Button applyFilterButton;

    private Stage dialogStage;
    private static Set<String> selectedTags = new HashSet<>(); // Menyimpan nama tag yang dipilih

    // --- DATA PLACEHOLDER UNTUK TAG ---
//    private final List<String> DUMMY_TAGS = Arrays.asList(
//            "Nature", "Biology", "Science", "Technology", "Art",
//            "History", "Programming", "Education", "Food", "Travel",
//            "Photography", "Music", "Sports", "Health", "Business",
//            "Design", "Marketing", "Finance", "Writing", "Cooking"
//    );
    // --- Akhir DATA PLACEHOLDER ---

    // Dipanggil dari controller utama untuk inisialisasi modal
//    public void setDialogStageAndInitialTags(Stage dialogStage, Set<String> currentSelectedTags) {
//        this.dialogStage = dialogStage;
//        if (currentSelectedTags != null) {
//            this.selectedTags.addAll(currentSelectedTags); // Set tag yang sudah aktif sebelumnya
//        }
//        loadAndDisplayTags(); // Muat dan tampilkan tombol tag
//    }

    @FXML
    public void initialize() {
        // Atur aksi untuk tombol
        clearSelectionButton.setOnAction(event -> handleClearSelection());
        applyFilterButton.setOnAction(event -> handleApplyFilter());
    }

    public static void loadAndDisplayTags(List<String> DUMMY_TAGS) {
        tagContainer.getChildren().clear(); // Bersihkan container

        if (DUMMY_TAGS.isEmpty()) {
            tagContainer.getChildren().add(new Label("Tidak ada tag tersedia."));
            return;
        }

        for (String tagName : DUMMY_TAGS) {
            Button tagButton = new Button(tagName);
            tagButton.getStyleClass().add("tag-button"); // Terapkan CSS dasar

            // Jika tag sudah dipilih sebelumnya, beri style 'active'
            if (selectedTags.contains(tagName)) {
                tagButton.getStyleClass().add("tag-button-active");
            }

            // Tambahkan event handler untuk toggle seleksi tag
            tagButton.setOnAction(event -> {
                String currentTagName = ((Button)event.getSource()).getText();
                if (selectedTags.contains(currentTagName)) {
                    selectedTags.remove(currentTagName);
                    tagButton.getStyleClass().remove("tag-button-active");
                } else {
                    selectedTags.add(currentTagName);
                    tagButton.getStyleClass().add("tag-button-active");
                }
            });
            tagContainer.getChildren().add(tagButton);
        }
    }

    private void handleClearSelection() {
        selectedTags.clear(); // Hapus semua pilihan
        // Perbarui tampilan semua tombol agar tidak aktif
        for (javafx.scene.Node node : tagContainer.getChildren()) {
            if (node instanceof Button) {
                node.getStyleClass().remove("tag-button-active");
            }
        }
    }

    private void handleApplyFilter() {
        // Untuk sekarang, kita hanya mencetak tag yang dipilih ke konsol.
        // Di sini Anda bisa menambahkan logika untuk menyimpan preferensi ke database (nanti).
        System.out.println("Tag yang dipilih dari modal: " + selectedTags);
        if (dialogStage != null) {
            dialogStage.close(); // Tutup modal
        }
    }

    // Metode ini dipanggil oleh TopbarController untuk mendapatkan tag yang dipilih
    public Set<String> getSelectedTags() {
        return selectedTags;
    }
}