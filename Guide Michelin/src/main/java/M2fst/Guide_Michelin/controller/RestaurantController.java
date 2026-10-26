package M2fst.Guide_Michelin.controller;

import M2fst.Guide_Michelin.dto.RestaurantDto;
import M2fst.Guide_Michelin.dto.RestaurantImagePutDto;
import M2fst.Guide_Michelin.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDto>> getRestaurants() {
        return ResponseEntity.ok(this.restaurantService.getRestaurants());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<RestaurantDto> getRestaurant(@PathVariable(name = "restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(this.restaurantService.getRestaurant(restaurantId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/restaurant")
    public ResponseEntity<RestaurantDto> addRestaurant(@Valid @RequestBody RestaurantDto restaurantDto) {
        RestaurantDto created = this.restaurantService.addRestaurant(restaurantDto.getNom(), restaurantDto.getAdresse());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/restaurant/{restaurantId}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable(name = "restaurantId") Integer restaurantId, @Valid @RequestBody RestaurantDto restaurantDto) {
        return ResponseEntity.ok(this.restaurantService.updateRestaurant(
                restaurantId, restaurantDto.getNom(), restaurantDto.getAdresse()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable(name = "restaurantId") Integer restaurantId) {
        this.restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/restaurant/{restaurantId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestaurantImagePutDto> uploadImage(@PathVariable(name = "restaurantId") Integer restaurantId, @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(this.restaurantService.uploadImage(restaurantId, file));
    }
}