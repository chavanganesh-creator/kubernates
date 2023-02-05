package com.kubernates.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.kubernates.dao.DownloadServiceDao;
import com.kubernates.serviceImpl.DownloadServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RestController
public class SearchFile {

	@Autowired
	DownloadServiceDao service;

	@RequestMapping(value = "/downloadZip", method = RequestMethod.POST)
	public ResponseEntity<String> downloadFile(HttpServletResponse response,@RequestBody String fileStringNames)
	{
 		if (fileStringNames.isEmpty() || fileStringNames.isBlank() || fileStringNames.length() == 0 || fileStringNames==null) {
			return new ResponseEntity<>("Inappropriate Input", HttpStatus.NOT_FOUND);
		}
 		
		List<String> fileNames = service.getFileName(fileStringNames);
		File checkFile = new File(DownloadServiceImpl.setFolderPath + fileStringNames);
		try {

            Paths.get(DownloadServiceImpl.setFolderPath + fileStringNames);

        } catch (InvalidPathException ex) {
          		return new ResponseEntity<>("Incorrect Path", HttpStatus.NOT_FOUND);
        }
		if (checkFile.isFile()) {

			response.setContentType("text/plain");

			Path pathFileLastName = Paths.get(fileStringNames);
			response.setHeader("Content-disposition",
					"attachment; filename=" + pathFileLastName.getFileName().toString()); // Used to name the download
			try {																				// file and its format
			File my_file = new File(fileNames.get(0));
			OutputStream out = response.getOutputStream();
			FileInputStream in = new FileInputStream(my_file);
			byte[] buffer = new byte[4096];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.flush();
			}catch(Exception e) {
 				return new ResponseEntity<>("Folder ZIP Creation Faild", HttpStatus.NOT_FOUND);
			}
			
 		} else if (checkFile.isDirectory()) {
			String sourceFile = DownloadServiceImpl.setFolderPath + fileStringNames;
			File directoryName = new File(sourceFile);  
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename="+directoryName.getAbsolutePath().substring(directoryName.getAbsolutePath().lastIndexOf("\\")+1)+".zip");
		
			
 			try {
			ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
			File fileToZip = new File(sourceFile);

			DownloadServiceImpl.zipFile(fileToZip, fileToZip.getName(), zipOut);
			zipOut.close();
 			 }catch(Exception e) {
 				return new ResponseEntity<>("Folder ZIP Creation Faild", HttpStatus.NOT_FOUND);
 			 }
			return new ResponseEntity<>("Directory Found", HttpStatus.OK);

		}  
		
		return new ResponseEntity<>("File Not Found", HttpStatus.NOT_FOUND);
 
 
 
 	}

}
