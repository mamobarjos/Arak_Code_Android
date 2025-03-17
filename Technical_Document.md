# Technical Document for Arak Project

## 1. Introduction

Arak is an innovative mobile application that combines **e-commerce and digital marketing**. The platform allows businesses to create their own stores and advertise products at competitive prices. Users can watch advertisements and earn rewards in return. Additionally, Arak features a dedicated store showcasing exclusive global products. The application is available on both **Android (Kotlin)** and **iOS (Swift)**.

## 2. Technology Stack

### 2.1 Mobile Application
- **Android Development:** Kotlin
- **iOS Development:** Swift
- **Minimum SDK:** 21 (Android)
- **Target SDK:** 34 (Android)
- **Development Environment:** Android Studio & Xcode
- **Dependency Management:** Gradle (Android), CocoaPods (iOS)
- **Third-Party Integrations:** Google AdMob (Rewarded Ads), Electronic Wallet Payment System

### 2.2 Backend and Database
- **Backend Hosting:** Vultr
- **Database:** MySQL
- **Database Management:** Prisma ORM
- **API Development:** RESTful APIs
- **Authentication:** Token-based authentication (JWT)
- **Server-Side Language:** Node.js (Express.js framework)

## 3. Database Architecture

### 3.1 Core Tables

#### **Users Table**
```sql
CREATE TABLE Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **Wallets Table** (For in-app payments)
```sql
CREATE TABLE Wallets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    balance DECIMAL(13, 4),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);
```

#### **AdsData Table (Rewarded Ads Tracking)**
```sql
CREATE TABLE AdsData (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    ad_id VARCHAR(255),
    reward_amount DECIMAL(10, 2),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);
```

#### **Stores Table (For Business Owners)**
```sql
CREATE TABLE Stores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT,
    name VARCHAR(255),
    description TEXT,
    rating DECIMAL(2,1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES Users(id)
);
```

#### **Arak Store Table (Exclusive Global Products)**
```sql
CREATE TABLE ArakStore (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    stock INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 4. API Endpoints

### 4.1 User Wallet Management

#### **Update Balance**
- **Endpoint:** `/updateBalance`
- **Method:** `POST`
- **Parameters:**
  - `user_id` (INT)
  - `amount` (DECIMAL)
- **Description:** Updates the user balance after completing a rewarded ad action.

#### **Retrieve Balance**
- **Endpoint:** `/getBalance`
- **Method:** `GET`
- **Parameters:**
  - `user_id` (INT)
- **Description:** Returns the current balance of a given user.

### 4.2 Store Management

#### **Create a Store**
- **Endpoint:** `/createStore`
- **Method:** `POST`
- **Parameters:**
  - `owner_id` (INT)
  - `name` (VARCHAR)
  - `description` (TEXT)
- **Description:** Allows a business owner to create a new store.

#### **List Stores**
- **Endpoint:** `/listStores`
- **Method:** `GET`
- **Description:** Retrieves all active stores on the platform.

#### **List Arak Store Products**
- **Endpoint:** `/arakStoreProducts`
- **Method:** `GET`
- **Description:** Retrieves all available products from the Arak global store.

### 4.3 Advertising System

#### **Create a Featured Ad**
- **Endpoint:** `/createFeaturedAd`
- **Method:** `POST`
- **Parameters:**
  - `store_id` (INT)
  - `ad_details` (TEXT)
  - `price` (DECIMAL)
- **Description:** Allows stores to create premium advertisements.

#### **List Featured Ads**
- **Endpoint:** `/listFeaturedAds`
- **Method:** `GET`
- **Description:** Retrieves all currently active featured ads.

## 5. Core Workflows

### 5.1 Rewarded Ad Flow
1. User watches an advertisement.
2. The application calls `updateBalance()` API.
3. The balance is updated and reflected in the UI via `updateBalanceUI()`.

### 5.2 Store Creation and Product Listing
1. A business owner creates a store via the `/createStore` API.
2. The store is listed in the marketplace.
3. Products can be added and managed through the store dashboard.
4. Users browse stores and purchase products.

### 5.3 Premium Advertising Workflow
1. A store owner selects a **featured ad package**.
2. The owner submits the ad details via `/createFeaturedAd` API.
3. The advertisement is published in the premium ad section.
4. Users see the advertisement and interact with promoted products.

## 6. Future Enhancements

1. **Push Notifications for Low Stock Alerts**: Notify store owners when stock is running low.
2. **Subscription Plans for Store Owners**: Offering premium advertising and promotional packages.
3. **Multi-Payment Gateway Support**: Expanding beyond e-wallets to include PayPal, Stripe, and credit cards.
4. **AI-based Ad Targeting**: Improving ad efficiency using machine learning algorithms.

---

This document provides a comprehensive technical overview of the Arak project, covering all implemented and planned features. Let me know if any additional details are required.

