package com.arakadds.arak.model.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.arakadds.arak.model.new_mapping_refactore.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class RegistrationEntity extends BaseResponse implements Parcelable {

    @SerializedName("fullname")
    private String fullname;

/*    @SerializedName("email")
    private String email;*/

    @SerializedName("company_name")
    private String companyName;

    @SerializedName("phone_no")
    private String phone_no;

    @SerializedName("password")
    private String password;

    @SerializedName("birthdate")
    private String birthDate;

    @SerializedName("password_confirmation")
    private String passwordConfirmation;

    @SerializedName("gender")
    private String gender;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("otp_code")
    private String otpCode;

    @SerializedName("social_token")
    private String socialToken;

    @SerializedName("has_wallet")
    private int hasWallet;

    public RegistrationEntity() {
    }


    protected RegistrationEntity(Parcel in) {
        super(in);
        fullname = in.readString();
        //email = in.readString();
        companyName = in.readString();
        phone_no = in.readString();
        password = in.readString();
        birthDate = in.readString();
        passwordConfirmation = in.readString();
        gender = in.readString();
        city = in.readString();
        country = in.readString();
        otpCode = in.readString();
        socialToken = in.readString();
        hasWallet = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(fullname);
        //dest.writeString(email);
        dest.writeString(companyName);
        dest.writeString(phone_no);
        dest.writeString(password);
        dest.writeString(birthDate);
        dest.writeString(passwordConfirmation);
        dest.writeString(gender);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(otpCode);
        dest.writeString(socialToken);
        dest.writeInt(hasWallet);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RegistrationEntity> CREATOR = new Creator<RegistrationEntity>() {
        @Override
        public RegistrationEntity createFromParcel(Parcel in) {
            return new RegistrationEntity(in);
        }

        @Override
        public RegistrationEntity[] newArray(int size) {
            return new RegistrationEntity[size];
        }
    };

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

/*
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
*/

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSocialToken() {
        return socialToken;
    }

    public void setSocialToken(String socialToken) {
        this.socialToken = socialToken;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getHasWallet() {
        return hasWallet;
    }

    public void setHasWallet(int hasWallet) {
        this.hasWallet = hasWallet;
    }

    @Override
    public String toString() {
        return "RegistrationEntity{" +
                "fullname='" + fullname + '\'' +
              //  ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", phone_no='" + phone_no + '\'' +
                ", password='" + password + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", passwordConfirmation='" + passwordConfirmation + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", otpCode='" + otpCode + '\'' +
                ", socialToken='" + socialToken + '\'' +
                ", hasWallet=" + hasWallet +
                '}';
    }
}
