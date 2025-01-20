package com.excelr.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.model.Items;
import com.excelr.model.Users;
import com.excelr.repository.ItemsRepo;
import com.excelr.repository.UsersRepository;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class ItemsService {

	@Autowired
	private UsersRepository userRepository;
	
    @Autowired
    private ItemsRepo repo;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    // S3 Client Initialization
    private final S3Client s3Client = S3Client.builder()
            .region(Region.EU_NORTH_1)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create("AKIAVFIWIUVJ4MKK5DBV", "4F9jPlPCp5vaVpTzyyrfG5rwGidhZOMOF1tq1RSp")
            ))
            .build();

    public List<Items> searchByCategory(String category) {
        return repo.findByCategory(category);
    }

    public Items saveItem(String name, String brand, String category, int cost, MultipartFile file) throws IOException {
        // Generate unique filename 
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            // Upload to S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3: " + e.getMessage());
        }

        // Construct the S3 file URL
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, Region.EU_NORTH_1.id(), fileName);
        System.out.println("File uploaded successfully. File URL: " + fileUrl);

        try {
            // Save item details to the database
            Items items = new Items();
            items.setName(name);
            items.setBrand(brand);
            items.setCategory(category);
            items.setCost(cost);
            items.setImage(fileUrl);

            System.out.println("Saving Item: name=" + name + ", brand=" + brand + ", category=" + category + ", cost=" + cost + ", image=" + fileUrl);

            return repo.save(items);
        } catch (Exception e) {
            throw new RuntimeException("Error saving item to the database: " + e.getMessage());
        }
    }
    
    public Users saveUser(Users user) {
    	return userRepository.save(user);
    }
}
