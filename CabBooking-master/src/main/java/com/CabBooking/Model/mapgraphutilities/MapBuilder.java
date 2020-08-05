package com.CabBooking.Model.mapgraphutilities;

import com.mongodb.BasicDBObject;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Class to build and store map in database
 */

public class MapBuilder {
    private static final String IP = "localhost";
    private static final String PORT = "27017";
    private static final String DATABASE = "CabBookingDB";
    public static void main(String[] args) {
        int vertices = 100;
        double edgeProbability = 0.2;
        EdgeWeightedGraph graph = new EdgeWeightedGraph(vertices);

        // Add edges between nodes with {@code edgeProbability} chance
        Random random = new Random();
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (i != j) {
                    if (random.nextDouble() < edgeProbability) {
                        int dist = random.nextInt(200) + 5;
                        graph.addEdge(new WeightedEdge(i, j, dist));
                    }
                }
            }
        }

        // Add a random direct path between disconnected vertices
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (i != j) {
                    ShortestPath sp = new ShortestPath(graph, i);
                    Stack<Integer> path = sp.getPathTo(j);
                    if (path.size() == 0) {
                        graph.addEdge(new WeightedEdge(i, j, random.nextInt(200) + 5));
                    }
                }
            }
        }

        // Create admin user to add graph, comment if admin user already exists
        // ---------- COMMENT STARTS ----------
        MongoClient mongo = MongoClients.create("mongodb://" + IP + ":" + PORT);
        MongoDatabase db = mongo.getDatabase(DATABASE);
        BasicDBObject createUserCmd = new BasicDBObject("createUser", "admin")
                .append("pwd", "admin")
                .append("roles",
                        Collections.singletonList(
                                new BasicDBObject(
                                        "role", "readWrite").append("db", DATABASE)
                        ));
        db.runCommand(createUserCmd);
        mongo.close();
        // ---------- COMMENT ENDS ----------

        // Login user with username and password
        String uri = "mongodb://" + "admin" + ":" + "admin" + "@" + IP + ":" + PORT + "/" + DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase(DATABASE);

        // Store graph in database through the GSON format
        MongoCollection<Document> graphs = database.getCollection("Graphs");
        Gson gson = new Gson();
        String json = gson.toJson(graph);
        Document doc = Document.parse(json);
        graphs.insertOne(doc);

        MongoCollection<Document> collection = database.getCollection("Drivers");

        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream("src/main/resources/DRIVER_DATA.json")));
            for (Object o : array) {
                JSONObject jsonObject = (JSONObject) o;
                doc = Document.parse(jsonObject.toJSONString());
                collection.insertOne(doc);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        System.out.println("GRAPH SUCCESSFULLY LOADED");
        mongoClient.close();
    }
}
