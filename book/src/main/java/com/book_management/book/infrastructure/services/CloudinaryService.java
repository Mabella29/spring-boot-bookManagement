package com.book_management.book.infrastructure.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public Mono<String> uploadImage(FilePart filePart){
        //let's wrap everything in mono.from callable since this is blocking operation
        return Mono.fromCallable(()->{

            //let's create a temporary file to hold the upload image(file)
            //which is blocking
                    Path tempFile = Files.createTempFile("book-cover",".jpg");

                    //transfer incoming file to the temp file I just created
            //block because it's already on bounded elastic thread
                    // else cloudinary will try to upload empty or incomplete file
            filePart.transferTo(tempFile).block();

//           upload to cloudinary
            Map result = cloudinary.uploader().upload(tempFile.toFile(), ObjectUtils.asMap(
                    "folder","book_covers",
                    "resource_type", "image"

            ));

            //del temporary file
            Files.deleteIfExists(tempFile);

            //return the url cloudinary gives back
            return (String) result.get("secure_url");

        }).subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(url -> log.info("Image uploaded successfully to cloudinary:{}",url))
                .doOnError(error -> log.error("error uploading image",error));
    }
}
