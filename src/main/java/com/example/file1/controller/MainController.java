package com.example.file1.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping("/file")
public class MainController 
{
	
	private static final String UPLOAD_DIR = "C:\\Java";
	
	@ResponseBody
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file)
	{
		try
		{
			File directory = new File(UPLOAD_DIR);
			if(!directory.exists())
			{
				directory.mkdirs();
			}
			String fileName = file.getOriginalFilename();
			Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
			
			Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/file/download/").path(fileName).toUriString();
			return ResponseEntity.ok("File Uploaded Successfully. Download Url: " + fileDownloadUri);
		}catch(IOException ex) {
			return ResponseEntity.status(500).body("could not upload the file: " + ex.getMessage());
		}
	}
	
	@GetMapping("/download/{fileName}")
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName)
	{
		try {
			Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
			File file = filePath.toFile();
			
			if(file.exists())
			{
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.setContentDispositionFormData("attachment", fileName);
				
				return ResponseEntity.ok().headers(headers).contentLength(file.length()).body(new FileSystemResource(file));
			}else
			{
				return ResponseEntity.notFound().build();
			}
		}catch(Exception ex)
		{
			return ResponseEntity.status(500).body("Failed to download the file");
		}
	}
}
