package com.kubernates.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

public interface DownloadServiceDao {
	public List<String> getFileName(String fileStringNames);

 
 }
