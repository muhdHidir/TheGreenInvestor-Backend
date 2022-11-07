package G2T6.G2T6.G2T6.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import G2T6.G2T6.G2T6.services.similarity.*;
import G2T6.G2T6.G2T6.payload.request.SimilarityRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SimilarityController {

    /**
     * Compares two similarity scores.
     * 
     * @param similarityRequest The similarity request.
     * @return The similarity score.
     */
    @PostMapping("/getSimilarity")
    public ResponseEntity<?> getSimilarity(@Valid @RequestBody SimilarityRequest similarityRequest) {

        try {

            StringSimilarityServiceImpl stringSimilarityServiceImpl = new StringSimilarityServiceImpl(
                    new LevenshteinDistanceStrategy());

            double score = stringSimilarityServiceImpl.score(similarityRequest.getInput(),
                    similarityRequest.getTarget());

            return new ResponseEntity<>(score, HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        }

    }

}
