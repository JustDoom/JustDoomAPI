package com.imjustdoom.justdoomapi.service;

import com.imjustdoom.justdoomapi.config.APIConfig;
import com.imjustdoom.justdoomapi.model.Update;
import com.imjustdoom.justdoomapi.repository.UpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UpdateRepository updateRepository;

    private final APIConfig config;

    public String getDownload(int updateId) {
        //Path path = Path.of("/updates/" + updateId + ".jar");
        //if (!Files.exists(path)) throw new RestException(RestErrorCode.DOWNLOAD_NOT_FOUND, "File not found");
        //File file = this.updateRepository.findById(updateId).orElseThrow(() -> new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND));
        //if (file.getDownloadLink() != null) throw new RestException(RestErrorCode.WRONG_FILE_TYPE, "File is provided via an external URL");

        Optional<Update> optionalUpdate = updateRepository.findById(updateId);
        if (optionalUpdate.isEmpty()) return null;//throw new RestException(RestErrorCode.RESOURCE_UPDATE_NOT_FOUND);
        Update update = optionalUpdate.get();

        return config.getBackendUrl() + "/" + updateId + "/" + update.getFilename();

        //return new FileReturn(path.toFile(), file.getFilename());
    }

    public record FileReturn(java.io.File file, String realName) {}
}
