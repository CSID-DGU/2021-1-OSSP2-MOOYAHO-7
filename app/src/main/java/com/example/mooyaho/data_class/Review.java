package com.example.mooyaho.data_class;

public class Review {
    public String reviewReceiver;
    public String reviewSender;
    public String reviewContent;
    public String reviewRate;
    public String reviewDate;

    public Review(){

    }

    public Review(String reviewSender, String reviewReceiver, String reviewContent, String reviewDate, String reviewRate) {
        this.reviewReceiver = reviewReceiver;
        this.reviewSender = reviewSender;
        this.reviewContent = reviewContent;
        this.reviewRate = reviewRate;
        this.reviewDate = reviewDate;
    }

    public String getReviewReceiver() {
        return reviewReceiver;
    }

    public void setReviewReceiver(String reviewReceiver) {
        this.reviewReceiver = reviewReceiver;
    }

    public String getReviewSender() {
        return reviewSender;
    }

    public void setReviewSender(String reviewSender) {
        this.reviewSender = reviewSender;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getReviewRate() {
        return reviewRate;
    }

    public void setReviewRate(String reviewRate) {
        this.reviewRate = reviewRate;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

}

