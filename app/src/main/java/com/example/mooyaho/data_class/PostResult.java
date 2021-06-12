package com.example.mooyaho.data_class;

public class PostResult { // Post 정보를 서버에서 가져와야 할 때 해당 클래스 형식으로 가져온다

    private String postID;;
    private String userEmail;
    private String postTitle;
    private String postContent;
    private String postStartLatitude;
    private String postStartLongitude;
    private String postEndLatitude;
    private String postEndLongitude;

    public PostResult() {}

    public PostResult(String postID, String userEmail, String postTitle, String postContent,
                      String postStartLatitude, String postStartLongitude, String postEndLatitude, String postEndLongitude) {
        this.postID = postID;
        this.userEmail = userEmail;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postStartLatitude = postStartLatitude;
        this.postStartLongitude = postStartLongitude;
        this.postEndLatitude = postEndLatitude;
        this.postEndLongitude = postEndLongitude;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPostStartLatitude() {
        return postStartLatitude;
    }

    public void setPostStartLatitude(String postStartLatitude) {
        this.postStartLatitude = postStartLatitude;
    }

    public String getPostStartLongitude() {
        return postStartLongitude;
    }

    public void setPostStartLongitude(String postStartLongitude) {
        this.postStartLongitude = postStartLongitude;
    }

    public String getPostEndLatitude() {
        return postEndLatitude;
    }

    public void setPostEndLatitude(String postEndLatitude) {
        this.postEndLatitude = postEndLatitude;
    }

    public String getPostEndLongitude() {
        return postEndLongitude;
    }

    public void setPostEndLongitude(String postEndLongitude) {
        this.postEndLongitude = postEndLongitude;
    }

}