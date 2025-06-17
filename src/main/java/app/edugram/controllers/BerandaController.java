package app.edugram.controllers;
import app.edugram.Main;
import app.edugram.controllers.Components.PostFrameController;
import app.edugram.models.PostModel;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class BerandaController extends BaseController implements Initializable {

    @FXML private GridPane postContentGrid;
    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentContainer;

    // Lazy loading variables
    private int currentPage = 0;
    private final int postsPerPage = 5;
    private boolean isLoading = false;
    private boolean hasMorePosts = true;
    private List<PostModel> allPosts = new ArrayList<>();
    private VBox loadingContainer; // Programmatically created loading indicator



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("BerandaController initialized");
        System.out.println("postContentGrid: " + postContentGrid);
        System.out.println("contentScrollPane: " + contentScrollPane);

        createLoadingIndicator();
        setupScrollListener();
        loadAllPostsData(); // This will call loadNextBatch when done
    }

    private void createLoadingIndicator() {
        loadingContainer = new VBox();
        loadingContainer.setAlignment(javafx.geometry.Pos.CENTER);
        loadingContainer.setPadding(new Insets(20));
        loadingContainer.setVisible(false);

        // Create a simple text loading indicator
        javafx.scene.control.Label loadingLabel = new javafx.scene.control.Label("Loading more posts...");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        loadingContainer.getChildren().add(loadingLabel);

        // Add to the bottom of the grid content
        postContentGrid.add(loadingContainer, 0, 999); // High row number to put it at bottom
        GridPane.setColumnSpan(loadingContainer, 2); // Span across both columns
    }

    private void setupScrollListener() {
        if (contentScrollPane != null) {
            contentScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                // Check if user scrolled to bottom (within 10% of the bottom)
                if (newValue.doubleValue() >= 0.9 && !isLoading && hasMorePosts) {
                    loadNextBatch();
                }
            });
        }
    }

    private void loadAllPostsData() {
        System.out.println("Loading all posts data...");

        Task<List<PostModel>> loadAllPostsTask = new Task<List<PostModel>>() {
            @Override
            protected List<PostModel> call() throws Exception {
                List<PostModel> posts = new ArrayList<>(initData());
                System.out.println("Total posts loaded: " + posts.size());
                return posts;
            }
        };

        loadAllPostsTask.setOnSucceeded(e -> {
            allPosts = loadAllPostsTask.getValue();
            hasMorePosts = !allPosts.isEmpty();
            System.out.println("Posts loaded successfully. Count: " + allPosts.size());
            System.out.println("Has more posts: " + hasMorePosts);

            // Now that data is loaded, load the first batch
            Platform.runLater(() -> {
                loadNextBatch();
            });
        });

        loadAllPostsTask.setOnFailed(e -> {
            System.err.println("Failed to load posts data: " + loadAllPostsTask.getException().getMessage());
//            e.printStackTrace();
            hasMorePosts = false;
        });

        Thread thread = new Thread(loadAllPostsTask);
        thread.setDaemon(true);
        thread.start();
    }

    public void loadNextBatch() {
        System.out.println("loadNextBatch called - isLoading: " + isLoading + ", hasMorePosts: " + hasMorePosts);

        if (isLoading || !hasMorePosts) {
            System.out.println("Skipping batch load - isLoading: " + isLoading + ", hasMorePosts: " + hasMorePosts);
            return;
        }

        isLoading = true;
        showLoadingIndicator();
        System.out.println("Starting to load batch " + currentPage);

        Task<List<PostModel>> loadBatchTask = new Task<List<PostModel>>() {
            @Override
            protected List<PostModel> call() throws Exception {
                System.out.println("In background task - allPosts size: " + allPosts.size());

                int startIndex = currentPage * postsPerPage;
                int endIndex = Math.min(startIndex + postsPerPage, allPosts.size());

                System.out.println("Batch indices - start: " + startIndex + ", end: " + endIndex);

                if (startIndex >= allPosts.size()) {
                    System.out.println("No more posts to load");
                    return new ArrayList<>();
                }

//                Thread.sleep(500);

                List<PostModel> batch = allPosts.subList(startIndex, endIndex);
                System.out.println("Returning batch of size: " + batch.size());
                return batch;
            }
        };

        loadBatchTask.setOnSucceeded(e -> {
            List<PostModel> batch = loadBatchTask.getValue();
            System.out.println("Batch loaded successfully. Size: " + batch.size());

            if (!batch.isEmpty()) {
                displayPostsBatch(batch);
                currentPage++;
                System.out.println("Current page updated to: " + currentPage);

                // Check if we've loaded all posts
                if (currentPage * postsPerPage >= allPosts.size()) {
                    hasMorePosts = false;
                    System.out.println("All posts loaded. No more posts available.");
                }
            } else {
                hasMorePosts = false;
                System.out.println("Empty batch received. No more posts.");
            }

            hideLoadingIndicator();
            isLoading = false;
        });

        loadBatchTask.setOnFailed(e -> {
            System.err.println("Failed to load batch: " + loadBatchTask.getException().getMessage());
            loadBatchTask.getException().printStackTrace();
            hideLoadingIndicator();
            isLoading = false;
            hasMorePosts = false;
        });

        Thread thread = new Thread(loadBatchTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void displayPostsBatch(List<PostModel> posts) {
        System.out.println("Displaying batch of " + posts.size() + " posts");

        try {
            for (int i = 0; i < posts.size(); i++) {
                PostModel post = posts.get(i);

                // Calculate position based on current grid children (excluding loading indicator)
                int currentChildren = postContentGrid.getChildren().size() - 1; // -1 for loading indicator
                int row = Math.max(0, currentChildren); // Each post gets its own row

                System.out.println("Adding post " + i + " to row " + row);
                addPostToGrid(post, 0, row); // Column 0 since you have single column
            }
            System.out.println("Batch display completed");
        } catch (IOException e) {
            System.err.println("Error displaying posts batch: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addPostToGrid(PostModel post, int columns, int row) throws IOException {
        System.out.println("Adding post to grid at column " + columns + ", row " + row);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Main.class.getResource("pages/components/post.fxml"));
        VBox box = fxmlLoader.load();

        PostFrameController postFrameController = fxmlLoader.getController();
        postFrameController.setData(post);
        postFrameController.setContetnContainer(contentContainer);

        postContentGrid.add(box, columns, row);
        GridPane.setMargin(box, new Insets(10));

        System.out.println("Post added successfully to grid");
    }

    private void showLoadingIndicator() {
        if (loadingContainer != null) {
            Platform.runLater(() -> loadingContainer.setVisible(true));
        }
    }

    private void hideLoadingIndicator() {
        if (loadingContainer != null) {
            Platform.runLater(() -> loadingContainer.setVisible(false));
        }
    }

    // Method to refresh posts (clears current posts and reloads from beginning)
    public void refreshPosts() {
        postContentGrid.getChildren().clear();
        currentPage = 0;
        hasMorePosts = true;
        loadAllPostsData();
        loadNextBatch();
    }

    // Original method kept for compatibility
    public void loadAndDisplayPosts() {
        // This method is now deprecated in favor of lazy loading
        // But kept for backward compatibility
        refreshPosts();
    }

    @Override
    public List<PostModel> initData() {
        System.out.println("initData() called");
        PostModel post = new PostModel();
        List<PostModel> posts = post.listAll("beranda");
        System.out.println("initData() returning " + posts.size() + " posts");
        return posts;
    }
}