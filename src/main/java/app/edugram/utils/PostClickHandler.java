package app.edugram.utils;

import app.edugram.models.PostModel;

public interface PostClickHandler {
    void onPostClicked(PostModel post);
    void onPostBackClicked(PostModel post);
}