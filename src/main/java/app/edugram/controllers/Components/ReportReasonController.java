package app.edugram.controllers.Components;

import app.edugram.utils.Sessions;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ReportReasonController {
    @FXML private Label reporterUsernameLabel;
    @FXML private Label reportReasonLabel;
    @FXML private Label reportTimestampLabel;

    public void setReportData (String username, String reason, LocalDateTime timestamp){
        reporterUsernameLabel.setText("Reported by: @" + Sessions.getUsername());
        reportReasonLabel.setText(reason);
        reportReasonLabel.setWrapText(true);

    }

}
