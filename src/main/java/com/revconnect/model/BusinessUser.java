package com.revconnect.model;

public class BusinessUser extends User {
    private String category;
    private String industry;
    private String detailedBio;
    private String businessAddress;
    private String contactInfo;
    private String businessHours;
    private String socialMediaLinks;
    private String endorsementLinks;

    public BusinessUser() {
        super();
        setUserType(UserType.BUSINESS);
    }

    public BusinessUser(String username, String email, String passwordHash, String fullName) {
        super(username, email, passwordHash, UserType.BUSINESS, fullName);
    }

    // Getters and Setters for business-specific fields
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDetailedBio() { return detailedBio; }
    public void setDetailedBio(String detailedBio) { this.detailedBio = detailedBio; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getSocialMediaLinks() { return socialMediaLinks; }
    public void setSocialMediaLinks(String socialMediaLinks) { this.socialMediaLinks = socialMediaLinks; }

    public String getEndorsementLinks() { return endorsementLinks; }
    public void setEndorsementLinks(String endorsementLinks) { this.endorsementLinks = endorsementLinks; }
}