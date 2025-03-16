package application.aicomic.repositories;

import application.aicomic.dataAccess.CreateMomoRequest;
import application.aicomic.dataAccess.CreateMomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(name = "momo", url = "${momo.end-point}")
public interface MomoRepository {
    @PostMapping("/create")
    CreateMomoResponse createMomoQR(@RequestBody CreateMomoRequest createMomoRequest);
}
