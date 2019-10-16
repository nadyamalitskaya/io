package com.nixsolutions.io;

import com.nixsolutions.ppp.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public class IOUtilsImpl implements IOUtils {

    public String gzip(String fileName, String folder)
            throws IllegalArgumentException {
        byte[] buffer = new byte[1024];
        String finalPath = folder + "New.gz";
        try {
            File file = new File(folder);
            if (!file.exists()) {
                throw new IllegalArgumentException();
            }
            GZIPOutputStream out = new GZIPOutputStream(
                    new FileOutputStream(finalPath));
            FileInputStream in = new FileInputStream(fileName);
            int totalSize;
            while ((totalSize = in.read(buffer)) > 0) {
                out.write(buffer, 0, totalSize);
            }
            in.close();
            out.finish();
            out.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        return finalPath;
    }

    public Integer searchText(String fileName, String mark)
            throws IllegalArgumentException {
        BufferedReader bufferedReader = null;
        StringBuilder lineFromFile = new StringBuilder();
        int amount = 0;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                throw new IllegalArgumentException();
            }
            String line;
            FileReader fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                lineFromFile.append(line);
            }
            System.out.println(lineFromFile);
            Pattern pattern = Pattern.compile(mark);
            Matcher matcher = pattern.matcher(lineFromFile);
            while (matcher.find())
                amount++;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return amount;
    }

    public String[] search(final File folder, String ext)
            throws IllegalArgumentException {
        if (!folder.exists()) {
            throw new IllegalArgumentException();
        }
        if (ext == null) {
            ext = "";
        }
        if (!ext.startsWith(".") && !ext.equals("")) {
            ext = "." + ext;
        }
        List<String> list = new ArrayList<>();
        final String finalExt = ext;
        File[] files = folder.listFiles(
                pathname -> pathname.getAbsolutePath().endsWith(finalExt));
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    list.add(folder.getAbsolutePath() + "\\" + file.getName());
                }
            }
        }
        File root = new File(folder.getAbsolutePath());
        File[] listOfFiles = root.listFiles();
        if (listOfFiles != null) {
            for (File f : listOfFiles) {
                if (f.isDirectory()) {
                    String[] stringArray = search(new File(f.getAbsolutePath()),
                            ext);
                    if (stringArray.length != 0) {
                        list.addAll(Arrays.asList(stringArray));
                    }
                }
            }
        }
        return list.toArray(new String[0]);
    }
}