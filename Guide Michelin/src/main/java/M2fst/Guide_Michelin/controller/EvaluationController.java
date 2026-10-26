package M2fst.Guide_Michelin.controller;

import M2fst.Guide_Michelin.dto.EvaluationDto;
import M2fst.Guide_Michelin.dto.EvaluationPhotoPutDto;
import M2fst.Guide_Michelin.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EvaluationController {
    private final EvaluationService evaluationService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/restaurant/{id}/evaluation")
    public ResponseEntity<EvaluationDto> addEvaluation(@PathVariable("id") Integer restaurantId, @Valid @RequestBody EvaluationDto evaluationDto, Authentication authentication) {
        EvaluationDto created = this.evaluationService.addEvaluation(restaurantId, authentication.getName(), evaluationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/evaluation/{evaluationId}")
    public ResponseEntity<EvaluationDto> updateEvaluation(@PathVariable("evaluationId") Integer evaluationId, @Valid @RequestBody EvaluationDto evaluationDto, Authentication authentication) {
        return ResponseEntity.ok(this.evaluationService.updateEvaluation(
                evaluationId, evaluationDto, authentication.getName(), this.isAdmin(authentication)));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/evaluation/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable("evaluationId") Integer evaluationId, Authentication authentication) {
        this.evaluationService.deleteEvaluation(evaluationId, authentication.getName(), this.isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/evaluations/search")
    public ResponseEntity<List<EvaluationDto>> search(@RequestParam String motsCles) {
        return ResponseEntity.ok(this.evaluationService.searchEvaluations(motsCles));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/evaluations/me")
    public ResponseEntity<List<EvaluationDto>> mesEvaluations(Authentication authentication) {
        return ResponseEntity.ok(this.evaluationService.getEvaluationsByAuthor(authentication.getName()));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping(value = "/evaluation/{evaluationId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvaluationPhotoPutDto> uploadPhoto(@PathVariable(name = "evaluationId") Integer evaluationId, @RequestParam("file") MultipartFile file, Authentication authentication) {
        return ResponseEntity.ok(this.evaluationService.uploadPhoto(evaluationId, file, authentication.getName(), this.isAdmin(authentication)));
    }

    private boolean isAdmin(Authentication authentication) {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }
}