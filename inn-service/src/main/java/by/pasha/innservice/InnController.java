package by.pasha.innservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InnController {
    private final InnService innService;

    @PostMapping("/api/inn")
    public Map<String, String> getInn(@RequestBody List<String> email) {
        Map<String, String> map = new HashMap<>();
        for (String em : email) {
            map.put(em, innService.generateIndividualINN());
        }
        return map;
    }
}
