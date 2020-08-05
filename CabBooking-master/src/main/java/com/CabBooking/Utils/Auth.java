package com.CabBooking.Utils;

import com.CabBooking.Model.Customer;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class Auth {

    public static void registerUser(String username, String password, String name, String mobile) {
        MongoClient mongoClient = MongoClients.create("mongodb://" + CommonConstants.IP + ":" + CommonConstants.PORT);
        MongoDatabase db = mongoClient.getDatabase(CommonConstants.DATABASE);
        BasicDBObject createUserCmd = new BasicDBObject("createUser", username)
                .append("pwd", password)
                .append("roles",
                        Collections.singletonList(
                                new BasicDBObject(
                                        "role", "readWrite").append("db", CommonConstants.DATABASE)
                        ));
        db.runCommand(createUserCmd);
        MongoCollection<Document> users = db.getCollection("Users");

        Customer customer = new Customer(username, name, mobile);

        // Deserialize object to json string
        Gson gson = new Gson();
        String json = gson.toJson(customer);

        // Parse to bson document and insert
        users.insertOne(Document.parse("{ username: " + getJSONString(username) + ", password: " + getJSONString(password) +
                ", userInfo: " + json + "}"));
        mongoClient.close();
    }

    public static MongoDatabase loadUserData(String username, char[] password) {
        String uri = "mongodb://" + username + ":" + new String(password) +
                "@" + CommonConstants.IP + ":" + CommonConstants.PORT + "/" + CommonConstants.DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        return mongoClient.getDatabase(CommonConstants.DATABASE);
    }

    // This method is used only when user is logged in
    public static MongoDatabase getUsersDatabase() {
        String uri = "mongodb://" + CommonConstants.IP + ":" + CommonConstants.PORT + "/" + CommonConstants.DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        return mongoClient.getDatabase(CommonConstants.DATABASE);
    }

    public static void updateUserInfo(Bson filter, Bson update, String username, String password) {
        String uri = "mongodb://" + username + ":" + password + "@" + CommonConstants.IP + ":" + CommonConstants.PORT + "/" + CommonConstants.DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase db = mongoClient.getDatabase(CommonConstants.DATABASE);
        MongoCollection<Document> users = db.getCollection("Users");
        users.updateOne(filter, update);
        mongoClient.close();
    }

    public static void updateUserPassword(String username, String oldPassword, String newPassword) {
        String uri = "mongodb://" + username + ":" + oldPassword + "@" + CommonConstants.IP + ":" + CommonConstants.PORT + "/" + CommonConstants.DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase db = mongoClient.getDatabase(CommonConstants.DATABASE);
        BasicDBObject modifyPasswordCmd = new BasicDBObject("updateUser", username)
                .append("pwd", newPassword);
        db.runCommand(modifyPasswordCmd);
        mongoClient.close();
    }

    public static Document getUserInfo(String username, MongoCollection<Document> database) {
        return (Document) Objects.requireNonNull(database.find(eq("username", username)).first()).get("userInfo");
    }

    public static Document getCurrentDriver(String name, String mobile, int vehicleID) {
        MongoClient mongoClient = MongoClients.create("mongodb://" + CommonConstants.IP + ":" + CommonConstants.PORT);
        MongoDatabase db = mongoClient.getDatabase(CommonConstants.DATABASE);
        MongoCollection<Document> drivers = db.getCollection("Drivers");
        return drivers.find(and(eq("name", name), eq("mobile", mobile),
                eq("vehicleID", vehicleID))).first();
    }

    public static FindIterable<Document> getDriverAtLocation(int location) {
        MongoClient mongoClient = MongoClients.create("mongodb://" + CommonConstants.IP + ":" + CommonConstants.PORT);
        MongoDatabase db = mongoClient.getDatabase(CommonConstants.DATABASE);
        MongoCollection<Document> driverCollection = db.getCollection("Drivers");
        return driverCollection.find(eq("location", location));
    }

    public static void updateDriverStats(Bson filter, Bson update) {
        MongoClient mongoClient = MongoClients.create("mongodb://" + CommonConstants.IP + ":" + CommonConstants.PORT);
        MongoDatabase db = mongoClient.getDatabase(CommonConstants.DATABASE);
        MongoCollection<Document> drivers = db.getCollection("Drivers");
        drivers.updateOne(filter, update);
    }

    public static void updateCustomerWallet(Customer c) {
        MongoDatabase users = getUsersDatabase();
        MongoCollection<Document> collection = users.getCollection("Users");
        collection.updateOne(eq("username", c.getUsername()), combine(set("userInfo.wallet", c.getMoney())));
    }


    public static void loginUser(Customer customer, String username, char[] password) {
        String uri = "mongodb://" + username + ":" + new String(password) + "@" + CommonConstants.IP + ":" + CommonConstants.PORT + "/" + CommonConstants.DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase db1 = mongoClient.getDatabase(CommonConstants.DATABASE);
        MongoCollection<Document> users = db1.getCollection("Users");
        customer.changeLoggedInStatus(true);
        users.updateOne(eq("username", username), combine(set("userInfo.loggedInStatus", true)));
        mongoClient.close();
    }

    public static void logoutUser(Customer customer) {
        String uri = "mongodb://" + CommonConstants.IP + ":" + CommonConstants.PORT + "/" + CommonConstants.DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase db1 = mongoClient.getDatabase(CommonConstants.DATABASE);
        MongoCollection<Document> users = db1.getCollection("Users");
        users.updateOne(eq("username", customer.getUsername()), combine(set("userInfo.loggedInStatus", false)));
        mongoClient.close();
    }

    public static boolean mobileMatchesFormat(String mobile) {
        return Pattern.matches("^([+]\\d{2}([ ])?)?\\d{10}$", mobile); // <start>optional((+2digits)optional( ))10digits<end>
    }

    public static boolean passwordMismatchesFormat(String password) {
        // c&i == check and ignore
        // <start>(c&i any character followed by lowercase)(c&i any character followed by uppercase)(c&i any character followed by digit)[at least 8 characters]<end>
        return !Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[@$!%*?&.a-zA-Z\\d]{8,16}$", password);
    }

    // Utility function to use to build JSON
    private static String getJSONString(String s) {
        return ("'" + s + "'");
    }
}
