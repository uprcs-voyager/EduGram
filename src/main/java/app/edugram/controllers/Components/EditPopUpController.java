package app.edugram.controllers.Components;
import app.edugram.Main;
import app.edugram.models.PostModel; // Pastikan PostModel sudah ada dan memiliki metode update
import app.edugram.models.PostTagModel;
import app.edugram.models.TagModel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.image.Image; // Import yang benar untuk Image
import javafx.scene.image.ImageView; // Import yang benar untuk ImageView
import javafx.scene.control.TextArea;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class EditPopUpController {

    @FXML private VBox editPostpopup;
    @FXML private TextField currentTitle;
    @FXML private TextArea currentDesc;
    @FXML private TextField currentImageUrlField;
    @FXML private TextField currentTags;
    @FXML private ImageView currentImage;
    @FXML private Button browseImageButton;
    @FXML private Button editPost;
    @FXML private Button cancelEditPost;
    private String oldImageName;
    private String prevTag;

    private Popup ownerPopup; // Referensi ke Popup ini sendiri
    private PostModel postToEdit; // Postingan yang sedang diedit
    private PostFrameController postFrameController; // Opsional: Untuk merefresh UI jika perlu
    private Popup parentPopup;
    private Stage dialogStage;
    private File selectedImageFile;


    public void initialize() {
        editPost.setOnAction(event -> handleSave());
        cancelEditPost.setOnAction(event -> handleCancel());
        browseImageButton.setOnAction(event -> handleBrowseImage());
    }

    public void setPopup(Popup popup) {
        this.ownerPopup = popup;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setParentPopup(Popup parentPopup) {
        this.parentPopup = parentPopup;
    }

    public void setPostData(PostModel post) {
        this.postToEdit = post;
        if (postToEdit != null) {
            currentTitle.setText(postToEdit.getTitle());
            currentDesc.setText(postToEdit.getDescription());

            List<String> tagsList = postToEdit.getTags();
            if (tagsList != null && !tagsList.isEmpty()) {
                String tagContent = tagsList.stream()
                        .map(tag -> tag.replace("#", ""))          // hapus tanda #
                        .map(tag -> tag.split("-")[0])             // ambil bagian sebelum '-'
                        .collect(Collectors.joining(" "));

                prevTag = tagContent;
                currentTags.setText(tagContent);
            } else {
                currentTags.setText("");
            }

            String imagePath = postToEdit.getPostContent();
            System.out.println("DEBUG: Image path from post: " + imagePath);

            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File(imagePath);
                System.out.println("DEBUG: Attempting to load image from path: " + imagePath);
                System.out.println("DEBUG: Absolute path of the file object: " + file.getAbsolutePath());
                System.out.println("DEBUG: File exists: " + file.exists());

                // If file doesn't exist and path doesn't contain directory separators, try constructing full path
                if (!file.exists() && !imagePath.contains("/") && !imagePath.contains("\\")) {
                    System.out.println("DEBUG: Trying to construct full path from filename");
                    String fullPath = System.getProperty("user.dir") + "/src/main/resources/app/edugram/userData/Images/posts/" + imagePath;
                    file = new File(fullPath);
                    System.out.println("DEBUG: Constructed full path: " + fullPath);
                    System.out.println("DEBUG: Full path file exists: " + file.exists());
                    oldImageName = imagePath;
                }

                if (file.exists()) {
                    boolean imageLoaded = false;

                    // Try method 1: URI conversion
                    try {
                        String uri = file.toURI().toString();
                        System.out.println("DEBUG: URI: " + uri);
                        Image image = new Image(uri);

                        if (!image.isError()) {
                            currentImage.setImage(image);
                            currentImageUrlField.setText(file.getAbsolutePath());
                            selectedImageFile = file;
                            imageLoaded = true;
                            System.out.println("DEBUG: Image loaded successfully with URI method");

                            // Force ImageView refresh
                            currentImage.setVisible(false);
                            currentImage.setVisible(true);
                        } else {
                            System.err.println("DEBUG: JavaFX Image has error flag set with URI method");
                        }
                    } catch (Exception e) {
                        System.err.println("DEBUG: URI method failed: " + e.getMessage());
                    }

                    // Try method 2: FileInputStream if URI method failed
                    if (!imageLoaded) {
                        try {
                            System.out.println("DEBUG: Trying FileInputStream method");
                            FileInputStream fis = new FileInputStream(file);
                            Image image = new Image(fis);

                            if (!image.isError()) {
                                currentImage.setImage(image);
                                currentImageUrlField.setText(file.getAbsolutePath());
                                selectedImageFile = file;
                                imageLoaded = true;
                                System.out.println("DEBUG: Image loaded successfully with FileInputStream method");

                                // Force ImageView refresh
                                currentImage.setVisible(false);
                                currentImage.setVisible(true);
                            } else {
                                System.err.println("DEBUG: JavaFX Image has error flag set with FileInputStream method");
                            }
                            fis.close();
                        } catch (Exception e) {
                            System.err.println("DEBUG: FileInputStream method failed: " + e.getMessage());
                        }
                    }

                    // Try method 3: BufferedImage conversion if both previous methods failed
                    if (!imageLoaded) {
                        try {
                            System.out.println("DEBUG: Trying BufferedImage conversion method");
                            BufferedImage bufferedImage = ImageIO.read(file);
                            if (bufferedImage != null) {
                                Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                                currentImage.setImage(fxImage);
                                currentImageUrlField.setText(file.getAbsolutePath());
                                selectedImageFile = file;
                                imageLoaded = true;
                                System.out.println("DEBUG: Image loaded successfully with BufferedImage method");

                                // Force ImageView refresh
                                currentImage.setVisible(false);
                                currentImage.setVisible(true);
                            }
                        } catch (Exception e) {
                            System.err.println("DEBUG: BufferedImage method failed: " + e.getMessage());
                        }
                    }

                    if (!imageLoaded) {
                        System.err.println("ERROR: All image loading methods failed for: " + file.getAbsolutePath());
                        currentImage.setImage(null);
                        currentImageUrlField.setText(imagePath);
                    }

                } else {
                    System.err.println("ERROR: Local image file not found: " + file.getAbsolutePath());
                    currentImage.setImage(null);
                    currentImageUrlField.setText(imagePath);
                }
            } else {
                System.out.println("DEBUG: No image path provided");
                currentImage.setImage(null);
                currentImageUrlField.setText("");
            }
        }
    }

    /**
     * Mengatur PostFrameController (jika diperlukan untuk refresh UI setelah edit).
     * @param controller Instans PostFrameController
     */
    public void setPostFrameController(PostFrameController controller) {
        this.postFrameController = controller;
    }

    private void handleSave() {
        String title = currentTitle.getText().trim();
        String description = currentDesc.getText().trim();
        List<String> tags = Arrays.asList(currentTags.getText().trim().split("\\s+"));

        // Get current window for alert - same approach as TambahPostController
        Window currentWindowForAlert = null;
        if (dialogStage != null) {
            currentWindowForAlert = dialogStage;
        } else if (editPostpopup != null && editPostpopup.getScene() != null) {
            currentWindowForAlert = editPostpopup.getScene().getWindow();
        }

        // Validasi input - same validation as TambahPostController
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Judul postingan tidak boleh kosong.", currentWindowForAlert);
            return;
        }
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Deskripsi postingan tidak boleh kosong.", currentWindowForAlert);
            return;
        }
        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Gambar Belum Dipilih", "Mohon pilih gambar untuk postingan Anda.", currentWindowForAlert);
            return;
        }

        if (postToEdit != null) {
            String imageFileName = null;

            // Only create new file name and store image if a new image was selected
            if (selectedImageFile != null && !selectedImageFile.getAbsolutePath().equals(postToEdit.getPostContent())) {
                imageFileName = createNewFileName();
                if (!storeProfileImage(imageFileName)) {
                    showAlert(Alert.AlertType.WARNING, "Gagal upload image", "Image post gagal diupload!.", currentWindowForAlert);
                    return;
                }
            } else {
                // Keep existing image filename
                imageFileName = postToEdit.getPostContent();
            }

            // Update post data
            postToEdit.setTitle(title);
            postToEdit.setDescription(description);
            postToEdit.setPostContent(imageFileName);

            // Call update method instead of create
            tagValidation(tags);
            boolean success = postToEdit.update(postToEdit);

            if (success) {
                PostModel.deleteImage(oldImageName);

                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Postingan berhasil diupdate!", currentWindowForAlert);
                if (dialogStage != null) {
                    dialogStage.close();
                } else if (ownerPopup != null) {
                    ownerPopup.hide();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal mengupdate postingan. Silakan coba lagi.", currentWindowForAlert);
            }
        }
    }

    private void handleCancel() {
        System.out.println("Edit postingan dibatalkan.");
        if (dialogStage != null) {
            dialogStage.close(); // Same as TambahPostController
        } else if (ownerPopup != null) {
            ownerPopup.hide(); // Fallback for popup
        }
    }

    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Postingan");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Get owner window - same approach as TambahPostController
        Window ownerWindowForFileChooser = null;
        if (dialogStage != null) {
            ownerWindowForFileChooser = dialogStage;
        } else if (editPostpopup != null && editPostpopup.getScene() != null) {
            ownerWindowForFileChooser = editPostpopup.getScene().getWindow();
        }

        selectedImageFile = fileChooser.showOpenDialog(ownerWindowForFileChooser);

        if (selectedImageFile != null) {
            currentImageUrlField.setText(selectedImageFile.getAbsolutePath());
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                if (!image.isError()) {
                    currentImage.setImage(image);
                    // Force ImageView refresh
                    currentImage.setVisible(false);
                    currentImage.setVisible(true);
                } else {
                    System.err.println("ERROR: Selected image has error flag set");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Tidak dapat memuat gambar: " + e.getMessage(), ownerWindowForFileChooser);
            }
        }
    }

    private String createNewFileName(){
        // Create new file name - same as TambahPostController
        Random randomInt = new Random();
        String getRandomNumber = String.valueOf(randomInt.nextInt(1000) + 1000);

        LocalDateTime getDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String formatDate = getDateTime.format(formatter);

        // Get extension
        String imageUrl = currentImage.getImage().getUrl();
        String imageExtension = "";
        if (imageUrl != null) {
            imageExtension = imageUrl.substring(imageUrl.lastIndexOf(".") + 1).toLowerCase();
            // Remove any query parameters if present
            if (imageExtension.contains("?")) {
                imageExtension = imageExtension.substring(0, imageExtension.indexOf("?"));
            }
            System.out.println("Extension: " + imageExtension);
        }

        String newProfileName = "post_" + getRandomNumber + formatDate + "." + imageExtension;
        return newProfileName;
    }

    private boolean storeProfileImage(String newFileName) {
        try {
            // Create target directory if it doesn't exist - same as TambahPostController
            Path targetDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "app", "edugram", "userData", "Images", "posts");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Get the image from the ImageView
            Image currentImageFromView = this.currentImage.getImage();
            BufferedImage originalBufferedImage = SwingFXUtils.fromFXImage(currentImageFromView, null);

            // Convert to RGB color space to prevent "Bogus input colorspace" error
            BufferedImage rgbImage = new BufferedImage(
                    originalBufferedImage.getWidth(),
                    originalBufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Draw the original image onto the new RGB image
            rgbImage.createGraphics().drawImage(originalBufferedImage, 0, 0, null);

            File outputFile = targetDir.resolve(newFileName).toFile();

            // Compress and save the RGB-converted image
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.8f); // 80% quality

            // Write the rgbImage
            writer.write(null, new IIOImage(rgbImage, null, null), param);

            ios.close();
            writer.dispose();

            System.out.println("Post image stored and compressed: " + newFileName);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to store profile image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void tagValidation(List<String> tags) {
        PostTagModel ptm = new PostTagModel();

        for(String tag : tags){
            tag = tag.replace("#", "");
            if(!TagModel.validate(tag)){
                TagModel.create(tag);
            }
        }

        // 0. Jika null, ubah ke list kosong
        if (tags == null) tags = new ArrayList<>();

        // 1. Ubah prevTag jadi Set<String> yang aman terhadap null
        Set<String> prevTagSet = new HashSet<>();
        if (this.prevTag != null && !this.prevTag.isBlank()) {
            prevTagSet = Arrays.stream(this.prevTag.split("\\s+"))
                    .map(tag -> tag.replace("#", "").split("-")[0])
                    .collect(Collectors.toSet());
        }

        // 2. Bersihkan tag baru dan jadikan Set
        Set<String> newTagSet = tags.stream()
                .map(tag -> tag.replace("#", "").split("-")[0])
                .collect(Collectors.toSet());

        // 3. Tag yang harus dihapus (ada di prev tapi tidak di baru)
        Set<String> toRemove = new HashSet<>(prevTagSet);
        toRemove.removeAll(newTagSet);

        // 4. Tag yang harus ditambah (ada di baru tapi tidak di prev)
        Set<String> toAdd = new HashSet<>(newTagSet);
        toAdd.removeAll(prevTagSet);

        // 5. Tag yang tidak berubah (ada di kedua set)
        Set<String> unchangedTags = new HashSet<>(prevTagSet);
        unchangedTags.retainAll(newTagSet);

        // Tambahkan tag baru
        for (String tag : toAdd) {
            System.out.println("Tag to add: " + tag);

            List<String> value = List.of(
                    String.valueOf(postToEdit.getId()),
                    TagModel.fetchIdTag(tag)
            );
            ptm.set(value);
        }

        // Hapus tag yang sudah tidak digunakan
        for (String tag : toRemove) {
            System.out.println("Tag to remove: " + tag);

            ptm.setData(TagModel.fetchIdTag(tag), String.valueOf(postToEdit.getId()));
            ptm.unset(0); // atau ganti id_tag sebenarnya jika tersedia
        }

        // Cetak tag yang tidak berubah
        for (String tag : unchangedTags) {
            System.out.println("Unchanged tag: " + tag);
            // Tidak perlu dilakukan apa-apa
        }
    }




    // Alert method - exactly same as TambahPostController
    private void showAlert(Alert.AlertType type, String title, String message, Window ownerWindow) {
        System.out.println(message);
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        if (ownerWindow != null) {
//            alert.initOwner(ownerWindow);
//        }
//        alert.showAndWait();
    }
}