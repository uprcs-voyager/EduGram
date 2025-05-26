package app.edugram.utils;

import javafx.scene.control.Alert;

public class Notices {
    public static void formNotFilled(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Form Tidak Lengkap");
        alert.setHeaderText(null);
        alert.setContentText("Semua field harus diisi!");
        alert.showAndWait();
    }

    public static void customNote(String note){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(note);
        alert.showAndWait();
    }
}
