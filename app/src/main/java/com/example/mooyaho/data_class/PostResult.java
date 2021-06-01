package com.example.mooyaho.data_class;

public class PostResult { // Post 정보를 서버에서 가져와야 할 때 해당 클래스 형식으로 가져온다

    private String postTitle;
    private String postContent;
    private String postLocation;
    private String postID;
    private String userEmail;

    public PostResult(String postID, String userEmail, String postTitle, String postContent, String postLocation) {
        this.postID = postID;
        this.userEmail = userEmail;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postLocation = postLocation;
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

    public String getLocation() {
        return postLocation;
    }

    public void setLocation(String location) {
        this.postLocation = location;
    }

    public void setPostID(String postID ) { this.postID = postID; }
    public String getPostID() { return postID; }

    public String getUserEmail(){return userEmail;}
}