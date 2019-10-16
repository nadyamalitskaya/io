package com.nixsolutions.io;

import com.nixsolutions.ppp.io.NIOUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class NIOUtilsImpl implements NIOUtils {

    public String searchText(Path file, int offset)
            throws IllegalArgumentException {
        if (!file.toFile().exists()) {
            throw new IllegalArgumentException();
        }
        final List<String> result = new ArrayList();
        try {
            Files.lines(file).forEach(line -> {
                List<String> characterList = Arrays.asList(line.split(" "));
                System.out.println(characterList);
                processMoveResult(result, characterList, line, offset,
                        countSpaces(line));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.get(result.size() - 1);
    }

    private int countSpaces(String line) {
        int charCounter = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') {
                charCounter += 1;
            }
        }
        return charCounter;
    }

    private void processMoveResult(List<String> result,
            List<String> characterList, String line, int offset,
            int spaceCount) {
        int position = moveInLine(spaceCount, line, offset);
        String value = characterList.get(position);
        if (isNumerical(value)) {
            result.add(value);
            processMoveResult(result, characterList, line,
                    offset + Integer.parseInt(value), spaceCount);
        }
        if (isLetter(value)) {
            result.add(value);
        }
    }

    private boolean isNumerical(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isLetter(String letter) {
        try {
            Integer.parseInt(letter);
            return false;
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    private int moveInLine(int spaceCount, String rawLine, int offset) {
        String cutString = rawLine.substring(offset);
        return spaceCount - countSpaces(cutString);
    }

    public String[] search(Path folder, String ext)
            throws IllegalArgumentException {
        if (ext == null) {
            ext = "";
        }
        if (!ext.startsWith(".") && !ext.equals("")) {
            ext = "." + ext;
        }
        if (!folder.toFile().exists()) {
            throw new IllegalArgumentException();
        }
        List<String> listOfPaths = new ArrayList<>();
        try {
            String finalExt = ext;
            Files.walkFileTree(folder, EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                    Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                        @Override public FileVisitResult preVisitDirectory(
                                Path dir, BasicFileAttributes attrs) {
                            try (DirectoryStream<Path> stream = Files
                                    .newDirectoryStream(dir, "*" + finalExt)) {
                                for (Path path : stream) {
                                    if (!path.toFile().isDirectory()) {
                                        listOfPaths
                                                .add(dir.toAbsolutePath() + "\\"
                                                        + path.getFileName());
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return listOfPaths.toArray(new String[0]);
    }
}