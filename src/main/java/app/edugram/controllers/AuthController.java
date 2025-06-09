package app.edugram.controllers;

import app.edugram.utils.PageAction;
import app.edugram.utils.Notices;
import app.edugram.utils.cookies.CookieUtil;
import app.edugram.utils.cookies.UserCookie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import app.edugram.models.UserModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.io.IOException;


public class AuthController {
    public Button hiddenRedirectButton;
    @FXML
    private Button cancelButton;
    public TextField usernameInp;
    public TextField passInp;
    public TextField emailInp;
    public TextField namaInp;
    @FXML
    private ImageView image_login_bg;

    @FXML
    public void initialize() {
        if(checkCookie()) {
            Platform.runLater(() -> {
                hiddenRedirectButton.fire(); // This creates a proper ActionEvent
            });
            return;
        }
        try{
            Region parent = (Region) image_login_bg.getParent();

            image_login_bg.fitWidthProperty().bind(parent.widthProperty());
            image_login_bg.fitHeightProperty().bind(parent.heightProperty());
            image_login_bg.setPreserveRatio(false); // Biar selalu isi seluruh area
        }catch (Exception e){
            System.out.println("Auth.Initialize: (notice) This content doesn't load / contain image");
        }
    }

    public boolean checkCookie() {
        UserCookie cookie = CookieUtil.loadCookie();
        System.out.println("Checking cookie for user: '" + cookie.getUsername() + "'");

        return UserModel.ValidateUser(cookie.getUsername(), cookie.getPassword(), true);
    }

    @FXML
    public void hiddenRedirectAction(ActionEvent event) {
        PageAction.switchPage(event, "baseView.fxml"); // Real ActionEvent, will work!
    }


    @FXML
    public void loginButtonAction(ActionEvent event) throws IOException {
        String username = usernameInp.getText();
        String password = passInp.getText();

        if(username.isEmpty() || password.isEmpty()){Notices.formNotFilled();return;}

//        boolean isValid = UserModel.ValidateUser(username, password, true);
        if(!loginAction(event, username, password)){
            System.out.println("Auth.loginButtonAction: Login failed");
        }
    }

    @FXML
    public void RegisterButtonAction(ActionEvent event) throws IOException {
        String username = usernameInp.getText();
        String password = passInp.getText();
        String nama = namaInp.getText();
        String email = emailInp.getText();

        if(username.isEmpty() || password.isEmpty() || nama.isEmpty() || email.isEmpty()){Notices.formNotFilled();return;}

        boolean isValid = UserModel.ValidateUser(username, password, false);
        if(isValid){
            Notices.customNote( "Notice","Username yang dipilih sudah diambil! Coba kombinasi lain!");
            return;
        }

        UserModel newUser = new UserModel(username, password, nama, email, username);
        if(!newUser.validate()){
            System.out.println("Syntax error");
            return;
        }

        if(!newUser.create(newUser)){
            System.out.println("Auth.RegisterButtonAction: Failed to create new user");
            return;
        }
        System.out.println("New user created");
        loginAction(event, username, password);
    }

    @FXML
    public boolean loginAction(ActionEvent event, String username, String password){
        if(UserModel.ValidateUser(username, password, true)){
            UserCookie cookie = new UserCookie();
            cookie.setLoggedIn(true);
            cookie.setUsername(username);
            cookie.setPassword(password);

            CookieUtil.saveCookie(cookie);
            PageAction.switchPage(event, "baseView.fxml");
            return true;
        }
        Notices.customNote("Invalid login", "Username/email atau password salah! Coba lagi!");
        return false;
    }

    @FXML
    public void gotoRegis(ActionEvent event){
        PageAction.switchPage(event, "signup.fxml");
    }

    @FXML
    public void gotoLogin(ActionEvent event){
        PageAction.switchPage(event, "login.fxml");
    }
}