package G2T6.G2T6.G2T6.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api")
@RestController
public class CarbonDataController {


    @GetMapping(value ="/carbon")
    public List<Object> getCarbonData(){
        String url = "https://www.carboninterface.com/api/v1/estimates";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + "2DA4NwXOsnGPMv4y6mw");

        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<Object[]> response = restTemplate.exchange(url, HttpMethod.GET, request, Object[].class);


        Object[] result = response.getBody();
        return Arrays.asList(result);

    }
}