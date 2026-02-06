package com.revconnect.model;

public class CreatorUser extends User {
    private String category;
    private String detailedBio;
    private String contactInfo;
    private String socialMediaLinks;
    private String endorsementLinks;

    public CreatorUser() {
        super();
        setUserType(UserType.CREATOR);
    }

    public CreatorUser(String username, String email, String passwordHash, String fullName) {
        super(username, email, passwordHash, UserType.CREATOR, fullName);
    }

    // Getters and Setters for creator-specific fields
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDetailedBio() { return detailedBio; }
    public void setDetailedBio(String detailedBio) { this.detailedBio = detailedBio; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getSocialMediaLinks() { return socialMediaLinks; }
    public void setSocialMediaLinks(String socialMediaLinks) { this.socialMediaLinks = socialMediaLinks; }

    public String getEndorsementLinks() { return endorsementLinks; }
    public void setEndorsementLinks(String endorsementLinks) { this.endorsementLinks = endorsementLinks; }
}