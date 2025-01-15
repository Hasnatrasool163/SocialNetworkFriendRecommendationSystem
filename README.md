
# Social Network Friend Recommendation System ✨

A **Social Network Friend Recommendation System** is an exciting project that leverages graph theory concepts to simulate real-world social networks. This project enables students to explore data structures and algorithms while building a feature-rich application similar to popular social media platforms.

---

## 🔍 Project Overview

In this project:

- Each user is represented as a **node (vertex)** in a graph.
- Friendships between users are represented as **edges** connecting the nodes.
- By constructing and traversing this graph, the system provides valuable social insights, such as identifying mutual friends and recommending new connections.

This mimics the **friend suggestion features** of popular social media platforms.

---

## 🔧 Key Features and Functions

### Core Features
1. 🧩 **Add Friends**  
   Enable users to form new friendships by creating edges between their nodes in the graph.

2. ❌ **Delete Friends**  
   Allow users to break friendships by removing edges from the graph.

3. 🧳 **Display Mutual Friends**  
   Help users discover mutual friends by analyzing shared connections in the graph.

4. 🔍 **Friend Recommendations**  
   Suggest new friends by identifying friends-of-friends, as they are more likely to have shared interests or connections.

5. 🖊️ **User Posting System**  
   Create a real-time activity feed:  
   - Users can make posts, stored in First-Come, First-Served (FCFS) order.  
   - Posts are displayed sequentially, simulating a timeline or feed feature.

### Additional Features
- ✔ **Search/Find Friends**  
- ✔ **Suggest Potential Friends Based on Common Connections & Attributes**  
- ✔ **Setup Security Question**  
- ✔ **Direct Messaging**  
- ✔ **Visualize Network**  
- ✔ **Admin Dashboard**  
- ✔ **Badges & Achievements**  
- ✔ **User Profile Avatars**  
- ✔ **Update Profile**

---

## 🛠️ Setup Guide

### 1. Prerequisites
- **Java Development Kit (JDK)**: Ensure JDK 23+ is installed.
- **MariaDB Database**: Ensure MariaDB is installed and running.
- **JavaFX**: Integrated with Zulu JDK or added separately.
- **IDE**: Use IntelliJ IDEA, Eclipse, or your preferred IDE with Maven support.

### 2. Database Configuration
1. Create a MariaDB database named `social_network`:
   ```sql
   CREATE DATABASE social_network;
   ```
2. Create necessary tables for users, friendships, posts, etc. Refer to the project SQL schema (included in the full project).

3. Update database credentials in the `DBConnection` class:
   ```java
   private static final String URL = "jdbc:mariadb://localhost:3306/social_network";
   private static final String USER = "your_user";
   private static final String PASSWORD = "your_password";
   ```

### 3. Import the Project
1. Clone or download the project:
   ```bash
   git clone https://github.com/your-repository/social-network-recommendation-system.git
   ```
2. Open the project in your IDE.
3. Build and resolve Maven dependencies.

### 4. Run the Application
1. Start the MariaDB server.
2. Run the main JavaFX application file.
3. Enjoy exploring the Social Network Friend Recommendation System!

---

## 📸 Screenshots

### Login Page  
![Login](https://github.com/user-attachments/assets/08a04005-54ec-42f7-b917-71ed0bc515a1)  

### Profile Page  
![Profile](https://github.com/user-attachments/assets/320187b3-d29e-40d2-965a-997185d83e44)  

### Dashboard  
![DashboardPage](https://github.com/user-attachments/assets/5cf3989b-4808-452d-afc3-476860c5cd3b)  

### Splash Screen  
![Splash](https://github.com/user-attachments/assets/b81f3323-43a5-4581-aca2-b6dfad395b04)  

---

## 🎥 Demo Video

Check out the [demo video here](https://www.linkedin.com/posts/hasnatrasool163_socialnetwork-datastructures-graphtheory-activity-7273927434654121984-e6Zo?utm_source=share&utm_medium=member_android).

---

## 🔗 Contact

For the complete project with JavaFX GUI frontend, contact **Hasnat Rasool**:  
📧 Email: [hasnatrasool163@gmail.com](mailto:hasnatrasool163@gmail.com)

---
