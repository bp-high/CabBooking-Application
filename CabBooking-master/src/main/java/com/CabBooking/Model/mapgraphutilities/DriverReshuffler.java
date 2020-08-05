package com.CabBooking.Model.mapgraphutilities;

import com.CabBooking.Utils.CommonConstants;
import com.google.gson.Gson;
import com.mongodb.client.*;

import org.bson.Document;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Code for driver reshuffling
 */
public class DriverReshuffler implements Runnable {
    private int from;
    public DriverReshuffler(int f) {
        from = f;
    }
    @Override
    public void run() {
        String uri = "mongodb://" + CommonConstants.IP + ":" + CommonConstants.PORT + "/" + CommonConstants.DATABASE;
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase cabBookingDB = mongoClient.getDatabase(CommonConstants.DATABASE);
        MongoCollection<Document> driverCollection = cabBookingDB.getCollection("Drivers");

        // If no driver exists at this location
        if (driverCollection.countDocuments(eq("location", from)) == 0) {
            // Get location with maximum drivers
            long maximum = -1;
            int maxPos = 0;
            for (int i = 0; i < CommonConstants.NUMBER_DESTINATIONS; i++) {
                long current = driverCollection.countDocuments(eq("location", i));
                if (current > maximum) {
                    maxPos = i;
                    maximum = current;
                }
            }
            FindIterable<Document> driversToTransfer = driverCollection.find(eq("location", maxPos));

            // Move half of the drivers to the empty location
            long i = 0, limit = maximum / 2;
            for (Document driver : driversToTransfer) {
                Driver driverToTransfer = (new Gson()).fromJson(driver.toJson(), Driver.class);
                driverToTransfer.setLocation(from);
                driverCollection.updateOne(and(eq("name", driverToTransfer.getName()), eq("mobile", driverToTransfer.getMobile()), eq("vehicleID",
                    Integer.parseInt(driverToTransfer.getVehicleID()))), combine(set("location", from)));
                i++;
                if (i == limit) {
                    break;
                }
            }
        }
        mongoClient.close();
    }
}
