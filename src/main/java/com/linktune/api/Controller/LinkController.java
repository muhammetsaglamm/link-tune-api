package com.linktune.api.Controller;

import com.linktune.api.Service.LinkService;
import com.linktune.api.model.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
public class LinkController {

    private final LinkService linkService;

    // Controller, sadece Service katmanını tanır.
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    /**
     * Yeni bir URL'yi kısaltmak için POST isteğini karşılar.
     * @param request JSON body'sinden gelen "longUrl" anahtarını içeren bir Map.
     * @return Oluşturulan yeni Link nesnesini ve 201 Created durum kodunu döner.
     */
    @PostMapping("/shorten")
    public ResponseEntity<Link> shortenUrl(@RequestBody Map<String, String> request) {
        String longUrl = request.get("longUrl");
        Link newLink = linkService.shortenUrl(longUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(newLink);
    }

    /**
     * Verilen kısa koda göre orijinal URL'ye yönlendirme yapar.
     * @param shortCode URL'den gelen path variable.
     * @return 302 Found durum kodu ile yönlendirme yanıtı döner.
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortCode) {
        Link link = linkService.getLongUrl(shortCode);

        // Bulunan linkin orijinal adresine (longUrl) yönlendir.
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(link.getLongUrl()))
                .build();
    }
}