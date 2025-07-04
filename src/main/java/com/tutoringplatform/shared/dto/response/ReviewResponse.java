package com.tutoringplatform.shared.dto.response;

import com.tutoringplatform.shared.dto.response.info.ReviewInfo;
import com.tutoringplatform.shared.dto.response.info.UserInfo;

import java.time.LocalDateTime;

public class ReviewResponse {
    private String id;
    private ReviewInfo review;
    private UserInfo studentInfo;
    private UserInfo tutorInfo;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ReviewInfo getReview() {
        return review;
    }

    public void setReview(ReviewInfo review) {
        this.review = review;
    }

    public UserInfo getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(UserInfo studentInfo) {
        this.studentInfo = studentInfo;
    }

    public UserInfo getTutorInfo() {
        return tutorInfo;
    }

    public void setTutorInfo(UserInfo tutorInfo) {
        this.tutorInfo = tutorInfo;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
