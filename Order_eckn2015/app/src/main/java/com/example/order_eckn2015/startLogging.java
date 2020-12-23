package com.example.order_eckn2015;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class startLogging {

    static String globalLogName = "logcat-" + getCurrentDate() + "-.txt";

    public static void startLog() {

        if (isExternalStorageWritable()) {
            //Delete old logs
            deleteOldLogs();

            File logDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/ElenaLogs/");
            File logFile = new File(logDirectory, globalLogName);


            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }
            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
                Log.d(globalLogName, "greaterFileSize" + (logFile.length() / 1024));
                if (logFile.length() / 1024 > 10) {
                    String dataToWrite = updateFileSize(logFile);
                    FileWriter fw = new FileWriter(logFile.getAbsoluteFile());
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(dataToWrite);
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(globalLogName, "greaterFileSize" + (logFile.length() / 1024));
        }
    }

    private static String updateFileSize(File logFile) throws IOException {
        FileInputStream fis = new FileInputStream(logFile);
        byte[] byteArray = new byte[(int) logFile.length()];
        fis.read(byteArray);
        String data = new String(byteArray);
        String[] stringArray = data.split("\n");
        List<String> stringList = Arrays.asList(stringArray);
        Log.d(globalLogName, "StringArray" + stringArray.length);
        String updatedData = data.substring(data.indexOf(stringList.get(50)));
        String[] updatedStringArray = updatedData.split("\n");
        Log.d(globalLogName, "updatedStringArray" + updatedStringArray.length);
        return updatedData;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        //Return the date in this format: 20200515, that is a larger number every day, more easy to delete old logs
        return formatter.format(date).replace("/", "");
    }

    public static String getCurrentLogPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/ElenaLogs/" + globalLogName;
    }

    private static String[] getFilesInDir() {
        //Get all the files in the ElenaLogs dir
        String[] fileNames;
        File f = new File(Environment.getExternalStorageDirectory().getPath() + "/ElenaLogs");
        fileNames = f.list();
        return fileNames;
    }

    private static boolean deleteFile(String dateToDelete) {

        String globalPath = Environment.getExternalStorageDirectory().getPath() + "/ElenaLogs/";

        String fileToDelete = "logcat-" + dateToDelete + "-.txt";
        String pathFile = globalPath + fileToDelete;

        String zipToDelete = "logcat-" + dateToDelete + "-.zip";
        String pathZip = globalPath + zipToDelete;

        File filetoDelete = new File(pathFile);
        File ziptoDelete = new File(pathZip);

        ziptoDelete.delete();

        return filetoDelete.delete();
    }

    private static void deleteOldLogs() {

        String date = getCurrentDate();
        int dateNumber = Integer.parseInt(date);
        String[] allLogs = getFilesInDir();

        try {
            for (String logFileName : allLogs) {

                int logDate = Integer.parseInt(logFileName.split("-")[1]);

                if (logDate < dateNumber) {
                    //Old file, delete
                    deleteFile(String.valueOf(logDate));
                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static String getPathForEmail() {

        String zippedFilename = globalLogName.replace(".txt", ".zip");
        String zippedFilePath = Environment.getExternalStorageDirectory().getPath() + "/ElenaLogs/" + zippedFilename;

        //Zip the current log file
        if (zipFileAtPath(getCurrentLogPath(), zippedFilePath)) {
            return zippedFilePath;
        } else {
            return getCurrentLogPath();
        }

    }

    /**
     * Zips the logs
     **/

    public static boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte[] data = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static void zipSubFolder(ZipOutputStream out, File folder,
                                     int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte[] data = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    /*
     * gets the last path component
     *
     * Example: getLastPathComponent("downloads/example/fileToZip");
     * Result: "fileToZip"
     */
    private static String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

}
