package com.kubernates.serviceImpl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.kubernates.dao.DownloadServiceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DownloadServiceImpl implements DownloadServiceDao {
	public static final String setFolderPath = "A:\\demo";
	

//	/home/ubuntu/jars
	@Override
	public List<String> getFileName(String fileStringNames) {
		List<String> fileList = new ArrayList<>();
		File fileName = new File(setFolderPath + fileStringNames);
		if (fileName.isFile()) {
			fileList.add(setFolderPath + fileStringNames);
			return fileList;
		} else if (fileName.isDirectory()) {

			for (String m : listFilesUsingJavaIO(setFolderPath + fileStringNames + "\\")) {
				fileList.add(setFolderPath + fileStringNames + "\\" + m);
			}
 			return fileList;
		} else
			System.out.println("File Not Found ");

		return fileList;
	}
	public List<String> listFilesUsingJavaIO(String dir) {
		return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory()).map(File::getName)
				.collect(Collectors.toList());

	}
	public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[4096];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
        
    }
	
}
