package com.thuctap.bookmanage.service;

import org.springframework.stereotype.Service;

import java.io.File;
@Service
public class DirService {
    public void deleteDir(File file) {
        if (file.isDirectory()) {
            String[] files = file.list();
            for (String child : files) {
                File childDir = new File(file, child);
                if (childDir.isDirectory()) {
                    deleteDir(childDir);
                } else {
                    childDir.delete();
                }
            }
            if (file.list().length == 0) {
                file.delete();
            }

        } else {
            file.delete();
        }
    }
}
