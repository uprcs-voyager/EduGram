package app.edugram.controllers;

// Abstract base class for controllers.
public abstract class BaseController<R, T> {

    // Repository instance
    protected R repository;

    // Initializes data.
    public abstract T initData();

    // Handles errors.
    public void handleError(Throwable error) {
        System.err.println("Error: " + error.getMessage());
        error.printStackTrace();
    }

    // Constructor with repository.
    public BaseController(R repository) {
        this.repository = repository;
    }

    // Default constructor.
    public BaseController() {
        // Repository can be set later
    }

    // Gets the repository.
    public R getRepository() {
        return repository;
    }

    // Sets the repository.
    public void setRepository(R repository) {
        this.repository = repository;
    }
}
