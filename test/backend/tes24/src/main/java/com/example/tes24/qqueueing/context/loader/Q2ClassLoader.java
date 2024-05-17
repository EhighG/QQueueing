package com.example.tes24.qqueueing.context.loader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.*;

public final class Q2ClassLoader {
    private List<Class<?>> classList;

    public List<Class<?>> getLoadedClasses() {
        if (classList == null) {
            try {
                classList = loadClass();
            } catch (ClassNotFoundException e) {
                System.err.println(e);
            }
        }

        return classList;
    }

    private List<Class<?>> loadClass() throws ClassNotFoundException {
        List<Class<?>> list = new ArrayList<>();

        File rootDirectory = new File(ClassLoader.getSystemClassLoader().getResource("./com/example/tes24/qqueueing").getFile());

        File[] classFiles = ForkJoinPool.commonPool().submit(new ExtractingClassFileRecursiveTask(rootDirectory)).join();

        for (File classFile : classFiles) {
            StringTokenizer stringTokenizer = new StringTokenizer(classFile.getPath(), "\\");
            while (stringTokenizer.hasMoreTokens()) {
                if (stringTokenizer.nextToken().equals("main")) break;
            }

            StringBuilder stringBuilder = new StringBuilder(stringTokenizer.nextToken());
            while (stringTokenizer.hasMoreTokens()) {
                stringBuilder.append(".");
                stringBuilder.append(stringTokenizer.nextToken());
            }
            stringBuilder.delete(stringBuilder.length() - ".class".length(), stringBuilder.length());

            list.add(ClassLoader.getSystemClassLoader().loadClass(stringBuilder.toString()));
        }

        return Collections.unmodifiableList(list);
    }

    static class ExtractingClassFileRecursiveTask extends RecursiveTask<File[]> {
        private static final FileFilter classFileFilter = pathname -> pathname.isFile() && pathname.getName().endsWith(".class");
        private static final FileFilter directoryFilter = File::isDirectory;
        private final File[] files;

        ExtractingClassFileRecursiveTask(File... files) {
            this.files = files;
        }

        @Override
        protected File[] compute() {
            List<File> fileList = new ArrayList<>();
            List<ForkJoinTask<File[]>> subtaskList = new ArrayList<>();

            for (File file : files) {
                if (file.isDirectory()) {
                    for (File classFile : file.listFiles(classFileFilter)) {
                        fileList.add(classFile);
                    }
                    for (File directory : file.listFiles(directoryFilter)) {
                        subtaskList.add(new ExtractingClassFileRecursiveTask(directory).fork());
                    }
                }
                if (file.isFile()) {
                    fileList.add(file);
                }
            }

            for (ForkJoinTask<File[]> subtask : subtaskList) {
                for (File result : subtask.join()) {
                    fileList.add(result);
                }
            }

            return fileList.stream().toArray(File[]::new);
        }
    }
}
