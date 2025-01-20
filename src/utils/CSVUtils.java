package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {

<<<<<<< HEAD
    // Reads the CSV file and returns a list of String arrays (including the header)
    public static List<String[]> readCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
=======

    // Type arrayList string, and it belongs to the class mean no need to create
    // instance ot the class to use
    public static List<String[]> readCSV(String filePath){
        // holds array of strings , in this cast we can use List interface as the reference
//        ArrayList<String []> data =new ArrayList<>();
        List<String[]> data =new ArrayList<>();
        // No need finally to close the file this called try-catch with resources
        try(BufferedReader br =new BufferedReader(new FileReader(filePath))){
>>>>>>> a75fa5b392e6f64f7fa293c022822d52505ec551
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] fields = line.split(",");
                data.add(fields); // Add array of strings (including the header)
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
        return data;
    }

    // Appends data to the CSV file
    public static void writeCSV(String filePath, List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
<<<<<<< HEAD
            throw new RuntimeException("Error writing to CSV file: " + e.getMessage(), e);
=======
            throw new RuntimeException(e);
        }
    }
    public static void updateCSV(String filePath,List<String []> updatedData,String Header){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))){
            List<String []> data =readCSV(filePath);
            bw.write(Header);
            bw.newLine();
            for(String[] row:updatedData){
                bw.write(String.join(",",row));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
>>>>>>> a75fa5b392e6f64f7fa293c022822d52505ec551
        }
    }

    // Overwrites the CSV file with updated data (preserves the header)
    public static void updateCSV(String filePath, List<String[]> updatedData) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            // Read the original data to get the header
            List<String[]> originalData = readCSV(filePath);
            if (!originalData.isEmpty()) {
                // Write the header row
                String[] header = originalData.get(0); // First row is the header
                bw.write(String.join(",", header));
                bw.newLine();
            }

            // Write the updated data
            for (String[] row : updatedData) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error updating CSV file: " + e.getMessage(), e);
        }
    }
}